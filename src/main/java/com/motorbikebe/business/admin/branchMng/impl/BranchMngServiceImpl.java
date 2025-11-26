package com.motorbikebe.business.admin.branchMng.impl;

import com.motorbikebe.business.admin.branchMng.service.BranchMngService;
import com.motorbikebe.business.common.service.service.CommonService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.branchMng.BranchDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSaveDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSearchDTO;
import com.motorbikebe.dto.common.userCurrent.UserCurrentInfoDTO;
import com.motorbikebe.entity.domain.BranchEntity;
import com.motorbikebe.entity.domain.UserEntity;
import com.motorbikebe.repository.business.admin.BranchRepository;
import com.motorbikebe.repository.business.admin.CarRepository;
import com.motorbikebe.repository.business.admin.ContractRepository;
import com.motorbikebe.repository.business.admin.UserRepository;
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
public class BranchMngServiceImpl implements BranchMngService {

    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ModelMapper modelMapper;
    private final CommonService commonService;

    @Override
    public PageableObject<BranchDTO> searchBranches(BranchSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<BranchDTO> branchPage = branchRepository.searchBranches(pageable, searchDTO);
        return new PageableObject<>(branchPage);
    }

    @Override
    public BranchDTO getBranchDetail(String id) {
        Optional<BranchEntity> branchEntity = branchRepository.findById(id);
        if (!branchEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        return modelMapper.map(branchEntity.get(), BranchDTO.class);
    }

    @Override
    @Transactional
    public Boolean saveBranch(@Valid BranchSaveDTO saveDTO) {
        BranchEntity branchEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            branchEntity = new BranchEntity();
        } else {
            Optional<BranchEntity> branchEntityFind = branchRepository.findById(saveDTO.getId());
            if (!branchEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            branchEntity = branchEntityFind.get();
        }

        branchEntity.setName(saveDTO.getName());
        branchEntity.setPhoneNumber(saveDTO.getPhoneNumber());
        branchEntity.setAddress(saveDTO.getAddress());
        branchEntity.setNote(saveDTO.getNote());
        branchEntity.setStatus(saveDTO.getStatus() != null ? saveDTO.getStatus() : 1);

        branchRepository.save(branchEntity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteBranch(String id) {
        Optional<BranchEntity> branchEntity = branchRepository.findById(id);
        if (!branchEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        // Kiểm tra xem chi nhánh có xe không
        List<com.motorbikebe.entity.domain.CarEntity> cars = carRepository.findByBranchId(id);
        if (cars != null && !cars.isEmpty()) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_BRANCH_HAS_RELATIONS);
        }

        // Kiểm tra xem chi nhánh có nhân viên không
        List<UserEntity> users = userRepository.findByBranchId(id);
        if (users != null && !users.isEmpty()) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_BRANCH_HAS_RELATIONS);
        }

        // Kiểm tra xem chi nhánh có được dùng trong hợp đồng không
        boolean hasContracts = contractRepository.existsByPickupBranchIdOrReturnBranchId(id);
        if (hasContracts) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_BRANCH_HAS_RELATIONS);
        }

        branchRepository.deleteById(id);
        return true;
    }

    @Override
    public List<BranchDTO> getAllActiveBranches() {
        List<BranchEntity> branches = branchRepository.findByStatus(1);
        return branches.stream()
                .map(branch -> modelMapper.map(branch, BranchDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BranchDTO getBranchByCurrentUser() {
        // Lấy thông tin user hiện tại
        UserCurrentInfoDTO userCurrentInfo = commonService.getUserCurrentInfo();
        if (userCurrentInfo == null) {
            throw new RestApiException(ApiStatus.UNAUTHORIZED);
        }

        // Lấy branchId từ user entity
        String branchId = userCurrentInfo.getBranchId();
        if (StringUtils.isBlank(branchId)) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        // Lấy thông tin chi nhánh
        Optional<BranchEntity> branchEntity = branchRepository.findById(branchId);
        if (!branchEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        return modelMapper.map(branchEntity.get(), BranchDTO.class);
    }
}

