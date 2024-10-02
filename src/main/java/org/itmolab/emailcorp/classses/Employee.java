package org.itmolab.emailcorp.classses;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String name;
    private String email;
    private List<Message> inbox;

    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
        this.inbox = new ArrayList<>();
    }

    public void receiveMessage(Message message) {
        this.inbox.add(message);
    }

    public List<Message> getInbox() {
        return inbox;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}