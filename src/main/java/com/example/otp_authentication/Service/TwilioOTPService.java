package com.example.otp_authentication.Service;

import com.example.otp_authentication.DTO.OtpStatus;
import com.example.otp_authentication.DTO.PasswordResetRequest;
import com.example.otp_authentication.DTO.PasswordResetResponseDto;
import com.example.otp_authentication.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwilioOTPService {
    @Autowired
    private TwilioConfig twilioConfig;

    Map<String,String> otpMap=new HashMap<>();

    public Mono<PasswordResetResponseDto> sendOTPForPasswordReset(PasswordResetRequest passwordResetRequest) {

        PasswordResetResponseDto passwordResetResponseDto;
        try {
            passwordResetResponseDto = null;
            PhoneNumber to = new PhoneNumber(passwordResetRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());

            String otp = generateOTp();
            String otpMessage = "Your OTP is" + otp;
            Message message = Message.creator(
                            to, from,
                            otpMessage)
                    .create();
            otpMap.put(passwordResetRequest.getUserName(), otp);
            passwordResetResponseDto = new PasswordResetResponseDto(otpMessage, OtpStatus.DELIVERED);
        } catch (Exception exception) {
            passwordResetResponseDto = new PasswordResetResponseDto(exception.getMessage(), OtpStatus.FAILED);
        }
        return Mono.just(passwordResetResponseDto);
    }

    public Mono<String> validateOTP(String userInputOtp,String userName){
        if(userInputOtp.equals(otpMap.get(userName))){
            otpMap.remove(userName,userInputOtp);
            return Mono.just("Valid OTP please proceed with your transaction");
        }
        else{
            return Mono.error(new IllegalArgumentException("Invalid OTP"));
        }
    }

    private String generateOTp(){
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
