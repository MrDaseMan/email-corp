/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.itmolab.emailcorp;

import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;

public class EmailCorp {

    public static void main(String[] args) {
        Faker faker = new Faker();
        Random random = new Random();

        // Create company
        Company company = new Company(faker.company().name());

        // Generate employees
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee(faker.name().fullName(), faker.internet().emailAddress());
            employees.add(employee);
            company.addEmployee(employee);
        }

        // Generate messages
        for (int i = 0; i < 100000; i++) {
            Employee sender = employees.get(random.nextInt(employees.size()));
            Employee receiver = employees.get(random.nextInt(employees.size()));
            if (!sender.equals(receiver)) {
                Message message = new Message(sender, receiver, faker.lorem().sentence());
                receiver.receiveMessage(message);
            }
        }

        // Calculate statistics
        System.out.println("Company: " + company.getName());
        System.out.println("Total Employees: " + company.getEmployees().size());

        for (Employee employee : company.getEmployees()) {
            System.out.println(employee.getName() + " has " + employee.getInbox().size() + " messages.");
        }
    }
}
