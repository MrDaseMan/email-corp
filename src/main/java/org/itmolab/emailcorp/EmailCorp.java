package org.itmolab.emailcorp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;
import org.itmolab.emailcorp.classses.generators.DataGenerator;
import org.itmolab.emailcorp.classses.collectors.SendedMessagesCollector;
import org.itmolab.emailcorp.classses.spliterators.MessageSpliterator;
import org.itmolab.emailcorp.classses.generators.MessagesGenerator;
import java.util.Random;

public class EmailCorp {

    private record BenchmarkResult(long duration, Map<Integer, Long> results) {

    }

    public static void main(String[] args) {
        // Создаем директории для результатов
        createResultsDirectory();
        long timestamp = Instant.now().toEpochMilli();
        createTimestampDirectory(timestamp);

        // Создаем максимальное количество сотрудников для всех тестов
        int maxEmployees = 500;
        Company baseCompany = new DataGenerator().generateAllData(maxEmployees, 1, 1)[0];
        List<Employee> employeePool = new ArrayList<>(baseCompany.getEmployees());

        System.out.println("=== Lab 1: Basic Statistics Collection ===");
        runLab1Benchmark(5000, timestamp, employeePool);
        runLab1Benchmark(50000, timestamp, employeePool);
        runLab1Benchmark(250000, timestamp, employeePool);

        System.out.println("\n=== Lab 2: Parallel Processing with Delays ===");
        
        // Тесты с разными задержками
        long[] delays = {0, 1, 2, 5};  // задержки в миллисекундах
        for (long delay : delays) {
            System.out.printf("\n=== Testing with delay: %dms ===\n", delay);
            runLab2Benchmark(5000, delay, timestamp, employeePool);
            runLab2Benchmark(50000, delay, timestamp, employeePool);
            runLab2Benchmark(250000, delay, timestamp, employeePool);
        }
    }

    private static void runLab1Benchmark(int count, long timestamp, List<Employee> employeePool) {
        System.out.println("\n--- Count: " + count + " ---");

        // Запускаем тесты для первой лабораторной с новыми сообщениями для каждого теста
        BenchmarkResult iterativeResult = measureExecution(() -> {
            Company company = generateCompanyWithNewMessages(employeePool.subList(0, count/500), 10); // Уменьшили количество сообщений
            return runIterative(company);
        });
        
        BenchmarkResult streamResult = measureExecution(() -> {
            Company company = generateCompanyWithNewMessages(employeePool.subList(0, count/500), 10);
            return runSequential(company, 0);
        });
        
        BenchmarkResult customCollectorResult = measureExecution(() -> {
            Company company = generateCompanyWithNewMessages(employeePool.subList(0, count/500), 10);
            return runCustomCollector(company);
        });

        // Сохраняем результаты
        saveLab1Results(timestamp, count, iterativeResult, streamResult, customCollectorResult);

        // Форматированный вывод результатов
        String format = "%-20s: %,15d ns%n";
        System.out.printf(format, "Iterative", iterativeResult.duration());
        System.out.printf(format, "Stream API", streamResult.duration());
        System.out.printf(format, "Custom Collector", customCollectorResult.duration());
    }

    private static void runLab2Benchmark(int count, long delay, long timestamp, List<Employee> employeePool) {
        System.out.println("\n--- Count: " + count + " ---");

        BenchmarkResult seqResult = measureExecution(() -> {
            Company company = generateCompanyWithNewMessages(employeePool.subList(0, count/500), 10);
            return runSequential(company, delay);
        });
        
        BenchmarkResult parResult = measureExecution(() -> {
            Company company = generateCompanyWithNewMessages(employeePool.subList(0, count/500), 10);
            return runParallel(company, delay);
        });
        
        BenchmarkResult optParResult = measureExecution(() -> {
            Company company = generateCompanyWithNewMessages(employeePool.subList(0, count/500), 10);
            return runOptimizedParallel(company, delay);
        });

        // Сохраняем результаты
        saveLab2Results(timestamp, count, delay, seqResult, parResult, optParResult);

        // Форматированный вывод результатов
        String format = "%-20s: %,15d ns%n";
        System.out.printf(format, "Sequential", seqResult.duration());
        System.out.printf(format, "Parallel", parResult.duration());
        System.out.printf(format, "Optimized Parallel", optParResult.duration());
    }

    private static BenchmarkResult measureExecution(Supplier<Map<Integer, Long>> test) {
        long startTime = System.nanoTime();
        Map<Integer, Long> result = test.get();
        long duration = System.nanoTime() - startTime;
        return new BenchmarkResult(duration, result);
    }

    private static Map<Integer, Long> runIterative(Company company) {
        Map<Integer, Long> result = new HashMap<>();
        
        for (Employee employee : company.getEmployees()) {
            for (Message message : employee.getMessages()) {
                int senderId = message.getSender().getId();
                result.merge(senderId, 1L, Long::sum);
            }
        }
        
        return result;
    }

    private static Map<Integer, Long> runSequential(Company company, long delay) {
        return company.getEmployees().stream()
                .flatMap(employee -> employee.getMessages().stream())
                .collect(Collectors.groupingBy(
                        message -> message.getSender(delay).getId(),
                        HashMap::new,
                        Collectors.counting()
                ));
    }

    private static Map<Integer, Long> runParallel(Company company, long delay) {
        return company.getEmployees().parallelStream()
                .flatMap(employee -> employee.getMessages().parallelStream())
                .collect(Collectors.groupingBy(
                        message -> message.getSender(delay).getId(),
                        ConcurrentHashMap::new,
                        Collectors.counting()
                ));
    }

    private static Map<Integer, Long> runCustomCollector(Company company) {
        return company.getEmployees().stream()
                .flatMap(employee -> employee.getMessages().stream())
                .collect(new SendedMessagesCollector());
    }

    private static Map<Integer, Long> runOptimizedParallel(Company company, long delay) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        ForkJoinPool customPool = new ForkJoinPool(numThreads);
        try {
            return customPool.submit(() ->
                company.getEmployees().parallelStream()
                    .flatMap(employee -> employee.getMessages().parallelStream())
                    .collect(Collectors.groupingByConcurrent(
                        message -> message.getSender(delay).getId(),
                        ConcurrentHashMap::new,
                        Collectors.counting()
                    ))
            ).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            customPool.shutdown();
        }
    }

    private static void createResultsDirectory() {
        createDirectoryIfNotExists(Paths.get("./results"));
    }

    private static void createTimestampDirectory(long timestamp) {
        Path timestampDir = Paths.get("./results", String.valueOf(timestamp));
        createDirectoryIfNotExists(timestampDir);
    }

    private static void createDirectoryIfNotExists(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveLab1Results(long timestamp, int count,
                                      BenchmarkResult iterativeResult,
                                      BenchmarkResult streamResult,
                                      BenchmarkResult customCollectorResult) {
        Path resultsDir = Paths.get("./results", String.valueOf(timestamp));
        Path lab1Dir = resultsDir.resolve("lab1");
        createDirectoryIfNotExists(lab1Dir);

        Path resultFile = lab1Dir.resolve(String.format("count_%d.txt", count));
        try (PrintWriter writer = new PrintWriter(new FileWriter(resultFile.toFile()))) {
            writer.printf("Count: %d%n", count);
            writer.printf("%-20s: %,15d ns%n", "Iterative", iterativeResult.duration());
            writer.printf("%-20s: %,15d ns%n", "Stream API", streamResult.duration());
            writer.printf("%-20s: %,15d ns%n", "Custom Collector", customCollectorResult.duration());
            
            writer.println("\nDetailed Results:");
            writer.println("Iterative:");
            writeDetailedResults(writer, iterativeResult.results());
            writer.println("\nStream API:");
            writeDetailedResults(writer, streamResult.results());
            writer.println("\nCustom Collector:");
            writeDetailedResults(writer, customCollectorResult.results());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveLab2Results(long timestamp, int count, long delay,
                                      BenchmarkResult seqResult,
                                      BenchmarkResult parResult,
                                      BenchmarkResult optParResult) {
        Path resultsDir = Paths.get("./results", String.valueOf(timestamp));
        Path lab2Dir = resultsDir.resolve("lab2");
        createDirectoryIfNotExists(lab2Dir);

        Path resultFile = lab2Dir.resolve(String.format("count_%d_delay_%d.txt", count, delay));
        try (PrintWriter writer = new PrintWriter(new FileWriter(resultFile.toFile()))) {
            writer.printf("Count: %d, Delay: %d ms%n", count, delay);
            writer.printf("%-20s: %,15d ns%n", "Sequential", seqResult.duration());
            writer.printf("%-20s: %,15d ns%n", "Parallel", parResult.duration());
            writer.printf("%-20s: %,15d ns%n", "Optimized Parallel", optParResult.duration());
            
            writer.println("\nDetailed Results:");
            writer.println("Sequential:");
            writeDetailedResults(writer, seqResult.results());
            writer.println("\nParallel:");
            writeDetailedResults(writer, parResult.results());
            writer.println("\nOptimized Parallel:");
            writeDetailedResults(writer, optParResult.results());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDetailedResults(PrintWriter writer, Map<Integer, Long> results) {
        writer.println("Employee ID | Messages Sent");
        writer.println("-----------+-------------");
        results.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                writer.printf("%10d | %,12d%n", entry.getKey(), entry.getValue())
            );
    }

    private static Company generateCompanyWithNewMessages(List<Employee> employees, int maxMessagesPerEmployee) {
        Random random = new Random();
        
        // Очищаем старые сообщения
        for (Employee employee : employees) {
            employee.setMessages(new ArrayList<>());
        }

        // Генерируем новые сообщения
        MessagesGenerator messageGenerator = new MessagesGenerator();
        for (Employee employee : employees) {
            int messageCount = random.nextInt(maxMessagesPerEmployee);
            for (int i = 0; i < messageCount; i++) {
                Message message = messageGenerator.generateMessage(employee);
                employee.addMessage(message);
            }
        }

        return new Company("Test Company", employees);
    }
}
