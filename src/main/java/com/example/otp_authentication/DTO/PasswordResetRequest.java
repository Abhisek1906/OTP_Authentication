package com.example.otp_authentication.DTO;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String phoneNumber;//destination
    private String userName;
    private String otp;


}
