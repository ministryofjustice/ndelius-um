package uk.co.bconline.ndelius.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.val;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateRangeValidator implements ConstraintValidator<DateRange, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private DateRange constraint;

    @Override
    public void initialize(DateRange constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        val min = LocalDate.from(formatter.parse(constraint.min()));
        val max = LocalDate.from(formatter.parse(constraint.max()));
        return value == null || !(value.isBefore(min) || value.isAfter(max));
    }
}
