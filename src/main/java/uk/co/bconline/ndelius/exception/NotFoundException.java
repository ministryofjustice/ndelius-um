package uk.co.bconline.ndelius.exception;

public class NotFoundException extends RuntimeException
{
	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
	}
}
