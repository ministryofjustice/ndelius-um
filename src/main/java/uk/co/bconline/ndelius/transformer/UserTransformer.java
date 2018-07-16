package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.*;
import uk.co.bconline.ndelius.model.entity.DatasetEntity;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDBusinessTransaction;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class UserTransformer {

    public Organisation map(OrganisationEntity organisationEntity) {
        return ofNullable(organisationEntity)
                .map(oe -> Organisation.builder()
                        .code(oe.getCode())
                        .description(oe.getDescription())
                        .build())
                .orElse(null);
    }

    public List<Dataset> map(List<DatasetEntity> datasetEntity) {
        return datasetEntity.stream()
                .map(de -> Dataset.builder()
                        .code(de.getProbationArea().getCode())
                        .description(de.getProbationArea().getDescription())
                        .organisation(map(de.getProbationArea().getOrganisation()))
						 .active("Y".equalsIgnoreCase(de.getProbationArea().getSelectable()))
                        .build())
                        .collect(toList());
    }

	public SearchResult map(UserEntity user)
	{
		return SearchResult.builder()
				.username(user.getUsername())
				.forenames(combineForenames(user.getForename(), user.getForename2()))
				.surname(user.getSurname())
				.staffCode(ofNullable(user.getStaff()).map(StaffEntity::getCode).orElse(null))
				.build();
	}

	public Optional<User> combine(UserEntity dbUser, OIDUser oidUser, ADUser ad1User, ADUser ad2User)
	{
		return Optional.of(ofNullable(ad2User)
				.map(v -> User.builder()
						// AD2 details
						.username(v.getUsername())
						.build()).orElse(new User()))
				.map(u -> ofNullable(ad1User).map(v -> u.toBuilder()
						// AD1 details
						.username(v.getUsername())
						.build()).orElse(u))
				.map(u -> ofNullable(dbUser).map(v -> u.toBuilder()
						// DB details
						.username(v.getUsername())
						.datasets(map(v.getDatasets()))
						.organisation(map(v.getOrganisation()))
						.staffCode(ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
						.endDate(v.getEndDate())
						.build()).orElse(u))
				.map(u -> ofNullable(oidUser).map(v -> u.toBuilder()
						// OID details
						.username(v.getUsername())
						.forenames(v.getForenames())
						.surname(v.getSurname())
						.transactions(ofNullable(v.getTransactions())
								.map(transactions -> transactions.stream()
										.map(this::map)
										.collect(toList()))
								.orElse(null))
						.build()).orElse(u));
	}

	private Transaction map(OIDBusinessTransaction transaction){
    	return Transaction.builder()
				.name(transaction.getName())
				.roles(transaction.getRoles())
				.description(transaction.getDescription())
				.build();
	}

	public Optional<User> map(OIDUser user)
	{
		return combine(null, user, null, null);
	}

	private String combineForenames(String forename, String forename2)
	{
		if (isEmpty(forename)) return "";
		if (isEmpty(forename2)) return forename;
		return forename + " " + forename2;
	}
}
