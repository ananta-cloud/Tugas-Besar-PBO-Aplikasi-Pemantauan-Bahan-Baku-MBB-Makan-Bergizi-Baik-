package com.mbg.testing;

import com.mbg.dao.BahanBakuDao;
import com.mbg.model.BahanBaku;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BahanBakuDaoTest {

    private static BahanBakuDao bahanDao;
    private static BahanBaku bahan;
    private static Integer bahanId;

    @BeforeAll
    static void init() {
        bahanDao = new BahanBakuDao();
        bahan = new BahanBaku();
        bahan.setNama("Bahan Test JUnit");
        bahan.setKategori("Sayuran");
        bahan.setJumlah(50);
        bahan.setSatuan("kg");
        bahan.setTanggalMasuk(Date.valueOf(LocalDate.now()));
        bahan.setStatus("tersedia");
        bahan.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    @Order(1)
    @DisplayName("Tambah Bahan Baku")
    void testSaveBahan() throws Exception {
        BahanBaku saved = bahanDao.save(bahan);
        bahanId = saved.getId();
        Assertions.assertNotNull(bahanId, "ID Bahan harus ada");
    }

    @Test
    @Order(2)
    @DisplayName("Update Stok Bahan")
    void testUpdateBahan() throws Exception {
        BahanBaku b = bahanDao.get(bahanId);
        b.setJumlah(25);
        bahanDao.update(b);

        BahanBaku updated = bahanDao.get(bahanId);
        Assertions.assertEquals(25, updated.getJumlah());
    }

    @Test
    @Order(3)
    @DisplayName("Hapus Bahan Baku")
    void testDeleteBahan() throws Exception {
        if (bahanId != null) {
            bahanDao.remove(bahanId);
            Assertions.assertNull(bahanDao.get(bahanId));
        }
    }
}