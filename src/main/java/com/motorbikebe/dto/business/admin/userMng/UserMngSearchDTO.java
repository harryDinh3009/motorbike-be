package com.motorbikebe.dto.business.admin.userMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMngSearchDTO extends PageableDTO {

    /** Tìm kiếm theo tên, email, hoặc số điện thoại */
    private String keyword;

    /** Lọc theo vai trò (role code) */
    private String role;

    /** Lọc theo trạng thái */
    private String status;
    
    /** Lọc theo chi nhánh */
    private String branchId;

}
