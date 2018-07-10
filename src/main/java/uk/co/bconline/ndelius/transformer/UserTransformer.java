package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.entity.DatasetEntity;
import uk.co.bconline.ndelius.entity.OrganisationEntity;
import uk.co.bconline.ndelius.entity.UserEntity;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserTransformer {
    public User userOf(User user, UserEntity userEntity) {

        user.setOrganisation(organisationOf(userEntity.getOrganisation()));

        user.setDatasets(datasetOf(userEntity.getDatasets()));

        return user;
    }

    public Organisation organisationOf(OrganisationEntity organisationEntity) {
        return Optional.ofNullable(organisationEntity)
                .map(oe -> Organisation.builder()
                        .id(oe.getOrganisationID())
                        .code(oe.getCode())
                        .description(oe.getDescription())
                        .build())
                .orElse(null);
    }

    public List<Dataset> datasetOf(List<DatasetEntity> datasetEntity) {
        return datasetEntity.stream()
                .map(de -> Dataset.builder()
                        .id(de.getKey().getProbationAreaID())
                        .code(de.getProbationArea().getCode())
                        .description(de.getProbationArea().getDescription())
                        .organisation(organisationOf(de.getProbationArea().getOrganisation()))
                        .build())
                        .collect(Collectors.toList());
    }
}
