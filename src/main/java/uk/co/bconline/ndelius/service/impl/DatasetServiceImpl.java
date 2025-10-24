package uk.co.bconline.ndelius.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;
import uk.co.bconline.ndelius.repository.db.ProbationAreaRepository;
import uk.co.bconline.ndelius.repository.db.SubContractedProviderRepository;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.transformer.DatasetTransformer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
@Service
public class DatasetServiceImpl implements DatasetService {
    private final DatasetTransformer transformer;
    private final ProbationAreaRepository repository;
    private final SubContractedProviderRepository subContractedProviderRepository;

    @Autowired
    public DatasetServiceImpl(
        ProbationAreaRepository repository,
        SubContractedProviderRepository subContractedProviderRepository,
        DatasetTransformer transformer) {
        this.repository = repository;
        this.subContractedProviderRepository = subContractedProviderRepository;
        this.transformer = transformer;
    }

    @Override
    public List<Dataset> getDatasets() {
        if (isNational()) {
            // If I am a national access user, return all datasets
            log.debug("National user, fetching all datasets");
            return repository.findAllSelectableNonEstablishments().stream()
                .map(transformer::map)
                .collect(toList());
        } else {
            // If I am not a national access user, only return datasets that are already assigned to my user
            log.debug("Non-national user, fetching user datasets for {}", myUsername());
            return getDatasets(myUsername());
        }
    }

    @Override
    public List<Dataset> getDatasets(String username) {
        return repository.findAllByUserLinks_User_Username(username).stream()
            .filter(p -> !p.isEstablishment())
            .map(transformer::map)
            .collect(toList());
    }

    @Override
    public Set<String> getDatasetCodes(String username) {
        return getDatasets(username).stream().map(Dataset::getCode).collect(toSet());
    }

    @Override
    public Optional<Long> getDatasetId(String code) {
        return repository.findIdByCode(code);
    }

    @Override
    public Optional<Dataset> getDatasetByCode(String code) {
        return repository.findByCode(code).map(transformer::map);
    }

    @Override
    public String getNextStaffCode(String datasetCode) {
        return repository.getNextStaffCode(datasetCode);
    }

    @Override
    public Optional<Long> getOrganisationIdByDatasetCode(String code) {
        return repository.findOrganisationIdByCode(code);
    }

    @Override
    public List<Dataset> getSubContractedProviders(String datasetCode) {
        return subContractedProviderRepository.findAllByProviderCode(datasetCode).stream()
            .map(transformer::map)
            .collect(toList());
    }

    @Override
    public Optional<Long> getSubContractedProviderId(String code) {
        return subContractedProviderRepository.findByCode(code).map(SubContractedProviderEntity::getId);
    }

    @Override
    public List<Dataset> getEstablishments() {
        return repository.findAllBySelectableTrueAndEstablishmentTrue().stream()
            .map(transformer::map)
            .collect(toList());
    }
}
