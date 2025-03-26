package com.juaracoding.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.juaracoding.dto.response.RespMenuDTO;
import com.juaracoding.dto.validation.ValMenuDTO;
import com.juaracoding.httpservice.MenuService;
import com.juaracoding.utils.ConstantPage;
import com.juaracoding.utils.GlobalFunction;
import feign.Response;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    private Map<String,Object> filterColumn = new HashMap<String,Object>();

    public MenuController() {
        filterColumn.put("nama","Nama");
        filterColumn.put("path","Path");
        filterColumn.put("group","Group");
    }

    @GetMapping
    public String findAll(Model model,
                          WebRequest webRequest){
        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = menuService.findAll(jwt);
            Map<String,Object> mResponse = (Map<String, Object>) response.getBody();
            GlobalFunction.setDataMainPage(model,webRequest,mResponse,
                    "menu",filterColumn);
//            System.out.println("Body Response : "+response.getBody());
        }catch (Exception e){
            return "redirect:/er";
        }
        return ConstantPage.MENU_MAIN_PAGE;
    }



    @PostMapping("")
    public String save(
           @ModelAttribute("data") @Valid ValMenuDTO valMenuDTO,
           BindingResult bindingResult,
           Model model,
           WebRequest webRequest){

        if(bindingResult.hasErrors()){
            model.addAttribute("data",valMenuDTO);
            return ConstantPage.MENU_ADD_PAGE;
        }

        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = menuService.save(jwt,valMenuDTO);
        }catch (Exception e){
            model.addAttribute("data",valMenuDTO);
            return ConstantPage.MENU_ADD_PAGE;
        }
        return ConstantPage.SUCCESS_MESSAGE;
    }

    @GetMapping("/a")
    public String openModalsAdd(
            Model model,
            WebRequest webRequest){
        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }
        model.addAttribute("data",new RespMenuDTO());
        return ConstantPage.MENU_ADD_PAGE;
    }

    @GetMapping("/e/{id}")
    public String openModalsEdit(
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){
        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }
        try{
            response = menuService.findById(jwt,id);
        }catch (Exception e){

        }
        Map<String,Object> map = (Map<String, Object>) response.getBody();
        Map<String,Object> mapData = (Map<String, Object>) map.get("data");
        model.addAttribute("data",new ObjectMapper().convertValue(mapData,RespMenuDTO.class));
        return ConstantPage.MENU_EDIT_PAGE;
    }

    @PostMapping("/e/{id}")
    public String edit(
            @ModelAttribute("data") @Valid ValMenuDTO valMenuDTO,
            BindingResult bindingResult,
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){
        if(bindingResult.hasErrors()){
            model.addAttribute("data",valMenuDTO);
            return ConstantPage.MENU_EDIT_PAGE;
        }

        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = menuService.edit(jwt,id,valMenuDTO);
        }catch (Exception e){
            model.addAttribute("data",valMenuDTO);
            return ConstantPage.MENU_EDIT_PAGE;
        }
        return ConstantPage.SUCCESS_MESSAGE;
    }

    @GetMapping("/d/{id}")
    public String delete(
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){

        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = menuService.delete(jwt,id);
        }catch (Exception e){
            return ConstantPage.MENU_MAIN_PAGE;
        }

        return "redirect:/menu";
    }

    // localhost:8093/menu/1
    @GetMapping("/{id}")
    public String findById(
            Model model,
           @PathVariable(value = "id") Long id,
           WebRequest webRequest){

        return null;
    }

    @GetMapping("/{sort}/{sortBy}/{page}")
    public String findByParam(
            Model model,
            @PathVariable(value = "sort") String sort,
            @PathVariable(value = "sortBy") String sortBy,
            @PathVariable(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value,
            WebRequest webRequest){
        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        page = page > 0 ?(page-1):0;
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = menuService.findByParam(jwt,sort,sortBy,page,size,column,value);
            Map<String,Object> mResponse = (Map<String, Object>) response.getBody();
            GlobalFunction.setDataMainPage(model,webRequest,mResponse,
                    "menu",filterColumn);
//            System.out.println("Body Response : "+response.getBody());
        }catch (Exception e){
            return "redirect:/er";
        }
        return ConstantPage.MENU_MAIN_PAGE;
    }

    @PostMapping("/upload-excel")
    public String uploadExcel(
            Model model,
            @RequestParam(value = "file") MultipartFile file,
            WebRequest webRequest){

        return null;
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> downloadExcel(
            Model model,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value,
            WebRequest webRequest
    ){
        ByteArrayResource resource =null;
        Response response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        String fileName = "";
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resource);
        }
        try{
            response = menuService.downloadExcel(jwt,column,value);
            fileName = response.headers().get("Content-Disposition").toString();
            System.out.println("Value Content-Disposition Server : "+fileName);
            InputStream inputStream = response.body().asInputStream();
            resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        }catch (Exception e){
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition",fileName.substring(0,fileName.length()-1));

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).
                body(resource);
    }

    @GetMapping("/pdf")
    public ResponseEntity<Resource> downloadPdf(
            Model model,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value,
            WebRequest webRequest
    ){

        ByteArrayResource resource =null;
        Response response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        String fileName = "";
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resource);
        }
        try{
            response = menuService.downloadPdf(jwt,column,value);
            fileName = response.headers().get("Content-Disposition").toString();
            System.out.println("Value Content-Disposition Server : "+fileName);
            InputStream inputStream = response.body().asInputStream();
            resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        }catch (Exception e){
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition",fileName.substring(0,fileName.length()-1));

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/pdf")).
                body(resource);
    }
}