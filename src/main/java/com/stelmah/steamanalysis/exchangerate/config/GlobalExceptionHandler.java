package com.stelmah.steamanalysis.exchangerate.config;

import com.stelmah.steamanalysis.exchangerate.dto.ErrorDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.exception.ExchangeRateApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ExchangeRateApiException.class)
    public ResponseEntity<ErrorDto> handleExchangeRateApiException(ExchangeRateApiException ex, WebRequest request) {
        log.error("Exchange Rate API error occurred: ", ex);

        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Exchange Rate API Error",
                "An unexpected error occurred. Please contact support.",
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorDto> handleExchangeRateApiException(HttpStatusCodeException ex, WebRequest request) {
        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getMessage(),
                request.getDescription(false)
        );

        log.error(errorDto.toString(), ex);

        return new ResponseEntity<>(errorDto, ex.getStatusCode());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> handleExchangeRateApiException(Throwable ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);

        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "System Error",
                "An unexpected error occurred. Please contact support.",
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
