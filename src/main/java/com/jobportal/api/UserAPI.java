package com.jobportal.api;
import com.jobportal.dto.*;
import com.jobportal.exception.JobPortalException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.jobportal.service.UserService;
@RestController
@CrossOrigin
@Validated
@RequestMapping("/users")
public class UserAPI {
	@Autowired
	private UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<UserDTO>registerUser(@RequestBody @Valid UserDTO userDTO) throws JobPortalException {
		
		userDTO = userService.registerUser(userDTO);
		return new ResponseEntity<>(userDTO , HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<UserDTO>loginUser(@RequestBody @Valid LoginDTO loginDTO) throws JobPortalException {
		return new ResponseEntity<>(userService.loginUser(loginDTO) , HttpStatus.OK);
	}
	@PostMapping("/login/with/otp/email")
	public ResponseEntity<UserDTO>loginUserWithOtp(@RequestBody @Valid LoginWithOtpEmailDTO loginWithOtpEmailDTO) throws JobPortalException {
		return new ResponseEntity<>(userService.loginUserWithOtpEmail(loginWithOtpEmailDTO) , HttpStatus.OK);
	}
	@PostMapping("/login/with/otp/mobile")
	public ResponseEntity<UserDTO>loginUserWithOtpMobile(@RequestBody @Valid LoginWithOtpMobileDTO loginWithOtpMobileDTO) throws JobPortalException {
		return new ResponseEntity<>(userService.loginUserWithOtpMobile(loginWithOtpMobileDTO) , HttpStatus.OK);
	}
	@PostMapping("/changePass")
	public ResponseEntity<ResponseDTO>changePassword(@RequestBody @Valid LoginDTO loginDTO) throws JobPortalException {
		return new ResponseEntity<>(userService.changePassword(loginDTO) , HttpStatus.OK);
	}
	@PostMapping("/sendOtp/{email}")
	public ResponseEntity<ResponseDTO>sendOtp(@PathVariable @Email(message="{user.email.invalid}") String email) throws Exception {
		userService.sendOtp(email);
		return new ResponseEntity<>(new ResponseDTO("OTP Sent Successfully"), HttpStatus.OK);
	}
	@GetMapping("/verifyOtp/{email}/{otp}")
	public ResponseEntity<ResponseDTO> verifyOtp(
			@PathVariable String email,
			@PathVariable @Pattern(regexp = "^[0-9]{6}$", message = "{otp.invalid}") String otp)
			throws Exception {
		userService.verifyOtp(email, otp);
		return new ResponseEntity<>(new ResponseDTO("OTP has been Verified"), HttpStatus.OK);
	}
	@PostMapping("/sendOtp/mobile/{mobile}")
	public ResponseEntity<ResponseDTO> sendOtpToMobile(@PathVariable String mobile) throws Exception {
		userService.sendOtpToMobile(mobile);
		return new ResponseEntity<>(new ResponseDTO("OTP Sent Successfully to Mobile"), HttpStatus.OK);
	}

	@PostMapping("/sendOtpReg/mobile/{mobile}")
	public ResponseEntity<ResponseDTO> sendOtpToMobileReg(@PathVariable String mobile) throws Exception {
		userService.sendOtpToMobileReg(mobile);
		return new ResponseEntity<>(new ResponseDTO("OTP Sent Successfully to Mobile"), HttpStatus.OK);
	}

	@PostMapping("/sendOtp/email/{email}")
	public ResponseEntity<ResponseDTO> sendOtpToEmail(@PathVariable String email) throws Exception {
		userService.sendOtpToEmail(email);
		return new ResponseEntity<>(new ResponseDTO("OTP Sent Successfully to Email"), HttpStatus.OK);
	}

	@GetMapping("/verifyOtp/mobile/{mobile}/{otp}")
	public ResponseEntity<ResponseDTO> verifyOtpForMobile(
			@PathVariable String mobile,
			@PathVariable @Pattern(regexp = "^[0-9]{6}$", message = "{otp.invalid}") String otp) throws Exception {

		userService.verifyOtpForMobile(mobile, otp);
		return new ResponseEntity<>(new ResponseDTO("Mobile OTP Verified Successfully"), HttpStatus.OK);
	}


}
