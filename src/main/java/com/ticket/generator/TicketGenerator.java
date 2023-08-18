package com.ticket.generator;

import java.util.Random;
import java.util.stream.IntStream;

public class TicketGenerator {

    private static Random random = new Random();

    public static void main(String args[]) {
        System.out.println("Generating 10.000 strips ....");
        var start = System.currentTimeMillis();
        IntStream.range(0, 10_000).parallel().forEach(i -> new Strip().generate());
        System.out.println("Done in  " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Generating sample strip");

        var strip = new com.ticket.generator.Strip().generate();
        printStrip(strip);
    }

    private static void printStrip(int[][] strip) {
        for (int row = 0; row < 18; row++) {
            if (row % 3 == 0) {
                System.out.println("---------------------------");
            }
            for (int column = 0; column < 9; column++) {
                if (strip[column][row] != 0) {
                    System.out.printf("%2d ", strip[column][row]);
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

}
