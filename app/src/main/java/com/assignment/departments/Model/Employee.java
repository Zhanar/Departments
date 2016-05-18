package com.assignment.departments.Model;

import com.assignment.departments.Model.Department;

import java.io.Serializable;

/**
 * Created by Жанар on 16-May-16.
 */
public class Employee implements Serializable {
    private int id;
    private String login;
    private String password;
    //private Department department;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
