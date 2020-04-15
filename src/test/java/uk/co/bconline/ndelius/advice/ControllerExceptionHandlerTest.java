package uk.co.bconline.ndelius.advice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.ErrorResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ControllerExceptionHandlerTest
{
	@Autowired
	ControllerExceptionHandler controllerExceptionHandler;

	@Test
	public void appExceptionReturns500ErrorWithMessage()
	{
		ErrorResponse response = controllerExceptionHandler.handle(new AppException("test message"));
		assertEquals("test message", response.getError().get(0));
	}

	@Test
	public void appExceptionDoesntIncludeStackTraceIfMessageProvided()
	{
		ErrorResponse response = controllerExceptionHandler.handle(new AppException("test message", new RuntimeException()));
		assertEquals("test message", response.getError().get(0));

		response = controllerExceptionHandler.handle(new AppException(new RuntimeException()));
		assertEquals("java.lang.RuntimeException", response.getError().get(0));
	}

	@Test
	public void appExceptionEmptyMessageReturnsEmptyBody()
	{
		ErrorResponse response = controllerExceptionHandler.handle(new AppException());
		assertNull(response);
	}
}