package org.itmolab.emailcorp.classses;

import java.time.LocalDate;

public class Message {
    private int id;
    private int sender;
    private int receiver;
    private String content;
    private LocalDate sentAt;

    private static int idCounter = 0;

    public Message(int sender, int receiver, String content) {
        this.id = generateId();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentAt = LocalDate.now();
    }

    public int getSenderID() {
        return sender;
    }

    public int getReceiverID() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    private static int generateId() {
        return idCounter++;
    }
}