package com.mbg.gui.dapur;

import com.mbg.dao.BahanBakuDao;
import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.model.BahanBaku;
import com.mbg.model.Permintaan;
import com.mbg.model.PermintaanDetail;
import com.mbg.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class dapur extends JFrame {

    private User loggedInUser;
    private BahanBakuDao bahanDao;
    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;

    private JTable tableStok;
    private DefaultTableModel modelStok;
    private JTextField txtMenuMasakan;
    private JTextField txtJumlahPorsi;
    private JComboBox<BahanBakuItem> cmbBahanBaku;
    private JTextField txtQtyBahan;
    private JTable tableKeranjang;
    private DefaultTableModel modelKeranjang;
    private List<PermintaanDetail> keranjangPermintaan;

    public dapur(User user) {
        this.loggedInUser = user;

        // Inisialisasi DAO
        this.bahanDao = new BahanBakuDao();
        this.permintaanDao = new PermintaanDao();
        this.detailDao = new PermintaanDetailDao();
        this.keranjangPermintaan = new ArrayList<>();

        initComponents();
        loadDataStok();
        loadDataBahanCombo();
    }

    private void initComponents() {
        setTitle("Aplikasi Dapur - " + (loggedInUser != null ? loggedInUser.getName() : "User"));
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panelStok = new JPanel(new BorderLayout(10, 10));
        panelStok.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblTitleStok = new JLabel("Stok Bahan Baku Tersedia");
        lblTitleStok.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelStok.add(lblTitleStok, BorderLayout.NORTH);
        String[] colStok = {"ID", "Nama Bahan", "Kategori", "Jumlah", "Satuan", "Status", "Kadaluarsa"};
        modelStok = new DefaultTableModel(colStok, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };
        tableStok = new JTable(modelStok);
        panelStok.add(new JScrollPane(tableStok), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Data Stok");
        btnRefresh.addActionListener(e -> loadDataStok());
        panelStok.add(btnRefresh, BorderLayout.SOUTH);

        tabbedPane.addTab("Lihat Stok Bahan", panelStok);
        JPanel panelPermintaan = createPermintaanPanel();
        tabbedPane.addTab("Buat Permintaan Baru", panelPermintaan);
        add(tabbedPane);
    }

    private JPanel createPermintaanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Form Header (Atas)
        JPanel panelHeader = new JPanel(new GridLayout(3, 2, 10, 10));
        panelHeader.setBorder(BorderFactory.createTitledBorder("Detail Masakan"));

        panelHeader.add(new JLabel("Nama Menu Masakan:"));
        txtMenuMasakan = new JTextField();
        panelHeader.add(txtMenuMasakan);

        panelHeader.add(new JLabel("Jumlah Porsi:"));
        txtJumlahPorsi = new JTextField();
        panelHeader.add(txtJumlahPorsi);

        panelHeader.add(new JLabel("Tanggal Masak:"));
        JLabel lblTgl = new JLabel(LocalDate.now().plusDays(1).toString() + " (Besok)");
        panelHeader.add(lblTgl);

        panel.add(panelHeader, BorderLayout.NORTH);

        // 2. Form Input Bahan (Tengah)
        JPanel panelTengah = new JPanel(new BorderLayout(5, 5));
        panelTengah.setBorder(BorderFactory.createTitledBorder("Daftar Bahan yang Dibutuhkan"));

        // Baris Input Bahan
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInput.add(new JLabel("Pilih Bahan:"));
        cmbBahanBaku = new JComboBox<>();
        cmbBahanBaku.setPreferredSize(new Dimension(250, 25));
        panelInput.add(cmbBahanBaku);

        panelInput.add(new JLabel("Jumlah:"));
        txtQtyBahan = new JTextField(5);
        panelInput.add(txtQtyBahan);

        JButton btnTambah = new JButton("+ Tambah ke List");
        btnTambah.addActionListener(e -> tambahBahanKeList());
        panelInput.add(btnTambah);

        panelTengah.add(panelInput, BorderLayout.NORTH);

        // Tabel Keranjang
        String[] colKeranjang = {"ID Bahan", "Nama Bahan", "Jumlah Diminta", "Satuan"};
        modelKeranjang = new DefaultTableModel(colKeranjang, 0);
        tableKeranjang = new JTable(modelKeranjang);
        panelTengah.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);

        panel.add(panelTengah, BorderLayout.CENTER);

        // 3. Tombol Aksi (Bawah)
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReset = new JButton("Reset Form");
        btnReset.addActionListener(e -> resetForm());

        JButton btnKirim = new JButton("Kirim Permintaan");
        btnKirim.setBackground(new Color(0, 128, 0));
        btnKirim.setForeground(Color.WHITE);
        btnKirim.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnKirim.addActionListener(e -> kirimPermintaan());

        panelBawah.add(btnReset);
        panelBawah.add(btnKirim);

        panel.add(panelBawah, BorderLayout.SOUTH);

        return panel;
    }

    private void loadDataStok() {
        try {
            modelStok.setRowCount(0); 
            List<BahanBaku> list = bahanDao.getAll();
            for (BahanBaku b : list) {
                modelStok.addRow(new Object[]{
                        b.getId(),
                        b.getNama(),
                        b.getKategori(),
                        b.getJumlah(),
                        b.getSatuan(),
                        b.getStatus(),
                        b.getTanggalKadaluarsa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat stok: " + e.getMessage());
        }
    }

    private void loadDataBahanCombo() {
        try {
            cmbBahanBaku.removeAllItems();
            List<BahanBaku> list = bahanDao.getAll();
            for (BahanBaku b : list) {
                cmbBahanBaku.addItem(new BahanBakuItem(b.getId(), b.getNama(), b.getSatuan()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tambahBahanKeList() {
        try {
            BahanBakuItem selected = (BahanBakuItem) cmbBahanBaku.getSelectedItem();
            String qtyStr = txtQtyBahan.getText().trim();

            if (selected == null || qtyStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih bahan dan masukkan jumlah!");
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0");
                return;
            }

            // Tambah ke list sementara (memori)
            PermintaanDetail detail = new PermintaanDetail();
            detail.setBahanId(selected.id);
            detail.setJumlahDiminta(qty);
            keranjangPermintaan.add(detail);

            // Tampilkan di tabel UI
            modelKeranjang.addRow(new Object[]{selected.id, selected.nama, qty, selected.satuan});

            // Reset input kecil
            txtQtyBahan.setText("");
            cmbBahanBaku.requestFocus();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka bulat!");
        }
    }

    private void kirimPermintaan() {
        if (txtMenuMasakan.getText().isEmpty() || txtJumlahPorsi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap isi Nama Menu dan Jumlah Porsi.");
            return;
        }

        if (keranjangPermintaan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Daftar bahan masih kosong. Tambahkan bahan terlebih dahulu.");
            return;
        }

        try {
            Permintaan p = new Permintaan();
            p.setPemohonId(loggedInUser != null ? loggedInUser.getId() : 0);
            p.setMenuMakan(txtMenuMasakan.getText());
            p.setJumlahPorsi(Integer.parseInt(txtJumlahPorsi.getText()));
            p.setTglMasak(Date.valueOf(LocalDate.now().plusDays(1)));
            p.setStatus("menunggu");
            p.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            p = permintaanDao.save(p);

            for (PermintaanDetail d : keranjangPermintaan) {
                d.setPermintaanId(p.getId());
                detailDao.save(d);
            }

            JOptionPane.showMessageDialog(this, "Sukses! Permintaan ID " + p.getId() + " berhasil dikirim.");
            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan permintaan: " + e.getMessage());
        }
    }

    private void resetForm() {
        txtMenuMasakan.setText("");
        txtJumlahPorsi.setText("");
        txtQtyBahan.setText("");
        modelKeranjang.setRowCount(0);
        keranjangPermintaan.clear();
    }

    // --- HELPER CLASS UNTUK COMBOBOX ---
    private static class BahanBakuItem {
        int id;
        String nama;
        String satuan;

        public BahanBakuItem(int id, String nama, String satuan) {
            this.id = id;
            this.nama = nama;
            this.satuan = satuan;
        }

        @Override
        public String toString() {
            return nama + " (" + satuan + ")";
        }
    }
}