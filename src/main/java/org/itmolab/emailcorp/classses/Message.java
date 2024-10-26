package org.itmolab.emailcorp.classses;

import java.time.LocalDate;

import org.itmolab.emailcorp.classses.Employee;

public class Message {
    private int id;
    private Employee sender;
    private String content;
    private LocalDate sentAt;

    private static int idCounter = 0;

    public Message(Employee sender, String content) {
        this.id = generateId();
        this.sender = sender;
        this.content = content;
        this.sentAt = LocalDate.now();
    }

    public Employee getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    private static int generateId() {
        return idCounter++;
    }
}