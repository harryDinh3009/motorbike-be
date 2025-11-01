package com.motorbikebe.repository.business.admin;

import com.motorbikebe.business.admin.userMng.response.UserMngListResponse;
import com.motorbikebe.dto.business.admin.userMng.UserMngSearchDTO;
import com.motorbikebe.dto.common.user.UserProfileProjection;
import com.motorbikebe.entity.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query (value = """
            SELECT ROW_NUMBER() OVER (ORDER BY u.created_date DESC) AS rowNum,
            u.id,
            COALESCE(u.user_name, '-') AS userName,
            COALESCE(u.full_name, '-') AS fullName,
            COALESCE(u.email, '-') AS email,
            COALESCE(u.gender, '-') AS genderNm,
            COALESCE(sr.rl_nm, '-') AS roleNm,
            COALESCE(u.phone_number, '-') AS phoneNumber,
            u.branch_id AS branchId,
            COALESCE(b.name, '-') AS branchName,
            COALESCE(u.status, '-') AS statusNm,
            u.avatar
            FROM user u 
            LEFT JOIN sys_user_role sur ON u.id = sur.user_id
            LEFT JOIN sys_role sr ON sr.rl_id = sur.rl_id
            LEFT JOIN branch b ON u.branch_id = b.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR u.full_name LIKE %:#{#req.keyword}% 
                   OR u.user_name LIKE %:#{#req.keyword}%
                   OR u.email LIKE %:#{#req.keyword}%
                   OR u.phone_number LIKE %:#{#req.keyword}%)
            AND (:#{#req.role} IS NULL OR :#{#req.role} = '' OR sr.rl_cd = :#{#req.role})
            AND (:#{#req.status} IS NULL OR :#{#req.status} = '' OR u.status = :#{#req.status})
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR u.branch_id = :#{#req.branchId})
            ORDER BY u.created_date DESC
            """, countQuery = """
            SELECT COUNT(u.id)
            FROM user u 
            LEFT JOIN sys_user_role sur ON u.id = sur.user_id
            LEFT JOIN sys_role sr ON sr.rl_id = sur.rl_id
            LEFT JOIN branch b ON u.branch_id = b.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR u.full_name LIKE %:#{#req.keyword}% 
                   OR u.user_name LIKE %:#{#req.keyword}%
                   OR u.email LIKE %:#{#req.keyword}%
                   OR u.phone_number LIKE %:#{#req.keyword}%)
            AND (:#{#req.role} IS NULL OR :#{#req.role} = '' OR sr.rl_cd = :#{#req.role})
            AND (:#{#req.status} IS NULL OR :#{#req.status} = '' OR u.status = :#{#req.status})
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR u.branch_id = :#{#req.branchId})
            """, nativeQuery = true)
    Page<UserMngListResponse> getPageUserMng(Pageable pageable, @Param ("req") UserMngSearchDTO req);

    UserEntity findByUserName(String userName);

    @Query ("SELECT u FROM UserEntity u WHERE u.userName = :userName AND u.id <> :id")
    UserEntity findByUserNameNotInId(@Param ("userName") String userName, @Param ("id") String id);

    UserEntity findByEmail(String email);

    UserEntity findByEmailAndStatus(String email, String status);

    @Query(value = """
            SELECT 
                u.email AS email,
                u.full_name AS fullName,
                u.user_name AS userName,
                u.gender AS genderCd,
                u.gender AS genderName,
                u.date_of_birth AS dateOfBirth,
                u.phone_number AS phoneNumber,
                u.avatar AS avatar,
                u.description AS description,
                u.status AS statusCd,
                u.status AS statusName
            FROM user u
            WHERE u.email = :email
            """, nativeQuery = true)
    UserProfileProjection getUserProfileByEmail(@Param("email") String email);

    UserEntity findByFacebookId(String facebookId);

    UserEntity findByFacebookIdAndStatus(String facebookId, String status);
    
    List<UserEntity> findByBranchId(String branchId);
    
    List<UserEntity> findByStatus(String status);

}
