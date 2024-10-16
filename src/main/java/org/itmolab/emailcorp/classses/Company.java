package org.itmolab.emailcorp.classses;

import java.util.ArrayList;
import java.util.List;

import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;

import com.github.javafaker.Faker;

public class Company {
    private String name;
    private List<Employee> employees;
    private List<Message> messages;

    public Company(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
        this.messages = new ArrayList<>();
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void pushMessages(List<Message> messages) {
        this.messages.addAll(messages);
    }
}