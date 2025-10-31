package com.translateai.business.admin.customerMng.service;

import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.customerMng.CustomerDTO;
import com.translateai.dto.business.admin.customerMng.CustomerSaveDTO;
import com.translateai.dto.business.admin.customerMng.CustomerSearchDTO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerMngService {

    /**
     * Tìm kiếm khách hàng với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<CustomerDTO>
     */
    PageableObject<CustomerDTO> searchCustomers(CustomerSearchDTO searchDTO);

    /**
     * Lấy chi tiết khách hàng
     *
     * @param id ID khách hàng
     * @return CustomerDTO
     */
    CustomerDTO getCustomerDetail(String id);

    /**
     * Tạo mới hoặc cập nhật khách hàng
     *
     * @param saveDTO DTO lưu khách hàng
     * @return Boolean
     */
    Boolean saveCustomer(@Valid CustomerSaveDTO saveDTO);

    /**
     * Xóa khách hàng
     *
     * @param id ID khách hàng
     * @return Boolean
     */
    Boolean deleteCustomer(String id);

    /**
     * Lấy tất cả khách hàng
     *
     * @return List<CustomerDTO>
     */
    List<CustomerDTO> getAllCustomers();

    /**
     * Upload ảnh CCCD cho khách hàng
     *
     * @param customerId ID khách hàng
     * @param file File ảnh CCCD
     * @return URL ảnh đã upload
     */
    String uploadCitizenIdImage(String customerId, MultipartFile file);

    /**
     * Upload ảnh bằng lái xe cho khách hàng
     *
     * @param customerId ID khách hàng
     * @param file File ảnh bằng lái xe
     * @return URL ảnh đã upload
     */
    String uploadDriverLicenseImage(String customerId, MultipartFile file);

    /**
     * Upload ảnh hộ chiếu cho khách hàng
     *
     * @param customerId ID khách hàng
     * @param file File ảnh hộ chiếu
     * @return URL ảnh đã upload
     */
    String uploadPassportImage(String customerId, MultipartFile file);
}

