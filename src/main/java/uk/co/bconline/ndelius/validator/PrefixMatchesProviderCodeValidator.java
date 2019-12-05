package uk.co.bconline.ndelius.validator;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.service.DatasetService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.util.StringUtils.isEmpty;

public class PrefixMatchesProviderCodeValidator implements ConstraintValidator<PrefixMatchesProviderCode, String>
{
	@Autowired
	private DatasetService datasetService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if (isEmpty(value)) return true;
		if (value.length() < 3) return false;

		val prefix = value.substring(0, 3);
		return datasetService.getDatasetByCode(prefix).isPresent();
	}
}
