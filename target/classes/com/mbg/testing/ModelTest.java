package com.mbg.testing;

import com.mbg.model.BahanBaku;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

public class ModelTest {

    @Test
    void testBahanBakuEntity() {
        // 1. Setup Data
        BahanBaku bahan = new BahanBaku();
        String nama = "Beras Putih";
        int jumlah = 50;
        String satuan = "kg";
        Date tglMasuk = Date.valueOf(LocalDate.now());

        // 2. Action (Set Data)
        bahan.setNama(nama);
        bahan.setJumlah(jumlah);
        bahan.setSatuan(satuan);
        bahan.setTanggalMasuk(tglMasuk);
        bahan.setStatus("tersedia");

        // 3. Assertion (Verifikasi Data)
        Assertions.assertEquals("Beras Putih", bahan.getNama());
        Assertions.assertEquals(50, bahan.getJumlah());
        Assertions.assertEquals("kg", bahan.getSatuan());
        Assertions.assertEquals("tersedia", bahan.getStatus());

        System.out.println("Test Model BahanBaku Berhasil.");
    }
}