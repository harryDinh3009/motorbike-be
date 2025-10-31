package com.translateai.business.admin.customerMng.web;

import com.translateai.business.admin.customerMng.service.CustomerMngService;
import com.translateai.common.ApiResponse;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.customerMng.CustomerDTO;
import com.translateai.dto.business.admin.customerMng.CustomerSaveDTO;
import com.translateai.dto.business.admin.customerMng.CustomerSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/list")
    public ApiResponse<PageableObject<CustomerDTO>> searchCustomers(@RequestBody CustomerSearchDTO searchDTO) {
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

    /**
     * Upload ảnh CCCD/CMND cho khách hàng
     *
     * @param customerId ID khách hàng
     * @param file File ảnh CCCD
     * @return URL ảnh đã upload
     */
    @PostMapping(value = "/upload-citizen-id", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> uploadCitizenIdImage(
            @RequestParam("customerId") String customerId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = customerMngService.uploadCitizenIdImage(customerId, file);
        return new ApiResponse<>(ApiStatus.SUCCESS, imageUrl);
    }

    /**
     * Upload ảnh bằng lái xe cho khách hàng
     *
     * @param customerId ID khách hàng
     * @param file File ảnh bằng lái xe
     * @return URL ảnh đã upload
     */
    @PostMapping(value = "/upload-driver-license", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> uploadDriverLicenseImage(
            @RequestParam("customerId") String customerId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = customerMngService.uploadDriverLicenseImage(customerId, file);
        return new ApiResponse<>(ApiStatus.SUCCESS, imageUrl);
    }

    /**
     * Upload ảnh hộ chiếu cho khách hàng
     *
     * @param customerId ID khách hàng
     * @param file File ảnh hộ chiếu
     * @return URL ảnh đã upload
     */
    @PostMapping(value = "/upload-passport", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> uploadPassportImage(
            @RequestParam("customerId") String customerId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = customerMngService.uploadPassportImage(customerId, file);
        return new ApiResponse<>(ApiStatus.SUCCESS, imageUrl);
    }
}

