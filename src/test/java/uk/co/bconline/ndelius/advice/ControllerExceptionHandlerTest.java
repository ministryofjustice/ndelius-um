package uk.co.bconline.ndelius.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.ErrorResponse;

public class ControllerExceptionHandlerTest
{
	@Test
	public void appExceptionReturns500ErrorWithMessage()
	{
		ErrorResponse response = new ControllerExceptionHandler().handle(new AppException("test message"));
		assertEquals("test message", response.getError().get(0));
	}

	@Test
	public void appExceptionDoesntIncludeStackTraceIfMessageProvided()
	{
		ErrorResponse response = new ControllerExceptionHandler().handle(new AppException("test message", new RuntimeException()));
		assertEquals("test message", response.getError().get(0));

		response = new ControllerExceptionHandler().handle(new AppException(new RuntimeException()));
		assertEquals("java.lang.RuntimeException", response.getError().get(0));
	}

	@Test
	public void appExceptionEmptyMessageReturnsEmptyBody()
	{
		ErrorResponse response = new ControllerExceptionHandler().handle(new AppException());
		assertNull(response);
	}
}