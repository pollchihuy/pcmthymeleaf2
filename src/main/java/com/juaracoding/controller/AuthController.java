package com.juaracoding.controller;

import com.juaracoding.config.OtherConfig;
import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.httpservice.AuthService;
import com.juaracoding.security.BcryptImpl;
import com.juaracoding.security.Crypto;
import com.juaracoding.utils.ConstantPage;
import com.juaracoding.utils.ConstantValue;
import com.juaracoding.utils.GenerateStringMenu;
import com.juaracoding.utils.GlobalFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
            Model model,
            @Valid @ModelAttribute("x") ValLoginDTO valLoginDTO,
            BindingResult result,
            WebRequest webRequest){
//        try{
//            Long sesId = Long.parseLong(Crypto.performDecrypt(valLoginDTO.getSesId()));
//            Long selisih = (System.currentTimeMillis()-sesId)/1000;
//            if(selisih<180){
//                throw new Exception("Anda Terkepung !!");
//            }
//        }catch (Exception e){
//
//        }

//        GlobalFunction.matchingPattern(valLoginDTO.getUsername(),"^[[a-z0-9\\\\.]]{8,16}$","username","Isi Username dengan benar!!",result);
//        GlobalFunction.matchingPattern(valLoginDTO.getCaptcha(),"^[\\w]{5}$","captcha","Isi Captcha dengan benar!!",result);

        String decodePassword = new String(Base64.decode(valLoginDTO.getPassword()));
        GlobalFunction.matchingPattern(decodePassword,"^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@_#\\-$])[\\w].{8,15}$",
                "password","Isi Password dengan benar!!","x",result);
//        String decodePassword = valLoginDTO.getPassword();
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

        if(result.hasErrors()){
            GlobalFunction.getCaptchaLogin(valLoginDTO);
            if(!isValid){
                model.addAttribute("captchaMessage","Invalid Captcha");
            }
            model.addAttribute("logo", ConstantValue.LOGIN_LOGO);
            return ConstantPage.LOGIN_PAGE;
        }

        /** REQUEST LOGIN */
        ResponseEntity<Object> response = null;
        String tokenJwt = "";
        String menuNavBar = "";
        try{
            response = authService.login(valLoginDTO);
            Map<String,Object> map = (Map<String, Object>) response.getBody();
            Map<String,Object> data = (Map<String, Object>) map.get("data");
            List<Map<String,Object>> ltMenu = (List<Map<String, Object>>) data.get("menu");
            menuNavBar = new GenerateStringMenu().stringMenu(ltMenu);
            tokenJwt = (String) data.get("token");
            System.out.println("Body Response : "+response.getBody());
            System.out.println("Token JWT : "+tokenJwt);

        }catch (Exception e){

        }
        webRequest.setAttribute("MENU_NAVBAR",menuNavBar,1);
        webRequest.setAttribute("JWT",tokenJwt,1);
        webRequest.setAttribute("USR_NAME",valLoginDTO.getUsername(),1);
        webRequest.setAttribute("PASSWORD",valLoginDTO.getPassword(),1);

        model.addAttribute("USR_NAME",valLoginDTO);
        model.addAttribute("MENU_NAVBAR",menuNavBar);

        return ConstantPage.HOME_PAGE;
    }

    @GetMapping("/logout")
    public String destroySession(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:"+ConstantPage.DEFAULT_PAGE;
    }
}