package com.ticket.generator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StripMetaInfo {
    public static final Random RANDOM = new Random();
    private final int[] rowNumbersCount = new int[18]; // tracks the count of numbers in a row
    private final int[] ticketNumbersCount = new int[6]; // tracks the count of numbers in a ticket
    private final Map<Integer, ColumnInfo> columnsInfo = new HashMap<>();

    public StripMetaInfo() {
        IntStream.range(0, 9).forEach(column -> columnsInfo.put(column, new ColumnInfo(column)));
    }

    public boolean canAddTheNumber(int column, int row) {
        if (!anySpaceLeftForNumber(column, row)) {
            return false;
        }
        var columnInfo = columnsInfo.get(column);
        return columnInfo.lastRowWithNoGeneratedNumber(row) ||
                columnInfo.mustFillTillEnd(row) ||
                columnInfo.shouldNumberBeGenerated(row);

    }

    private boolean anySpaceLeftForNumber(int column, int row) {
        return rowNumbersCount[row] < 5 && columnsInfo.get(column).spaceForNumberInTicket(row);
    }

    public int addNumber(int column, int row) {
        rowNumbersCount[row]++;
        ticketNumbersCount[(int) Math.floor((float) row / 3)]++;
        return columnsInfo.get(column).addNumber(row);
    }

    public boolean mustAddTheNumber(int columnsLeft, int row) {
        // e.g. when 5 columns are left and there are no numbers yet
        // we need to fill every column in such case
        return 5 - columnsLeft == rowNumbersCount[row];
    }

    public int addPlaceholder(int column, int row) {
        rowNumbersCount[row]++;
        ticketNumbersCount[(int) Math.floor((float) row / 3)]++;
        return columnsInfo.get(column).addPlaceholder(row);
    }

    public boolean numberExistsInTicketColumn(int column, int ticket) {
        return columnsInfo.get(column).anyNumberExists(ticket);
    }

    public int rowWithLeastNumbers(int ticketNumber) {
        Map<Integer, Integer> rowCount = new HashMap<>();
        rowCount.put((ticketNumber * 3), rowNumbersCount[ticketNumber * 3]);
        rowCount.put((ticketNumber * 3 + 1), rowNumbersCount[ticketNumber * 3 + 1]);
        rowCount.put((ticketNumber * 3 + 2), rowNumbersCount[ticketNumber * 3 + 2]);
        return rowCount.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue()).get()
                .getKey();
    }

    public List<Map.Entry<Integer, Integer>> sortedTickets() {
        var ticketNumbersMap = IntStream.range(0, 6).boxed().collect(Collectors.toMap(Function.identity(), i -> ticketNumbersCount[i]));
        return ticketNumbersMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
    }

    public int countOfNumberYetToBeFilled(int column) {
        return columnsInfo.get(column).countOfNumberYetToBeFilled();
    }

    public List<Integer> rowsWithLeastNumbers(int column, Integer ticketNumber) {
        Map<Integer, Integer> rowCount = new HashMap<>();
        rowCount.put((ticketNumber * 3), rowNumbersCount[ticketNumber * 3]);
        rowCount.put((ticketNumber * 3 + 1), rowNumbersCount[ticketNumber * 3 + 1]);
        rowCount.put((ticketNumber * 3 + 2), rowNumbersCount[ticketNumber * 3 + 2]);
        return rowCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
