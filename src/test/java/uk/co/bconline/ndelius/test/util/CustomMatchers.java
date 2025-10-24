package uk.co.bconline.ndelius.test.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;

public class CustomMatchers {
    public static class WithinMatcherBuilder {
        private Duration duration;

        WithinMatcherBuilder(Duration duration) {
            this.duration = duration;
        }

        public Matcher<String> of(LocalDateTime expected) {
            return new TypeSafeMatcher<String>() {
                @Override
                public void describeTo(Description description) {
                    description.appendText(String.format("a value within <%s ms> of <%s>", duration.toMillis(), expected));
                }

                @Override
                public boolean matchesSafely(String item) {
                    LocalDateTime dateItem = LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(item));
                    return dateItem.isAfter(expected.minus(duration)) && dateItem.isBefore(expected.plus(duration));
                }
            };
        }
    }

    public static WithinMatcherBuilder isWithin(long amount, TemporalUnit unit) {
        return new WithinMatcherBuilder(Duration.of(amount, unit));
    }
}
