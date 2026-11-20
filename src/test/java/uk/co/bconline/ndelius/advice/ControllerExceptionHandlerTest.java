package uk.co.bconline.ndelius.advice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ControllerExceptionHandlerTest {
    @Autowired
    ControllerExceptionHandler controllerExceptionHandler;

    @Test
    public void appExceptionReturns500ErrorWithMessage() {
        ErrorResponse response = controllerExceptionHandler.handle(new AppException("test message"));
        assertThat(response.getError()).first().isEqualTo("test message");
    }

    @Test
    public void appExceptionDoesntIncludeStackTraceIfMessageProvided() {
        ErrorResponse response = controllerExceptionHandler.handle(new AppException("test message", new RuntimeException()));
        assertThat(response.getError()).first().isEqualTo("test message");

        response = controllerExceptionHandler.handle(new AppException(new RuntimeException()));
        assertThat(response.getError()).first().isEqualTo("java.lang.RuntimeException");
    }

    @Test
    public void appExceptionEmptyMessageReturnsEmptyBody() {
        ErrorResponse response = controllerExceptionHandler.handle(new AppException());
        assertThat(response).isNull();
    }
}
