package com.juaracoding.utils;

import cn.apiclub.captcha.Captcha;
import com.juaracoding.config.OtherConfig;
import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.security.BcryptImpl;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalFunction {


    public static void getCaptchaLogin(ValLoginDTO valLoginDTO){
        Captcha captcha = CaptchaUtils.createCaptcha(275,70);
        String answer = captcha.getAnswer();
        if(OtherConfig.getEnableAutomationTesting().equals("y")){
            valLoginDTO.setHiddenCaptcha(answer);
        }else {
            valLoginDTO.setHiddenCaptcha(BcryptImpl.hash(answer));
        }
        valLoginDTO.setCaptcha("");
        valLoginDTO.setRealCaptcha(CaptchaUtils.encodeCaptcha(captcha));
    }

    public static void matchingPattern(String value,String regex, 
                                       String field,String message,
                                       String modelAttribut,
                                       BindingResult result){
        Boolean isValid = Pattern.compile(regex).matcher(value).find();
        if(!isValid){
            result.rejectValue(field,"error."+modelAttribut,message);
        }
    }
}
