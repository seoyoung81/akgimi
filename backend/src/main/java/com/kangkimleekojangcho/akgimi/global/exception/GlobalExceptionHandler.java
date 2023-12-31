package com.kangkimleekojangcho.akgimi.global.exception;


import com.kangkimleekojangcho.akgimi.common.application.MattermostSender;
import com.kangkimleekojangcho.akgimi.global.response.FailResponse;
import com.kangkimleekojangcho.akgimi.global.response.ResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SignatureException;
import java.util.Enumeration;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpBusinessException.class)
    public ResponseEntity<FailResponse> httpBusinessException(HttpBusinessException e) {
        log.error("비즈니스 에러 예외 발생! << code: {}", e.getCode());
        log.error("에러 로그: {}",getPrintStackTrace(e));
        return ResponseFactory.fail(e);
    }


    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<FailResponse> serverErrorException(ServerErrorException e,
                                                             HttpServletRequest request) {
        log.error("서버 에러 예외 발생! << code: {}", e.getCode());
        log.error("에러 로그: {}",getPrintStackTrace(e));
        return ResponseFactory.failWithServerError();
    }

    public String getPrintStackTrace(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<FailResponse> signatureException(SignatureException e,
                                                           HttpServletRequest request) {
        log.error("유효하지 않은 토큰 입력!");
        log.error("에러 로그: {}",getPrintStackTrace(e));
        return ResponseFactory.fail(new UnauthorizedException(UnauthorizedExceptionCode.INVALID_TOKEN));
    }

    // query string validation 검사 후 실패 하면 호출된다.
    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<FailResponse> bindException(BindException e,
                                                      HttpServletRequest request) {
        BadRequestExceptionCode invalidInput = BadRequestExceptionCode.INVALID_INPUT;
        String message = getBindingResultMessage(e.getBindingResult(),
                invalidInput.getDescriptionMessage());

        FailResponse failResponse = new FailResponse(message, invalidInput.getCode());
        return ResponseFactory.fail(failResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<FailResponse> businessConstraintViolationException(ConstraintViolationException e,HttpServletRequest request) {
        BadRequestExceptionCode invalidInput = BadRequestExceptionCode.INVALID_INPUT;
        String message = e.getMessage();
        log.error(e.getStackTrace());
        FailResponse failResponse = new FailResponse(message, invalidInput.getCode());
        return ResponseFactory.fail(failResponse, HttpStatus.BAD_REQUEST);
    }

    private String getParams(HttpServletRequest req) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> keys = req.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            params.append("- ").append(key).append(" : ").append(req.getParameter(key)).append("\n");
        }
        return params.toString();
    }


    private String getBindingResultMessage(BindingResult bindingResult, String defaultMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(defaultMessage).append("] ")
                .append(this.getBindingResultErrorMessage(bindingResult));
        return sb.toString();
    }

    private String getBindingResultErrorMessage(BindingResult bindingResult) {
        ObjectError objectError = bindingResult.getAllErrors()
                .stream()
                .findFirst()
                .get();
        return objectError.getDefaultMessage();
    }


}
