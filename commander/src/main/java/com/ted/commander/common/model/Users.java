/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.util.ArrayList;
import java.util.List;


public class Users {
    private List<User> users = new ArrayList<User>();

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Users users1 = (Users) o;

        if (users != null ? !users.equals(users1.users) : users1.users != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return users != null ? users.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Users{" +
                "users=" + users +
                '}';
    }
}
