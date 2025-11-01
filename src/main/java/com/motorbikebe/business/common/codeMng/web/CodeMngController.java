package com.motorbikebe.business.common.codeMng.web;

import com.motorbikebe.business.common.codeMng.service.CodeMngService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.dto.common.codeMng.CodeMngListDTO;
import com.motorbikebe.dto.common.codeMng.CodeMngListReqDTO;
import com.motorbikebe.dto.common.codeMng.CodeMngReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
public class CodeMngController {

    private final CodeMngService codeMngService;

    /**
     * Get List Code Mng By UpCdId
     *
     * @param codeMngReqDTO .
     * @return List<CodeMngListDTO>
     */
    @PostMapping("/list")
    public ApiResponse<List<CodeMngListDTO>> getListCodeMngByUpCdId(@RequestBody CodeMngReqDTO codeMngReqDTO) {
        List<CodeMngListDTO> res = codeMngService.getListCodeMngByUpCdId(codeMngReqDTO);

        return new ApiResponse<>(ApiStatus.SUCCESS, res);
    }

    /**
     * Get List Code Mng By List UpCdId
     *
     * @param codeMngListReqDTO .
     * @return List<CodeMngListDTO>
     */
    @PostMapping("/list-code")
    public ApiResponse<List<CodeMngListDTO>> getListCodeMngByListUpCd(@RequestBody CodeMngListReqDTO codeMngListReqDTO) {
        List<CodeMngListDTO> res = codeMngService.getListCodeMngByListUpCd(codeMngListReqDTO);

        return new ApiResponse<>(ApiStatus.SUCCESS, res);
    }

}