/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.admin;


import com.ted.commander.common.enums.UserState;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents a user enrolled in the com.com.ted.commander system.
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUser implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private String middleName;

    @Column(name = "state", columnDefinition = "TINYINT")
    private UserState userState;

    private Long joinDate;

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

    public Long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Long joinDate) {
        this.joinDate = joinDate;
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

        AdminUser adminUser = (AdminUser) o;

        if (id != null ? !id.equals(adminUser.id) : adminUser.id != null) return false;
        if (username != null ? !username.equals(adminUser.username) : adminUser.username != null) return false;
        if (firstName != null ? !firstName.equals(adminUser.firstName) : adminUser.firstName != null) return false;
        if (lastName != null ? !lastName.equals(adminUser.lastName) : adminUser.lastName != null) return false;
        if (middleName != null ? !middleName.equals(adminUser.middleName) : adminUser.middleName != null) return false;
        if (userState != adminUser.userState) return false;
        return !(joinDate != null ? !joinDate.equals(adminUser.joinDate) : adminUser.joinDate != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (userState != null ? userState.hashCode() : 0);
        result = 31 * result + (joinDate != null ? joinDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", userState=" + userState +
                ", joinDate=" + joinDate +
                '}';
    }

}
