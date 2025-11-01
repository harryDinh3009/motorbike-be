package com.translateai.business.admin.surchargeTypeMng.impl;

import com.translateai.business.admin.surchargeTypeMng.service.SurchargeTypeMngService;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.config.exception.RestApiException;
import com.translateai.dto.business.admin.surchargeTypeMng.SurchargeTypeDTO;
import com.translateai.dto.business.admin.surchargeTypeMng.SurchargeTypeSaveDTO;
import com.translateai.dto.business.admin.surchargeTypeMng.SurchargeTypeSearchDTO;
import com.translateai.entity.domain.SurchargeTypeEntity;
import com.translateai.repository.business.admin.SurchargeTypeRepository;
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
public class SurchargeTypeMngServiceImpl implements SurchargeTypeMngService {

    private final SurchargeTypeRepository surchargeTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageableObject<SurchargeTypeDTO> searchSurchargeTypes(SurchargeTypeSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<SurchargeTypeDTO> page = surchargeTypeRepository.searchSurchargeTypes(pageable, searchDTO);

        return PageableObject.<SurchargeTypeDTO>builder()
                .data(page.getContent())
                .totalRecords(page.getTotalElements())
                .currentPage(searchDTO.getPage())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public SurchargeTypeDTO getSurchargeTypeDetail(String id) {
        Optional<SurchargeTypeEntity> surchargeTypeEntity = surchargeTypeRepository.findById(id);
        if (!surchargeTypeEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        return modelMapper.map(surchargeTypeEntity.get(), SurchargeTypeDTO.class);
    }

    @Override
    @Transactional
    public Boolean saveSurchargeType(@Valid SurchargeTypeSaveDTO saveDTO) {
        SurchargeTypeEntity surchargeTypeEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            surchargeTypeEntity = new SurchargeTypeEntity();
        } else {
            Optional<SurchargeTypeEntity> surchargeTypeEntityFind = surchargeTypeRepository.findById(saveDTO.getId());
            if (!surchargeTypeEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            surchargeTypeEntity = surchargeTypeEntityFind.get();
        }

        surchargeTypeEntity.setName(saveDTO.getName());
        surchargeTypeEntity.setPrice(saveDTO.getPrice());
        surchargeTypeEntity.setDescription(saveDTO.getDescription());
        surchargeTypeEntity.setStatus(saveDTO.getStatus() != null ? saveDTO.getStatus() : 1);

        surchargeTypeRepository.save(surchargeTypeEntity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteSurchargeType(String id) {
        Optional<SurchargeTypeEntity> surchargeTypeEntity = surchargeTypeRepository.findById(id);
        if (!surchargeTypeEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        surchargeTypeRepository.deleteById(id);
        return true;
    }

    @Override
    public List<SurchargeTypeDTO> getAllActiveSurchargeTypes() {
        List<SurchargeTypeEntity> surchargeTypeEntities = surchargeTypeRepository.findByStatus(1);
        return surchargeTypeEntities.stream()
                .map(entity -> modelMapper.map(entity, SurchargeTypeDTO.class))
                .collect(Collectors.toList());
    }
}

