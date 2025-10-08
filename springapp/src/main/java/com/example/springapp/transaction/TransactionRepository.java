package com.example.springapp.transaction;

import com.example.springapp.account.Account;
import com.example.springapp.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findAllByUser(UserEntity user);


    List<Transaction> findAllByAccount(Account account);
    
    @NativeQuery("select * from transaction where category_category_id=?1")
    List<Transaction> findByCategory(Integer id);

    @NativeQuery("""
            SELECT
                subquery.month,
                COALESCE(expenses, 0) AS expenses,
                COALESCE(income, 0) AS income
            FROM (
                SELECT
                    MONTHNAME(DATE_SUB(DATE_FORMAT(NOW(), '%Y-%m-01'), INTERVAL n.num MONTH)) AS month,
                    ROW_NUMBER() OVER (ORDER BY n.num DESC) AS rn
                FROM
                    (SELECT 0 AS num UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS n
            ) AS subquery
            LEFT JOIN (
                SELECT
                    MONTHNAME(FROM_UNIXTIME(t.date_time/1000)) AS month,
                    SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) AS expenses,
                    SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) AS income
                FROM transaction t
                JOIN category c ON t.category_category_id = c.category_id
                WHERE
                    t.user_id = ?1 AND
                    FROM_UNIXTIME(t.date_time/1000) >= DATE_SUB(DATE_FORMAT(NOW(), '%Y-%m-01'), INTERVAL 5 MONTH)
                GROUP BY month
            ) AS data ON subquery.month = data.month
            ORDER BY subquery.rn DESC;\
            """)
    List<Object[]> getMonthlyData(Integer userId);


    @NativeQuery("SELECT " +
            "c.name AS category, " +
            "COALESCE(SUM(t.amount), 0) AS expenses " +
            "FROM " +
            "transaction t " +
            "JOIN category c ON t.category_category_id = c.category_id " +
            "WHERE " +
            "t.user_id = ?1 " +
            "AND c.type = 'expense' " +
            "AND MONTH(FROM_UNIXTIME(t.date_time/1000)) = MONTH(NOW()) " +
            "AND YEAR(FROM_UNIXTIME(t.date_time/1000)) = YEAR(NOW()) " +
            "GROUP BY " +
            "c.name " +
            "ORDER BY " +
            "expenses DESC;")
    List<Object[]> getThisMonthExpenses(Integer userId);

    @NativeQuery("SELECT " +
            "c.name AS category, " +
            "COALESCE(SUM(t.amount), 0) AS income " +
            "FROM " +
            "transaction t " +
            "JOIN category c ON t.category_category_id = c.category_id " +
            "WHERE " +
            "t.user_id = ?1 " +
            "AND c.type = 'income' " +
            "AND MONTH(FROM_UNIXTIME(t.date_time/1000)) = MONTH(NOW()) " +
            "AND YEAR(FROM_UNIXTIME(t.date_time/1000)) = YEAR(NOW()) " +
            "GROUP BY " +
            "c.name " +
            "ORDER BY " +
            "income DESC;")
    List<Object[]> getThisMonthIncome(Integer userId);

    @NativeQuery("""
            SELECT
                COALESCE(SUM(CASE WHEN c.type = 'expense' THEN t.amount END), 0) AS total_expenses,
                COALESCE(SUM(CASE WHEN c.type = 'income' THEN t.amount END), 0) AS total_income
            FROM
                transaction t
                JOIN category c ON t.category_category_id = c.category_id
            WHERE
                t.user_id = ?1
                AND MONTH(FROM_UNIXTIME(t.date_time/1000)) = MONTH(NOW())
                AND YEAR(FROM_UNIXTIME(t.date_time/1000)) = YEAR(NOW());\
            """)
    List<Object[]> getThisMonthTotalIncomeAndExpenses(Integer userId);
}
