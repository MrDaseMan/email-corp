package org.itmolab.emailcorp.classses.generators;

import com.github.javafaker.Faker;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;

public class MessagesGenerator {

    private Faker faker;

    public MessagesGenerator() {
        faker = new Faker();
    }

    public Message generateMessage(Employee sender) {
        String text = faker.lorem().sentence();
        return new Message(sender, text);
    }
}