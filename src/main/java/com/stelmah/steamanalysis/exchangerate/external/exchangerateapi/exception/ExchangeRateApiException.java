package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.exception;

import org.apache.commons.lang3.exception.ContextedRuntimeException;

public class ExchangeRateApiException extends ContextedRuntimeException {

    public ExchangeRateApiException() {
        super();
    }

    public ExchangeRateApiException(String message) {
        super(message);
    }

    public ExchangeRateApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRateApiException(Throwable cause) {
        super(cause);
    }

}
