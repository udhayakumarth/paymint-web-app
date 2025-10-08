package com.example.springapp.category;

import com.example.springapp.BaseResponceDto;
import com.example.springapp.transaction.Transaction;
import com.example.springapp.transaction.TransactionRepository;
import com.example.springapp.user.UserEntity;
import com.example.springapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionRepository transactionRepository;
    
    public List<Category> getCategoriesByUserName(String userName){
        try{
            UserEntity user = userRepository.findByEmail(userName).orElseThrow();
            return categoryRepository.findAllByUserId(user);
        }catch (UsernameNotFoundException e){
            return null;
        }


    }

    public String addCategories(Category category, String userName){
        try{
            UserEntity user = userRepository.findByEmail(userName).orElseThrow();
            category.setUserId(user);
            categoryRepository.save(category);
            return "success";
        }catch (UsernameNotFoundException e){
            return e.getMessage();
        }

    }
    public String deleteCategories(int category_TD){
        try{
            Category entity=categoryRepository.getReferenceById(category_TD);
            categoryRepository.delete(entity);
        }catch (Exception e){
            return e.getMessage();
        }
        return "success";

    }

    public Category getCategoryById(Integer id){
        return categoryRepository.findById(id).orElseThrow();
    }

	public ResponseEntity<BaseResponceDto> sortTransaction(Integer categoryId) {
		List<Transaction> transactions = transactionRepository.findByCategory(categoryId);
		return ResponseEntity.ok(new BaseResponceDto("success",transactions));
	}

}
