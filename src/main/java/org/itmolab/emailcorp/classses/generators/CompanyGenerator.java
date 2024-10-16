package org.itmolab.emailcorp.classses.generators;

import com.github.javafaker.Faker;

import org.itmolab.emailcorp.classses.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.generators.EmployeeGenerator;

import org.itmolab.emailcorp.classses.Message;
import org.itmolab.emailcorp.classses.generators.MessagesGenerator;

public class CompanyGenerator {
    private Faker faker;
    private Random random;

    public CompanyGenerator() {
        faker = new Faker();
        random = new Random();
    }

    public Company generateCompany( int numEmployees, int numMessages, int numMaxMessagesPerEmployee ) {
        String name = faker.company().name();

        Company company = new Company(name);

        // Generate employees
        EmployeeGenerator employeeGenerator = new EmployeeGenerator();
        for (int i = 0; i < numEmployees; i++) {
            Employee employee = employeeGenerator.generateEmployee(0, 1000);
            company.addEmployee(employee);
        }

        // for each employee, generate messages
        MessagesGenerator messagesGenerator = new MessagesGenerator();
        for (Employee employee : company.getEmployees()) {
            List<Message> messages = new ArrayList<>();
            for (int i = 0; i < random.nextInt(numMaxMessagesPerEmployee); i++) {
                int reciver = random.nextInt(numEmployees);
                if (reciver == employee.getId()) {
                    reciver = (reciver + 1) % numEmployees;
                }
                Message message = messagesGenerator.generateMessage(employee.getId(), reciver);
                messages.add(message);
            }
            company.pushMessages(messages);
        }

        return company;
    }
}
