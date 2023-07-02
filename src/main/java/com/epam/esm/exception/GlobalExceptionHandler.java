package com.epam.esm.exception;

import javax.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ErrorResponse handleNotValidException(Exception e) {
        String errorCode = "40001";
        return new ErrorResponse(e.getMessage(), errorCode);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleInvalidRequestException(ConstraintViolationException e) {
        String errorCode = "40002";
        StringJoiner joiner = new StringJoiner(", ");
        new ArrayList<>(e.getConstraintViolations()).forEach(cv -> joiner.add(cv.getMessage()));
        return new ErrorResponse(joiner.toString(), errorCode);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidSortRequestException.class)
    public ErrorResponse handleInvalidSortRequestException(InvalidSortRequestException e) {
        String errorCode = "40003";
        String errorMessage = "wrong format of sort parameter, required: " + e.getMessage();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTagNameException.class)
    public ErrorResponse handleInvalidTagNameException() {
        String errorCode = "40004";
        String errorMessage = "name of tag can not be null or empty";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException e) {
        String errorCode = "40101";
        String errorMessage = "Wrong email or password";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        String errorCode = "40301";
        String errorMessage = e.getMessage();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ErrorResponse handleJwtAuthenticationException(JwtAuthenticationException e) {
        String errorCode = "40302";
        String errorMessage = e.getMessage();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        String errorCode = "40303";
        String errorMessage = e.getMessage();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidIdException.class)
    public ErrorResponse handleResourceNotFoundException(InvalidIdException e) {
        String errorCode = "40401" + e.getId();
        String errorMessage = "Requested " + e.getResourceName() + " not found (id=" + e.getId() + ")";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidUserOrderIdException.class)
    public ErrorResponse handleWrongOrderIdForUserException(InvalidUserOrderIdException e) {
        String errorCode = "40402" + e.getOrderId();
        String errorMessage = "Order with id=" + e.getOrderId() + " not found for user with id=" + e.getUserId();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MostWidelyUsedTagNotFoundException.class)
    public ErrorResponse handleWidelyUsedTagNotFoundException(MostWidelyUsedTagNotFoundException e) {
        String errorCode = "40403" + e.getUserId();
        String errorMessage = "Most widely used tag not found for user with id=" + e.getUserId();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TagsNotFoundException.class)
    public ErrorResponse handleTagsNotFoundException(TagsNotFoundException e) {
        String errorCode = "40404" + e.getGiftCertificateId();
        String errorMessage = "Tags not found for GiftCertificate with id=" + e.getGiftCertificateId();
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDuplicateUserException(DataIntegrityViolationException e) {
        String errorCode = "40901";
        String errorMessage = "Resource deleting restricted";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateTagNameException.class)
    public ErrorResponse handleOnDeleteRestrictException() {
        String errorCode = "40902";
        String errorMessage = "Tag already exists";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllException(Exception e) {
        String errorCode = "50000";
        String errorMessage = "Unrecognised server error";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ErrorResponse handleDatabaseError(Exception e) {
        String errorCode = "50001";
        String errorMessage = "Database error";
        return new ErrorResponse(errorMessage, errorCode);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(NoContentException.class)
    public void handleNoContentException() {
        //Empty method for return HttpResponse with NO_CONTENT status
    }

    private class ErrorResponse {
        private String errorMessage;
        private String errorCode;

        public ErrorResponse(String errorMessage, String errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
