package org.itmolab.emailcorp.classses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.itmolab.emailcorp.classses.additional.Department;
import org.itmolab.emailcorp.classses.additional.Position;

public class Employee {

    private int id;
    private String name;
    private String email;
    private Date birthDate;
    private Department department;
    private Position position;
    private List<Message> inbox;

    private static int idCounter = 0;

    public Employee(String name, String email, Date birthDate, Department department, Position position) {
        this.id = generateId();
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.department = department;
        this.position = position;
        this.inbox = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return inbox;
    }

    public void setMessages(List<Message> inbox) {
        this.inbox = inbox;
    }

    public void pushMessages(List<Message> inbox) {
        this.inbox.addAll(inbox);
    }

    public void addMessage(Message message) {
        this.inbox.add(message);
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    private static int generateId() {
        return idCounter++;
    }

    public static int getIdCounter() {
        return idCounter;
    }
}
