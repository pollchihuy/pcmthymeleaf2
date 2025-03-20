package com.juaracoding.dto.report;


public class RepMenuDTO {

    private Long id;
    private String nama;
    private String path;
    private String namaGroup;
    private String deskripsiGroup;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNamaGroup() {
        return namaGroup;
    }

    public void setNamaGroup(String namaGroup) {
        this.namaGroup = namaGroup;
    }

    public String getDeskripsiGroup() {
        return deskripsiGroup;
    }

    public void setDeskripsiGroup(String deskripsiGroup) {
        this.deskripsiGroup = deskripsiGroup;
    }
}
