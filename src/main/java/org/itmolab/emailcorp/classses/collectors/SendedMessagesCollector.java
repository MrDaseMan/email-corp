package org.itmolab.emailcorp.classses.collectors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.itmolab.emailcorp.classses.Message;

public class SendedMessagesCollector implements Collector<Message, Map<Integer, Long>, Map<Integer, Long>> {

    @Override
    public Supplier<Map<Integer, Long>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, Long>, Message> accumulator() {
        return (map, message) -> {
            map.put(message.getSender().getId(), map.getOrDefault(message.getSender().getId(), 0L) + 1);
        };
    }

    @Override
    public BinaryOperator<Map<Integer, Long>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Function<Map<Integer, Long>, Map<Integer, Long>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
