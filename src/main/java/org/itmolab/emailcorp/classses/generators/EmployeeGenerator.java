package org.itmolab.emailcorp.classses.generators;

import com.github.javafaker.Faker;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.additional.Department;
import org.itmolab.emailcorp.classses.additional.Position;
import org.itmolab.emailcorp.classses.Message;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class EmployeeGenerator {
    private Faker faker;

    public EmployeeGenerator() {
        faker = new Faker();
    }

    public Employee generateEmployee(int minMessageCount, int maxMessageCount) {
        // if not specified, set minMessageCount to 1
        if (minMessageCount == 0) {
            minMessageCount = 1;
        }

        // if not specified, set maxMessageCount to 5
        if (maxMessageCount == 0) {
            maxMessageCount = 10;
        }
        
        // if minMessageCount is greater than maxMessageCount, swap them
        if (minMessageCount > maxMessageCount) {
            int temp = minMessageCount;
            minMessageCount = maxMessageCount;
            maxMessageCount = temp;
        }

        int id = faker.number().numberBetween(1, 1000);
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        Date birthDate = faker.date().birthday();
        Department department = new Department(faker.commerce().department(), faker.lorem().sentence());
        Position position = Position.values()[faker.number().numberBetween(0, 2)];

        Employee employee = new Employee(name, email, birthDate, department, position);

        return employee;
    }
}