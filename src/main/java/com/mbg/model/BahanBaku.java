package com.mbg.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Table(name = "bahan_baku")
public class BahanBaku {

    @Id
    private Integer id;

    private String nama;
    private String kategori;
    private Integer jumlah;
    private String satuan;

    @Column(name = "tanggal_masuk")
    private Date tanggalMasuk;

    @Column(name = "tanggal_kadaluarsa")
    private Date tanggalKadaluarsa;

    private String status; // tersedia, segera_kadaluarsa, kadaluarsa, habis

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Getters and Setters (Ringkas)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public Integer getJumlah() { return jumlah; }
    public void setJumlah(Integer jumlah) { this.jumlah = jumlah; }
    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }
    public Date getTanggalKadaluarsa() { return tanggalKadaluarsa; }
    public void setTanggalKadaluarsa(Date tanggalKadaluarsa) { this.tanggalKadaluarsa = tanggalKadaluarsa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}