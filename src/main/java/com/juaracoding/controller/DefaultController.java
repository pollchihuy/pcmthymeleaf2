package com.juaracoding.controller;


import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.utils.GlobalFunction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String defaultPage(Model model) {
        ValLoginDTO loginDTO = new ValLoginDTO();
        GlobalFunction.getCaptchaLogin(loginDTO);
        model.addAttribute("x",loginDTO);
//        model.addAttribute("captchaMessage","Ini Page Awal");
        return "auth/login";
    }

    @GetMapping("/regis")
    public String regis(Model model) {
        return "auth/regis";
    }

}
