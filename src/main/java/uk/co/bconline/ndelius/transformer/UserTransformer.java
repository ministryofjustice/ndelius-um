package uk.co.bconline.ndelius.transformer;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.entity.DatasetEntity;
import uk.co.bconline.ndelius.entity.OrganisationEntity;
import uk.co.bconline.ndelius.entity.UserEntity;
import uk.co.bconline.ndelius.model.*;

@Component
public class UserTransformer {

    public Organisation map(OrganisationEntity organisationEntity) {
        return ofNullable(organisationEntity)
                .map(oe -> Organisation.builder()
                        .id(oe.getOrganisationID())
                        .code(oe.getCode())
                        .description(oe.getDescription())
                        .build())
                .orElse(null);
    }

    public List<Dataset> map(List<DatasetEntity> datasetEntity) {
        return datasetEntity.stream()
                .map(de -> Dataset.builder()
                        .id(de.getKey().getProbationAreaID())
                        .code(de.getProbationArea().getCode())
                        .description(de.getProbationArea().getDescription())
                        .organisation(map(de.getProbationArea().getOrganisation()))
                        .build())
                        .collect(Collectors.toList());
    }

	public Optional<NDUser> combine(UserEntity dbUser, OIDUser oidUser, ADUser ad1User, ADUser ad2User)
	{
		return Optional.of(ofNullable(ad2User)
				.map(v -> NDUser.builder()
						// AD2 details
						.username(v.getUsername())
						.build()).orElse(new NDUser()))
				.map(u -> ofNullable(ad1User).map(v -> u.toBuilder()
						// AD1 details
						.username(v.getUsername())
						.build()).orElse(u))
				.map(u -> ofNullable(dbUser).map(v -> u.toBuilder()
						// DB details
						.username(v.getUsername())
						.datasets(map(v.getDatasets()))
						.organisation(map(v.getOrganisation()))
						.endDate(v.getEndDate())
						.build()).orElse(u))
				.map(u -> ofNullable(oidUser).map(v -> u.toBuilder()
						// OID details
						.username(v.getUsername())
						.forenames(v.getForenames())
						.surname(v.getSurname())
						.build()).orElse(u));
	}
}
