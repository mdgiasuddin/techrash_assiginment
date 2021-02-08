package com.example.techrash;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    private DataFormat getDetailsFromUrl(String url) {
        DataFormat dataFormat = new DataFormat();
        try {
            String content = Jsoup.connect(url).get().text();
            int starting = content.indexOf("Date Open High Low Close* Adj Close** Volume");
            int ending = content.indexOf(" *Close price adjusted for splits.**");

            if (starting == -1 || ending == -1)
                throw new NoDataFoundException("No Data Found!");

            content = content.substring(starting, ending);

            String headerPart = content.substring(0, 45);
            content = content.substring(45);
            String[] splitted = headerPart.split(" ");
            String[] header = new String[]{splitted[0], splitted[1], splitted[2], splitted[3], splitted[4]
                    , splitted[5] + " " + splitted[6], splitted[7]};

            List<String[]> otherRowList = new ArrayList<>();

            String[] splittedRowList = content.split("-");
            for (String splittedRow : splittedRowList) {
                splitted = splittedRow.split(" ");

                String[] otherRow = new String[]{splitted[0] + " " + splitted[1] + " " + splitted[2]
                        , splitted[3], splitted[4], splitted[5], splitted[6], splitted[7], "-"};
                otherRowList.add(otherRow);

            }
            dataFormat.setHeader(header);
            dataFormat.setOtherRowList(otherRowList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataFormat;
    }

    private void createExcelFile(final DataFormat dataFormat) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet");

            XSSFCellStyle headerCellStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            headerCellStyle.setFont(font);

            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            XSSFColor color = new XSSFColor(new Color(230, 200, 200));
            headerCellStyle.setFillForegroundColor(color);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0, colNum = 0;
            Row row = sheet.createRow(rowNum);
            for (String string : dataFormat.getHeader()) {
                Cell cell = row.createCell(colNum);
                cell.setCellValue(string);
                cell.setCellStyle(headerCellStyle);
                colNum++;
            }
            rowNum++;

            for (String[] otherRow : dataFormat.getOtherRowList()) {
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

    private void createCSVFile(final DataFormat dataFormat) {
        String fileName = "src/resources/CBOE.csv";
        File file = new File(fileName);

        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);

            List<String[]> data = new ArrayList();
            data.add(dataFormat.getHeader());
            data.addAll(dataFormat.getOtherRowList());
            writer.writeAll(data);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void generateCSVFileFromWeb() throws NoDataFoundException {
        String url = "https://finance.yahoo.com/quote/%5EVIX/history?p=^VIX";
        DataFormat dataFormat = getDetailsFromUrl(url);

        if (dataFormat.getHeader() == null || dataFormat.getOtherRowList() == null
                || dataFormat.getHeader().length == 0 || dataFormat.getOtherRowList().isEmpty())
            throw new NoDataFoundException("No Data Found!");

        //Excel Format
        createExcelFile(dataFormat);

        //CSV Format
        createCSVFile(dataFormat);
    }
}
