package com.example.employeemanagementsystem.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String TIMESTAMP = "timestamp";
	private static final String STATUS = "status";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";

	private static final ZoneId APPLICATION_ZONE = ZoneId.of("Asia/Kolkata");

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleEmployeeNotFound(EmployeeNotFoundException exception) {

		Map<String, Object> response = new LinkedHashMap<>();
		response.put(TIMESTAMP, LocalDateTime.now(APPLICATION_ZONE));
		response.put(STATUS, HttpStatus.NOT_FOUND.value());
		response.put(ERROR, "Employee Not Found");
		response.put(MESSAGE, exception.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {

		Map<String, Object> response = new LinkedHashMap<>();
		response.put(TIMESTAMP, LocalDateTime.now(APPLICATION_ZONE));
		response.put(STATUS, HttpStatus.BAD_REQUEST.value());
		response.put(ERROR, "Bad Request");
		response.put(MESSAGE, exception.getMessage());

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {

		Map<String, Object> response = new LinkedHashMap<>();

		String message = exception.getBindingResult().getFieldErrors().stream().findFirst()
				.map(error -> error.getDefaultMessage()).orElse("Validation failed");

		response.put(TIMESTAMP, LocalDateTime.now(APPLICATION_ZONE));
		response.put(STATUS, HttpStatus.BAD_REQUEST.value());
		response.put(ERROR, "Validation Failed");
		response.put(MESSAGE, message);

		return ResponseEntity.badRequest().body(response);
	}
}