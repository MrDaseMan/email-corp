package org.itmolab.emailcorp.benchmarks;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import io.reactivex.rxjava3.core.Observable;

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
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import org.itmolab.emailcorp.classses.Company;
import org.itmolab.emailcorp.classses.Message;
import org.itmolab.emailcorp.classses.generators.DataGenerator;
import org.itmolab.emailcorp.classses.spliterators.MessageSpliterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class StatisticsBenchmark {

    @Param({"1000", "5000", "10000", "100000"})
    private int count;

    @Param({"0", "1"})
    private long delay;

    private Company company;

    @Setup
    public void setup() {
        company = new DataGenerator().generateAllData(count / 100, 1, count / 2)[0];
    }

    // Lab 2 benchmarks
//     @Benchmark
//     public Map<Integer, Long> sequentialWithDelay() {
//         return company.getEmployees().stream()
//                 .flatMap(employee -> employee.getMessages().stream())
//                 .collect(Collectors.groupingBy(
//                         message -> message.getSender(delay).getId(),
//                         HashMap::new,
//                         Collectors.counting()
//                 ));
//     }
//     @Benchmark
//     public Map<Integer, Long> parallelWithDelay() {
//         return company.getEmployees().parallelStream()
//                 .flatMap(employee -> employee.getMessages().parallelStream())
//                 .collect(Collectors.groupingBy(
//                         message -> message.getSender(delay).getId(),
//                         ConcurrentHashMap::new,
//                         Collectors.counting()
//                 ));
//     }
//     @Benchmark
//     public Map<Integer, Long> optimizedParallelWithDelay() {
//         MessageSpliterator spliterator = new MessageSpliterator(company.getEmployees());
//         return StreamSupport.stream(spliterator, true)
//                 .collect(Collectors.groupingBy(
//                         message -> message.getSender(delay).getId(),
//                         ConcurrentHashMap::new,
//                         Collectors.counting()
//                 ));
//     }
    // Реактивная обработка с Observable
    @Benchmark
    public Map<Integer, Long> reactiveWithDelay() {
        Map<Integer, Long> statistics = new ConcurrentHashMap<>();

        Observable.create(emitter -> {
            MessageSpliterator spliterator = new MessageSpliterator(company.getEmployees());
            spliterator.forEachRemaining(emitter::onNext);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .blockingForEach((Object messageObj) -> {
                    Message message = (Message) messageObj;
                    int senderId = message.getSender(delay).getId();
                    statistics.merge(senderId, 1L, Long::sum);
                });

        return statistics;
    }

    // Реактивная обработка с Flowable и backpressure
    @Benchmark
    public Map<Integer, Long> reactiveBackpressureWithDelay() {
        Map<Integer, Long> statistics = new ConcurrentHashMap<>();

        Flowable.create(emitter -> {
            MessageSpliterator spliterator = new MessageSpliterator(company.getEmployees());
            spliterator.forEachRemaining(emitter::onNext);
            emitter.onComplete();
        }, io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .blockingForEach((Object messageObj) -> {
                    Message message = (Message) messageObj;
                    int senderId = message.getSender(delay).getId();
                    statistics.merge(senderId, 1L, Long::sum);
                });

        return statistics;
    }

    // Реактивная обработка с контролем потока
    @Benchmark
    public Map<Integer, Long> reactiveControlledFlow() {
        Map<Integer, Long> statistics = new ConcurrentHashMap<>();

        Flowable<Message> messages = ReactiveStatistics.generateMessages(ELEMENT_COUNT);
        messages.observeOn(Schedulers.computation())
                .count()
                .blockingGet();

        return statistics;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StatisticsBenchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.CSV) // Указание формата результата
                .result("benchmark_results.csv") // Имя выходного файла
                .build();

        new Runner(opt).run();
    }
}
