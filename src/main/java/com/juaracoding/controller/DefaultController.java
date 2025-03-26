package com.juaracoding.controller;


import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.security.Crypto;
import com.juaracoding.utils.ConstantPage;
import com.juaracoding.utils.ConstantValue;
import com.juaracoding.utils.GlobalFunction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String defaultPage(Model model) {
        ValLoginDTO valLoginDTO = new ValLoginDTO();
        GlobalFunction.getCaptchaLogin(valLoginDTO);
//        valLoginDTO.setSesId(Crypto.performEncrypt(String.valueOf(System.currentTimeMillis())));
        model.addAttribute("x",valLoginDTO);
        model.addAttribute("logo", ConstantValue.LOGIN_LOGO);
        return "auth/login";
    }

    @GetMapping("/er")
    public String errorPage(Model model, HttpServletRequest request) {
        request.getSession().invalidate();
        ValLoginDTO valLoginDTO = new ValLoginDTO();
        GlobalFunction.getCaptchaLogin(valLoginDTO);
        model.addAttribute("x",valLoginDTO);
        model.addAttribute("authProblem","Terjadi Kesalahan Di Server");
        model.addAttribute("logo", ConstantValue.LOGIN_LOGO);
        return "auth/login";
    }

    @GetMapping("/home")
    public String home(Model model, WebRequest webRequest) {
        model.addAttribute("USR_NAME",webRequest.getAttribute("USR_NAME",1));
        model.addAttribute("MENU_NAVBAR",webRequest.getAttribute("MENU_NAVBAR",1));
        return "auth/home";
    }
    @GetMapping("/regis")
    public String regis(Model model, WebRequest webRequest) {
        model.addAttribute("USR_NAME",webRequest.getAttribute("USR_NAME",1));
        model.addAttribute("MENU_NAVBAR",webRequest.getAttribute("MENU_NAVBAR",1));
        return "auth/regis";
    }
}