package com.example.springapp.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {

    @NativeQuery("SELECT budget.id, budget.amount, budget.category_id, budget.user_id, " +
            "(SELECT SUM(transaction.amount) " +
            "FROM transaction WHERE transaction.user_id = ?1 && budget.category_id = transaction.category_category_id " +
            "&& transaction.date_time >= UNIX_TIMESTAMP('2023-07-01 00:00:00') * 1000 " +
            "AND transaction.date_time <= UNIX_TIMESTAMP('2023-07-31 00:00:00') * 1000 ) AS used, " +
            "(budget.amount - (SELECT SUM(transaction.amount) " +
            "FROM transaction WHERE transaction.user_id = ?1 && budget.category_id = transaction.category_category_id " +
            "&& transaction.date_time >= UNIX_TIMESTAMP('2023-07-01 00:00:00') * 1000 " +
            "AND transaction.date_time <= UNIX_TIMESTAMP('2023-07-31 00:00:00') * 1000 )) AS balance " +
            "FROM budget WHERE budget.user_id=?1")
    List<Budget> findAllByUser(Integer user_id);


}
