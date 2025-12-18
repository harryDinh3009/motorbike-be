package com.motorbikebe.business.admin.carMng.impl;

import com.motorbikebe.business.admin.carMng.service.CarModelService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.carMng.CarModelDTO;
import com.motorbikebe.dto.business.admin.carMng.CarModelInfoDTO;
import com.motorbikebe.dto.business.admin.carMng.CarModelSaveDTO;
import com.motorbikebe.entity.domain.BrandEntity;
import com.motorbikebe.entity.domain.CarModelEntity;
import com.motorbikebe.repository.business.admin.BrandRepository;
import com.motorbikebe.repository.business.admin.CarModelRepository;
import com.motorbikebe.repository.business.admin.CarRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class CarModelServiceImpl implements CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CarModelDTO> getAllCarModels() {
        return carModelRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(entity -> {
                    CarModelDTO dto = modelMapper.map(entity, CarModelDTO.class);
                    if (entity.getBrandId() != null) {
                        brandRepository.findById(entity.getBrandId())
                                .ifPresent(brand -> dto.setBrandName(brand.getName()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getActiveModelNames() {
        return carModelRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(CarModelEntity::getName)
                .collect(Collectors.toList());
    }

    @Override
    public CarModelInfoDTO getCarModelInfo(String modelName) {
        CarModelEntity entity = carModelRepository.findByName(modelName);
        if (entity == null) {
            throw new RestApiException(ApiStatus.NOT_FOUND, "Model not found: " + modelName);
        }

        CarModelInfoDTO info = new CarModelInfoDTO();
        info.setBrandId(entity.getBrandId());
        info.setBaseDailyPrice(entity.getBaseDailyPrice());
        info.setBaseHourlyPrice(entity.getBaseHourlyPrice());

        // Get brand name if brandId exists
        if (entity.getBrandId() != null) {
            BrandEntity brand = brandRepository.findById(entity.getBrandId()).orElse(null);
            if (brand != null) {
                info.setBrandName(brand.getName());
            }
        }

        return info;
    }

    @Override
    @Transactional
    public CarModelDTO createCarModel(@Valid CarModelSaveDTO saveDTO) {
        validateNameUnique(saveDTO.getName(), null);

        CarModelEntity entity = CarModelEntity.builder()
                .name(saveDTO.getName().trim())
                .brand(StringUtils.trimToNull(saveDTO.getBrand())) // Legacy field
                .brandId(saveDTO.getBrandId())
                .description(StringUtils.trimToNull(saveDTO.getDescription()))
                .baseDailyPrice(saveDTO.getBaseDailyPrice())
                .baseHourlyPrice(saveDTO.getBaseHourlyPrice())
                .active(saveDTO.getActive() != null ? saveDTO.getActive() : Boolean.TRUE)
                .build();

        CarModelEntity saved = carModelRepository.save(entity);
        return modelMapper.map(saved, CarModelDTO.class);
    }

    @Override
    @Transactional
    public CarModelDTO updateCarModel(String id, @Valid CarModelSaveDTO saveDTO) {
        CarModelEntity entity = carModelRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ApiStatus.NOT_FOUND));

        if (StringUtils.isNotBlank(saveDTO.getName())) {
            validateNameUnique(saveDTO.getName(), id);
            entity.setName(saveDTO.getName().trim());
        }

        if (saveDTO.getBrand() != null) {
            entity.setBrand(StringUtils.trimToNull(saveDTO.getBrand())); // Legacy field
        }
        if (saveDTO.getBrandId() != null) {
            entity.setBrandId(saveDTO.getBrandId());
        }
        if (saveDTO.getDescription() != null) {
            entity.setDescription(StringUtils.trimToNull(saveDTO.getDescription()));
        }
        if (saveDTO.getBaseDailyPrice() != null) {
            entity.setBaseDailyPrice(saveDTO.getBaseDailyPrice());
        }
        if (saveDTO.getBaseHourlyPrice() != null) {
            entity.setBaseHourlyPrice(saveDTO.getBaseHourlyPrice());
        }
        if (saveDTO.getActive() != null) {
            entity.setActive(saveDTO.getActive());
        }

        CarModelEntity saved = carModelRepository.save(entity);
        return modelMapper.map(saved, CarModelDTO.class);
    }

    @Override
    @Transactional
    public Boolean deleteCarModel(String id) {
        CarModelEntity entity = carModelRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ApiStatus.NOT_FOUND));
        
        // Kiểm tra xem có xe nào đang dùng mẫu xe này không
        Long count = carRepository.existsByModel(entity.getName());
        if (count != null && count > 0) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_CAR_MODEL_HAS_CARS);
        }
        
        carModelRepository.delete(entity);
        return true;
    }

    private void validateNameUnique(String name, String excludeId) {
        if (StringUtils.isBlank(name)) {
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }
        String normalized = name.trim();
        boolean exists = excludeId == null
                ? carModelRepository.existsByNameIgnoreCase(normalized)
                : carModelRepository.existsByNameIgnoreCaseAndIdNot(normalized, excludeId);
        if (exists) {
            throw new RestApiException(ApiStatus.CONFLICT);
        }
    }
}

