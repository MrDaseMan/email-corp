package org.itmolab.emailcorp.classses;

import java.util.ArrayList;
import java.util.List;

import org.itmolab.emailcorp.classses.Employee;

import com.github.javafaker.Faker;

public class Company {
    private String name;
    private List<Employee> employees;

    public Company(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public Company(String name, List<Employee> employees) {
        this.name = name;
        this.employees = new ArrayList<>(employees);
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public String getName() {
        return name;
    }
}