package com.assignment.departments.Model;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;

import java.util.ArrayList;

/**
 * Created by Жанар on 16-May-16.
 */
public class SubDepartment {
    private int id;
    private String name;
    private Department department;
    private ArrayList<Employee> employees;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = employees;
    }
}
