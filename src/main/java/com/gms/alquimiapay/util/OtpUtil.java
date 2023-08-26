package com.gms.alquimiapay.util;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.model.GmsUserDevice;
import com.gms.alquimiapay.modules.user.model.GmsUserOtp;
import com.gms.alquimiapay.modules.user.repository.IUserDeviceRepository;
import com.gms.alquimiapay.modules.user.repository.IUserOtpRepository;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.payload.MailData;
import com.gms.alquimiapay.payload.OtpSendInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class OtpUtil
{
    @Autowired
    private Environment env;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private IUserOtpRepository otpRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private IUserDeviceRepository userDeviceRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private MessageTemplateUtil messageTemplateUtil;

    private static final int OTP_LENGTH = 6;

    private String generateOtp(){
        StringBuilder stringBuilder = new StringBuilder("");
        for(int i = 0; i < OTP_LENGTH; i++){
            int randomInteger = (int)(Math.random() * 10);
            stringBuilder.append(randomInteger);
        }
        return stringBuilder.toString();
    }

    public OtpSendInfo sendSignUpOtpToMail(String email){
        GmsUser user = userRepository.findByEmailAddress(email);
        String fileName = "otp-signup";
        String expirationTime = env.getProperty("gms.otp.signup.expirationTimeInMin");
        OtpSendInfo otpSendInfo = new OtpSendInfo();

        String signupTemplate = messageTemplateUtil.getTemplateOf(fileName);
        String otp = generateOtp();
        otpSendInfo.setOtpSent(otp);
        otpSendInfo.setRecipientEmail(email);
        otpSendInfo.setCreatedDateTime(LocalDateTime.now());
        otpSendInfo.setExpirationDateTime(otpSendInfo.getCreatedDateTime().plusMinutes(Integer.parseInt(expirationTime)));

        if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())){
            signupTemplate = signupTemplate
                    .replace("{lastName}", user.getBusinessName())
                    .replace("{firstName}", StringValues.EMPTY_STRING);
        }
        else if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
            signupTemplate = signupTemplate
                    .replace("{lastName}", user.getLastName())
                    .replace("{firstName}", user.getFirstName());
        }

        signupTemplate = signupTemplate
                .replace("{otp}", otp)
                .replace("{otpExpiration}", expirationTime);

        System.out.println("Signup email verification message: {}" + signupTemplate);

        MailData mailData = new MailData();
        mailData.setRecipientMail(email);
        mailData.setSubject("GMS Email Verification");
        mailData.setContent(signupTemplate);
        mailUtil.sendNotification(mailData);

        return otpSendInfo;
    }

    public OtpSendInfo sendForgetPasswordOtpToMail(String email){
        GmsUser user = userRepository.findByEmailAddress(email);
        String fileName = "otp-forgotpassword";
        String expirationTime = env.getProperty("gms.otp.signup.expirationTimeInMin");
        OtpSendInfo otpSendInfo = new OtpSendInfo();
        String forgetPasswordTemplate = messageTemplateUtil.getTemplateOf(fileName);
        String otp = generateOtp();
        otpSendInfo.setOtpSent(otp);
        otpSendInfo.setRecipientEmail(email);

        otpSendInfo.setCreatedDateTime(LocalDateTime.now());
        otpSendInfo.setExpirationDateTime(otpSendInfo.getCreatedDateTime().plusMinutes(Integer.parseInt(expirationTime)));

        if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())){
            forgetPasswordTemplate = forgetPasswordTemplate
                    .replace("{lastName}", user.getBusinessName())
                    .replace("{firstName}", StringValues.EMPTY_STRING);
        }
        else if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
            forgetPasswordTemplate = forgetPasswordTemplate
                    .replace("{lastName}", user.getLastName())
                    .replace("{firstName}", user.getFirstName());
        }

        forgetPasswordTemplate = forgetPasswordTemplate
                .replace("{otp}", otp)
                .replace("{otpExpiration}", expirationTime);

        System.out.println("Signup email verification message: {}" + forgetPasswordTemplate);

        // Create a new otp entry for user if it doesn't exist.
        GmsUserOtp userOtp = otpRepository.findByOtpTypeAndOtpOwner("FORGOT_PASSWORD", email);
        if(userOtp == null){
            userOtp = new GmsUserOtp();
            userOtp.setOtpFor("User forgot password");
            userOtp.setOtpOwner(email);
            userOtp.setOtpType("FORGOT_PASSWORD");
            userOtp.setOtpValue(passwordUtil.hashPassword(otp));
            userOtp.setVerified(false);
            userOtp.setCreatedAt(LocalDateTime.now().toString());
            userOtp.setUpdatedAt(LocalDateTime.now().toString());
        }

        userOtp.setOtpValue(passwordUtil.hashPassword(otp));
        userOtp.setVerified(false);
        userOtp.setUpdatedAt(LocalDateTime.now().toString());
        userOtp.setExpAt(LocalDateTime.now().plusMinutes(Integer.parseInt(expirationTime)).toString());
        otpRepository.saveAndFlush(userOtp);

        MailData mailData = new MailData();
        mailData.setRecipientMail(email);
        mailData.setSubject("GMS Forgot Password Verification Email");
        mailData.setContent(forgetPasswordTemplate);
        mailUtil.sendNotification(mailData);

        return otpSendInfo;
    }

    public OtpSendInfo sendNewDeviceLinkOtp(String email, String newDeviceId) {
        GmsUser user = userRepository.findByEmailAddress(email);
        String fileName = "new-device-link";
        String expirationTime = env.getProperty("gms.otp.signup.expirationTimeInMin");
        OtpSendInfo otpSendInfo = new OtpSendInfo();
        String deviceLinkingTemplate = messageTemplateUtil.getTemplateOf(fileName);
        String otp = generateOtp();
        otpSendInfo.setOtpSent(otp);
        otpSendInfo.setRecipientEmail(email);

        otpSendInfo.setCreatedDateTime(LocalDateTime.now());
        otpSendInfo.setExpirationDateTime(otpSendInfo.getCreatedDateTime().plusMinutes(Integer.parseInt(expirationTime)));

        if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())){
            deviceLinkingTemplate = deviceLinkingTemplate
                    .replace("{lastName}", user.getBusinessName())
                    .replace("{firstName}", StringValues.EMPTY_STRING);
        }
        else if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
            deviceLinkingTemplate = deviceLinkingTemplate
                    .replace("{lastName}", user.getLastName())
                    .replace("{firstName}", user.getFirstName());
        }

        deviceLinkingTemplate = deviceLinkingTemplate
                .replace("{otp}", otp)
                .replace("{expiresIn}", expirationTime);

        System.out.println("Signup email verification message: {}" + deviceLinkingTemplate);

        // Create a new otp entry for user.
        GmsUserOtp userOtp = otpRepository.findByOtpTypeAndOtpOwner("DEVICE_LINK", email);
        if(userOtp == null){
            userOtp = new GmsUserOtp();
            userOtp.setOtpFor("User new device linking");
            userOtp.setOtpOwner(email);
            userOtp.setOtpType("DEVICE_LINK");
            userOtp.setCreatedAt(LocalDateTime.now().toString());
            userOtp.setUpdatedAt(LocalDateTime.now().toString());
        }

        userOtp.setDeviceId(newDeviceId);
        userOtp.setOtpValue(passwordUtil.hashPassword(otp));
        userOtp.setVerified(false);
        userOtp.setUpdatedAt(LocalDateTime.now().toString());
        userOtp.setExpAt(LocalDateTime.now().plusMinutes(Integer.parseInt(expirationTime)).toString());
        GmsUserOtp savedOtp =  otpRepository.saveAndFlush(userOtp);

        // Create a new device entry for the user.
        GmsUserDevice userDevice = userDeviceRepository.findByDeviceIdAndOwnerEmail(newDeviceId, email);
        if(userDevice == null){
            userDevice = new GmsUserDevice();
        }
        userDevice.setLinked(false);
        userDevice.setLinkingOtpId(savedOtp.getId());
        userDevice.setOwnerEmail(email);
        userDevice.setCreatedAt(LocalDateTime.now().toString());
        userDevice.setUpdatedAt(LocalDateTime.now().toString());
        userDevice.setDeviceId(newDeviceId);
        userDeviceRepository.saveAndFlush(userDevice);

        MailData mailData = new MailData();
        mailData.setRecipientMail(email);
        mailData.setSubject("GMS New Device Linking Verification Email");
        mailData.setContent(deviceLinkingTemplate);
        mailUtil.sendNotification(mailData);

        return otpSendInfo;
    }

    public OtpSendInfo sendPinChangeOtpRequest(String email, String newDeviceId){
        GmsUser user = userRepository.findByEmailAddress(email);
        String fileName = "pin-forget-otp";
        String expirationTime = env.getProperty("gms.otp.signup.expirationTimeInMin");
        OtpSendInfo otpSendInfo = new OtpSendInfo();
        String pinChangeTemplate = messageTemplateUtil.getTemplateOf(fileName);
        String otp = generateOtp();
        otpSendInfo.setOtpSent(otp);
        otpSendInfo.setRecipientEmail(email);

        otpSendInfo.setCreatedDateTime(LocalDateTime.now());
        otpSendInfo.setExpirationDateTime(otpSendInfo.getCreatedDateTime().plusMinutes(Integer.parseInt(expirationTime)));

        pinChangeTemplate = pinChangeTemplate
                .replace("{otp}", otp)
                .replace("{expiresIn}", expirationTime);

        if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
            String fullName = String.join(StringValues.SINGLE_EMPTY_SPACE, user.getLastName(), user.getFirstName(), user.getMiddleName());
            pinChangeTemplate = pinChangeTemplate.replace("{fullName}", fullName);
        }
        else if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())){
            pinChangeTemplate = pinChangeTemplate.replace("{fullName}", user.getBusinessName());
        }

        System.out.println("Pin change message: {}" + pinChangeTemplate);

        // Create a new otp entry for user.
        GmsUserOtp userOtp = otpRepository.findByOtpTypeAndOtpOwner("PIN_CHANGE", email);
        if(userOtp == null){
            userOtp = new GmsUserOtp();
            userOtp.setOtpFor("User Pin change");
            userOtp.setOtpOwner(email);
            userOtp.setOtpType("PIN_CHANGE");
            userOtp.setCreatedAt(LocalDateTime.now().toString());
            userOtp.setUpdatedAt(LocalDateTime.now().toString());
        }

        userOtp.setDeviceId(user.getDeviceId());
        userOtp.setOtpValue(passwordUtil.hashPassword(otp));
        userOtp.setVerified(false);
        userOtp.setUpdatedAt(LocalDateTime.now().toString());
        userOtp.setExpAt(LocalDateTime.now().plusMinutes(Integer.parseInt(expirationTime)).toString());
        GmsUserOtp savedOtp =  otpRepository.saveAndFlush(userOtp);

        MailData mailData = new MailData();
        mailData.setRecipientMail(email);
        mailData.setSubject("GMS Transaction PIN Change");
        mailData.setContent(pinChangeTemplate);
        mailUtil.sendNotification(mailData);

        return otpSendInfo;
    }
}
