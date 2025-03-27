package com.juaracoding.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.juaracoding.dto.rel.RelMenuDTO;
import com.juaracoding.dto.response.RespAksesDTO;
import com.juaracoding.dto.validation.SelectAksesDTO;
import com.juaracoding.dto.validation.SelectMenuDTO;
import com.juaracoding.dto.validation.ValAksesDTO;
import com.juaracoding.dto.validation.ValMenuDTO;
import com.juaracoding.httpservice.AksesService;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("akses")
public class AksesController {

    @Autowired
    private AksesService aksesService;

    @Autowired
    private MenuService menuService;

    private Map<String,Object> filterColumn = new HashMap<String,Object>();

    public AksesController() {
        filterColumn.put("nama","Nama");
        filterColumn.put("deskripsi","Deskripsi");
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
            response = aksesService.findAll(jwt);
            Map<String,Object> mResponse = (Map<String, Object>) response.getBody();
            GlobalFunction.setDataMainPage(model,webRequest,mResponse,
                    "akses",filterColumn);
//            System.out.println("Body Response : "+response.getBody());
        }catch (Exception e){
            return "redirect:/er";
        }
        return ConstantPage.AKSES_MAIN_PAGE;
    }

    @PostMapping("")
    public String save(
           @ModelAttribute("data") @Valid SelectAksesDTO selectAksesDTO,
           BindingResult bindingResult,
           Model model,
           WebRequest webRequest){

        ValAksesDTO valAksesDTO = convertToValAksesDTO(selectAksesDTO);
        if(bindingResult.hasErrors()){
            model.addAttribute("data",valAksesDTO);
            return ConstantPage.AKSES_ADD_PAGE;
        }

        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = aksesService.save(jwt,valAksesDTO);
        }catch (Exception e){
            model.addAttribute("data",selectAksesDTO);
            return ConstantPage.AKSES_ADD_PAGE;
        }
        return ConstantPage.SUCCESS_MESSAGE;
    }

    private ValAksesDTO convertToValAksesDTO(SelectAksesDTO selectAksesDTO){
        ValAksesDTO valAksesDTO = new ValAksesDTO();
        valAksesDTO.setNama(selectAksesDTO.getNama());
        valAksesDTO.setDeskripsi(selectAksesDTO.getDeskripsi());
        List<RelMenuDTO> relMenuDTOList = new ArrayList<>();
        RelMenuDTO relMenuDTO = new RelMenuDTO();
        for (String s:
                selectAksesDTO.getLtMenu()) {
            relMenuDTO = new RelMenuDTO();
            relMenuDTO.setId(Long.parseLong(s));
            relMenuDTOList.add(relMenuDTO);
        }
        valAksesDTO.setLtMenu(relMenuDTOList);
        return valAksesDTO;
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

        try{
            response = menuService.allMenu(jwt);
        }catch (Exception e){
        }

        Map<String,Object> map = (Map<String, Object>) response.getBody();
        List<Map<String,Object>> ltMenu = (List<Map<String,Object>>) map.get("data");

        model.addAttribute("data",new RespAksesDTO());
        model.addAttribute("x",ltMenu);
        return ConstantPage.AKSES_ADD_PAGE;
    }

    @GetMapping("/e/{id}")
    public String openModalsEdit(
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){
        ResponseEntity<Object> response = null;
        ResponseEntity<Object> responseMenu = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }
        try{
            response = aksesService.findById(jwt,id);
            responseMenu = menuService.allMenu(jwt);
        }catch (Exception e){

        }
        Map<String,Object> map = (Map<String, Object>) response.getBody();
        Map<String,Object> mapData = (Map<String, Object>) map.get("data");
        List<Map<String,Object>> ltMenuAkses = (List<Map<String,Object>>) mapData.get("ltMenu");

        Map<String,Object> mapMenu = (Map<String, Object>) responseMenu.getBody();
        List<Map<String,Object>> ltMenu = (List<Map<String,Object>>) mapMenu.get("data");
        List<SelectMenuDTO> listAllMenu = getAllMenu(ltMenu);
        List<SelectMenuDTO> selectedMenuDTO = new ArrayList<>();
        for (SelectMenuDTO menu : listAllMenu) {
            for(Map<String,Object> m:ltMenuAkses){
                if(menu.getId()==Long.parseLong(m.get("id").toString())){
                    selectedMenuDTO.add(menu);
                    break;
                }
            }
        }
        Set<Long> menuSelected = selectedMenuDTO.stream().map(SelectMenuDTO::getId).collect(Collectors.toSet());
        model.addAttribute("data",new ObjectMapper().convertValue(mapData,RespAksesDTO.class));
        model.addAttribute("listMenu",listAllMenu);
        model.addAttribute("menuSelected",menuSelected);
        return ConstantPage.AKSES_EDIT_PAGE;
    }

    public List<SelectMenuDTO> getAllMenu(List<Map<String,Object>> ltMenu){
        List<SelectMenuDTO> selectMenuDTOS = new ArrayList<>();
        SelectMenuDTO selectMenuDTO = null;
        for(Map<String,Object> menu:ltMenu){
            selectMenuDTO = new SelectMenuDTO();
            selectMenuDTO.setId(Long.parseLong(menu.get("id").toString()));
            selectMenuDTO.setNama(menu.get("nama").toString());
            selectMenuDTOS.add(selectMenuDTO);
        }
        return selectMenuDTOS;
    }

    @PostMapping("/e/{id}")
    public String edit(
            @ModelAttribute("data") @Valid SelectAksesDTO selectAksesDTO,
            BindingResult bindingResult,
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){

        ValAksesDTO valAksesDTO = convertToValAksesDTO(selectAksesDTO);
        if(bindingResult.hasErrors()){
            model.addAttribute("data",valAksesDTO);
            return ConstantPage.AKSES_EDIT_PAGE;
        }

        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = aksesService.edit(jwt,id,valAksesDTO);
        }catch (Exception e){
            model.addAttribute("data",selectAksesDTO);
            return ConstantPage.AKSES_EDIT_PAGE;
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
            response = aksesService.delete(jwt,id);
        }catch (Exception e){
            return ConstantPage.AKSES_MAIN_PAGE;
        }

        return "redirect:/akses";
    }

    // localhost:8093/akses/1
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
            response = aksesService.findByParam(jwt,sort,sortBy,page,size,column,value);
            Map<String,Object> mResponse = (Map<String, Object>) response.getBody();
            GlobalFunction.setDataMainPage(model,webRequest,mResponse,
                    "akses",filterColumn);
//            System.out.println("Body Response : "+response.getBody());
        }catch (Exception e){
            return "redirect:/er";
        }
        return ConstantPage.AKSES_MAIN_PAGE;
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
            response = aksesService.downloadExcel(jwt,column,value);
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
            response = aksesService.downloadPdf(jwt,column,value);
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