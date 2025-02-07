package org.itmolab.emailcorp.classses.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;

import com.github.javafaker.Faker;

public class CompanyGenerator {

    private Faker faker;
    private Random random;

    public CompanyGenerator() {
        faker = new Faker();
        random = new Random();
    }

    public Company generateCompany(int numEmployees, int numMaxMessagesPerEmployee) {
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
                // get random sender
                Employee sender = company.getEmployees().get(random.nextInt(company.getEmployees().size()));

                if (sender.getId() == employee.getId()) {
                    continue;
                }

                Message message = messagesGenerator.generateMessage(sender);
                messages.add(message);
            }

            employee.pushMessages(messages);
        }

        return company;
    }
}
