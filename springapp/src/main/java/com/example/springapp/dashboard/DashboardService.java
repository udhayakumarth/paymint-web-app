package com.example.springapp.dashboard;

import com.example.springapp.transaction.TransactionRepository;
import com.example.springapp.user.UserEntity;
import com.example.springapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public List<Map<String, Object>> getMonthlyData(String userName) {
        UserEntity user = userRepository.findByEmail(userName).orElseThrow();
        return convertMonthlyData(transactionRepository.getMonthlyData(user.getUserId()));
    }

    public List<Map<String, Object>> convertMonthlyData(List<Object[]> queryResult) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] o: queryResult
             ) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("month", o[0]);
            temp.put("expenses", o[1]);
            temp.put("income", o[2]);
            result.add(temp);
        }
        return result;
    }

    public List<Map<String, Object>> convertThisMonthExpenses(List<Object[]> queryResult) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] o: queryResult
        ) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("category", o[0]);
            temp.put("expenses", o[1]);
            result.add(temp);
        }
        return result;
    }

    public List<Map<String, Object>> convertThisMonthIncome(List<Object[]> queryResult) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] o: queryResult
        ) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("category", o[0]);
            temp.put("income", o[1]);
            result.add(temp);
        }
        return result;
    }

    public Map<String, Object> convertThisMonthTotalIncomeAndExpenses(List<Object[]> queryResult) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (queryResult == null || queryResult.isEmpty()) {
            result.put("total_expenses", 0);
            result.put("total_income", 0);
            return result;
        }

        Object[] row = queryResult.get(0);

        result.put("total_expenses", row[0]);
        result.put("total_income", row[1]);
        return result;
    }


    public List<Map<String, Object>> getThisMonthExpenses(String userName) {
        UserEntity user = userRepository.findByEmail(userName).orElseThrow();
        return convertThisMonthExpenses(transactionRepository.getThisMonthExpenses(user.getUserId()));
    }

    public List<Map<String, Object>> getThisMonthIncome(String userName) {
        UserEntity user = userRepository.findByEmail(userName).orElseThrow();
        return convertThisMonthIncome(transactionRepository.getThisMonthIncome(user.getUserId()));
    }

    public Map<String, Object> getThisMonthTotalIncomeAndExpenses(String userName) {
        UserEntity user = userRepository.findByEmail(userName).orElseThrow();
        return convertThisMonthTotalIncomeAndExpenses(transactionRepository.getThisMonthTotalIncomeAndExpenses(user.getUserId()));
    }
}
