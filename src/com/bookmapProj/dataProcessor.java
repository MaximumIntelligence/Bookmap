package com.bookmapProj;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class dataProcessor {
    public void dataProcessor() {
        //TreeMap для числовых значений bid и ask заявок
        TreeMap<Integer, Integer> bidMap = new TreeMap<>();
        TreeMap<Integer, Integer> askMap = new TreeMap<>();

        File file = new File("src/com/bookmapProj/inputFile");
        File outPutFile = new File("src/com/bookmapProj/outputFile");

        BufferedWriter bw = null;
        BufferedReader br = null;
        FileReader fr = null;
        FileWriter fw = null;
        String currentLine = null;
        Scanner scn = null;

        try {
            fr = new FileReader(file);
            fw = new FileWriter(outPutFile);
            bw = new BufferedWriter(fw);
            br = new BufferedReader(fr);
            scn = new Scanner(file);

            while (scn.hasNextLine()) {
                currentLine = br.readLine();
                if (currentLine != null) {
                    //Если текущая считанная линия содержит символы u, bid, то создаём массив String, записываем
                    //строку по разделителю ",". После выцепляем 1 и 2 элемент (числа) для дальнейшей записи в bidMap
                    if (currentLine.contains("u") && currentLine.contains("bid")) {
                        String[] buffer = currentLine.split(",");
                        String price = buffer[1];
                        String shares = buffer[2];

                        int intPrice = Integer.parseInt(price);
                        int intShares = Integer.parseInt(shares);
                        int newShares = intShares;

                        //Если считали запись с ценой, с которой существует запись в TreeMap, то увеличиваем кол-во
                        //акций для этой записи. Иначе просто записываем значения как новые
                        if (bidMap.containsKey(intPrice)) {
                            newShares = newShares + (bidMap.get(intPrice));
                            bidMap.put(intPrice, newShares);
                        } else
                            bidMap.put(intPrice, intShares);
                    }
                    //Если текущая считанная линия содержит символы u, ask, то создаём массив String, записываем
                    //строку по разделителю ",". После выцепляем 1 и 2 элемент (числа) для дальнейшей записи в askMap
                    if (currentLine.contains("u") && currentLine.contains("ask")) {
                        String[] buffer = currentLine.split(",");
                        String price = buffer[1];
                        String shares = buffer[2];

                        int intPrice = Integer.parseInt(price);
                        int intShares = Integer.parseInt(shares);
                        int newShares = intShares;

                        //Если считали запись с ценой, с которой существует запись в TreeMap, то увеличиваем кол-во
                        //акций для этой записи. Иначе просто записываем значения как новые
                        if (askMap.containsKey(intPrice)) {
                            newShares = newShares + askMap.get(intShares);
                            askMap.put(intPrice, newShares);
                        } else
                            askMap.put(intPrice, intShares);
                    }
                    //Достаём из bidMap отдельно ключ и значение для дальнейшей форматированной записи в файл
                    if (currentLine.contains("q,best_bid")) {
                        Map.Entry<Integer, Integer> entry = bidMap.lastEntry();

                        String bid_KeyToWrite = String.valueOf(entry.getKey());
                        String bid_ValueToWrite = String.valueOf(entry.getValue());

                        bw.write(bid_KeyToWrite.concat("," + bid_ValueToWrite));
                        bw.newLine();
                        bw.flush();

                    }
                    //Достаём из askMap отдельно ключ и значение для дальнейшей форматированной записи в файл
                    if (currentLine.contains("q,best_ask")) {
                        Map.Entry<Integer, Integer> entry = askMap.firstEntry();

                        String ask_KeyToWrite = String.valueOf(entry.getKey());
                        String ask_ValueToWrite = String.valueOf(entry.getValue());

                        bw.write(ask_KeyToWrite.concat("," + ask_ValueToWrite));
                        bw.newLine();
                        bw.flush();
                    }

                    if (currentLine.contains("q,size")) {
                        String[] buffer = currentLine.split(",");
                        String price = buffer[2];
                        int intPrice = Integer.parseInt(price);
                        //Если находим запись по ключу в bidMap, то получаем значение тоже. После пишем в файл
                        if (bidMap.containsKey(intPrice)) {
                            Map.Entry entry = bidMap.ceilingEntry(intPrice);
                            String bidToWrite = String.valueOf(entry.getValue());
                            bw.write(bidToWrite);
                            bw.newLine();
                            bw.flush();
                        }
                        //Если находим запись по ключу в askMap, то получаем значение тоже. После пишем в файл
                        if (askMap.containsKey(intPrice)) {
                            Map.Entry entry = askMap.ceilingEntry(intPrice);
                            String askToWrite = String.valueOf(entry.getValue());
                            bw.write(askToWrite);
                            bw.newLine();
                            bw.flush();
                        }
                    }

                    if (currentLine.contains("o,buy")) {
                        String[] buffer = currentLine.split(",");
                        String shares = buffer[2];
                        int intShares = Integer.parseInt(shares);
                        //Находим запись в askMap со значением intShares, после вычитаем с текущего значения полученное,
                        //тем самым обновляем запись
                        Map.Entry temp = askMap.firstEntry();
                        Integer currentKey = Integer.valueOf((Integer) temp.getKey());
                        Integer currentTempValue = Integer.valueOf((Integer) temp.getValue());
                        Integer currentValue = currentTempValue - intShares;
                        askMap.put(currentKey, currentValue);
                        //Если при вычитании значения получается значение < 0, то мы удаляем текущую запись
                        if(currentValue <= 0) {
                            askMap.remove(currentKey);
                        }
                    }
                    if (currentLine.contains("o,sell")) {
                        String[] buffer = currentLine.split(",");
                        String shares = buffer[2];
                        int intShares = Integer.parseInt(shares);
                        //Находим запись в bidMap со значением intShares, после вычитаем с текущего значения полученное,
                        //тем самым обновляем запись
                        Map.Entry temp = bidMap.lastEntry();
                        Integer currentKey = Integer.valueOf((Integer) temp.getKey());
                        Integer currentTempValue = Integer.valueOf((Integer) temp.getValue());
                        Integer currentValue = currentTempValue - intShares;
                        bidMap.put(currentKey, currentValue);
                        //Если при вычитании значения получается значение < 0, то мы удаляем текущую запись
                        if(currentValue <= 0) {
                            bidMap.remove(currentKey);
                        }
                    }
                }else
                    break;
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

