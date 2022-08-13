package com.social.moinda.web.member.advice;

import com.social.moinda.api.member.exception.NotFoundMemberException;
import com.social.moinda.api.member.exception.RegisteredEmailException;
import com.social.moinda.core.exception.ErrorCode;
import com.social.moinda.core.exception.ErrorResponse;
import com.social.moinda.web.member.MemberApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = MemberApiController.class)
public class MemberApiControllerAdvice {

    // TODO : 전체적으로 쓸지
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestBodyHandler(BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCode.notValidRequestResponse(bindingResult));
    }

    @ExceptionHandler(value = NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> notFoundMemberExceptionHandler(NotFoundMemberException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorResponse());
    }

    @ExceptionHandler(value = RegisteredEmailException.class)
    public ResponseEntity<ErrorResponse> registeredEmailExceptionHandler(RegisteredEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorResponse());
    }

}