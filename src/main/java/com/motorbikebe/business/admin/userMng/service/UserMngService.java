package com.motorbikebe.business.admin.userMng.service;

import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.userMng.UserMngListDTO;
import com.motorbikebe.dto.business.admin.userMng.UserMngSaveDTO;
import com.motorbikebe.dto.business.admin.userMng.UserMngSearchDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface UserMngService {

    /**
     * Get Page User
     *
     * @param userMngSearchDTO .
     * @return PageableObject<UserMngListDTO>
     */
    PageableObject<UserMngListDTO> getPageUser(UserMngSearchDTO userMngSearchDTO);

    /**
     * Save User
     *
     * @param userMngSaveDTO .
     * @return Boolean
     */
    Boolean saveUser(@Valid UserMngSaveDTO userMngSaveDTO);

    /**
     * Detail User
     *
     * @param id .
     * @return UserMngListDTO
     */
    UserMngListDTO detailUser(String id);
    
    /**
     * Delete User
     *
     * @param id .
     * @return Boolean
     */
    Boolean deleteUser(String id);
    
    /**
     * Get Users by Branch
     *
     * @param branchId .
     * @return List<UserMngListDTO>
     */
    List<UserMngListDTO> getUsersByBranch(String branchId);
    
    /**
     * Get All Active Users
     *
     * @return List<UserMngListDTO>
     */
    List<UserMngListDTO> getAllActiveUsers();

}
