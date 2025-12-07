package com.mbg.testing;

import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import com.mbg.dao.UserDao;
import com.mbg.model.User;

public class UserDaoTest {

    private UserDao userDao;
    private User testUser;

    @BeforeEach
    void setUp() {
        userDao = new UserDao();
        // User dummy untuk testing
        testUser = new User();
        testUser.setName("User Test JUnit");
        testUser.setEmail("junit.test@mbg.id");
        // Hash password
        String hashedPass = BCrypt.hashpw("password123", BCrypt.gensalt());
        testUser.setPassword(hashedPass);
        testUser.setRole("gudang");
        testUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void testSaveAndFindUser() {
        try {
            System.out.println("Menyimpan user test...");
            User savedUser = userDao.save(testUser);
            Assertions.assertNotNull(savedUser.getId(), "ID User tidak boleh null setelah save");
            System.out.println("Mencari user berdasarkan email...");
            User foundUser = userDao.getByEmail("junit.test@mbg.id");
            Assertions.assertNotNull(foundUser, "User harus ditemukan di database");
            Assertions.assertEquals("User Test JUnit", foundUser.getName());
            boolean passMatch = BCrypt.checkpw("password123", foundUser.getPassword());
            Assertions.assertTrue(passMatch, "Password harus valid");
            System.out.println("Test CRUD User Berhasil. ID Baru: " + savedUser.getId());
            testUser.setId(savedUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Gagal melakukan operasi DB: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (testUser.getId() != null) {
            try {
                userDao.remove(testUser.getId());
                System.out.println("Data test berhasil dihapus (ID: " + testUser.getId() + ")");
            } catch (Exception e) {
                System.err.println("Gagal menghapus data test: " + e.getMessage());
            }
        }
    }
}