package com.motorbikebe.business.admin.userMng.web;

import com.motorbikebe.business.admin.userMng.service.UserMngService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.userMng.UserMngListDTO;
import com.motorbikebe.dto.business.admin.userMng.UserMngSaveDTO;
import com.motorbikebe.dto.business.admin.userMng.UserMngSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/user-mng")
@RequiredArgsConstructor
public class UserMngController {

    private final UserMngService userMngService;

    /**
     * Get Page User
     *
     * @param userMngSearchDTO .
     * @return PageableObject<UserMngListDTO>
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<UserMngListDTO>> getPageUser(@RequestBody UserMngSearchDTO userMngSearchDTO) {
        PageableObject<UserMngListDTO> pageableRes = userMngService.getPageUser(userMngSearchDTO);

        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Save User
     *
     * @param userMngSaveDTO .
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveUser(@RequestBody UserMngSaveDTO userMngSaveDTO) {
        Boolean response = userMngService.saveUser(userMngSaveDTO);

        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Detail User
     *
     * @param id .
     * @return UserMngListDTO
     */
    @GetMapping("/detail")
    public ApiResponse<UserMngListDTO> detailUser(@RequestParam("id") String id) {
        UserMngListDTO response = userMngService.detailUser(id);

        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Delete User
     *
     * @param id .
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteUser(@RequestParam("id") String id) {
        Boolean response = userMngService.deleteUser(id);

        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Get Users by Branch
     *
     * @param branchId .
     * @return List<UserMngListDTO>
     */
    @GetMapping("/by-branch")
    public ApiResponse<List<UserMngListDTO>> getUsersByBranch(@RequestParam("branchId") String branchId) {
        List<UserMngListDTO> response = userMngService.getUsersByBranch(branchId);

        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Get All Active Users
     *
     * @return List<UserMngListDTO>
     */
    @GetMapping("/all-active")
    public ApiResponse<List<UserMngListDTO>> getAllActiveUsers() {
        List<UserMngListDTO> response = userMngService.getAllActiveUsers();

        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

}
