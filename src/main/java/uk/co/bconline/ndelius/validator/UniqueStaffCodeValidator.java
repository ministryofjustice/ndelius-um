package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.repository.db.StaffRepository;

public class UniqueStaffCodeValidator implements ConstraintValidator<UniqueStaffCode, User>
{
	@Autowired
	private StaffRepository staffRepository;

	@Override
	@Transactional
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String staffCode = user.getStaffCode();
		String username = user.getUsername();

		// no staff code => valid
		if (isEmpty(staffCode) || isEmpty(username)) return true;

		// staff doesn't exist, or staff is already linked to current user => valid
		val staff = staffRepository.findByCode(staffCode);
		return !staff.isPresent() || staff
				.map(StaffEntity::getUser)
				.map(Set::iterator)
				.filter(Iterator::hasNext)
				.map(Iterator::next)
				.map(UserEntity::getUsername)
				.map(username::equals)
				.orElse(false);
	}
}
