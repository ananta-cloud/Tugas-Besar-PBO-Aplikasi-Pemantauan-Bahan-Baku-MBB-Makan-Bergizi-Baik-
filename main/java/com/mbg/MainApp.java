package com.mbg;

import com.mbg.dao.*;
import com.mbg.model.*;
import com.codeway.daoTemplate.utils.TemplateLogger;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    private static Scanner scan = new Scanner(System.in);
    private static UserDao userDao = new UserDao();
    private static BahanBakuDao bahanDao = new BahanBakuDao();
    private static PermintaanDao permintaanDao = new PermintaanDao();
    private static PermintaanDetailDao detailDao = new PermintaanDetailDao();

    private static User loggedInUser = null;

    public static void main(String[] args) {
        // Matikan log query agar console bersih
        TemplateLogger.shouldLog = false;

        System.out.println("=== APLIKASI PEMANTAUAN BAHAN BAKU MBG ===");

        try {
            login();
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan sistem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- HELPER UNTUK VALIDASI ANGKA (ANTI CRASH) ---
    private static int bacaInputAngka(String pesan) {
        while (true) {
            System.out.print(pesan);
            String input = scan.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">> ERROR: Harap masukkan angka saja (contoh: 10). Jangan tulis satuan seperti 'kg' atau 'liter'.");
            }
        }
    }

    private static void login() throws Exception {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Masukkan Email: ");
        String email = scan.nextLine();

        System.out.print("Masukkan Password (pass123): ");
        String pass = scan.nextLine();

        User user = userDao.getByEmail(email);

        if (user != null) {
            loggedInUser = user;
            System.out.println("Login Berhasil! Selamat datang, " + user.getName() + " [" + user.getRole().toUpperCase() + "]");

            if ("gudang".equalsIgnoreCase(user.getRole())) {
                menuGudang();
            } else if ("dapur".equalsIgnoreCase(user.getRole())) {
                menuDapur();
            }
        } else {
            System.out.println(">> Gagal: Email tidak ditemukan!");
            login();
        }
    }

    // ================= MENU DAPUR =================
    private static void menuDapur() throws Exception {
        while (true) {
            System.out.println("\n--- MENU DAPUR ---");
            System.out.println("1. Lihat Stok Bahan Baku");
            System.out.println("2. Buat Permintaan Baru");
            System.out.println("3. Keluar");

            // Menggunakan String untuk menu agar fleksibel
            System.out.print("Pilih: ");
            String choice = scan.nextLine();

            if ("1".equals(choice)) {
                lihatStok();
            } else if ("2".equals(choice)) {
                buatPermintaan();
            } else if ("3".equals(choice)) {
                System.out.println("Sampai Jumpa!");
                System.exit(0);
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void lihatStok() throws Exception {
        List<BahanBaku> list = bahanDao.getAll();
        System.out.println("\n--- STOK BAHAN BAKU ---");
        System.out.printf("%-5s %-25s %-10s %-10s %-15s\n", "ID", "Nama", "Jumlah", "Satuan", "Status");
        System.out.println("---------------------------------------------------------------------");
        for (BahanBaku b : list) {
            System.out.printf("%-5d %-25s %-10d %-10s %-15s\n",
                    b.getId(), b.getNama(), b.getJumlah(), b.getSatuan(), b.getStatus());
        }
        System.out.println("---------------------------------------------------------------------");
    }

    private static void buatPermintaan() throws Exception {
        System.out.println("\n--- BUAT PERMINTAAN ---");

        Permintaan p = new Permintaan();
        p.setPemohonId(loggedInUser.getId());
        p.setTglMasak(Date.valueOf(LocalDate.now().plusDays(1))); // Masak besok
        p.setStatus("menunggu");
        p.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        System.out.print("Menu Masakan: ");
        p.setMenuMakan(scan.nextLine());

        // Gunakan helper validasi angka
        p.setJumlahPorsi(bacaInputAngka("Jumlah Porsi (angka): "));

        // Simpan Header Permintaan
        p = permintaanDao.save(p);
        System.out.println(">> Header permintaan dibuat (ID: " + p.getId() + "). Silakan masukkan bahan.");

        lihatStok(); // Tampilkan stok agar user tahu ID bahan

        // Input Detail Bahan
        while (true) {
            System.out.println("\n-- Tambah Bahan --");
            int idBahan = bacaInputAngka("ID Bahan (ketik 0 untuk SELESAI): ");

            if (idBahan == 0) break;

            // Validasi apakah ID bahan ada (opsional, untuk UX lebih baik)
            BahanBaku cekBahan = bahanDao.get(idBahan);
            if (cekBahan == null) {
                System.out.println(">> Error: ID Bahan tidak ditemukan.");
                continue;
            }

            int qty = bacaInputAngka("Jumlah Diminta (" + cekBahan.getSatuan() + "): ");

            PermintaanDetail pd = new PermintaanDetail();
            pd.setPermintaanId(p.getId());
            pd.setBahanId(idBahan);
            pd.setJumlahDiminta(qty);
            detailDao.save(pd);

            System.out.println(">> OK: " + cekBahan.getNama() + " (" + qty + " " + cekBahan.getSatuan() + ") ditambahkan.");
        }
        System.out.println(">> SUKSES: Permintaan berhasil dikirim ke Gudang.");
    }

    // ================= MENU GUDANG =================
    private static void menuGudang() throws Exception {
        while (true) {
            System.out.println("\n--- MENU GUDANG ---");
            System.out.println("1. Kelola Permintaan (Approve/Reject)");
            System.out.println("2. Lihat Stok Bahan");
            System.out.println("3. Keluar");
            System.out.print("Pilih: ");
            String choice = scan.nextLine();

            if ("1".equals(choice)) {
                kelolaPermintaan();
            } else if ("2".equals(choice)) {
                lihatStok();
            } else if ("3".equals(choice)) {
                System.exit(0);
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void kelolaPermintaan() throws Exception {
        List<Permintaan> pending = permintaanDao.getByStatus("menunggu");
        if (pending.isEmpty()) {
            System.out.println(">> Tidak ada permintaan dengan status 'menunggu'.");
            return;
        }

        for (Permintaan p : pending) {
            User pemohon = userDao.get(p.getPemohonId());
            System.out.println("\n========================================");
            System.out.println("ID REQUEST: " + p.getId());
            System.out.println("Pemohon   : " + (pemohon!=null ? pemohon.getName() : "Unknown"));
            System.out.println("Menu      : " + p.getMenuMakan() + " (" + p.getJumlahPorsi() + " porsi)");
            System.out.println("========================================");

            List<PermintaanDetail> details = detailDao.getByPermintaanId(p.getId());
            System.out.println("Daftar Bahan:");

            boolean stokCukup = true;
            for (PermintaanDetail pd : details) {
                BahanBaku bb = bahanDao.get(pd.getBahanId());
                String statusStok = (bb.getJumlah() >= pd.getJumlahDiminta()) ? "[OK]" : "[KURANG]";
                System.out.printf(" - %-20s : Minta %-3d | Stok %-3d %s\n",
                        bb.getNama(), pd.getJumlahDiminta(), bb.getJumlah(), statusStok);

                if (bb.getJumlah() < pd.getJumlahDiminta()) stokCukup = false;
            }

            System.out.println("----------------------------------------");
            System.out.println("1: SETUJUI (Approve)");
            System.out.println("2: TOLAK (Reject)");
            System.out.println("0: LEWATI (Skip)");

            int aksi = bacaInputAngka("Pilih Aksi: ");

            if (aksi == 1) {
                if (!stokCukup) {
                    System.out.println(">> GAGAL: Stok tidak mencukupi untuk menyetujui permintaan ini.");
                } else {
                    // Kurangi Stok
                    for (PermintaanDetail pd : details) {
                        BahanBaku bb = bahanDao.get(pd.getBahanId());
                        bb.setJumlah(bb.getJumlah() - pd.getJumlahDiminta());
                        if (bb.getJumlah() == 0) bb.setStatus("habis");
                        bahanDao.update(bb);
                    }
                    p.setStatus("disetujui");
                    permintaanDao.update(p);
                    System.out.println(">> SUKSES: Permintaan DISETUJUI. Stok telah dikurangi.");
                }
            } else if (aksi == 2) {
                p.setStatus("ditolak");
                permintaanDao.update(p);
                System.out.println(">> SUKSES: Permintaan DITOLAK.");
            } else {
                System.out.println(">> Permintaan dilewati.");
            }
        }
    }
}