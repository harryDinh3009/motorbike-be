package com.translateai.business.admin.contractMng.service;

import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.contractMng.CustomerDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSaveDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSearchDTO;
import jakarta.validation.Valid;

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
}

