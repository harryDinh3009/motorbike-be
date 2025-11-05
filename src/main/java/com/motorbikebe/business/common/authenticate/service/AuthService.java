package com.motorbikebe.business.common.authenticate.service;

import com.motorbikebe.dto.common.authenticate.LoginFacebookDTO;
import com.motorbikebe.dto.common.authenticate.LoginGoogleDTO;
import com.motorbikebe.dto.common.authenticate.LoginRequestDTO;
import com.motorbikebe.dto.common.authenticate.LoginResponseDTO;
import com.motorbikebe.dto.common.authenticate.LogoutResponseDTO;
import jakarta.validation.Valid;

public interface AuthService {

    /**
     * Login Basic Admin
     *
     * @param loginRequestDTO .
     * @return LoginResponseDTO
     */
    LoginResponseDTO loginBasicAdmin(@Valid LoginRequestDTO loginRequestDTO);

    /**
     * Login Basic Client
     *
     * @param loginRequestDTO .
     * @return LoginResponseDTO
     */
    LoginResponseDTO loginBasicClient(@Valid LoginRequestDTO loginRequestDTO);

    /**
     * Login Google Client
     *
     * @param loginGoogleDTO .
     * @return LoginResponseDTO
     */
    LoginResponseDTO loginGoogleClient(@Valid LoginGoogleDTO loginGoogleDTO);

    /**
     * Login Facebook Client
     *
     * @param loginFacebookDTO .
     * @return LoginResponseDTO
     */
    LoginResponseDTO loginFacebookClient(@Valid LoginFacebookDTO loginFacebookDTO);

    /**
     * Logout User
     *
     * @return LogoutResponseDTO
     */
    LogoutResponseDTO logout();

}
