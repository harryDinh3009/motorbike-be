package com.translateai.business.admin.branchMng.impl;

import com.translateai.business.admin.branchMng.service.BranchMngService;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.config.exception.RestApiException;
import com.translateai.dto.business.admin.branchMng.BranchDTO;
import com.translateai.dto.business.admin.branchMng.BranchSaveDTO;
import com.translateai.dto.business.admin.branchMng.BranchSearchDTO;
import com.translateai.entity.domain.BranchEntity;
import com.translateai.repository.business.admin.BranchRepository;
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
    private final ModelMapper modelMapper;

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
}

