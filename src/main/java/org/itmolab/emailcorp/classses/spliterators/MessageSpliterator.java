package org.itmolab.emailcorp.classses.spliterators;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.itmolab.emailcorp.classses.Employee;
import org.itmolab.emailcorp.classses.Message;

public class MessageSpliterator implements Spliterator<Message> {

    private final List<Employee> employees;
    private int employeeIndex;
    private int messageIndex;
    private final int characteristics;
    private long estimateSize;

    public MessageSpliterator(List<Employee> employees) {
        this.employees = employees;
        this.employeeIndex = 0;
        this.messageIndex = 0;
        this.characteristics = ORDERED | SIZED | SUBSIZED | NONNULL | IMMUTABLE;
        this.estimateSize = employees.stream()
                .mapToLong(e -> e.getMessages().size())
                .sum();
    }

    private MessageSpliterator(List<Employee> employees, int employeeIndex, int messageIndex, long estimateSize) {
        this.employees = employees;
        this.employeeIndex = employeeIndex;
        this.messageIndex = messageIndex;
        this.characteristics = ORDERED | SIZED | SUBSIZED | NONNULL | IMMUTABLE;
        this.estimateSize = estimateSize;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Message> action) {
        while (employeeIndex < employees.size()) {
            Employee employee = employees.get(employeeIndex);
            List<Message> messages = employee.getMessages();

            if (messageIndex < messages.size()) {
                action.accept(messages.get(messageIndex++));
                estimateSize--;
                return true;
            }

            employeeIndex++;
            messageIndex = 0;
        }
        return false;
    }

    @Override
    public Spliterator<Message> trySplit() {
        // Уменьшаем пороговое значение для разделения
        if (estimateSize < 100) {
            return null;
        }

        // Находим середину для разделения
        long targetSize = estimateSize / 2;
        long count = 0;
        int splitEmployeeIndex = employeeIndex;
        int splitMessageIndex = messageIndex;

        // Ищем точку разделения
        while (splitEmployeeIndex < employees.size()) {
            Employee employee = employees.get(splitEmployeeIndex);
            List<Message> messages = employee.getMessages();
            int remainingInEmployee = messages.size() - splitMessageIndex;

            if (count + remainingInEmployee <= targetSize) {
                count += remainingInEmployee;
                splitEmployeeIndex++;
                splitMessageIndex = 0;
            } else {
                splitMessageIndex += (int) (targetSize - count);
                break;
            }
        }

        // Если не удалось найти точку разделения
        if (splitEmployeeIndex >= employees.size()
                || (splitEmployeeIndex == employeeIndex && splitMessageIndex == messageIndex)) {
            return null;
        }

        // Создаем новый сплитератор для первой половины
        MessageSpliterator newSpliterator = new MessageSpliterator(
                employees, employeeIndex, messageIndex, targetSize
        );

        // Обновляем текущий сплитератор для второй половины
        this.employeeIndex = splitEmployeeIndex;
        this.messageIndex = splitMessageIndex;
        this.estimateSize -= targetSize;

        return newSpliterator;
    }

    @Override
    public long estimateSize() {
        return estimateSize;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }
}
