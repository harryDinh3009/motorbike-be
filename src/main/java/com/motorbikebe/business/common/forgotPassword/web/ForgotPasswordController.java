package com.motorbikebe.business.common.forgotPassword.web;

import com.motorbikebe.business.common.forgotPassword.service.ForgotPasswordService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.dto.common.forgotPassword.ForgotPasswordRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cmm/forgot-pass")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping
    public ApiResponse<Boolean> forgotPassword(@RequestBody ForgotPasswordRequestDTO req) {
        return new ApiResponse<>(ApiStatus.SUCCESS, forgotPasswordService.forgotPassword(req));
    }

}
