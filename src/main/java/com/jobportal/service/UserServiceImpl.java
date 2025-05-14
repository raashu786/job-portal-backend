package com.jobportal.service;
import com.jobportal.dto.*;
import com.jobportal.entity.OTP;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repositary.OTPRepository;
import com.jobportal.utility.Data;
import com.jobportal.utility.Utilities;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jobportal.entity.User;
import com.jobportal.repositary.UserRepositary;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service(value="userService")


public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepositary userRepositary;
	@Autowired
	private OTPRepository otpRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private NotificationService notificationService;

	@Override
	public UserDTO registerUser(UserDTO userDTO) throws JobPortalException {
		Optional<User> optional = userRepositary.findByEmail(userDTO.getEmail());
		if(optional.isPresent()) throw new JobPortalException("User_Found");
		userDTO.setProfileId(profileService.createProfile(userDTO.getEmail(), userDTO.getMobile()));
		userDTO.setId(Utilities.getNextSequence("users"));
		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		User user = userDTO.toEntity();
		user = userRepositary.save(user);
		return user.toDTO();
	}


	@Override
	public UserDTO getUserByEmail(String email) throws JobPortalException {
		return userRepositary.findByEmail(email).orElseThrow(()->new JobPortalException("User_not_found")).toDTO();
	}

	@Override
	public UserDTO getUserByMobile(String mobile) throws JobPortalException {
		return userRepositary.findByMobile(mobile).orElseThrow(()->new JobPortalException("User_not_found")).toDTO();
	}

	@Override
	public UserDTO loginUser(LoginDTO loginDTO) throws JobPortalException {
		User user=userRepositary.findByEmail(loginDTO.getEmail()).orElseThrow(()->new JobPortalException("User_not_found"));
		if(!passwordEncoder.matches(loginDTO.getPassword(),user.getPassword()))throw new JobPortalException("Invalid_credential");
		return user.toDTO();
	}
	@Override
	public UserDTO loginUserWithOtpMobile(LoginWithOtpMobileDTO loginWithOtpMobileDTO) throws JobPortalException {
		User user=userRepositary.findByMobile(loginWithOtpMobileDTO.getMobile()).orElseThrow(()->new JobPortalException("User_not_found"));
		return user.toDTO();
	}

	@Override
	public UserDTO loginUserWithOtpEmail(LoginWithOtpEmailDTO loginWithOtpEmailDTO) throws JobPortalException {
		User user=userRepositary.findByMobile(loginWithOtpEmailDTO.getEmail()).orElseThrow(()->new JobPortalException("User_not_found"));
		return user.toDTO();
	}



	@Override
	public boolean sendOtp(String email) throws Exception {
		   User  user = userRepositary.findByEmail(email).orElseThrow(()->new Exception("This Email Id is not Registered."));
		   MimeMessage mm= mailSender.createMimeMessage();
		   MimeMessageHelper message= new MimeMessageHelper (mm,true);
		   message.setTo(email);
		   message.setSubject("Your OTP Code Sent");
		   String genOtp = Utilities.generateOTP();
		   OTP otp = new OTP(email, genOtp, LocalDateTime.now());
		   otpRepository.save(otp);
		   message.setText(Data.getMessageBody(genOtp, user.getName()), true);
		   mailSender.send(mm);
		   return true;
	}


	@Override
	public boolean sendOtpToMobile(String mobile) throws Exception {
		if (userRepositary.existsByMobile(mobile)) {
			throw new Exception("Mobile number is already registered.");
		}
		String genOtp = Utilities.generateOTP();
		String rawMessage = genOtp + " is your OTP/Verification code for YBREAK and is valid for 5 minutes. -YBREAK";
		URI uri = new URI("https://www.smsgatewayhub.com/api/mt/SendSMS" +
				"?APIKey=Eeg0nyazCkS0cOAWmqkQfw" +
				"&senderid=YBREAK" +
				"&channel=2" +
				"&DCS=0" +
				"&flashsms=0" +
				"&number=" + mobile +
				"&text=" + URLEncoder.encode(rawMessage, StandardCharsets.UTF_8.toString()) +
				"&route=1" +
				"&EntityId=1701170538820030839" +
				"&dlttemplateid=1707170659631489875");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
		OTP otp = new OTP(mobile, genOtp, LocalDateTime.now());
		otpRepository.save(otp);
		return true;
	}
	@Override
	public boolean sendOtpToMobileReg(String mobile) throws Exception {
		User  user = userRepositary.findByMobile(mobile).orElseThrow(()->new Exception("This Mobile number is not Registered."));
		String genOtp = Utilities.generateOTP();
		String rawMessage = genOtp + " is your OTP/Verification code for YBREAK and is valid for 5 minutes. -YBREAK";
		URI uri = new URI("https://www.smsgatewayhub.com/api/mt/SendSMS" +
				"?APIKey=Eeg0nyazCkS0cOAWmqkQfw" +
				"&senderid=YBREAK" +
				"&channel=2" +
				"&DCS=0" +
				"&flashsms=0" +
				"&number=" + mobile +
				"&text=" + URLEncoder.encode(rawMessage, StandardCharsets.UTF_8.toString()) +
				"&route=1" +
				"&EntityId=1701170538820030839" +
				"&dlttemplateid=1707170659631489875");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
		OTP otp = new OTP(mobile, genOtp, LocalDateTime.now());
		otpRepository.save(otp);
		return true;
	}
	@Override
	public boolean verifyOtpForMobile(String mobile, String otp) throws JobPortalException {
		OTP otpEntity = otpRepository.findById(mobile)
				.orElseThrow(() -> new JobPortalException("Otp_not_found"));
		if (!otpEntity.getOtpCode().trim().equalsIgnoreCase(otp.trim())) {
			throw new JobPortalException("otp_is_wrong");
		}
		return true;
	}
	@Override
	public boolean sendOtpToEmail(String email) throws Exception {
		if (userRepositary.existsByEmail(email)) {
			throw new Exception("This Email ID is already registered.");
		}
		MimeMessage mm= mailSender.createMimeMessage();
		MimeMessageHelper message= new MimeMessageHelper (mm,true);
		message.setTo(email);
		message.setSubject("Your OTP Code Sent");
		String genOtp = Utilities.generateOTP();
		OTP otp = new OTP(email, genOtp, LocalDateTime.now());
		otpRepository.save(otp);
		message.setText(Data.getMessageBody(genOtp, "Akhsh Society"), true);
		mailSender.send(mm);
		return true;
	}
	@Override
	public boolean verifyOtp(String email, String otp) throws JobPortalException {
		OTP otpEntity = otpRepository.findById(email)
				.orElseThrow(() -> new JobPortalException("Otp_not_found"));
		if (!otpEntity.getOtpCode().trim().equalsIgnoreCase(otp.trim())) {
			throw new JobPortalException("otp_is_wrong");
		}
		return true;
	}
	@Override
	public ResponseDTO changePassword(LoginDTO loginDTO) throws JobPortalException {
		User user = null;

		if (loginDTO.getEmail() != null && !loginDTO.getEmail().isEmpty()) {
			user = userRepositary.findByEmail(loginDTO.getEmail())
					.orElseThrow(() -> new JobPortalException("This Email Id is not registered"));
		} else if (loginDTO.getMobile() != null && !loginDTO.getMobile().isEmpty()) {
			user = userRepositary.findByMobile(loginDTO.getMobile())
					.orElseThrow(() -> new JobPortalException("This Phone number is not registered"));
		} else {
			throw new JobPortalException("Email or Mobile must be provided.");
		}

		user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
		userRepositary.save(user);

		NotificationDTO noti = new NotificationDTO();
		noti.setUserId(user.getId());
		noti.setMessage("Password Reset Successful");
		noti.setAction("Password Reset");
		notificationService.sendNotification(noti);

		return new ResponseDTO("Password Changed Successfully.");
	}




	@Scheduled(fixedRate = 60000)
	public void removeExpiredOtp() {
		LocalDateTime expiry = LocalDateTime.now().minusMinutes(5);
        List<OTP> expiredOTPs = otpRepository.findByCreationTimeBefore(expiry);
        if(!expiredOTPs.isEmpty()) {
			otpRepository.deleteAll(expiredOTPs);
			System.out.println("Removed "+expiredOTPs.size()+"Expired OTPs.");
		}
	}
}
