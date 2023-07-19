package com.group6.moneymanagementbooking.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.group6.moneymanagementbooking.enity.OTP;
import com.group6.moneymanagementbooking.repository.OTPRepository;
import com.group6.moneymanagementbooking.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPRepository otpRepository;

    public void confirm(String userEmail, String userOTPInput) throws Exception {
    

        Optional<OTP> otpCheckNull = otpRepository.findByEmail(userEmail);
        if (otpCheckNull.isPresent()) {
            OTP otp = otpCheckNull.get();
            if (!StringUtils.isNumberic(userOTPInput)) {
                throw new Exception("OTP is not valid!!!");
            }
            if(!userOTPInput.equals(otp.getCode())){
                throw new Exception("OTP not correct!!");
            }

            if (LocalDateTime.now().isBefore(otp.getDate_create().plusMinutes(1))) {
                if (!otp.getCode().equals(userOTPInput)) {
                    throw new Exception("Your OTP is not correct");
                }
            } else {
                throw new Exception("Your OTP is expired!!");
            }
            if(otp.isUsed()){
                throw new Exception("This OTP already used");
            }else{
                otp.setUsed(true);
                otpRepository.save(otp);
            }
        }else{
            throw new Exception("Your OTP not exist");
        }
    }

    public void saveOTP(OTP otp) {
        Optional<OTP> checkDuplicate = otpRepository.findByEmail(otp.getEmail());
        if (checkDuplicate.isPresent()) {
            otp.setId(checkDuplicate.get().getId());
        }
        otpRepository.save(otp);
    }

    public String OTPEmailTemplate(String otp){
        return "<div style='font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2'>"
       + "<div style='margin:50px auto;width:70%;padding:20px 0'>"
       + "<div style='border-bottom:1px solid #eee'> <a href='' style='font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600'>Your Brand</a> </div>"
       + "<p style='font-size:1.1em'>Hi,</p>"
       + "<p>Thank you for using PMM service. Use the following OTP to complete your Procedures. OTP is valid for 1 minutes</p>"
       + "<h2 style='background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;''>"+otp+"</h2>"
       + "<p style='font-size:0.9em;'>Regards,<br/>PMM</p>"
       + "<hr style='border:none;border-top:1px solid #eee' />"
       + "<div style='float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300'>"
       + "<p>Personal Money Management</p><p>FPTUniversity</p><p>Hanoi</p></div></div></div>";
    }
}
