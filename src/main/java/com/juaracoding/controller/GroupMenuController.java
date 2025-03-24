package com.juaracoding.controller;


import com.juaracoding.dto.validation.ValGroupMenuDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("group-menu")
public class GroupMenuController {

    @GetMapping
    public String findAll(Model model,
                          WebRequest webRequest){

        return null;
    }

    //localhost:8093/group-menu
    //post
    @PostMapping("")
    public String save(
           @ModelAttribute("data") @Valid ValGroupMenuDTO valGroupMenuDTO,
           BindingResult bindingResult,
           Model model,
           WebRequest webRequest){

        return null;
    }

    @PostMapping("/e/{id}")
    public String edit(
            @ModelAttribute("data") @Valid ValGroupMenuDTO valGroupMenuDTO,
            BindingResult bindingResult,
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){

        return null;
    }

    @GetMapping("/d/{id}")
    public String delete(
            Model model,
            @PathVariable(value = "id") Long id,
            WebRequest webRequest){

        return null;
    }

    // localhost:8093/group-menu/1
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

        return null;
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
            WebRequest webRequest,
            HttpServletResponse response
    ){
        return null;
    }

    @GetMapping("/pdf")
    public ResponseEntity<Resource> downloadPdf(
            Model model,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value,
            WebRequest webRequest,
            HttpServletResponse response
    ){
        return null;
    }
}