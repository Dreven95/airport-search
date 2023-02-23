package com.dreven95.airportsearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class AirportSearch {

    private static final String fileName = "airports.csv";
    private static final String delimiter = ",";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Введите: java -jar airports-search.jar <номер столбца>");
            System.exit(1);
        }
        int column = Integer.parseInt(args[0]) - 1;
        List<String[]> data = loadData(fileName);
        System.out.println("Загруженно " + data.size() + " строк");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Введите поисковый запрос (или введите !quit чтобы завершить работу программы): ");
            String term = scanner.nextLine().trim().toLowerCase();
            if (term.equals("!quit")) {
                break;
            }
            long startTime = System.currentTimeMillis();
            List<String[]> matches = search(data, column, term);
            long endTime = System.currentTimeMillis();
            System.out.println("Найденно " + matches.size() + " совпадений за " + (endTime - startTime) + " ms");
            for (String[] match : matches) {
                System.out.print(match[column]);
                System.out.print("[");
                for (int i = 0; i < match.length; i++) {
                    System.out.print(match[i] + " ");
                }
                System.out.print("]");
                System.out.println();
            }
        }
    }

    private static List<String[]> loadData(String filename) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(delimiter);
                data.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить данные из " + filename + ": " + e.getMessage());
            System.exit(1);
        }
        return data;
    }

    private static List<String[]> search(List<String[]> data, int column, String term) {
        List<String[]> matches = new ArrayList<>();
        for (String[] row : data) {
            if (row[column].toLowerCase().startsWith("\"" + term)) {
                matches.add(row);
            }
        }
        Comparator<String[]> comparator = getComparator(column);
        Collections.sort(matches, comparator);
        return matches;
    }

    public static Comparator<String[]> getComparator(int column) {
        return (row1, row2) -> {
            String value1 = row1[column];
            String value2 = row2[column];
            if (value1.startsWith("\"") && value1.endsWith("\"")) {
                value1 = value1.substring(1, value1.length() - 1);
                value2 = value2.substring(1, value2.length() - 1);
                return value1.compareTo(value2);
            }
            else {
                try {
                    double num1 = Double.parseDouble(value1);
                    double num2 = Double.parseDouble(value2);
                    return Double.compare(num1, num2);
                }
                catch (NumberFormatException e) {
                    return value1.compareTo(value2);
                }
            }
        };
    }

}