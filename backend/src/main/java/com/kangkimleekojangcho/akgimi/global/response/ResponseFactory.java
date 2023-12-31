package com.kangkimleekojangcho.akgimi.global.response;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kangkimleekojangcho.akgimi.global.exception.HttpBusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static com.kangkimleekojangcho.akgimi.global.response.SuccessResponseMessage.message;

@RequiredArgsConstructor
public class ResponseFactory {

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data) {
        SuccessResponse<T> successResponse = new SuccessResponse<>(message.getValue(), data);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    public static ResponseEntity<SuccessResponse<String>> successWithoutData(String message) {
        SuccessResponse<String> response = new SuccessResponse<>(message, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static void fail(HttpServletResponse response, HttpBusinessException e) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(e.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        FailResponse failResponse = new FailResponse(e.getMessage(), e.getCode());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(failResponse));
    }

    public static ResponseEntity<FailResponse> fail(HttpBusinessException e) {
        FailResponse failResponse = new FailResponse(e.getMessage(), e.getCode());
        return new ResponseEntity<>(failResponse, HttpStatus.valueOf(e.getStatusCode()));
    }

    public static ResponseEntity<FailResponse> fail(HttpStatus statusCode, String message) {
        FailResponse failResponse = new FailResponse(message, "");
        return new ResponseEntity<>(failResponse, statusCode);
    }

    public static ResponseEntity<FailResponse> failWithServerError() {
        FailResponse failResponse = new FailResponse("", "000");
        return new ResponseEntity<>(failResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<FailResponse> fail(FailResponse failResponse, HttpStatus status) {
        return new ResponseEntity<>(failResponse, status);
    }
}
