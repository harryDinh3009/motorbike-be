package com.motorbikebe.business.admin.brandMng.impl;

import com.motorbikebe.business.admin.brandMng.service.BrandMngService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.brandMng.BrandDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSaveDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSearchDTO;
import com.motorbikebe.entity.domain.BrandEntity;
import com.motorbikebe.repository.business.admin.BrandRepository;
import com.motorbikebe.repository.business.admin.CarRepository;
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
public class BrandMngServiceImpl implements BrandMngService {

    private final BrandRepository brandRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageableObject<BrandDTO> searchBrands(BrandSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<BrandDTO> brandPage = brandRepository.searchBrands(pageable, searchDTO);
        return new PageableObject<>(brandPage);
    }

    @Override
    public BrandDTO getBrandDetail(String id) {
        Optional<BrandEntity> brandEntity = brandRepository.findById(id);
        if (!brandEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        return modelMapper.map(brandEntity.get(), BrandDTO.class);
    }

    @Override
    @Transactional
    public Boolean saveBrand(@Valid BrandSaveDTO saveDTO) {
        BrandEntity brandEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            brandEntity = new BrandEntity();
            // Kiểm tra tên hãng xe đã tồn tại chưa
            BrandEntity existingBrand = brandRepository.findByName(saveDTO.getName());
            if (existingBrand != null) {
                throw new RestApiException(ApiStatus.BRAND_NAME_ALREADY_EXISTS);
            }
        } else {
            Optional<BrandEntity> brandEntityFind = brandRepository.findById(saveDTO.getId());
            if (!brandEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            brandEntity = brandEntityFind.get();
            // Kiểm tra tên hãng xe đã tồn tại chưa (trừ chính nó)
            BrandEntity existingBrand = brandRepository.findByName(saveDTO.getName());
            if (existingBrand != null && !existingBrand.getId().equals(saveDTO.getId())) {
                throw new RestApiException(ApiStatus.BRAND_NAME_ALREADY_EXISTS);
            }
        }

        brandEntity.setName(saveDTO.getName());
        brandEntity.setDescription(saveDTO.getDescription());

        brandRepository.save(brandEntity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteBrand(String id) {
        Optional<BrandEntity> brandEntity = brandRepository.findById(id);
        if (!brandEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        // Kiểm tra xem hãng xe có xe nào đang sử dụng không
        Long count = carRepository.existsByBrandId(id);
        if (count != null && count > 0) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_BRAND_HAS_CARS);
        }

        brandRepository.deleteById(id);
        return true;
    }

    @Override
    public List<BrandDTO> getAllBrands() {
        List<BrandEntity> brands = brandRepository.findAllByOrderByNameAsc();
        return brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
    }
}

