package com.juaracoding.dto.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class ValLoginDTO {

    @Pattern(message = "Isi Username dengan benar!!",
            regexp = "^[a-z0-9\\.]{8,16}$")
    private String username;

    @NotEmpty(message = "Tidak Boleh Kosong !!")
    private String password;

    @Pattern(message = "Isi Captcha Dengan Benar !!",
            regexp = "^[\\w]{5}$")
    private String captcha;
    private String hiddenCaptcha;
    private String realCaptcha;

//    @NotEmpty
//    private String sesId;
//
//    public String getSesId() {
//        return sesId;
//    }
//
//    public void setSesId(String sesId) {
//        this.sesId = sesId;
//    }

    public String getCaptcha() {
        return captcha;
    }

    public void resetValue() {
        this.captcha = "";
        this.password = "";
        this.username = "";
    }
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getHiddenCaptcha() {
        return hiddenCaptcha;
    }

    public void setHiddenCaptcha(String hiddenCaptcha) {
        this.hiddenCaptcha = hiddenCaptcha;
    }

    public String getRealCaptcha() {
        return realCaptcha;
    }

    public void setRealCaptcha(String realCaptcha) {
        this.realCaptcha = realCaptcha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}