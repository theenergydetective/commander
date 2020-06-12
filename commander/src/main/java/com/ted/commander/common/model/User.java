/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.ted.commander.common.enums.LanguageCode;
import com.ted.commander.common.enums.UserState;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Represents a user enrolled in the com.com.ted.commander system.
 */

public class User implements Serializable {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private UserState userState;
    private long joinDate;
    private LanguageCode languageCode;
    private boolean billingEnabled = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isBillingEnabled() {
        return true;
    }

    public void setBillingEnabled(boolean billingEnabled) {
        this.billingEnabled = billingEnabled;
    }

    @JsonIgnore
    public String getFormattedName() {
        StringBuilder stringBuilder = new StringBuilder();
        if (lastName != null && !lastName.isEmpty()) stringBuilder.append(lastName);
        if (firstName != null && !firstName.isEmpty()) {
            if (stringBuilder.length() != 0) stringBuilder.append(", ");
            stringBuilder.append(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            if (stringBuilder.length() != 0) stringBuilder.append(" ");
            stringBuilder.append(middleName);
        }
        if (stringBuilder.length() == 0) stringBuilder.append(username).append("*");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (joinDate != user.joinDate) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (middleName != null ? !middleName.equals(user.middleName) : user.middleName != null) return false;
        if (userState != user.userState) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (userState != null ? userState.hashCode() : 0);
        result = 31 * result + (int) (joinDate ^ (joinDate >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", userState=" + userState +
                ", joinDate=" + joinDate +
                '}';
    }

    public LanguageCode getLanguageCode() {
        if (languageCode == null) languageCode = LanguageCode.EN;
        return languageCode;
    }
}
