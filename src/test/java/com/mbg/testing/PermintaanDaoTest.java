package com.mbg.testing;

import com.mbg.dao.PermintaanDao;
import com.mbg.model.Permintaan;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PermintaanDaoTest {

    private static PermintaanDao permintaanDao;
    private static Permintaan permintaan;
    private static Integer reqId;

    @BeforeAll
    static void init() {
        permintaanDao = new PermintaanDao();
        permintaan = new Permintaan();
        permintaan.setPemohonId(1); // Asumsi User ID 1 ada (Admin/Seeder)
        permintaan.setMenuMakan("Nasi Goreng Test");
        permintaan.setJumlahPorsi(100);
        permintaan.setTglMasak(Date.valueOf(LocalDate.now().plusDays(1)));
        permintaan.setStatus("menunggu");
        permintaan.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    @Order(1)
    @DisplayName("Buat Permintaan Baru")
    void testCreateRequest() throws Exception {
        Permintaan saved = permintaanDao.save(permintaan);
        reqId = saved.getId();
        Assertions.assertNotNull(reqId);
    }

    @Test
    @Order(2)
    @DisplayName("Cek Status Menunggu")
    void testGetPending() throws Exception {
        List<Permintaan> pending = permintaanDao.getPendingRequests();
        boolean found = pending.stream().anyMatch(p -> p.getId().equals(reqId));
        Assertions.assertTrue(found, "Permintaan baru harus muncul di list pending");
    }

    @Test
    @Order(3)
    @DisplayName("Update Status jadi Disetujui")
    void testApproveRequest() throws Exception {
        Permintaan p = permintaanDao.get(reqId);
        p.setStatus("disetujui");
        permintaanDao.update(p);

        Permintaan updated = permintaanDao.get(reqId);
        Assertions.assertEquals("disetujui", updated.getStatus());
    }

    @AfterAll
    static void cleanUp() {
        try {
            if (reqId != null) permintaanDao.remove(reqId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}