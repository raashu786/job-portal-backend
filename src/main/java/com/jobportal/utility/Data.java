package com.jobportal.utility;

public class Data {
    public static String getMessageBody(String otp , String name){

        String subject = "OTP Verification Code";
       return"<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <title>OTP Verification</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }\n" +
                "        .container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px;\n" +
                "                     border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center; }\n" +
                "        .header { background-color: #ffbd20; color: #ffffff; padding: 15px; font-size: 20px; font-weight: bold;\n" +
                "                  border-top-left-radius: 8px; border-top-right-radius: 8px; }\n" +
                "        .otp { font-size: 28px; font-weight: bold; color: #23e80e; margin: 20px 0; background: #c5f8c0;\n" +
                "               padding: 10px; display: inline-block; border-radius: 5px; }\n" +
                "        .footer { margin-top: 20px; font-size: 14px; color: #ffffff; background-color: #343a40; padding: 10px;\n" +
                "                  border-bottom-left-radius: 8px; border-bottom-right-radius: 8px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='container'>\n" +
                "        <div class='header'>OTP Verification</div>\n" +
                "        <p>Your One-Time Password (OTP) for verification is:</p>\n" +
                "        <p>Hello,<span style='color:#ffbd20; font-weight:bold;'>" + name+"</span></p>\n" +
                "        <p class='otp'>" + otp + "</p>\n" +
                "        <p>Please enter this OTP to verify your email address. This OTP is valid for 10 minutes.</p>\n" +
                "        <p>If you did not request this, please ignore this email.</p>\n" +
                "        <div class='footer'>&copy; 2025 <span style='color:#ffbd20; font-weight:bold;'>Akhsh</span> Job Society. All Rights Reserved.</div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    };
}
