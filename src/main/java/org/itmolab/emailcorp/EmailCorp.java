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

import org.itmolab.emailcorp.classses.generators.DataGenerator;

import org.itmolab.emailcorp.classses.collectors.ReceivedMessagesCollector;

import java.util.Map;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import java.time.Instant;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class EmailCorp {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int[] counts = {5000, 50000, 250000};
            for (int count : counts) {

                System.out.println("\n==================\n Count: " + count + ":");

                // Data generation
                DataGenerator dataGenerator = new DataGenerator();
                Company company = dataGenerator.generateAllData(count / 100, count, 1, count / 2)[0];

                Path resultsFolder = Paths.get("./results");
                if (!Files.exists(resultsFolder)) {
                    try {
                        Files.createDirectories(resultsFolder);
                    } catch (IOException e) {
                        System.err.println("Error creating results folder: " + e.getMessage());
                    }
                }

                // Iteration
                long startTime = System.nanoTime();
                Map<Integer, Integer> receivedMessagesCount = new HashMap<>();
                for (Employee employee : company.getEmployees()) {
                    int receivedMessages = 0;
                    for (Message message : company.getMessages()) {
                        if (message.getReceiverID() == employee.getId()) {
                            receivedMessages++;
                        }
                    }
                    receivedMessagesCount.put(employee.getId(), receivedMessages);
                }
                long endTime = System.nanoTime();
                System.out.println("Iteration: " + (endTime - startTime) + " ns");

                Instant instant = Instant.now();
                String fileName = String.format("./results/iteration_results_%d_%d_%d.txt", i + 1, count, instant.toEpochMilli());

                try (FileWriter iterationWriter = new FileWriter(fileName)) {
                    iterationWriter.write("Iteration results:\n");
                    iterationWriter.write("Time: " + (endTime - startTime) + " ns\n");
                    iterationWriter.write("Received messages count:\n");
                    for (Map.Entry<Integer, Integer> entry : receivedMessagesCount.entrySet()) {
                        iterationWriter.write("Employee ID: " + entry.getKey() + ", Received messages: " + entry.getValue() + "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }

                // Stream API
                startTime = System.nanoTime();
                Map<Integer, Long> receivedMessagesCountStream = company.getMessages().stream()
                        .collect(Collectors.groupingBy(Message::getReceiverID, Collectors.counting()));
                endTime = System.nanoTime();
                System.out.println("Stream API: " + (endTime - startTime) + " ns");

                instant = Instant.now();
                fileName = String.format("./results/stream_results_%d_%d_%d.txt", i + 1, count, instant.toEpochMilli());

                try (FileWriter streamWriter = new FileWriter(fileName)) {
                    streamWriter.write("Stream API results:\n");
                    streamWriter.write("Time: " + (endTime - startTime) + " ns\n");
                    streamWriter.write("Received messages count:\n");
                    for (Map.Entry<Integer, Long> entry : receivedMessagesCountStream.entrySet()) {
                        streamWriter.write("Employee ID: " + entry.getKey() + ", Received messages: " + entry.getValue() + "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }

                // Custom collector
                startTime = System.nanoTime();
                Map<Integer, Integer> receivedMessagesCountCollector = company.getEmployees().stream()
                        .collect(new ReceivedMessagesCollector(company.getMessages()));
                endTime = System.nanoTime();
                System.out.println("Custom collector: " + (endTime - startTime) + " ns");

                instant = Instant.now();
                fileName = String.format("./results/collector_results_%d_%d_%d.txt", i + 1, count, instant.toEpochMilli());

                try (FileWriter collectorWriter = new FileWriter(fileName)) {
                    collectorWriter.write("Collector results:\n");
                    collectorWriter.write("Time: " + (endTime - startTime) + " ns\n");
                    collectorWriter.write("Received messages count:\n");
                    for (Map.Entry<Integer, Integer> entry : receivedMessagesCountCollector.entrySet()) {
                        collectorWriter.write("Employee ID: " + entry.getKey() + ", Received messages: " + entry.getValue() + "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }
            }   
        }
    }
}