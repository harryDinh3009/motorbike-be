package com.translateai.business.admin.contractMng.impl;

import com.translateai.business.admin.contractMng.service.CustomerMngService;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.config.exception.RestApiException;
import com.translateai.dto.business.admin.contractMng.CustomerDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSaveDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSearchDTO;
import com.translateai.entity.domain.CustomerEntity;
import com.translateai.repository.business.admin.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class CustomerMngServiceImpl implements CustomerMngService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageableObject<CustomerDTO> searchCustomers(CustomerSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<CustomerDTO> customerPage = customerRepository.searchCustomers(pageable, searchDTO);
        return new PageableObject<>(customerPage);
    }

    @Override
    public CustomerDTO getCustomerDetail(String id) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        return modelMapper.map(customerEntity.get(), CustomerDTO.class);
    }

    @Override
    @Transactional
    public Boolean saveCustomer(@Valid CustomerSaveDTO saveDTO) {
        CustomerEntity customerEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            customerEntity = new CustomerEntity();
        } else {
            Optional<CustomerEntity> customerEntityFind = customerRepository.findById(saveDTO.getId());
            if (!customerEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            customerEntity = customerEntityFind.get();
        }

        customerEntity.setFullName(saveDTO.getFullName());
        customerEntity.setPhoneNumber(saveDTO.getPhoneNumber());
        customerEntity.setEmail(saveDTO.getEmail());
        customerEntity.setCitizenId(saveDTO.getCitizenId());
        customerEntity.setAddress(saveDTO.getAddress());
        customerEntity.setDriverLicense(saveDTO.getDriverLicense());

        customerRepository.save(customerEntity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteCustomer(String id) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        customerRepository.deleteById(id);
        return true;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }
}

