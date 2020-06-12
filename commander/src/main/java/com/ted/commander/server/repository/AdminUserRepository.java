/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.repository;


import com.ted.commander.server.model.admin.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;


@Transactional
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {


    Page<AdminUser> findAll(Specification<AdminUser> spec, Pageable pageable);

}
