package com.example.springapp.user;

import com.example.springapp.BaseResponceDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.springapp.config.auth.LoginDto;
import com.example.springapp.config.auth.JWTGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
public class UserController {
	
	private UserService userService;
    private UserRepository userRepository;
    private JWTGenerator jwtGenerator;
	private OTPStorage otpStorage;

    public UserController(UserRepository userRepository, JWTGenerator jwtGenerator, UserService userService,OTPStorage otpStorage) {
    	this.userService = userService;
        this.userRepository = userRepository;
        this.jwtGenerator = jwtGenerator;
		this.otpStorage = otpStorage;
    }

	@PostMapping("/api/auth/register")
	public ResponseEntity<BaseResponceDto> register(@RequestBody UserEntity user) {
		return userService.register(user);
	}

	@PostMapping("/api/auth/login")
	public ResponseEntity<BaseResponceDto> login(@RequestBody LoginDto user) {		
		return userService.login(user);
	}

	@GetMapping("/api/auth/validateToken")
	public ResponseEntity<BaseResponceDto> home(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
		Map<Object,Object> data = new HashMap<>();
		if(jwtGenerator.validateToken(jwtGenerator.getTokenFromHeader(token))) {
			Optional<UserEntity> user = userRepository.findByEmail(jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token)));
			data.put("user",user);
			return ResponseEntity.ok(new BaseResponceDto("success",data));
		}
		return new ResponseEntity<>(new BaseResponceDto("Session Expired",data), HttpStatus.UNAUTHORIZED);
	}

	@PostMapping("/api/profile/image")
	public ResponseEntity<BaseResponceDto> updateProfilePicture(@RequestHeader(value = "Authorization", defaultValue = "") String token,@ModelAttribute ProfileImageDto profileImageDto){
		try {
			String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
			userService.updateUserProfileImage(profileImageDto,userName);
			return ResponseEntity.ok(new BaseResponceDto("success"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponceDto("Failed to update user profile image."));
		}
	}

	@PostMapping("/api/profile/name")
	public ResponseEntity<BaseResponceDto> updateProfileName(@RequestHeader(value = "Authorization", defaultValue = "") String token,@RequestBody ProfileNameDto profileNameDto){
		try {
			String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
			userService.updateUserProfileName(profileNameDto,userName);
			return ResponseEntity.ok(new BaseResponceDto("success"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponceDto("Failed to update user profile name."));
		}
	}

	@PostMapping("/api/profile/email")
	public ResponseEntity<BaseResponceDto> updateProfileName(@RequestHeader(value = "Authorization", defaultValue = "") String token,@RequestBody ProfileEmailDto profileEmailDto){
		try {
			String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
			userService.updateUserProfileEmail(profileEmailDto,userName);
			return ResponseEntity.ok(new BaseResponceDto("success"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponceDto("Failed to update user profile email."));
		}
	}


	@PostMapping("/api/auth/send-verification-email")
	public ResponseEntity<BaseResponceDto> sendVerificationEmail(@RequestParam(value = "email") String email){
		try{
			if(userRepository.existsByEmail(email)) {
				return ResponseEntity.badRequest().body(new BaseResponceDto("User already exists", null));
			}
			userService.sendVerificationEmail(email);
			return ResponseEntity.ok(new BaseResponceDto("success"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BaseResponceDto("Failed Try again"));
		}
	}

	@PostMapping("/api/auth/verify-security-code")
	public ResponseEntity<BaseResponceDto> verifyOTP(@RequestParam(value = "email") String email, @RequestParam(value = "otp") String otp) {
		String storedOTP = otpStorage.getOTP(email);
		if (storedOTP == null || !storedOTP.equals(otp)) {
			return ResponseEntity.badRequest().body(new BaseResponceDto("Invalid OTP"));
		}
		otpStorage.removeOTP(email);
		return ResponseEntity.ok(new BaseResponceDto("OTP verified successfully"));
	}

	@PostMapping("/api/auth/forgot-password/send-verification-email")
	public ResponseEntity<BaseResponceDto> forgetPasswordSendVerificationEmail(@RequestParam(value = "email") String email){
		try{
			userService.sendVerificationEmail(email);
			return ResponseEntity.ok(new BaseResponceDto("success"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BaseResponceDto("Failed Try again"));
		}
	}

	@PutMapping("/api/auth/new-password")
	public ResponseEntity<BaseResponceDto> newPassword(@RequestParam(value = "email") String email,@RequestParam(value = "password") String password){
		try {
			userService.newPassword(email, password);
			return ResponseEntity.ok(new BaseResponceDto("success"));
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponceDto("Failed to update user profile password!"));
		}
	}

	@PutMapping("/api/profile/password")
	public ResponseEntity<BaseResponceDto> updatePassword(@RequestHeader(value = "Authorization", defaultValue = "") String token, @RequestBody ProfilePasswordDto profilePasswordDto){
		try {
			String userName = jwtGenerator.getUsernameFromJWT(jwtGenerator.getTokenFromHeader(token));
			return userService.updatePassword(profilePasswordDto, userName);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponceDto("Failed to update user profile password!"));
		}
	}
}