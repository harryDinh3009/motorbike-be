package com.motorbikebe.business.common.codeMng.service;


import com.motorbikebe.dto.common.codeMng.CodeMngListDTO;
import com.motorbikebe.dto.common.codeMng.CodeMngListReqDTO;
import com.motorbikebe.dto.common.codeMng.CodeMngReqDTO;

import java.util.List;

public interface CodeMngService {

    List<CodeMngListDTO> getListCodeMngByUpCdId(CodeMngReqDTO codeMngResDTO);

    List<CodeMngListDTO> getListCodeMngByListUpCd(CodeMngListReqDTO codeMngResDTO);

}
