package org.itmolab.emailcorp.classses.collectors;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Map;
import java.io.IOException;
import java.util.stream.Collector;
import java.util.List;
import java.util.HashMap;
import java.io.FileWriter;

import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;

public class ReceivedMessagesCollector implements Collector<Employee, Map<Integer, Integer>, Map<Integer, Integer>> {

    private final List<Message> messages;

    public ReceivedMessagesCollector(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public Supplier<Map<Integer, Integer>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, Integer>, Employee> accumulator() {
        return (map, employee) -> {
            int receivedMessages = 0;
            for (Message message : messages) {
                if (message.getReceiverID() == employee.getId()) {
                    receivedMessages++;
                }
            }
            map.put(employee.getId(), receivedMessages);
        };
    }

    @Override
    public BinaryOperator<Map<Integer, Integer>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Function<Map<Integer, Integer>, Map<Integer, Integer>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}