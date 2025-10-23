package com.translateai.business.admin.contractMng.impl;

import com.translateai.business.admin.contractMng.service.CarMngService;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.config.exception.RestApiException;
import com.translateai.dto.business.admin.contractMng.CarDTO;
import com.translateai.dto.business.admin.contractMng.CarSaveDTO;
import com.translateai.dto.business.admin.contractMng.CarSearchDTO;
import com.translateai.entity.domain.CarEntity;
import com.translateai.repository.business.admin.CarRepository;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class CarMngServiceImpl implements CarMngService {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageableObject<CarDTO> searchCars(CarSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<CarDTO> carPage = carRepository.searchCars(pageable, searchDTO);
        
        // Map status description
        carPage.forEach(car -> {
            if (car.getStatus() != null) {
                car.setStatusNm(car.getStatus().getDescription());
            }
        });
        
        return new PageableObject<>(carPage);
    }

    @Override
    public CarDTO getCarDetail(String id) {
        Optional<CarEntity> carEntity = carRepository.findById(id);
        if (!carEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        CarDTO carDTO = modelMapper.map(carEntity.get(), CarDTO.class);
        if (carDTO.getStatus() != null) {
            carDTO.setStatusNm(carDTO.getStatus().getDescription());
        }
        return carDTO;
    }

    @Override
    @Transactional
    public Boolean saveCar(@Valid CarSaveDTO saveDTO) {
        CarEntity carEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            // Kiểm tra biển số xe đã tồn tại
            CarEntity existingCar = carRepository.findByLicensePlate(saveDTO.getLicensePlate());
            if (Objects.nonNull(existingCar)) {
                throw new RestApiException(ApiStatus.BAD_REQUEST);
            }
            carEntity = new CarEntity();
        } else {
            Optional<CarEntity> carEntityFind = carRepository.findById(saveDTO.getId());
            if (!carEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            carEntity = carEntityFind.get();
        }

        carEntity.setName(saveDTO.getName());
        carEntity.setLicensePlate(saveDTO.getLicensePlate());
        carEntity.setCarType(saveDTO.getCarType());
        carEntity.setDailyPrice(saveDTO.getDailyPrice());
        carEntity.setStatus(saveDTO.getStatus());
        carEntity.setImageUrl(saveDTO.getImageUrl());
        carEntity.setDescription(saveDTO.getDescription());

        carRepository.save(carEntity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteCar(String id) {
        Optional<CarEntity> carEntity = carRepository.findById(id);
        if (!carEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        carRepository.deleteById(id);
        return true;
    }

    @Override
    public List<CarDTO> getAllCars() {
        List<CarEntity> cars = carRepository.findAll();
        return cars.stream()
                .map(car -> {
                    CarDTO dto = modelMapper.map(car, CarDTO.class);
                    if (dto.getStatus() != null) {
                        dto.setStatusNm(dto.getStatus().getDescription());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

