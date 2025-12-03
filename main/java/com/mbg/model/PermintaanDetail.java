package com.mbg.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "permintaan_detail")
public class PermintaanDetail {

    @Id
    private Integer id;

    @Column(name = "permintaan_id")
    private Integer permintaanId;

    @Column(name = "bahan_id")
    private Integer bahanId;

    @Column(name = "jumlah_diminta")
    private Integer jumlahDiminta;
    
    @Transient
    private BahanBaku bahanBaku;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPermintaanId() { return permintaanId; }
    public void setPermintaanId(Integer permintaanId) { this.permintaanId = permintaanId; }
    public Integer getBahanId() { return bahanId; }
    public void setBahanId(Integer bahanId) { this.bahanId = bahanId; }
    public Integer getJumlahDiminta() { return jumlahDiminta; }
    public void setJumlahDiminta(Integer jumlahDiminta) { this.jumlahDiminta = jumlahDiminta; }
    
    public BahanBaku getBahanBaku() { return bahanBaku; }
    public void setBahanBaku(BahanBaku bahanBaku) { this.bahanBaku = bahanBaku; }
}