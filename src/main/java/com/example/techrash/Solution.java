package com.example.techrash;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {

    private DataFormat getDetailsFromUrl(String url) {
        DataFormat dataFormat = new DataFormat();
        try {
            String content = Jsoup.connect(url).get().text();
            System.out.println("ContentFirst : " + content);
            int starting = content.indexOf("Date Open High Low Close* Adj Close** Volume");
            int ending = content.indexOf(" *Close price adjusted for splits.**");
            content = content.substring(starting, ending);

            String headerPart = content.substring(0, 45);
            System.out.println("Header : " + headerPart);
            content = content.substring(45);
            System.out.println("Content : " + content);
            String[] splitted = headerPart.split(" ");
            List<String> header = Arrays.asList(splitted[0], splitted[1], splitted[2]
                    , splitted[3], splitted[4], splitted[5] + " " + splitted[6], splitted[7]);

            List<List<String>> otherRowList = new ArrayList<>();

            String[] splittedRowList = content.split("-");
            for (String splittedRow : splittedRowList) {
                splitted = splittedRow.split(" ");

                List<String> otherRow = Arrays.asList(splitted[0] + " " + splitted[1] + " " + splitted[2]
                        , splitted[3], splitted[4], splitted[5], splitted[6], splitted[7], "-");
                otherRowList.add(otherRow);

            }
            dataFormat.setHeader(header);
            dataFormat.setOtherRowList(otherRowList);

            System.out.println(dataFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataFormat;
    }

    private void createCSVFile(DataFormat dataFormat) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet");

            int rowNum = 0, colNum = 0;
            Row row = sheet.createRow(rowNum);
            for (String string : dataFormat.getHeader()) {
                row.createCell(colNum).setCellValue(string);
                colNum++;
            }
            rowNum++;

            for (List<String> otherRow : dataFormat.getOtherRowList()) {
                colNum = 0;
                row = sheet.createRow(rowNum);
                for (String string : otherRow) {
                    row.createCell(colNum).setCellValue(string);
                    colNum++;
                }
                rowNum++;
            }
            String fileName = "src/resources/CBOE.xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCSVFileFromWeb() {
        String url = "https://finance.yahoo.com/quote/%5EVIX/history?p=^VIX";
        DataFormat dataFormat = getDetailsFromUrl(url);
        createCSVFile(dataFormat);
    }
}
