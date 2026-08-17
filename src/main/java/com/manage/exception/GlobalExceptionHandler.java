package com.manage.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex, HttpServletRequest req) {
		ErrorResponse error = new ErrorResponse();
		error.setMessage(ex.getMessage());
		error.setPath(req.getRequestURI());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setTimestamp(LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex){
		Map<String,String> errors=new HashMap<>();
		ex.getFieldErrors().forEach(error->errors.put(error.getField(),error.getDefaultMessage()));
		
		return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
	}
}