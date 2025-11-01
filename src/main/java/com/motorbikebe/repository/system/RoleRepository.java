package com.motorbikebe.repository.system;

import com.motorbikebe.entity.system.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByRlCd(String rlCd);

}
