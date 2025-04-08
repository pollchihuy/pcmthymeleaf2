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
            model.addAttribute("data",selectAksesDTO);
            setDataTempAdd(model,webRequest);
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
            setDataTempAdd(model,webRequest);
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
        int ltMenuSize = ltMenu.size();
        Long[] data1 = new Long[ltMenuSize];
        String[] data2 = new String[ltMenuSize];
        int i=0;
        for (Map<String,Object> map1 : ltMenu) {
            data1[i] = Long.parseLong(map1.get("id").toString());
            data2[i] = (String) map1.get("nama");
            i++;
        }
        webRequest.setAttribute("data1",data1,1);
        webRequest.setAttribute("data2",data2,1);

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
        /** cara untuk menyimpan data di session, kurang lebih sama untuk CSR data nya disimpan di dalam storage di web browser
         *  Notes : table session tidak dapat menyimpan data berbentuk object serialize, sehingga nanti dirangkai menjadi array 1 atau 2 dimensi tergantung kebuuthan
         */
        setDataMenuToEdit(response,responseMenu,model,webRequest);
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

        selectAksesDTO.setId(id);
        if(bindingResult.hasErrors()){
            model.addAttribute("data",selectAksesDTO);
            setDataTempEdit(model,webRequest);
            return ConstantPage.AKSES_EDIT_PAGE;
        }

        ValAksesDTO valAksesDTO = convertToValAksesDTO(selectAksesDTO);
        ResponseEntity<Object> response = null;
        String jwt = GlobalFunction.tokenCheck(model, webRequest);
        if(jwt.equals(ConstantPage.LOGIN_PAGE)){
            return jwt;
        }

        try{
            response = aksesService.edit(jwt,id,valAksesDTO);
        }catch (Exception e){
            model.addAttribute("data",selectAksesDTO);
            setDataTempEdit(model,webRequest);
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

    /** fungsi untuk mencocokkan data menu dengan data yang sudah ada di table di database */
    private void setDataMenuToEdit(ResponseEntity<Object> response,ResponseEntity<Object> responseMenu, Model model,WebRequest webRequest){
        Map<String,Object> map = (Map<String, Object>) response.getBody();
        Map<String,Object> mapMenu = (Map<String, Object>) responseMenu.getBody();

        Map<String,Object> mapData = (Map<String, Object>) map.get("data");

        List<Map<String,Object>> ltMenuAkses = (List<Map<String, Object>>) mapData.get("ltMenu");
        List<Map<String,Object>> listOfMenu = (List<Map<String, Object>>) mapMenu.get("data");
        List<SelectMenuDTO> listAllMenu = getAllMenu(listOfMenu);//harus di serialize di thymeleaf
        int listAllMenuSize = listAllMenu.size();
        int listMenuAksesSize = ltMenuAkses.size();
        List<SelectMenuDTO> selectedMenuDTO = new ArrayList<>();
        Long data1[] = new Long[listAllMenuSize];//menampung data id dari all menu
        String data2[] = new String[listAllMenuSize];//menampung data nama dari all menu
        Long data3[] = new Long[listMenuAksesSize];//menampung data id dari menu akses
        String data4[] = new String[listMenuAksesSize];//menampung data nama dari menu akses

        int i=0;
        int j=0;
        for (SelectMenuDTO menu : listAllMenu) {
            data1[i]=menu.getId();
            data2[i]=menu.getNama();
            for(Map<String,Object> m:ltMenuAkses){
                if(menu.getId()==Long.parseLong(m.get("id").toString())){
                    data3[j]=menu.getId();
                    data4[j]=menu.getNama();
                    j++;
                    selectedMenuDTO.add(menu);
                    break;
                }
            }
            i++;
        }
        Set<Long> menuSelected = selectedMenuDTO.stream().map(SelectMenuDTO::getId).collect(Collectors.toSet());
        model.addAttribute("data",new ObjectMapper().convertValue(mapData,RespAksesDTO.class));
        model.addAttribute("listMenu",listAllMenu);
        model.addAttribute("menuSelected", menuSelected);
        webRequest.setAttribute("data1",data1,1);
        webRequest.setAttribute("data2",data2,1);
        webRequest.setAttribute("data3",data3,1);
        webRequest.setAttribute("data4",data4,1);
    }

    /** fungsi untuk mengambil data web yang sudah di set sebelumnya di function setDataMenuToEdit , agar tidak menghubungi server lagi meminta data yang sama */
    private void setDataTempEdit(Model model , WebRequest webRequest){
        Long data1[] = (Long[]) webRequest.getAttribute("data1",1);//menampung data id dari all menu
        String data2[] = (String[]) webRequest.getAttribute("data2",1);//menampung data nama dari all menu
        Long data3[] = (Long[]) webRequest.getAttribute("data3",1);//menampung data id dari menu akses
        String data4[] = (String[]) webRequest.getAttribute("data4",1);//menampung data nama dari menu akses
        List<SelectMenuDTO> selectedMenuDTO = new ArrayList<>();
        List<SelectMenuDTO> listAllMenu = new ArrayList<>();
        SelectMenuDTO selectMenuDTO = null;
        for(int i=0;i<data1.length;i++){
            selectMenuDTO = new SelectMenuDTO();
            selectMenuDTO.setId(data1[i]);
            selectMenuDTO.setNama(data2[i]);
            listAllMenu.add(selectMenuDTO);
        }

        for(int i=0;i<data3.length;i++){
            selectMenuDTO = new SelectMenuDTO();
            selectMenuDTO.setId(data3[i]);
            selectMenuDTO.setNama(data4[i]);
            selectedMenuDTO.add(selectMenuDTO);
        }
        Set<Long> menuSelected = selectedMenuDTO.stream().map(SelectMenuDTO::getId).collect(Collectors.toSet());
        model.addAttribute("listMenu",listAllMenu);
        model.addAttribute("menuSelected", menuSelected);
    }

    /** fungsi untuk mengambil data web yang sudah di set sebelumnya di function openModalAdd , agar tidak menghubungi server lagi meminta data menu yang sama */
    private void setDataTempAdd(Model model , WebRequest webRequest){
        Long data1[] = (Long[]) webRequest.getAttribute("data1",1);//menampung data id dari all menu
        String data2[] = (String[]) webRequest.getAttribute("data2",1);//menampung data nama dari all menu
        List<SelectMenuDTO> listAllMenu = new ArrayList<>();
        SelectMenuDTO selectMenuDTO = null;
        for(int i=0;i<data1.length;i++){
            selectMenuDTO = new SelectMenuDTO();
            selectMenuDTO.setId(data1[i]);
            selectMenuDTO.setNama(data2[i]);
            listAllMenu.add(selectMenuDTO);
        }
        model.addAttribute("x",listAllMenu);
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