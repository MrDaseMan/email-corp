package org.itmolab.emailcorp.benchmarks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.generators.DataGenerator;
import org.itmolab.emailcorp.classses.spliterators.MessageSpliterator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class StatisticsBenchmark {

    @Param({"5000", "50000", "250000"})
    private int count;

    @Param({"0", "1", "2", "5"})
    private long delay;

    private Company company;

    @Setup
    public void setup() {
        company = new DataGenerator().generateAllData(count / 100, 1, count / 2)[0];
    }

    // Lab 2 benchmarks
    @Benchmark
    public Map<Integer, Long> sequentialWithDelay() {
        return company.getEmployees().stream()
                .flatMap(employee -> employee.getMessages().stream())
                .collect(Collectors.groupingBy(
                        message -> message.getSender(delay).getId(),
                        HashMap::new,
                        Collectors.counting()
                ));
    }

    @Benchmark
    public Map<Integer, Long> parallelWithDelay() {
        return company.getEmployees().parallelStream()
                .flatMap(employee -> employee.getMessages().parallelStream())
                .collect(Collectors.groupingBy(
                        message -> message.getSender(delay).getId(),
                        ConcurrentHashMap::new,
                        Collectors.counting()
                ));
    }

    @Benchmark
    public Map<Integer, Long> optimizedParallelWithDelay() {
        MessageSpliterator spliterator = new MessageSpliterator(company.getEmployees());
        return StreamSupport.stream(spliterator, true)
                .collect(Collectors.groupingBy(
                        message -> message.getSender(delay).getId(),
                        ConcurrentHashMap::new,
                        Collectors.counting()
                ));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StatisticsBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
