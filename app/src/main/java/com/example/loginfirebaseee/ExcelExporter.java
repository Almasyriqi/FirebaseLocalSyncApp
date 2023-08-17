package com.example.loginfirebaseee;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

public class ExcelExporter {
    public void exportToExcel(List<InputData> inputData, String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);

        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        headerStyle.setFont(font);

        int rowNum = 0;
        XSSFRow row = sheet.createRow(rowNum);

        XSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(1);
        cell.setCellValue("Panjang (m)");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(2);
        cell.setCellValue("Latitude");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        cell.setCellValue("Longitude");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(4);
        cell.setCellValue("Timestamp");
        cell.setCellStyle(headerStyle);

        rowNum++;

        for (InputData data : inputData) {
            row = sheet.createRow(rowNum++);
            int colNum = 0;

            cell = row.createCell(colNum++);
            cell.setCellValue(data.getData_Id());
            sheet.setColumnWidth(0, (data.getData_Id().length()) * 256);

            cell = row.createCell(colNum++);
            cell.setCellValue(data.getData_Data());
            sheet.setColumnWidth(1, (data.getData_Data().length()) * 256);

            cell = row.createCell(colNum++);
            cell.setCellValue(data.getLatitude());
            String latitude = String.valueOf(data.getLatitude());
            sheet.setColumnWidth(2, (latitude.length()) * 256);

            cell = row.createCell(colNum++);
            cell.setCellValue(data.getLongitude());
            String longitude = String.valueOf(data.getLongitude());
            sheet.setColumnWidth(3, (longitude.length()) * 256);

            cell = row.createCell(colNum);
            cell.setCellValue(data.getFormattedTimestamp());
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "0");
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
