package uk.co.bconline.ndelius.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import uk.co.bconline.ndelius.exception.AppException;

public class ControllerExceptionHandlerTest
{
	@Test
	public void appExceptionReturns500ErrorWithMessage()
	{
		Map response = new ControllerExceptionHandler().handle(new AppException("test message"));
		assertEquals("test message", response.get("error"));
	}

	@Test
	public void appExceptionDoesntIncludeStackTraceIfMessageProvided()
	{
		Map response = new ControllerExceptionHandler().handle(new AppException("test message", new RuntimeException()));
		assertEquals("test message", response.get("error"));

		response = new ControllerExceptionHandler().handle(new AppException(new RuntimeException()));
		assertEquals("java.lang.RuntimeException", response.get("error"));
	}

	@Test
	public void appExceptionEmptyMessageReturnsEmptyBody()
	{
		Map response = new ControllerExceptionHandler().handle(new AppException());
		assertNull(response);
	}
}