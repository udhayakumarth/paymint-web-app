package com.example.springapp.debt;

import com.example.springapp.user.UserEntity;
import com.example.springapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;
@Transactional
@Service
public class DebtService {

    @Autowired
    private DebtRepo debtR;
    @Autowired
    UserRepository userRepository;
    public DebtEntity debtCreate(DebtEntity deb,String uName) {
        try{
            UserEntity user = userRepository.findByEmail(uName).orElseThrow();
            deb.setUser(user);
        }catch (Exception ignored){

        }
        return debtR.save(deb);
    }

    public DebtEntity debtUpdate(DebtEntity deb, Integer debtId) {
        DebtEntity debt = debtR.findById(debtId).get();

        if(!"0".equalsIgnoreCase(String.valueOf(deb.getAmount()))){
            debt.setAmount(deb.getAmount());
        }
        if(Objects.nonNull(deb.getMoneyFrom()) &&
                !"".equalsIgnoreCase(deb.getMoneyFrom())){
            debt.setMoneyFrom(deb.getMoneyFrom());
        }
        if(Objects.nonNull(deb.getStatus()) &&
                !"".equalsIgnoreCase(deb.getStatus())){
            debt.setStatus(deb.getStatus());
        }
        if(Objects.nonNull(deb.getDueDate()) &&
                !"".equalsIgnoreCase(deb.getDueDate())){
            debt.setDueDate(deb.getDueDate());
        }
        return debtR.save(debt);
    }

    public String debtDelete(Integer dId) {
        debtR.deleteById(dId);
        return "Deleted";
    }

    public List<DebtEntity> debtGet(String uName,Integer value) {
        try {
            UserEntity user = userRepository.findByEmail(uName).orElseThrow();
            if(value==1){
                return debtR.findAllByUserOrderByAmountDesc(user);
            } else if (value==2) {
                List<DebtEntity> debts = debtR.findAllByUser(user);

//                return  debtR.findAllByUserOrderByDueDateAsc(user);
                return debts.stream()
                        .sorted(Comparator.comparing(debt -> parseDueDate(debt.getDueDate())))
                        .collect(Collectors.toList());
            }
            return debtR.findAllByUser(user);
        } catch (Exception e){
            return null;
        }
    }
    private Date parseDueDate(String dueDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            return formatter.parse(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<DebtEntity> getAllDebts() {
        return debtR.findAll();
    }

    public  DebtEntity debtGetId(Integer dId ){
        return debtR.findById(dId).get();
    }
}
