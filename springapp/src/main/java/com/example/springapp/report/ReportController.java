package com.example.springapp.report;

import com.example.springapp.config.auth.JWTGenerator;
import com.example.springapp.transaction.Transaction;
import com.example.springapp.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ReportController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    JWTGenerator jwtGenerator;

    @GetMapping("/api/report/transaction/excel")
    public void transactionReportExcel(@RequestHeader(value = "Authorization", defaultValue = "") String token,HttpServletResponse httpServletResponse) throws IOException {
        String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
        httpServletResponse.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        httpServletResponse.setHeader(headerKey, headerValue);

        List<Transaction> transactionList = transactionService.getTransactionsByUserName(userName);

        TransactionExcelExporter transactionExcelExporter = new TransactionExcelExporter(transactionList);

        transactionExcelExporter.export(httpServletResponse);

    }
}
