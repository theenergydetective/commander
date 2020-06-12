/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.repository;


import com.ted.commander.server.model.CommanderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;


@Transactional
public interface CommanderUserRepository extends JpaRepository<CommanderUser, Long> {

    @Query(nativeQuery = true, value = "select * from user where username=?1 and password=?2")
    CommanderUser findByEmailAndPassword(String username, String password);

    @Query(nativeQuery = true, value = "select * from user where username=?1")
    CommanderUser findByEmail(String email);

    @Query(nativeQuery = true, value = "select * from user where alexaAccessToken=?1")
    CommanderUser findByAlexaAccessToken(String accessToken);
}
