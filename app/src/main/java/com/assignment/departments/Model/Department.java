package com.assignment.departments.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Жанар on 16-May-16.
 */
public class Department implements Serializable {

    private int id;
    private String title;
    private Employee headUser;
    private Department orgUnitParrent;
    private ArrayList<Employee> employees;
    private ArrayList<Department> orgUnitChilds;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = employees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Department getOrgUnitParrent() {
        return orgUnitParrent;
    }

    public void setOrgUnitParrent(Department orgUnitParrent) {
        this.orgUnitParrent = orgUnitParrent;
    }

    public ArrayList<Department> getOrgUnitChilds() {
        return orgUnitChilds;
    }

    public void setOrgUnitChilds(ArrayList<Department> orgUnitChilds) {
        this.orgUnitChilds = orgUnitChilds;
    }

    public Employee getHeadUser() {
        return headUser;
    }

    public void setHeadUser(Employee headUser) {
        this.headUser = headUser;
    }
}
