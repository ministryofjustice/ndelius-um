package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.util.Optionals;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entity.export.UserExportEntity;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEventType;
import uk.co.bconline.ndelius.repository.db.ChangeNoteRepository;
import uk.co.bconline.ndelius.repository.db.ProbationAreaUserRepository;
import uk.co.bconline.ndelius.repository.db.SearchResultRepository;
import uk.co.bconline.ndelius.repository.db.StaffRepository;
import uk.co.bconline.ndelius.repository.db.StaffTeamRepository;
import uk.co.bconline.ndelius.repository.db.UserEntityRepository;
import uk.co.bconline.ndelius.service.DomainEventService;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.transformer.SearchResultTransformer;
import uk.co.bconline.ndelius.util.SearchUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    private final UserEntityRepository repository;
    private final StaffRepository staffRepository;
    private final SearchResultRepository searchResultRepository;
    private final ProbationAreaUserRepository probationAreaUserRepository;
    private final StaffTeamRepository staffTeamRepository;
    private final ChangeNoteRepository changeNoteRepository;
    private final SearchResultTransformer searchResultTransformer;
    private final DomainEventService domainEventService;

    @Autowired
    public UserEntityServiceImpl(
        UserEntityRepository repository,
        StaffRepository staffRepository,
        SearchResultRepository searchResultRepository,
        ProbationAreaUserRepository probationAreaUserRepository,
        StaffTeamRepository staffTeamRepository,
        ChangeNoteRepository changeNoteRepository,
        SearchResultTransformer searchResultTransformer,
        DomainEventService domainEventService) {
        this.repository = repository;
        this.staffRepository = staffRepository;
        this.searchResultRepository = searchResultRepository;
        this.probationAreaUserRepository = probationAreaUserRepository;
        this.staffTeamRepository = staffTeamRepository;
        this.changeNoteRepository = changeNoteRepository;
        this.searchResultTransformer = searchResultTransformer;
        this.domainEventService = domainEventService;
    }

    @Override
    public boolean usernameExists(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<UserEntity> getUser(String username) {
        val t = LocalDateTime.now();
        val u = repository.findFirstByUsernameIgnoreCase(username);
        log.trace("--{}ms	DB lookup", MILLIS.between(t, LocalDateTime.now()));
        return u;
    }

    @Override
    public Optional<StaffEntity> getStaffByStaffCode(String code) {
        try {
            return staffRepository.findByCodeAndEndDateIsNull(code).or(() -> staffRepository.findByCode(code));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new AppException("Unable to select a unique Staff Record for code: " + code);
        }
    }

    @Override
    public Optional<UserEntity> getUserByStaffCode(String code) {
        return getStaffByStaffCode(code)
            .map(s -> s.getUser().isEmpty() ?
                UserEntity.builder().staff(s).build() :
                s.getUser().iterator().next());
    }

    @Override
    public long getUserId(String username) {
        return repository.getUserId(username).orElseThrow(() ->
            new UsernameNotFoundException(String.format("Unable to find Entity ID for user '%s'", username)));
    }

    @Override
    public long getMyUserId() {
        return getUserId(myUsername());
    }

    @Override
    public Stream<UserExportEntity> export() {
        return repository.export(now()); // Must pass in the current date, as using `CURRENT_DATE` in JPQL/Oracle includes the timestamp
    }

    @Override
    public List<SearchResult> search(String query, boolean includeInactiveUsers, Set<String> datasets) {
        val t = LocalDateTime.now();
        val results = SearchUtils.streamTokens(query).parallel()
            .flatMap(token -> searchForToken(token, includeInactiveUsers, datasets))
            .map(searchResultTransformer::map)
            .collect(groupingByConcurrent(SearchResult::getUsername, reducing((a, b) -> a.withScore(a.getScore() + b.getScore()))))
            .values().stream().flatMap(Optional::stream)
            .collect(toList());
        log.debug("Found {} DB results in {}ms", results.size(), MILLIS.between(t, LocalDateTime.now()));
        return results;
    }

    private Stream<SearchResultEntity> searchForToken(String token, boolean includeInactiveUsers, Set<String> datasets) {
        log.debug("Searching DB: {}", token);
        var filterDatasets = true;
        if (isEmpty(datasets)) {
            filterDatasets = false;
            datasets = singleton("");
        }
        val isOracle = datasourceUrl.startsWith("jdbc:oracle");
        return (isOracle ?
            searchResultRepository.search(token, includeInactiveUsers, filterDatasets, datasets) :
            searchResultRepository.simpleSearch(token, includeInactiveUsers ? 1 : 0, filterDatasets ? 1 : 0, datasets))
            .stream()
            .collect(groupingBy(SearchResultEntity::getUsername, reducing(searchResultTransformer::reduceTeams)))
            .values().stream().flatMap(Optionals::toStream);
    }

    @Override
    public UserEntity save(UserEntity user) {
        val t = LocalDateTime.now();

        boolean sendDomainEvent = ofNullable(user.getId()).flatMap(repository::findById).map(userEntity ->
        {
            // Only send for changes in forenames/surname/grade/code
            return !user.getForename().equals(userEntity.getForename())
                || !user.getSurname().equals(userEntity.getSurname())
                || !user.getNullSafeStaffGradeId().equals(userEntity.getNullSafeStaffGradeId())
                || !user.getNullSafeForename2().equals(userEntity.getNullSafeForename2())
                || !user.getNullSafeStaffCode().equals(userEntity.getNullSafeStaffCode());
        }).orElse(true);

        // Staff/Teams
        ofNullable(user.getStaff()).ifPresent(staff -> {
            log.debug("Retrieving existing staff");
            ofNullable(staff.getId()).flatMap(staffRepository::findById).ifPresentOrElse(existingStaff -> {
                    log.debug("Unlinking any other users with the same staff code");
                    unlinkOtherUsersFromStaff(user, existingStaff);
                    log.debug("Deleting team links");
                    val allTeamEntities = staffTeamRepository.findStaffTeamEntitiesByStaffId(existingStaff.getId());
                    staffTeamRepository.deleteAll(allTeamEntities);
                    log.debug("Saving team links");
                    staffTeamRepository.saveAll(staff.getTeamLinks());
                    log.debug("Saving staff");
                    staffRepository.save(staff);
                },
                () -> {
                    log.debug("Saving staff");
                    staffRepository.save(staff);
                    log.debug("Saving team links");
                    staffTeamRepository.saveAll(staff.getTeamLinks());
                });
        });

        // User/Datasets
        log.debug("Retrieving existing user");
        val savedUser = ofNullable(user.getId()).flatMap(repository::findById).map(existingUser -> {
            handlePreviousStaffRecord(user.getStaff(), existingUser.getStaff());
            log.debug("Deleting datasets");
            val allDataSets = probationAreaUserRepository.findProbationAreaUserEntitiesByUserId(user.getId());
            probationAreaUserRepository.deleteAll(allDataSets);
            log.debug("Saving new datasets");
            probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
            log.debug("Saving user");
            return repository.save(user);
        }).orElseGet(() -> {
            log.debug("Saving user");
            val userEntity = repository.save(user);
            log.debug("Saving new datasets");
            probationAreaUserRepository.saveAll(user.getProbationAreaLinks());
            return userEntity;
        });
        log.debug("Updating user history");
        changeNoteRepository.saveAll(user.getHistory());

        // Send Domain event if user has staff code
        if (user.getStaff() != null && user.getStaff().getCode() != null && !user.getStaff().getCode().isEmpty() && sendDomainEvent) {
            val additionalInformation = Map.of("staffCode", user.getStaff().getCode());
            domainEventService.insertDomainEvent(HmppsDomainEventType.UMT_STAFF_UPDATED, additionalInformation);
        }

        log.debug("Finished saving user to database in {}ms", MILLIS.between(t, LocalDateTime.now()));
        return savedUser;
    }

    private void unlinkOtherUsersFromStaff(UserEntity user, StaffEntity existingStaff) {
        // This is required due to the OneToOne user/staff relationship being modelled in the database as a OneToMany
        val userId = ofNullable(user).map(UserEntity::getId).orElse(null);
        ofNullable(existingStaff).map(StaffEntity::getUser)
            .ifPresent(users -> users.stream()
                .filter(u -> !u.getId().equals(userId))
                .map(u -> u.toBuilder().staff(null).createdBy(null).updatedBy(null).build())
                .forEach(repository::save));
    }

    // When a user is given a new staff code, an End Date should be added to their previous staff record.
    private void handlePreviousStaffRecord(StaffEntity newStaff, StaffEntity previousStaff) {
        if (previousStaff == null || !StringUtils.hasLength(previousStaff.getCode())) {
            // the user never had a staff code - nothing to end-date
            return;
        }
        if (newStaff == null || !StringUtils.hasLength(newStaff.getCode()) ||
            !newStaff.getCode().equals(previousStaff.getCode())) {
            // staff code removed or changed - set an end-date of yesterday to the old one
            val endDate = now().minusDays(1);
            log.debug(String.format("Adding an end-date of %s to previous staff record (%s)", endDate, previousStaff.getCode()));
            // if setting the end-date will cause it to be before the start-date (e.g if the start-date is today), then also update the start-date
            LocalDate startDate = previousStaff.getStartDate();
            if (startDate != null && startDate.isAfter(endDate)) startDate = endDate;
            staffRepository.save(previousStaff.toBuilder()
                .startDate(startDate)
                .endDate(endDate)
                .updatedAt(LocalDateTime.now())
                .updatedById(getMyUserId())
                .build());
        }
    }
}
