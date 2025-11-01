package com.motorbikebe.repository.business.client;

import com.motorbikebe.entity.domain.UserAuthCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthCodeRepository extends JpaRepository<UserAuthCodeEntity, String> {

    UserAuthCodeEntity findByEmail(String email);

}
