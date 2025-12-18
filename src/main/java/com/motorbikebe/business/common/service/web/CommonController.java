package com.motorbikebe.business.common.service.web;

import com.motorbikebe.business.common.service.service.CommonService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.dto.common.userCurrent.UserCurrentInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cmn/user")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @GetMapping("/current")
    public ApiResponse<UserCurrentInfoDTO> getCurrentUser() {
        UserCurrentInfoDTO userCurrentInfo = commonService.getUserCurrentInfo();
        return new ApiResponse<>(ApiStatus.SUCCESS, userCurrentInfo);
    }
}

