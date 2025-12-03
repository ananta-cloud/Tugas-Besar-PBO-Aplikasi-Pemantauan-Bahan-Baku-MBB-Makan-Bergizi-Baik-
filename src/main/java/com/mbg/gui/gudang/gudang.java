package com.mbg.gui.gudang;

import com.mbg.pattern.observer.Observer;
import com.mbg.pattern.observer.PermintaanSubject;

import com.mbg.dao.BahanBakuDao;
import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.dao.UserDao;
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
import java.util.List;

public class gudang extends JFrame implements Observer {

    private User loggedInUser;
    private BahanBakuDao bahanDao;
    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;
    private UserDao userDao;
    private PermintaanSubject permintaanSubject;

    // Tab 1: Kelola Stok Bahan
    private JTable tableStokGudang;
    private DefaultTableModel modelStokGudang;
    private JTextField txtNamaBahan, txtKategori, txtJumlah, txtSatuan;
    private JTextField txtTanggalMasuk, txtTanggalKadaluarsa;
    private JComboBox<String> cmbStatus;

    // Tab 2: Kelola Permintaan
    private JTable tablePermintaan;
    private DefaultTableModel modelPermintaan;
    private JTextArea txtDetailPermintaan;

    public gudang(User user) {
        this.loggedInUser = user;
        
        // Inisialisasi DAO
        this.bahanDao = new BahanBakuDao();
        this.permintaanDao = new PermintaanDao();
        this.detailDao = new PermintaanDetailDao();
        this.userDao = new UserDao();
        this.permintaanSubject = new PermintaanSubject(permintaanDao);
        this.permintaanSubject.attach(this);

        initComponents();
        loadDataStokGudang();
        loadDataPermintaan();
    }

    @Override
    public void update(String eventType, Object data) {
        System.out.println("Dashboard Gudang menerima notifikasi: " + eventType);
        
        if ("PERMINTAAN_BARU".equals(eventType)) {
            JOptionPane.showMessageDialog(this, 
                    "Ada permintaan baru dari dapur!", 
                    "Notifikasi", 
                    JOptionPane.INFORMATION_MESSAGE);
            loadDataPermintaan();
        } else if ("PERMINTAAN_DIUPDATE".equals(eventType)) {
            loadDataPermintaan();
        } else if ("PERMINTAAN_DIHAPUS".equals(eventType)) {
            loadDataPermintaan();
        }
    }

    private void initComponents() {
        setTitle("Aplikasi Gudang - " + (loggedInUser != null ? loggedInUser.getName() : "User"));
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Kelola Stok Bahan
        JPanel panelStok = createPanelKelolaStok();
        tabbedPane.addTab("Kelola Stok Bahan", panelStok);

        // Tab 2: Kelola Permintaan dari Dapur
        JPanel panelPermintaan = createPanelPermintaan();
        tabbedPane.addTab("Kelola Permintaan", panelPermintaan);

        add(tabbedPane);
    }

    // ========== TAB 1: KELOLA STOK BAHAN ==========
    private JPanel createPanelKelolaStok() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel lblTitle = new JLabel("Kelola Stok Bahan Baku");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Tabel Stok
        String[] colStok = {"ID", "Nama Bahan", "Kategori", "Jumlah", "Satuan", "Tgl Masuk", "Tgl Kadaluarsa", "Status"};
        modelStokGudang = new DefaultTableModel(colStok, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableStokGudang = new JTable(modelStokGudang);
        tableStokGudang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBahanToForm();
            }
        });

        JScrollPane scrollStok = new JScrollPane(tableStokGudang);
        panel.add(scrollStok, BorderLayout.CENTER);

        // Form Input
        JPanel panelForm = createFormInputBahan();
        panel.add(panelForm, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormInputBahan() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Form Input/Edit Bahan"));

        JPanel panelInput = new JPanel(new GridLayout(4, 4, 10, 10));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Baris 1
        panelInput.add(new JLabel("Nama Bahan:"));
        txtNamaBahan = new JTextField();
        panelInput.add(txtNamaBahan);

        panelInput.add(new JLabel("Kategori:"));
        txtKategori = new JTextField();
        panelInput.add(txtKategori);

        // Baris 2
        panelInput.add(new JLabel("Jumlah:"));
        txtJumlah = new JTextField();
        panelInput.add(txtJumlah);

        panelInput.add(new JLabel("Satuan:"));
        txtSatuan = new JTextField();
        panelInput.add(txtSatuan);

        // Baris 3
        panelInput.add(new JLabel("Tanggal Masuk (YYYY-MM-DD):"));
        txtTanggalMasuk = new JTextField(LocalDate.now().toString());
        panelInput.add(txtTanggalMasuk);

        panelInput.add(new JLabel("Tanggal Kadaluarsa:"));
        txtTanggalKadaluarsa = new JTextField();
        panelInput.add(txtTanggalKadaluarsa);

        // Baris 4
        panelInput.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"tersedia", "segera_kadaluarsa", "kadaluarsa", "habis"});
        panelInput.add(cmbStatus);

        panelInput.add(new JLabel("")); // Spacer
        panelInput.add(new JLabel("")); // Spacer

        panel.add(panelInput, BorderLayout.CENTER);

        // Tombol Aksi
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnTambah = new JButton("Tambah Bahan Baru");
        btnTambah.setBackground(new Color(34, 139, 34));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnTambah.addActionListener(e -> tambahBahanBaru());

        JButton btnUpdate = new JButton("Update Bahan Terpilih");
        btnUpdate.setBackground(new Color(30, 144, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnUpdate.addActionListener(e -> updateBahan());

        JButton btnHapus = new JButton("Hapus Bahan Terpilih");
        btnHapus.setBackground(new Color(220, 20, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnHapus.addActionListener(e -> hapusBahan());

        JButton btnReset = new JButton("Reset Form");
        btnReset.addActionListener(e -> resetFormBahan());

        panelButton.add(btnReset);
        panelButton.add(btnTambah);
        panelButton.add(btnUpdate);
        panelButton.add(btnHapus);

        panel.add(panelButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadDataStokGudang() {
        try {
            modelStokGudang.setRowCount(0);
            List<BahanBaku> list = bahanDao.getAll();
            for (BahanBaku b : list) {
                modelStokGudang.addRow(new Object[]{
                        b.getId(),
                        b.getNama(),
                        b.getKategori(),
                        b.getJumlah(),
                        b.getSatuan(),
                        b.getTanggalMasuk(),
                        b.getTanggalKadaluarsa(),
                        b.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data stok: " + e.getMessage());
        }
    }

    private void loadSelectedBahanToForm() {
        int selectedRow = tableStokGudang.getSelectedRow();
        if (selectedRow >= 0) {
            txtNamaBahan.setText(modelStokGudang.getValueAt(selectedRow, 1).toString());
            txtKategori.setText(modelStokGudang.getValueAt(selectedRow, 2).toString());
            txtJumlah.setText(modelStokGudang.getValueAt(selectedRow, 3).toString());
            txtSatuan.setText(modelStokGudang.getValueAt(selectedRow, 4).toString());
            
            Object tglMasuk = modelStokGudang.getValueAt(selectedRow, 5);
            txtTanggalMasuk.setText(tglMasuk != null ? tglMasuk.toString() : "");
            
            Object tglKadaluarsa = modelStokGudang.getValueAt(selectedRow, 6);
            txtTanggalKadaluarsa.setText(tglKadaluarsa != null ? tglKadaluarsa.toString() : "");
            
            cmbStatus.setSelectedItem(modelStokGudang.getValueAt(selectedRow, 7).toString());
        }
    }

    private void tambahBahanBaru() {
        try {
            if (txtNamaBahan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama bahan harus diisi!");
                return;
            }

            BahanBaku bahan = new BahanBaku();
            bahan.setNama(txtNamaBahan.getText().trim());
            bahan.setKategori(txtKategori.getText().trim());
            bahan.setJumlah(Integer.parseInt(txtJumlah.getText().trim()));
            bahan.setSatuan(txtSatuan.getText().trim());
            bahan.setTanggalMasuk(Date.valueOf(txtTanggalMasuk.getText().trim()));
            
            String kadaluarsa = txtTanggalKadaluarsa.getText().trim();
            if (!kadaluarsa.isEmpty()) {
                bahan.setTanggalKadaluarsa(Date.valueOf(kadaluarsa));
            }
            
            bahan.setStatus(cmbStatus.getSelectedItem().toString());
            bahan.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            bahanDao.save(bahan);
            JOptionPane.showMessageDialog(this, "Bahan berhasil ditambahkan!");
            loadDataStokGudang();
            resetFormBahan();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambah bahan: " + e.getMessage());
        }
    }

    private void updateBahan() {
        int selectedRow = tableStokGudang.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih bahan yang akan diupdate!");
            return;
        }

        try {
            Integer id = (Integer) modelStokGudang.getValueAt(selectedRow, 0);
            BahanBaku bahan = bahanDao.get(id);

            if (bahan == null) {
                JOptionPane.showMessageDialog(this, "Data bahan tidak ditemukan!");
                return;
            }

            bahan.setNama(txtNamaBahan.getText().trim());
            bahan.setKategori(txtKategori.getText().trim());
            bahan.setJumlah(Integer.parseInt(txtJumlah.getText().trim()));
            bahan.setSatuan(txtSatuan.getText().trim());
            bahan.setTanggalMasuk(Date.valueOf(txtTanggalMasuk.getText().trim()));
            
            String kadaluarsa = txtTanggalKadaluarsa.getText().trim();
            if (!kadaluarsa.isEmpty()) {
                bahan.setTanggalKadaluarsa(Date.valueOf(kadaluarsa));
            }
            
            bahan.setStatus(cmbStatus.getSelectedItem().toString());

            bahanDao.update(bahan);
            JOptionPane.showMessageDialog(this, "Bahan berhasil diupdate!");
            loadDataStokGudang();
            resetFormBahan();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal update bahan: " + e.getMessage());
        }
    }

    private void hapusBahan() {
        int selectedRow = tableStokGudang.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih bahan yang akan dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus bahan ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Integer id = (Integer) modelStokGudang.getValueAt(selectedRow, 0);
                bahanDao.remove(id);
                JOptionPane.showMessageDialog(this, "Bahan berhasil dihapus!");
                loadDataStokGudang();
                resetFormBahan();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus bahan: " + e.getMessage());
            }
        }
    }

    private void resetFormBahan() {
        txtNamaBahan.setText("");
        txtKategori.setText("");
        txtJumlah.setText("");
        txtSatuan.setText("");
        txtTanggalMasuk.setText(LocalDate.now().toString());
        txtTanggalKadaluarsa.setText("");
        cmbStatus.setSelectedIndex(0);
        tableStokGudang.clearSelection();
    }

    // ========== TAB 2: KELOLA PERMINTAAN ==========
    private JPanel createPanelPermintaan() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Daftar Permintaan dari Dapur");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Split Panel: Kiri = Tabel, Kanan = Detail
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Panel Kiri: Tabel Permintaan
        JPanel panelKiri = new JPanel(new BorderLayout(5, 5));
        
        String[] colPermintaan = {"ID", "Pemohon", "Menu Masakan", "Porsi", "Tgl Masak", "Status"};
        modelPermintaan = new DefaultTableModel(colPermintaan, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePermintaan = new JTable(modelPermintaan);
        tablePermintaan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadDetailPermintaan();
            }
        });

        panelKiri.add(new JScrollPane(tablePermintaan), BorderLayout.CENTER);

        JButton btnRefreshPermintaan = new JButton("Refresh Data Permintaan");
        btnRefreshPermintaan.addActionListener(e -> loadDataPermintaan());
        panelKiri.add(btnRefreshPermintaan, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelKiri);

        // Panel Kanan: Detail & Aksi
        JPanel panelKanan = new JPanel(new BorderLayout(10, 10));
        panelKanan.setBorder(BorderFactory.createTitledBorder("Detail Permintaan"));

        txtDetailPermintaan = new JTextArea();
        txtDetailPermintaan.setEditable(false);
        txtDetailPermintaan.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelKanan.add(new JScrollPane(txtDetailPermintaan), BorderLayout.CENTER);

        // Tombol Aksi
        JPanel panelAksi = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JButton btnSetuju = new JButton("✓ Setujui Permintaan");
        btnSetuju.setBackground(new Color(34, 139, 34));
        btnSetuju.setForeground(Color.WHITE);
        btnSetuju.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSetuju.addActionListener(e -> prosesPermintaan("disetujui"));

        JButton btnTolak = new JButton("✗ Tolak Permintaan");
        btnTolak.setBackground(new Color(220, 20, 60));
        btnTolak.setForeground(Color.WHITE);
        btnTolak.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnTolak.addActionListener(e -> prosesPermintaan("ditolak"));

        panelAksi.add(btnSetuju);
        panelAksi.add(btnTolak);

        panelKanan.add(panelAksi, BorderLayout.SOUTH);

        splitPane.setRightComponent(panelKanan);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadDataPermintaan() {
        try {
            modelPermintaan.setRowCount(0);
            List<Permintaan> list = permintaanDao.getAll();
            
            for (Permintaan p : list) {
                String namaPemohon = "Unknown";
                try {
                    User pemohon = userDao.get(p.getPemohonId());
                    if (pemohon != null) {
                        namaPemohon = pemohon.getName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                modelPermintaan.addRow(new Object[]{
                        p.getId(),
                        namaPemohon,
                        p.getMenuMakan(),
                        p.getJumlahPorsi(),
                        p.getTglMasak(),
                        p.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat permintaan: " + e.getMessage());
        }
    }

    private void loadDetailPermintaan() {
        int selectedRow = tablePermintaan.getSelectedRow();
        if (selectedRow < 0) {
            txtDetailPermintaan.setText("");
            return;
        }

        try {
            Integer permintaanId = (Integer) modelPermintaan.getValueAt(selectedRow, 0);
            Permintaan p = permintaanDao.get(permintaanId);
            
            if (p == null) {
                txtDetailPermintaan.setText("Data permintaan tidak ditemukan.");
                return;
            }

            List<PermintaanDetail> details = detailDao.getByPermintaanId(permintaanId);

            StringBuilder sb = new StringBuilder();
            sb.append("=== DETAIL PERMINTAAN ===\n\n");
            sb.append("ID Permintaan : ").append(p.getId()).append("\n");
            sb.append("Menu Masakan  : ").append(p.getMenuMakan()).append("\n");
            sb.append("Jumlah Porsi  : ").append(p.getJumlahPorsi()).append("\n");
            sb.append("Tanggal Masak : ").append(p.getTglMasak()).append("\n");
            sb.append("Status        : ").append(p.getStatus()).append("\n");
            sb.append("\n=== DAFTAR BAHAN ===\n\n");

            if (details.isEmpty()) {
                sb.append("Tidak ada detail bahan.\n");
            } else {
                sb.append(String.format("%-5s %-25s %-10s %-10s\n", "No", "Nama Bahan", "Jumlah", "Satuan"));
                sb.append("─".repeat(60)).append("\n");

                int no = 1;
                for (PermintaanDetail d : details) {
                    BahanBaku bahan = bahanDao.get(d.getBahanId());
                    String namaBahan = bahan != null ? bahan.getNama() : "Unknown";
                    String satuan = bahan != null ? bahan.getSatuan() : "";

                    sb.append(String.format("%-5d %-25s %-10d %-10s\n", 
                            no++, namaBahan, d.getJumlahDiminta(), satuan));
                }
            }

            txtDetailPermintaan.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            txtDetailPermintaan.setText("Error loading details: " + e.getMessage());
        }
    }

    private void prosesPermintaan(String status) {
        int selectedRow = tablePermintaan.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih permintaan yang akan diproses!");
            return;
        }

        try {
            Integer permintaanId = (Integer) modelPermintaan.getValueAt(selectedRow, 0);
            Permintaan p = permintaanDao.get(permintaanId);

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Data permintaan tidak ditemukan!");
                return;
            }

            String currentStatus = p.getStatus();
            if (!currentStatus.equals("menunggu")) {
                JOptionPane.showMessageDialog(this, 
                        "Permintaan ini sudah diproses dengan status: " + currentStatus);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Yakin ingin " + status + " permintaan ini?", 
                    "Konfirmasi", 
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Update status permintaan
                p.setStatus(status);
                permintaanDao.update(p);

                // TAMBAH: Notify observers
                permintaanSubject.notifyObservers("PERMINTAAN_DIUPDATE", p);

                // Jika disetujui, kurangi stok bahan
                if (status.equals("disetujui")) {
                    List<PermintaanDetail> details = detailDao.getByPermintaanId(permintaanId);
                    for (PermintaanDetail d : details) {
                        BahanBaku bahan = bahanDao.get(d.getBahanId());
                        if (bahan != null) {
                            int stokBaru = bahan.getJumlah() - d.getJumlahDiminta();
                            bahan.setJumlah(Math.max(0, stokBaru));
                            
                            if (stokBaru <= 0) {
                                bahan.setStatus("habis");
                            }
                            
                            bahanDao.update(bahan);
                        }
                    }
                    loadDataStokGudang();
                }

                JOptionPane.showMessageDialog(this, 
                        "Permintaan berhasil " + status + "!");
                loadDataPermintaan();
                txtDetailPermintaan.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memproses permintaan: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        if (permintaanSubject != null) {
            permintaanSubject.detach(this);
        }
        super.dispose();
    }
}