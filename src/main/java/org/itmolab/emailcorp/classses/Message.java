package org.itmolab.emailcorp.classses;

import java.time.LocalDate;

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
        return getSender(0);
    }

    public Employee getSender(long delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return sender;
    }

    public String getContent() {
        return content;
    }

    private static int generateId() {
        return idCounter++;
    }
}
