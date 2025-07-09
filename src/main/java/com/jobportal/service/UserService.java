package com.jobportal.service;

import com.jobportal.dto.*;
import com.jobportal.exception.JobPortalException;

public interface UserService {
	public UserDTO registerUser(UserDTO userDTO) throws JobPortalException;
	public UserDTO getUserByEmail(String email) throws JobPortalException;
	public UserDTO getUserByMobile(String mobile) throws JobPortalException;
	public UserDTO loginUser(LoginDTO loginDTO) throws JobPortalException;
	public ResponseDTO changePassword(LoginDTO loginDTO) throws JobPortalException;
	public UserDTO loginUserWithOtpEmail(LoginWithOtpEmailDTO loginWithOtpEmailDTO) throws JobPortalException;
	public UserDTO loginUserWithOtpMobile(LoginWithOtpMobileDTO loginWithOtpMobileDTO) throws JobPortalException;
	public boolean sendOtp(String email) throws Exception;
	public boolean verifyOtp(String email, String otp) throws Exception;
	public boolean sendOtpToMobile(String mobile) throws Exception;
	public boolean sendOtpToMobileReg(String mobile) throws Exception;
	public boolean verifyOtpForMobile(String mobile, String otp) throws Exception;
	public boolean sendOtpToEmail(String email) throws Exception;
}
