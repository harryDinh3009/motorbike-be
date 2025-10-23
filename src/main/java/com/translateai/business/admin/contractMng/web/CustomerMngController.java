package com.translateai.business.admin.contractMng.web;

import com.translateai.business.admin.contractMng.service.CustomerMngService;
import com.translateai.common.ApiResponse;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.contractMng.CustomerDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSaveDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/customer-mng")
@RequiredArgsConstructor
public class CustomerMngController {

    private final CustomerMngService customerMngService;

    /**
     * Tìm kiếm khách hàng với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<CustomerDTO>
     */
    @GetMapping("/list")
    public ApiResponse<PageableObject<CustomerDTO>> searchCustomers(CustomerSearchDTO searchDTO) {
        PageableObject<CustomerDTO> pageableRes = customerMngService.searchCustomers(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết khách hàng
     *
     * @param id ID khách hàng
     * @return CustomerDTO
     */
    @GetMapping("/detail")
    public ApiResponse<CustomerDTO> getCustomerDetail(@RequestParam("id") String id) {
        CustomerDTO response = customerMngService.getCustomerDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật khách hàng
     *
     * @param saveDTO DTO lưu khách hàng
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveCustomer(@RequestBody CustomerSaveDTO saveDTO) {
        Boolean response = customerMngService.saveCustomer(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa khách hàng
     *
     * @param id ID khách hàng
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteCustomer(@RequestParam("id") String id) {
        Boolean response = customerMngService.deleteCustomer(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy tất cả khách hàng
     *
     * @return List<CustomerDTO>
     */
    @GetMapping("/all")
    public ApiResponse<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> response = customerMngService.getAllCustomers();
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}

