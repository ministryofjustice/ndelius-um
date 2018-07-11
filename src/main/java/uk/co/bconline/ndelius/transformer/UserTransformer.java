package uk.co.bconline.ndelius.transformer;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.DatasetEntity;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.ldap.ADUser;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

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
                        .build())
                        .collect(Collectors.toList());
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
						.staffCode(Optional.ofNullable(v.getStaff()).map(StaffEntity::getCode).orElse(null))
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
