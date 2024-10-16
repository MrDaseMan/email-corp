package org.itmolab.emailcorp.classses.generators;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.itmolab.emailcorp.classses.generators.CompanyGenerator;
import org.itmolab.emailcorp.classses.generators.EmployeeGenerator;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Employee;

// rand
import java.util.Random;

public class DataGenerator {
    public static Company[] generateAllData(int numEmployees, int numMessages, int numCompanies, int numMessagesPerEmployee) {
        // array of companies
        Company allData[] = new Company[numCompanies];

        Random rand = new Random();

        // Generate companies
        CompanyGenerator companyGenerator = new CompanyGenerator();
        for (int i = 0; i < numCompanies; i++) {
            Company company = companyGenerator.generateCompany(numEmployees, numMessages, numMessagesPerEmployee);
            allData[i] = company;
        }

        return allData;
    }
}