package com.ted.commander.server.model.admin;


import com.ted.commander.common.enums.UserState;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


/**
 * Created by pete on 7/16/2015.
 */
@StaticMetamodel(AdminUser.class)
public class AdminUser_ {
    public static volatile SingularAttribute<AdminUser, Long> id;
    public static volatile SingularAttribute<AdminUser, String> username;
    public static volatile SingularAttribute<AdminUser, String> firstName;
    public static volatile SingularAttribute<AdminUser, String> lastName;
    public static volatile SingularAttribute<AdminUser, String> middleName;
    public static volatile SingularAttribute<AdminUser, UserState> userState;
    public static volatile SingularAttribute<AdminUser, Long> joinDate;

}
