package com.juaracoding.dto.validation;


import com.juaracoding.dto.rel.RelMenuDTO;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class SelectAksesDTO {

    @Pattern(regexp = "^[\\w\\s]{5,50}$",message = "Alfanumerik dengan spasi min 5 maks 50 karakter")
    private String nama;

    @Pattern(regexp = "^[\\w\\s]{5,100}$",message = "Alfanumerik dengan spasi min 5 maks 100 karakter")
    private String deskripsi;

    private List<String> ltMenu;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public List<String> getLtMenu() {
        return ltMenu;
    }

    public void setLtMenu(List<String> ltMenu) {
        this.ltMenu = ltMenu;
    }
}
