package org.itmolab.emailcorp.classses.generators;

import java.util.Random;

import org.itmolab.emailcorp.classses.Company;

public class DataGenerator {
    public static Company[] generateAllData(int numEmployees, int numCompanies, int numMessagesPerEmployee) {
        // array of companies
        Company allData[] = new Company[numCompanies];

        Random rand = new Random();

        // Generate companies
        CompanyGenerator companyGenerator = new CompanyGenerator();
        for (int i = 0; i < numCompanies; i++) {
            Company company = companyGenerator.generateCompany(numEmployees, numMessagesPerEmployee);
            allData[i] = company;
        }

        return allData;
    }
}