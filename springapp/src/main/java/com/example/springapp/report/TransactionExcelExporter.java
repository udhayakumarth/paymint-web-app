package com.example.springapp.report;

import com.example.springapp.transaction.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Transaction> transactionList;

    public TransactionExcelExporter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Transactions");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Transaction ID", style);
        createCell(row, 1, "Date", style);
        createCell(row, 2, "Time", style);
        createCell(row, 3, "Amount", style);
        createCell(row, 4, "Category", style);
        createCell(row, 5, "Type", style);
        createCell(row, 6, "Description", style);
        createCell(row, 7, "Account", style);
        createCell(row, 8, "Payment Type", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer integer) {
            cell.setCellValue(integer);
        } else if (value instanceof Boolean boolean1) {
            cell.setCellValue(boolean1);
        }else if (value instanceof Long long1){
            cell.setCellValue(long1);
        }else if (value instanceof Double double1){
            cell.setCellValue(double1);
        }else  {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Transaction transaction : transactionList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            Instant instant = Instant.ofEpochMilli(transaction.getDateTime());
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedDate = dateTime.format(dateFormatter);
            String formattedTime = dateTime.format(timeFormatter);

            createCell(row, columnCount++, transaction.getId(), style);
            createCell(row, columnCount++, formattedDate, style);
            createCell(row, columnCount++, formattedTime, style);
            createCell(row, columnCount++, transaction.getAmount(), style);
            createCell(row, columnCount++, transaction.getCategory().getName(), style);
            createCell(row, columnCount++, transaction.getCategory().getType(), style);
            createCell(row, columnCount++, transaction.getDescription(), style);
            createCell(row, columnCount++, transaction.getAccount().getName(), style);
            createCell(row, columnCount++, transaction.getPaymentType(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

}
