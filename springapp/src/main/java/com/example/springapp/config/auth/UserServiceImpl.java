package com.example.springapp.config.auth;

import com.example.springapp.BaseResponceDto;
import com.example.springapp.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JWTGenerator jwtGenerator;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private OTPStorage otpStorage;
	@Override
	public ResponseEntity<BaseResponceDto> register(UserEntity user) {
		if(userRepository.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>(new BaseResponceDto("User already exists",null), HttpStatus.BAD_REQUEST);
		}
		
		user.setPassword(passwordEncoder.encode((user.getPassword())));
		userRepository.save(user);
		return new ResponseEntity<>(new BaseResponceDto("success",null), HttpStatus.OK);
	}

	@Override
	public void updateUserProfileImage(ProfileImageDto profileImageDto, String userName) {
		try{
			UserEntity user = userRepository.findByEmail(userName).orElseThrow();
			user.setProfileImage(profileImageDto.getImage().getBytes());
			userRepository.save(user);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateUserProfileName(ProfileNameDto profileNameDto, String userName) {
		UserEntity user = userRepository.findByEmail(userName).orElseThrow();
		user.setFirstName(profileNameDto.getFirstName());
		user.setLastName(profileNameDto.getLastName());
		userRepository.save(user);
	}

	@Override
	public void updateUserProfileEmail(ProfileEmailDto profileEmailDto, String userName) {
		UserEntity user = userRepository.findByEmail(userName).orElseThrow();
		user.setEmail(profileEmailDto.getEmail());
		userRepository.save(user);
	}


	@Override
	public void sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException, MessagingException, UnsupportedEncodingException {
		String fromAddress = "paymint.ltd@outlook.com";
		String senderName = "Paymint Team";
		String subject = "Paymint account security code";
		String content = """
                <div>
                    <span style="color:#808080;padding: 2px;font-family: sans-serif;">Paymint Account</span><br>
                    <span style="color:#5C6AC4;padding: 2px;font-size:32px;font-family: sans-serif;"><b>Security code</b></span><br><br>
                    <span style="font-family: sans-serif;">Please use the following security code for the Paymint account.</span><br><br><br>
                    <span style="font-family: sans-serif;">Security code: <b>[[CODE]]</b></span><br><br><br>
                    <span style="font-family: sans-serif;">Thanks,</span><br>
                    <span style="font-family: sans-serif;">The Paymint Team</span>
                </div>""";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(email);
		helper.setSubject(subject);
		String code = otpStorage.generateOTP(email);
		content = content.replace("[[CODE]]", code);
		helper.setText(content, true);
		mailSender.send(message);
	}

	@Override
	public void newPassword(String email, String password) {
		UserEntity user = userRepository.findByEmail(email).orElseThrow();
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}

	@Override
	public ResponseEntity<BaseResponceDto> updatePassword(ProfilePasswordDto profilePasswordDto, String userName) {
		UserEntity user = userRepository.findByEmail(userName).orElseThrow();
		if(new BCryptPasswordEncoder().matches(profilePasswordDto.getOldPassword(), user.getPassword())) {
			if(new BCryptPasswordEncoder().matches(profilePasswordDto.getPassword(), user.getPassword())) {
				return new ResponseEntity<>(new BaseResponceDto("New Password can't be same as Old Password!",null), HttpStatus.BAD_REQUEST);
			}
			user.setPassword(passwordEncoder.encode((profilePasswordDto.getPassword())));
			userRepository.save(user);
			return new ResponseEntity<>(new BaseResponceDto("Password updated successfully!",null), HttpStatus.OK); 
		}
		return new ResponseEntity<>(new BaseResponceDto("Old Password didn't match!",null), HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<BaseResponceDto> login(LoginDto user) {
		
		UserEntity u = userRepository.findByEmail(user.getEmail()).orElse(null);
		if(!userRepository.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>(new BaseResponceDto("Incorrect Email or Password...",null), HttpStatus.BAD_REQUEST);
		}
		if(!new BCryptPasswordEncoder().matches(user.getPassword(), u.getPassword())) {
			return new ResponseEntity<>(new BaseResponceDto("Incorrect Email or Password...",null), HttpStatus.BAD_REQUEST);
		}
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
		Map<Object,Object> data = new HashMap<>();
		data.put("token",token);
        return ResponseEntity.ok(new BaseResponceDto("success",data));
	}
}
