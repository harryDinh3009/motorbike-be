package com.motorbikebe.business.admin.userMng.impl;

import com.motorbikebe.business.admin.userMng.response.UserMngListResponse;
import com.motorbikebe.business.admin.userMng.service.UserMngService;
import com.motorbikebe.business.common.service.service.CommonService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.Constants;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.config.mail.EmailSender;
import com.motorbikebe.constant.classconstant.ScreenConstants;
import com.motorbikebe.dto.business.admin.userMng.UserMngListDTO;
import com.motorbikebe.dto.business.admin.userMng.UserMngSaveDTO;
import com.motorbikebe.dto.business.admin.userMng.UserMngSearchDTO;
import com.motorbikebe.entity.domain.UserEntity;
import com.motorbikebe.entity.system.RoleEntity;
import com.motorbikebe.entity.system.UserRoleEntity;
import com.motorbikebe.repository.business.admin.BranchRepository;
import com.motorbikebe.repository.business.admin.ContractRepository;
import com.motorbikebe.repository.business.admin.UserRepository;
import com.motorbikebe.repository.system.RoleRepository;
import com.motorbikebe.repository.system.UserRoleRepository;
import com.motorbikebe.entity.domain.BranchEntity;
import com.motorbikebe.util.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class UserMngServiceImpl implements UserMngService {

    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final BranchRepository branchRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailSender emailSender;

    private final CommonService commonService;

    /**
     * Get Page User
     *
     * @param userMngSearchDTO .
     * @return PageableObject<UserMngListDTO>
     */
    @Override
    public PageableObject<UserMngListDTO> getPageUser(UserMngSearchDTO userMngSearchDTO) {
        Pageable pageable = PageRequest.of(userMngSearchDTO.getPage() - 1, userMngSearchDTO.getSize());
        Page<UserMngListResponse> pageResponse = userRepository.getPageUserMng(pageable, userMngSearchDTO);

        Page<UserMngListDTO> dtoPage = pageResponse.map(
                response -> modelMapper.map(response, UserMngListDTO.class)
        );

        return new PageableObject<>(dtoPage);
    }

    /**
     * Save User
     *
     * @param userMngSaveDTO .
     * @return Boolean
     */
    @Override
    @Transactional
    public Boolean saveUser(@Valid UserMngSaveDTO userMngSaveDTO) {
        UserEntity userEntity = new UserEntity();
        boolean isNew = StringUtils.isBlank(userMngSaveDTO.getId());

        if (isNew) {

            UserEntity userFindUsername = userRepository.findByUserName(userMngSaveDTO.getUsername());
            if (Objects.nonNull(userFindUsername)) {
                throw new RestApiException(ApiStatus.BAD_REQUEST_USERNAME_EXISTS);
            }

            UserEntity userFindEmail = userRepository.findByEmail(userMngSaveDTO.getEmail());
            if (Objects.nonNull(userFindEmail)) {
                throw new RestApiException(ApiStatus.BAD_REQUEST_EMAIL_EXISTS);
            }

        } else {

            Optional<UserEntity> userEntityFind = userRepository.findById(userMngSaveDTO.getId());
            if (!userEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }

            UserEntity userFindUsernameUpdate = userRepository.findByUserNameNotInId(userMngSaveDTO.getUsername(), userMngSaveDTO.getId());
            if (Objects.nonNull(userFindUsernameUpdate)) {
                throw new RestApiException(ApiStatus.BAD_REQUEST_USERNAME_EXISTS);
            }

            userEntity = userEntityFind.get();

        }

        userEntity.setUserName(userMngSaveDTO.getUsername());
        userEntity.setFullName(userMngSaveDTO.getFullName());
        userEntity.setEmail(userMngSaveDTO.getEmail());
        userEntity.setGender(userMngSaveDTO.getGenderCd());
        userEntity.setDateOfBirth(Utils.convertStringToDate(userMngSaveDTO.getDateOfBirth(), "yyyy-MM-dd"));
        userEntity.setPhoneNumber(userMngSaveDTO.getPhoneNumber());
        userEntity.setAddress(userMngSaveDTO.getAddress());
        userEntity.setBranchId(userMngSaveDTO.getBranchId());
        userEntity.setStatus(userMngSaveDTO.getStatusCd());
        userEntity.setAvatar(ScreenConstants.URL_AVATAR_DEFAULT);

        if (isNew) {
//            String password = Utils.generateRandomPassword();
            String password = userMngSaveDTO.getPassword();
            userEntity.setPassword(passwordEncoder.encode(password));
            String htmlBody = "<p>Mật khẩu của bạn là:</p><br/><div style=\"text-align: center; font-weight: bold; font-size: 25px;\"><strong>" + password + "</strong></div>";
            emailSender.sendEmail(new String[]{userMngSaveDTO.getEmail()}, "[MENTOR_MATCH] Thông báo mật khẩu", "Thông báo mật khẩu sau khi đăng ký tài khoản", htmlBody);
        }

        UserEntity savedUser = userRepository.save(userEntity);

        // Handle role assignment for both new and existing users
        if (StringUtils.isNotBlank(userMngSaveDTO.getRoleCd())) {
            RoleEntity roleEntity = roleRepository.findByRlCd(userMngSaveDTO.getRoleCd());
            
            // Auto-create role if not exists (for ADMIN role)
            if (Objects.isNull(roleEntity) && "ADMIN".equals(userMngSaveDTO.getRoleCd())) {
                roleEntity = new RoleEntity();
                roleEntity.setRlCd("ADMIN");
                roleEntity.setRlNm("Administrator");
                roleEntity.setRlDesc("System Administrator with full access");
                roleEntity.setCategory("SITE_01");
                roleEntity = roleRepository.save(roleEntity);
            }
            
            if (Objects.isNull(roleEntity)) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            
            // For existing users, delete old roles first
            if (!isNew) {
                userRoleRepository.deleteAllByUserId(savedUser.getId());
            }
            
            // Create new user role
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserId(savedUser.getId());
            userRoleEntity.setRlId(roleEntity.getRlId());
            userRoleRepository.save(userRoleEntity);
        } else if (isNew) {
            // Role is required for new users
            throw new RestApiException(ApiStatus.BAD_REQUEST);
        }

        return true;
    }

    /**
     * Detail User
     *
     * @param id .
     * @return UserMngListDTO
     */
    @Override
    public UserMngListDTO detailUser(String id) {
        UserMngListDTO userMngListDTO = new UserMngListDTO();

        Optional<UserEntity> userEntityFind = userRepository.findById(id);
        if (!userEntityFind.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        UserEntity user = userEntityFind.get();
        userMngListDTO.setId(user.getId());
        userMngListDTO.setUserName(user.getUserName());
        userMngListDTO.setEmail(user.getEmail());
        userMngListDTO.setFullName(user.getFullName());
        userMngListDTO.setPhoneNumber(user.getPhoneNumber());
        userMngListDTO.setAddress(user.getAddress());
        userMngListDTO.setBranchId(user.getBranchId());
        userMngListDTO.setAvatar(user.getAvatar());

        // Map gender code to name
        String genderCd = user.getGender();
        if (genderCd != null) {
            switch (genderCd) {
                case "MALE":
                    userMngListDTO.setGenderNm("Nam");
                    break;
                case "FEMALE":
                    userMngListDTO.setGenderNm("Nữ");
                    break;
                case "OTHER":
                    userMngListDTO.setGenderNm("Khác");
                    break;
                default:
                    userMngListDTO.setGenderNm(genderCd);
            }
        }

        // Map status code to name
        String statusCd = user.getStatus();
        if (statusCd != null) {
            switch (statusCd) {
                case "ACTIVE":
                    userMngListDTO.setStatusNm("Đang làm");
                    break;
                case "INACTIVE":
                    userMngListDTO.setStatusNm("Nghỉ");
                    break;
                default:
                    userMngListDTO.setStatusNm(statusCd);
            }
        }

        // Get role name and code
        UserRoleEntity userRoleEntity = userRoleRepository.findByUserId(user.getId());
        if (userRoleEntity != null) {
            Optional<RoleEntity> roleEntityFind = roleRepository.findById(userRoleEntity.getRlId());
            if (roleEntityFind.isPresent()) {
                RoleEntity roleEntity = roleEntityFind.get();
                userMngListDTO.setRoleNm(roleEntity.getRlNm());
                userMngListDTO.setRoleCd(roleEntity.getRlCd());
            }
        }

        // Get branch name
        if (user.getBranchId() != null) {
            Optional<BranchEntity> branchEntityFind = branchRepository.findById(user.getBranchId());
            if (branchEntityFind.isPresent()) {
                userMngListDTO.setBranchName(branchEntityFind.get().getName());
            }
        }

        return userMngListDTO;
    }

    /**
     * Delete User
     *
     * @param id .
     * @return Boolean
     */
    @Override
    @Transactional
    public Boolean deleteUser(String id) {
        Optional<UserEntity> userEntityFind = userRepository.findById(id);
        if (!userEntityFind.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        // Kiểm tra xem nhân viên có được ghi nhận là người giao/nhận xe trong hợp đồng không
        // Cần inject ContractRepository
        Long count = contractRepository.existsByDeliveryUserIdOrReturnUserId(id);
        if (count != null && count > 0) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_USER_HAS_CONTRACTS);
        }
        
        // Delete all user roles first using native query (safe and efficient)
        // This will delete all roles for the user, even if multiple roles exist
        userRoleRepository.deleteAllByUserId(id);
        
        // Delete user
        userRepository.deleteById(id);
        
        return true;
    }

    /**
     * Get Users by Branch
     *
     * @param branchId .
     * @return List<UserMngListDTO>
     */
    @Override
    public List<UserMngListDTO> getUsersByBranch(String branchId) {
        List<UserEntity> users = userRepository.findByBranchId(branchId);
        
        List<UserMngListDTO> result = new ArrayList<>();
        for (UserEntity user : users) {
            UserMngListDTO dto = new UserMngListDTO();
            dto.setId(user.getId());
            dto.setUserName(user.getUserName());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setAddress(user.getAddress());
            dto.setBranchId(user.getBranchId());
            dto.setAvatar(user.getAvatar());
            result.add(dto);
        }
        
        return result;
    }

    /**
     * Get All Active Users
     *
     * @return List<UserMngListDTO>
     */
    @Override
    public List<UserMngListDTO> getAllActiveUsers() {
        List<UserEntity> users = userRepository.findByStatus("ACTIVE");
        
        List<UserMngListDTO> result = new ArrayList<>();
        for (UserEntity user : users) {
            UserMngListDTO dto = new UserMngListDTO();
            dto.setId(user.getId());
            dto.setUserName(user.getUserName());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setAddress(user.getAddress());
            dto.setBranchId(user.getBranchId());
            dto.setAvatar(user.getAvatar());
            result.add(dto);
        }
        
        return result;
    }

}
