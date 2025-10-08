package com.example.springapp.user;

import com.example.springapp.BaseResponceDto;
import com.example.springapp.config.auth.LoginDto;

import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface UserService {

	ResponseEntity<BaseResponceDto> register(UserEntity user);

    void updateUserProfileImage(ProfileImageDto profileImageDto, String userName);

    void updateUserProfileName(ProfileNameDto profileNameDto, String userName);

    void updateUserProfileEmail(ProfileEmailDto profileEmailDto, String userName);

    void sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException;

	ResponseEntity<BaseResponceDto> updatePassword(ProfilePasswordDto profilePasswordDto, String userName);

	ResponseEntity<BaseResponceDto> login(LoginDto user);

    void newPassword(String email, String password);
}

