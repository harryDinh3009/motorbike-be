package com.motorbikebe.business.common.forgotPassword.service;

import com.motorbikebe.dto.common.forgotPassword.ForgotPasswordRequestDTO;
import jakarta.validation.Valid;

public interface ForgotPasswordService {

    /**
     * Forgot Password
     *
     * @param forgotPasswordRequestDTO .
     * @return boolean
     */
    boolean forgotPassword(@Valid ForgotPasswordRequestDTO forgotPasswordRequestDTO);

}
