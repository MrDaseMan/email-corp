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

import org.itmolab.emailcorp.classses.collectors.SendedMessagesCollector;

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

                System.out.println("\n==================\n--- Count: " + count + ":");

                // Data generation
                DataGenerator dataGenerator = new DataGenerator();
                Company company = dataGenerator.generateAllData(count / 100, 1, count / 2)[0];

                Path resultsFolder = Paths.get("./results");
                if (!Files.exists(resultsFolder)) {
                    try {
                        Files.createDirectories(resultsFolder);
                    } catch (IOException e) {
                        System.err.println("Error creating results folder: " + e.getMessage());
                    }
                }

                long timestamp = Instant.now().toEpochMilli();
                Path resultsTimestampFolder = Paths.get(String.format("./results/%d", timestamp));
                if (!Files.exists(resultsTimestampFolder)) {
                    try {
                        Files.createDirectories(resultsTimestampFolder);
                    } catch (IOException e) {
                        System.err.println("Error creating results file: " + e.getMessage());
                    }
                }

                long totalMessagesCount = 0L;
                for (Employee employee : company.getEmployees()) {
                    totalMessagesCount += employee.getMessages().size();

                    for (Message message : employee.getMessages()) {
                        // save sender id in file
                        try (FileWriter senderIdWriter = new FileWriter("./results/" + timestamp + "/sender_id.txt", true)) {
                            senderIdWriter.append(String.format("%d\n", message.getSender().getId()));
                        } catch (IOException e) {
                            System.err.println("Error saving sender id: " + e.getMessage());
                        }
                    }
                }
                System.out.println("Total messages count: " + totalMessagesCount);

                // Iteration
                long startTime = System.nanoTime();
                Map<Integer, Long> sendedMessagesCount = new HashMap<>();
                for (Employee employee : company.getEmployees()) {
                    totalMessagesCount += employee.getMessages().size();
                    for (Message message : employee.getMessages()) {
                        sendedMessagesCount.put(message.getSender().getId(), sendedMessagesCount.getOrDefault(message.getSender().getId(), 0L) + 1L);
                    }
                }
                
                long endTime = System.nanoTime();
                System.out.println("Iteration: " + (endTime - startTime) + " ns");

                Instant instant = Instant.now();
                String fileName = String.format("./results/" + timestamp + "/iteration_results_%d_%d.txt", i + 1, count);

                try (FileWriter iterationWriter = new FileWriter(fileName)) {
                    iterationWriter.write("Iteration results:\n");
                    iterationWriter.write("Time: " + (endTime - startTime) + " ns\n");
                    iterationWriter.write("Received messages count:\n");
                    for (Map.Entry<Integer, Long> entry : sendedMessagesCount.entrySet()) {
                        iterationWriter.write("Employee ID: " + entry.getKey() + ", Received messages: " + entry.getValue() + "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }

                // Stream API
                startTime = System.nanoTime();
                // count sended messages by each employee
                Map<Integer, Long> sendedMessagesCountStream = company.getEmployees().stream()
                        .flatMap(employee -> employee.getMessages().stream())
                        .collect(Collectors.groupingBy(
                            message -> message.getSender().getId(), 
                            Collectors.counting()
                        ));
                endTime = System.nanoTime();
                System.out.println("Stream API: " + (endTime - startTime) + " ns");

                instant = Instant.now();
                fileName = String.format("./results/" + timestamp + "/stream_results_%d_%d.txt", i + 1, count);

                try (FileWriter streamWriter = new FileWriter(fileName)) {
                    streamWriter.write("Stream API results:\n");
                    streamWriter.write("Time: " + (endTime - startTime) + " ns\n");
                    streamWriter.write("Received messages count:\n");
                    for (Map.Entry<Integer, Long> entry : sendedMessagesCountStream.entrySet()) {
                        streamWriter.write("Employee ID: " + entry.getKey() + ", Received messages: " + entry.getValue() + "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }

                // Custom collector
                startTime = System.nanoTime();
                Map<Integer, Long> sendedMessagesCountCollector = company.getEmployees().stream()
                        .flatMap(employee -> employee.getMessages().stream())
                        .collect(new SendedMessagesCollector());
                endTime = System.nanoTime();
                System.out.println("Custom collector: " + (endTime - startTime) + " ns");

                instant = Instant.now();
                fileName = String.format("./results/" + timestamp + "/collector_results_%d_%d.txt", i + 1, count);

                try (FileWriter collectorWriter = new FileWriter(fileName)) {
                    collectorWriter.write("Collector results:\n");
                    collectorWriter.write("Time: " + (endTime - startTime) + " ns\n");
                    collectorWriter.write("Received messages count:\n");
                    for (Map.Entry<Integer, Long> entry : sendedMessagesCountCollector.entrySet()) {
                        collectorWriter.write("Employee ID: " + entry.getKey() + ", Received messages: " + entry.getValue() + "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }
            }   
        }
    }
}