package com.juaracoding.dto.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ValProductDTO {

    @NotNull
    private Long id;

    @Pattern(regexp = "^[\\w\\s]{5,10}$",message = "Alfanumerik dan Spasi Min 5 Maks 10")
//    @JsonProperty("nama-lengkap")
    private String nama;//nama-lengkap

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{10,20}$",message = "Alfanumerik dan Spasi Min 10 Maks 20")
    private String desc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}