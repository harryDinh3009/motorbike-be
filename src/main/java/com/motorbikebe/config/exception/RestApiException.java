package com.motorbikebe.config.exception;

import com.motorbikebe.common.ApiStatus;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    private final ApiStatus apiStatus;
    private final String customMessage;

    public RestApiException(ApiStatus apiStatus) {
        super(apiStatus.getMessage());
        this.apiStatus = apiStatus;
        this.customMessage = null;
    }

    public RestApiException(ApiStatus apiStatus, String customMessage) {
        super(customMessage != null ? customMessage : apiStatus.getMessage());
        this.apiStatus = apiStatus;
        this.customMessage = customMessage;
    }

}
