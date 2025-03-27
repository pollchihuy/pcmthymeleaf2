package com.juaracoding.httpservice;


import com.juaracoding.dto.validation.ValMenuDTO;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "menu-services",url = "http://localhost:8083/menu")
public interface MenuService {

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("Authorization") String token);

    @GetMapping("/all")
    public ResponseEntity<Object> allMenu(@RequestHeader("Authorization") String token);

    @GetMapping("/{sort}/{sortBy}/{page}")
    public ResponseEntity<Object> findByParam(
            @RequestHeader("Authorization") String token,
            @PathVariable(value = "sort") String sort,
            @PathVariable(value = "sortBy") String sortBy,
            @PathVariable(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value);

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("Authorization") String token,
                                       @RequestBody ValMenuDTO valMenuDTO);
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("Authorization") String token,
                                           @PathVariable(value = "id") Long id);

    @PutMapping("/{id}")
    public ResponseEntity<Object> edit(@RequestHeader("Authorization") String token,
                                       @PathVariable(value = "id") Long id,
                                       @RequestBody ValMenuDTO valMenuDTO);

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@RequestHeader("Authorization") String token,
            @PathVariable(value = "id") Long id);

    @GetMapping("/excel")
    public Response downloadExcel(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value
            );

    @GetMapping("/pdf")
    public Response downloadPdf(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "value") String value
    );

}