package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import lombok.val;
import uk.co.bconline.ndelius.model.User;

public class ConditionallyRequiredValidator implements ConstraintValidator<ConditionallyRequired, User>
{
	private String requiredFieldName;
	private String requiredFieldLabel;
	private String ifPopulatedFieldName;
	private String ifPopulatedFieldLabel;

	@Override
	public void initialize(ConditionallyRequired annotation)
	{
		val required = annotation.required().split(":");
		val ifPopulated = annotation.ifPopulated().split(":");
		requiredFieldName = required[1];
		requiredFieldLabel = required[0];
		ifPopulatedFieldName = ifPopulated[1];
		ifPopulatedFieldLabel = ifPopulated[0];
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
					String.format("%s is required if %s is populated", requiredFieldLabel, ifPopulatedFieldLabel))
					.addConstraintViolation();
		}

		return valid;
	}
}
