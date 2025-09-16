package uk.co.bconline.ndelius.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.service.DatasetService;

import static org.springframework.util.StringUtils.hasLength;

public class PrefixMatchesProviderCodeValidator implements ConstraintValidator<PrefixMatchesProviderCode, String>
{
	@Autowired
	private DatasetService datasetService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if (!hasLength(value)) return true;
		if (value.length() < 3) return false;

		val prefix = value.substring(0, 3);
		return datasetService.getDatasetByCode(prefix).isPresent();
	}
}
