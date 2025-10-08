package com.example.springapp.account;

import com.example.springapp.transaction.Transaction;
import com.example.springapp.transaction.TransactionService;
import com.example.springapp.user.UserEntity;
import com.example.springapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AccountService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Lazy
    @Autowired
    TransactionService transactionService;

    public void addAccount(Account account, String userName) {
        try{
            UserEntity user = userRepository.findByEmail(userName).orElseThrow();
            account.setUser(user);
            accountRepository.save(account);
        }catch (Exception ignored){

        }
    }

    public List<AccountResponseDto> getAccountsByUsername(String userName) {
        try{
            UserEntity user = userRepository.findByEmail(userName).orElseThrow();
            List<Account> accountList= accountRepository.findAllByUserAndIsDeletedFalse(user);
            List<AccountResponseDto> accountResponseDtoList = new ArrayList<>();
            for (Account account: accountList
                 ) {
                double totalExpenses =0;
                double totalIncome =0;
                List<Transaction> transactionList = transactionService.getTransactionsByAccount(userName,account);
                for (Transaction transaction: transactionList
                     ) {
                    if(transaction.getCategory().getType().equals("expense")){
                        totalExpenses += transaction.getAmount();
                    } else if (transaction.getCategory().getType().equals("income")) {
                        totalIncome += transaction.getAmount();
                    }
                }
                AccountResponseDto accountResponseDto = new AccountResponseDto(
                        account.getAccountId(),
                        account.getName(),
                        account.getCurrentBalance(),
                        account.getPaymentTypes(),
                        totalExpenses,
                        totalIncome
                );
                accountResponseDtoList.add(accountResponseDto);
            }
            return accountResponseDtoList;
        }catch (Exception e){
            return null;
        }
    }

    public void deleteAccount(String accountId) {
        try{
            Account entity= accountRepository.findById(Integer.valueOf(accountId)).orElseThrow();
            entity.setIsDeleted(true);
            accountRepository.save(entity);
        }catch (Exception ignored){
        }
    }

    public boolean hasAccount(String accountId) {
        try{
            Account entity= accountRepository.getReferenceById(Integer.valueOf(accountId));
            return entity.getAccountId() == Integer.parseInt(accountId);
        }catch (Exception ignored){
            return false;
        }
    }

    public boolean hasPermission(String userName, String accountId) {
        try{
            UserEntity user = userRepository.findByEmail(userName).orElseThrow();
            Account entity= accountRepository.getReferenceById(Integer.valueOf(accountId));
            return Objects.equals(entity.getUser().getUserId(), user.getUserId());
        }catch (Exception ignored){
            return false;
        }
    }

    public Account getAccountById(Integer id){
        return accountRepository.findById(id).orElseThrow();
    }


    public void debitBalance(Account account, double amount) {
        account.setCurrentBalance(account.getCurrentBalance()-amount);
        accountRepository.save(account);
    }

    public void creditBalance(Account account, double amount) {
        account.setCurrentBalance(account.getCurrentBalance()+amount);
        accountRepository.save(account);
    }

    public void updateAccount(Account account, Integer accountId) {
        Account acc =  accountRepository.findById(accountId).orElseThrow();
        acc.setCurrentBalance(account.getCurrentBalance());
        acc.setName(account.getName());
        acc.setPaymentTypes(account.getPaymentTypes());
        accountRepository.save(acc);
    }
}
