package com.ticket.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Strip {
    private final StripMetaInfo info = new StripMetaInfo();

    public int[][] generate() {
        int[][] strip = new int[9][18];

        fillRandomNumbers(strip);
        solveForEmptyColumns(strip);

        return strip;
    }

    /**
     * generate random numbers for 4 columns
     * just 4 because in worst case some row might end up with 0 numbers
     * in that case we have to fill it manually
     *
     * @param strip
     */
    private void fillRandomNumbers(int[][] strip) {
        // to allow some randomness
        // we fill some columns with random numbers
        IntStream.range(0, 9)
                .filter(column -> !Arrays.asList(3, 4, 5, 6, 7).contains(column))
                .forEach(column -> fillColumnWithRandomNumbers(strip, column));
    }

    private void fillColumnWithRandomNumbers(int[][] strip, int column) {
        IntStream.range(0, 18).forEach(row -> {
            if (info.canAddTheNumber(column, row)) {
                strip[column][row] = info.addNumber(column, row);
            }
        });
    }


    private void solveForEmptyColumns(int[][] strip) {
        var columnsLeft = 5;
        for (int column = 3; column <= 7; column++) {
            List<Integer> rowNumbersToBeFilled = new ArrayList<>();

            // determine rows that must be filled out with a number
            for (int row = 0; row < 18; row++) {
                if (info.mustAddTheNumber(columnsLeft, row)) {
                    rowNumbersToBeFilled.add(row);
                    strip[column][row] = info.addPlaceholder(column, row);
                }
            }
            columnsLeft--;

            // add 1 number to each ticket to a row with the smallest amount of numbers
            for (int ticketNumber = 0; ticketNumber < 6; ticketNumber++) {
                if (!info.numberExistsInTicketColumn(column, ticketNumber)) {
                    var rowWithLeastNumbers = info.rowWithLeastNumbers(ticketNumber);
                    rowNumbersToBeFilled.add(rowWithLeastNumbers);
                    strip[column][rowWithLeastNumbers] = info.addPlaceholder(column, rowWithLeastNumbers);
                }
            }

            // fill the reminding amount of numbers in groups with the smallest amount of numbers
            var sortedTickets = info.sortedTickets();
            var numberOfGaps = info.countOfNumberYetToBeFilled(column);
            for (int i = 0; i < numberOfGaps; i++) {
                var ticketEntry = sortedTickets.get(i % sortedTickets.size());
                for (var row : info.rowsWithLeastNumbers(column, ticketEntry.getKey())) {
                    if (strip[column][row] != 0) {
                        continue;
                    }
                    rowNumbersToBeFilled.add(row);
                    strip[column][row] = info.addPlaceholder(column, row);
                    break;
                }
            }

            exchangePlaceholdersWithActualNumbers(strip, column, rowNumbersToBeFilled);
        }
    }

    private void exchangePlaceholdersWithActualNumbers(int[][] strip, int column, List<Integer> rowNumbersToBeFilled) {
        rowNumbersToBeFilled.sort(Integer::compareTo);
        var number = column * 10;
        for (var row : rowNumbersToBeFilled) {
            strip[column][row] = number;
            number++;
        }
    }
}
