package com.motorbikebe.config.exception;

import com.motorbikebe.common.ApiStatus;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    private final ApiStatus apiStatus;

    public RestApiException(ApiStatus apiStatus) {
        super(apiStatus.getMessage());
        this.apiStatus = apiStatus;
    }

}
