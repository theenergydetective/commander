package com.ted.commander.server.restServices;


import com.ted.commander.common.model.Advice;
import com.ted.commander.server.dao.AccessControlDAO;
import com.ted.commander.server.dao.AdviceDAO;
import com.ted.commander.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Access Control Service
 */
@Service("aclService")
public class ACLService {

    final static Logger LOGGER = LoggerFactory.getLogger(ACLService.class);

    @Autowired
    AccessControlDAO accessControlDAO;

    @Autowired
    UserService userService;

    @Autowired
    AdviceDAO adviceDAO;

    public boolean canViewLocation(String userName, Long locationId) {
        if (userName == null) LOGGER.error("userName is null");
        if (locationId == null) LOGGER.error("locationId is null");
        if (locationId.equals(0L)) return true;
        return accessControlDAO.canViewLocation(locationId, userName);
    }

    public boolean canEditLocation(String userName, Long locationId) {
        if (userName == null) LOGGER.error("userName is null");
        if (locationId == null) LOGGER.error("locationId is null");
        if (locationId.equals(0L)) return true;
        return accessControlDAO.canViewLocation(locationId, userName);
    }

    public boolean canEditAdvice(String userName, Long accountId, Long adviceId) {
        if (userName == null) LOGGER.error("userName is null");
        if (accountId == null) LOGGER.error("accountId is null");
        if (accountId.equals(0L)) return true;
        if (accessControlDAO.canEditAccount(accountId, userName)){
            Advice advice = adviceDAO.findOne(adviceId);
            return (advice.getAccountId().equals(accountId));
        }
        return false;
    }


    public boolean canViewAccount(String username, Long accountId) {
        if (username == null) LOGGER.error("userName is null");
        if (accountId == null) LOGGER.error("accountId is null");
        return accessControlDAO.canViewAccount(accountId, username);
    }

    public boolean canEditAccount(String username, Long accountId) {
        if (username == null) LOGGER.error("userName is null");
        if (accountId == null) LOGGER.error("accountId is null");
        return accessControlDAO.canEditAccount(accountId, username);
    }
}
