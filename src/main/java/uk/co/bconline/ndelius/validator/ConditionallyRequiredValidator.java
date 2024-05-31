package uk.co.bconline.ndelius.validator;

import lombok.val;
import org.springframework.beans.BeanWrapperImpl;
import uk.co.bconline.ndelius.model.User;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.springframework.util.ObjectUtils.isEmpty;
import static uk.co.bconline.ndelius.util.NameUtils.camelCaseToTitleCase;

public class ConditionallyRequiredValidator implements ConstraintValidator<ConditionallyRequired, User>
{
	private String requiredFieldName;
	private String ifPopulatedFieldName;

	@Override
	public void initialize(ConditionallyRequired annotation)
	{
		requiredFieldName = annotation.required();
		ifPopulatedFieldName = annotation.ifPopulated();
	}

	@Override
	public boolean isValid(User user, ConstraintValidatorContext ctx)
	{
		val bean = new BeanWrapperImpl(user);
		val requiredFieldValue = bean.getPropertyValue(requiredFieldName);
		val ifPopulatedFieldValue = bean.getPropertyValue(ifPopulatedFieldName);
		val valid = isEmpty(ifPopulatedFieldValue) || !isEmpty(requiredFieldValue);

		if (!valid)
		{
			ctx.disableDefaultConstraintViolation();
			ctx.buildConstraintViolationWithTemplate(
					String.format("%s is required if %s is populated",
							camelCaseToTitleCase(requiredFieldName),
							camelCaseToTitleCase(ifPopulatedFieldName)))
					.addConstraintViolation();
		}

		return valid;
	}
}
