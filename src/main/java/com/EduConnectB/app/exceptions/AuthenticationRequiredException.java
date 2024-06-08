package com.EduConnectB.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // Código de estado HTTP 401 Unauthorized ojo, no es el 403
public class AuthenticationRequiredException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationRequiredException(String message) {
        super(message);
    }
}
