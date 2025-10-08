package com.example.springapp.category;

import com.example.springapp.BaseResponceDto;
import com.example.springapp.config.auth.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    JWTGenerator jwtGenerator;

    @GetMapping("/api/categories")
    public BaseResponceDto getCategories(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
        String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
        List<Category> categories = categoryService.getCategoriesByUserName(userName);
        return new BaseResponceDto("success", categories);
    }

    @PostMapping("/api/categories")
    public BaseResponceDto addCategories(@RequestHeader(value = "Authorization", defaultValue = "") String token, @RequestBody Category category) {
        String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
        categoryService.addCategories(category, userName);
        return new BaseResponceDto(categoryService.addCategories(category, userName), null);
    }
    @DeleteMapping("/api/categories/{category_id}")
    public BaseResponceDto deleteCourse(@PathVariable String category_id) {
        return new BaseResponceDto(categoryService.deleteCategories(Integer.parseInt(category_id)));
    }
    
    @GetMapping("/api/categories/total-transactions/{id}")
    public ResponseEntity<BaseResponceDto> sortTransaction(@RequestHeader(value = "Authorization", defaultValue = "") String token, @PathVariable("id") Integer categoryId){
    	return categoryService.sortTransaction(categoryId);
    }

}
