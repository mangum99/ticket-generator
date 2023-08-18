package com.ticket.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

class ColumnInfo {
    private int numbersToAdd;
    private int numbersAdded;
    private int startingNumber;
    private int currentNumber;
    private final Map<Integer, TicketColumn> ticket = new HashMap<>();

    public ColumnInfo(int column) {
        numbersToAdd = column == 0 ? 9 : column == 8 ? 11 : 10;
        numbersAdded = 0;
        startingNumber = (column == 0 ? 1 : (column * 10));
        currentNumber = startingNumber;

        IntStream.range(0, 6).forEach(ticketNumber -> ticket.put(ticketNumber, new TicketColumn(ticketNumber)));
    }

    public boolean spaceForNumberInTicket(int row) {
        var ticketNumber = (int) Math.floor((float) row / 3);
        return ticket.get(ticketNumber).spaceForNumberInTicket();
    }

    public boolean lastRowWithNoGeneratedNumber(int row) {
        var ticketNumber = (int) Math.floor((float) row / 3);
        return ticket.get(ticketNumber).lastRowWithNoGeneratedNumber(row);
    }

    /**
     * // if count of rows left is equal to count of numbers left
     * (e.g. there is 8,9 left and we are in row 17 then we have to add them)
     *
     * @param row
     * @return
     */
    public boolean mustFillTillEnd(int row) {
        return numbersToAdd - numbersAdded == 18 - row;
    }

    public boolean shouldNumberBeGenerated(int row) {
        // if the count of numbers left can't cover the rest of groups, we can't generate the number
        if (((numbersToAdd - numbersAdded) * 3) <= 17 - row) {
            return false;
        }
        return StripMetaInfo.RANDOM.nextInt(2) == 0;
    }

    public int addNumber(int row) {
        var ticketNumber = (int) Math.floor((float) row / 3);
        numbersAdded++;
        ticket.get(ticketNumber).numberAdded();
        var numberToBeAdded = currentNumber;
        currentNumber++;
        return numberToBeAdded;
    }

    public int addPlaceholder(int row) {
        var ticketNumber = (int) Math.floor((float) row / 3);
        numbersAdded++;
        ticket.get(ticketNumber).numberAdded();
        return -1;
    }

    public boolean anyNumberExists(int ticketNumber) {
        return ticket.get(ticketNumber).anyNumberExists();
    }

    public int countOfNumberYetToBeFilled() {
        return numbersToAdd - numbersAdded;
    }

    class TicketColumn {
        private int number;
        private int numbersInTicket;

        public TicketColumn(int number) {
            this.number = number;
            this.numbersInTicket = 0;
        }

        public boolean spaceForNumberInTicket() {
            return numbersInTicket < 3;
        }

        public boolean lastRowWithNoGeneratedNumber(int row) {
            return numbersInTicket == 0 && isLastRow(row);
        }

        private boolean isLastRow(int row) {
            return (row + 1) % 3 == 0;
        }

        public void numberAdded() {
            numbersInTicket++;
        }

        public boolean anyNumberExists() {
            return numbersInTicket > 0;
        }
    }
}