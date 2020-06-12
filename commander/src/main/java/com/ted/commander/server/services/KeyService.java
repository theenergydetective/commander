/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.common.model.Account;
import com.ted.commander.server.dao.AccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class KeyService {

    public static final char[] SECURITY_KEY_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9'};

    static final Logger LOGGER = LoggerFactory.getLogger(KeyService.class);

    @Autowired
    AccountDAO accountDAO;

    /**
     * Generates a random series of letters and numbers for the given length.
     *
     * @param length
     * @return
     */
    public static String generateSecurityKey(int length) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            key.append(SECURITY_KEY_CHARS[Math.abs(bytes[i]) % SECURITY_KEY_CHARS.length]);
        }
        return key.toString();
    }

    public String generateKey() {
        String key = null;
        while (key == null) {
            String testKey = generateSecurityKey(10);
            Account account = accountDAO.findByActivationKey(testKey);
            if (account == null) key = testKey;
        }
        return key;
    }

    public String generateEmailKey() {
        return generateSecurityKey(20);
    }
}
