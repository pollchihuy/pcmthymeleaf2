package com.juaracoding.controller;


import cn.apiclub.captcha.Captcha;
import com.juaracoding.config.OtherConfig;
import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.httpservice.AuthService;
import com.juaracoding.security.BcryptImpl;
import com.juaracoding.utils.CaptchaUtils;
import com.juaracoding.utils.GlobalFunction;
import jakarta.validation.Valid;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(
            @ModelAttribute("x") @Valid ValLoginDTO valLoginDTO,
            BindingResult result,
            Model model,
            WebRequest webRequest){

        String decodePassword = new String(Base64.decode(valLoginDTO.getPassword()));
        System.out.println("Password setelah di decode : "+decodePassword);
        valLoginDTO.setPassword(decodePassword);
        String strAnswer = valLoginDTO.getHiddenCaptcha();
        Boolean isValid = false;
        if(OtherConfig.getEnableAutomationTesting().equals("y")){
            // kalau mode automation testing
            isValid = valLoginDTO.getCaptcha().equals(strAnswer);
        }else {
            // kalau mode production
            isValid = BcryptImpl.verifyHash(valLoginDTO.getCaptcha(),strAnswer);
        }
        if(!isValid || result.hasErrors()){
            GlobalFunction.getCaptchaLogin(valLoginDTO);
            model.addAttribute("x",valLoginDTO);
            model.addAttribute("captchaMessage","Invalid Captcha");
            return "auth/login";
        }
        /** REQUEST LOGIN */
        ResponseEntity<Object> response = null;
        String tokenJwt = "";
        String menuNavBar = "";
        try{
            response = authService.login(valLoginDTO);
            Map<String,Object> map = (Map<String, Object>) response.getBody();
            System.out.println("Body Response : "+response.getBody());
            tokenJwt = (String) map.get("token");
            List<Map<String,Object>> ltMenu = (List<Map<String, Object>>) map.get("menu");

            System.out.println("Token JWT : "+tokenJwt);
        }catch (Exception e){

        }
        return "auth";
    }
}