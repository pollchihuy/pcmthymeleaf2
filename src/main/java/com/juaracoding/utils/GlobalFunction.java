package com.juaracoding.utils;

import cn.apiclub.captcha.Captcha;
import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.security.BcryptImpl;

public class GlobalFunction {


    public static void getCaptchaLogin(ValLoginDTO valLoginDTO){
        Captcha captcha = CaptchaUtils.createCaptcha(275,70);
        String answer = captcha.getAnswer();
        valLoginDTO.setHiddenCaptcha(BcryptImpl.hash(answer));
        valLoginDTO.setCaptcha("");
        valLoginDTO.setRealCaptcha(CaptchaUtils.encodeCaptcha(captcha));
    }
}
