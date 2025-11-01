package com.motorbikebe.repository.system;

import com.motorbikebe.entity.system.ResourceRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRoleRepository extends JpaRepository<ResourceRoleEntity, Long> {
}
