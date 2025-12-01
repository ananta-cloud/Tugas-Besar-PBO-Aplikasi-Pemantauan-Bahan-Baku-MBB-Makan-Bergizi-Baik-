package com.mbg.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Table(name = "permintaan")
public class Permintaan {

    @Id
    private Integer id;

    @Column(name = "pemohon_id")
    private Integer pemohonId;

    @Column(name = "tgl_masak")
    private Date tglMasak;

    @Column(name = "menu_makan")
    private String menuMakan;

    @Column(name = "jumlah_porsi")
    private Integer jumlahPorsi;

    private String status; // menunggu, disetujui, ditolak

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Properti Transient (Tidak disimpan langsung ke tabel permintaan, tapi untuk relasi di Java)
    @Transient
    private User pemohon;
    
    @Transient
    private List<PermintaanDetail> detail;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPemohonId() { return pemohonId; }
    public void setPemohonId(Integer pemohonId) { this.pemohonId = pemohonId; }
    public Date getTglMasak() { return tglMasak; }
    public void setTglMasak(Date tglMasak) { this.tglMasak = tglMasak; }
    public String getMenuMakan() { return menuMakan; }
    public void setMenuMakan(String menuMakan) { this.menuMakan = menuMakan; }
    public Integer getJumlahPorsi() { return jumlahPorsi; }
    public void setJumlahPorsi(Integer jumlahPorsi) { this.jumlahPorsi = jumlahPorsi; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public User getPemohon() { return pemohon; }
    public void setPemohon(User pemohon) { this.pemohon = pemohon; }
    public List<PermintaanDetail> getDetail() { return detail; }
    public void setDetail(List<PermintaanDetail> detail) { this.detail = detail; }
}