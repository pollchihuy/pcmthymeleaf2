package com.juaracoding.utils;

import cn.apiclub.captcha.Captcha;
import com.juaracoding.config.OtherConfig;
import com.juaracoding.dto.validation.ValLoginDTO;
import com.juaracoding.security.BcryptImpl;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public static void setDataMainPage(Model model,WebRequest webRequest,
                                       Map<String,Object> map,
                                       String pathServer,Map<String,Object> filterColumn){
        setGlobalAttribute(model,webRequest);
        setPagingElement(model,map,pathServer,filterColumn);
    }

    public static void setGlobalAttribute(Model model, WebRequest request){
        model.addAttribute("USR_NAME",request.getAttribute("USR_NAME",1));
        model.addAttribute("MENU_NAVBAR",request.getAttribute("MENU_NAVBAR",1));
    }

    public static void setPagingElement(Model model, Map<String,Object> map,
                                        String pathServer,
                                        Map<String,Object> filterColumn){

        Map<String,Object> data = (Map<String, Object>) map.get("data");//element yang di dalam paging
        List<Map<String,Object>> listContent = (List<Map<String, Object>>) data.get("content");
        Map<String,Object> listKolom = new LinkedHashMap<>();
        List<String> listHelper = new ArrayList<>();
        Map<String,Object> columnHeader = listContent.get(0);
        String keyVal = "";
        for(Map.Entry<String,Object> entry : columnHeader.entrySet()){
            keyVal = entry.getKey();
            listHelper.add(keyVal);
            listKolom.put(keyVal,GlobalFunction.camelToStandard(keyVal).toUpperCase());
        }
        /** Content Pagination */
        model.addAttribute("listKolom",listKolom);
        model.addAttribute("listContent",listContent);
        model.addAttribute("listHelper",listHelper);

        /** Element Pagination */
        int currentPage = (int) data.get("current-page");
        model.addAttribute("sort",data.get("sort"));
        model.addAttribute("sortBy",data.get("sort-by"));
        model.addAttribute("currentPage",currentPage==0?1:(currentPage+1));
        model.addAttribute("columnName",data.get("column-name"));
        model.addAttribute("value",data.get("value"));
        model.addAttribute("sizePerPage",data.get("size-per-page"));
        model.addAttribute("totalPage",data.get("total-page"));
        model.addAttribute("totalData",data.get("total-data"));
        model.addAttribute("pathServer",pathServer);
        model.addAttribute("SIZE_COMPONENT",ConstantValue.SIZE_COMPONENT);
        model.addAttribute("filterColumn",filterColumn);
    }

    private static String camelToStandard(String camel){
        StringBuilder sb = new StringBuilder();
        char c = camel.charAt(0);
        sb.append(Character.toLowerCase(c));
        for (int i = 1; i < camel.length(); i++) {
            char c1 = camel.charAt(i);
            if(Character.isUpperCase(c1)){
                sb.append(' ').append(Character.toLowerCase(c1));
            }
            else {
                sb.append(c1);
            }
        }
        return sb.toString();
    }

    public static String tokenCheck(Model model,WebRequest request){
        Object tokenJwt = request.getAttribute("JWT",1);
        if(tokenJwt == null){
            ValLoginDTO valLoginDTO = new ValLoginDTO();
            GlobalFunction.getCaptchaLogin(valLoginDTO);
            model.addAttribute("logo", ConstantValue.LOGIN_LOGO);
            model.addAttribute("authProblem","Lakukan Login Terlebih Dahulu !!");
            model.addAttribute("x",valLoginDTO);
            return ConstantPage.LOGIN_PAGE;
        }
        return "Bearer "+tokenJwt.toString();
    }
}
