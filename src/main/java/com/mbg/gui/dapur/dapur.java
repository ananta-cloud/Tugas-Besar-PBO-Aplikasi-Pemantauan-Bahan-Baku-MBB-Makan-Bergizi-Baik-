package com.mbg.gui.dapur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.mbg.dao.BahanBakuDao;
import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.model.BahanBaku;
import com.mbg.model.Permintaan;
import com.mbg.model.PermintaanDetail;
import com.mbg.model.User;

public class dapur extends JFrame {

    private User loggedInUser;
    private BahanBakuDao bahanDao;
    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;

    // Dashboard Components
    private JTable tablePesanan;
    private DefaultTableModel modelPesanan;
    private JComboBox<String> cmbFilterStatus;
    private JTextField txtSearch;
    private List<Permintaan> allPesanan;
    private int expandedRow = -1;

    // Input Pesanan Components
    private JTextField txtMenuMasakan;
    private JTextField txtJumlahPorsi;
    private JComboBox<BahanBakuItem> cmbBahanBaku;
    private JTextField txtQtyBahan;
    private JTable tableKeranjang;
    private DefaultTableModel modelKeranjang;
    private List<PermintaanDetail> keranjangPermintaan;

    // Auto-refresh timer
    private Timer refreshTimer;

    public dapur(User user) {
        this.loggedInUser = user;
        this.bahanDao = new BahanBakuDao();
        this.permintaanDao = new PermintaanDao();
        this.detailDao = new PermintaanDetailDao();
        this.keranjangPermintaan = new ArrayList<>();
        this.allPesanan = new ArrayList<>();

        initComponents();
        startAutoRefresh();
    }

    private void initComponents() {
        setTitle("Aplikasi Dapur - " + (loggedInUser != null ? loggedInUser.getName() : "User"));
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Dashboard
        JPanel panelDashboard = createDashboardPanel();
        tabbedPane.addTab("Dashboard Pesanan", panelDashboard);

        // Tab 2: Input Pesanan
        JPanel panelInputPesanan = createInputPesananPanel();
        tabbedPane.addTab("Buat Pesanan Baru", panelInputPesanan);

        add(tabbedPane);
        setVisible(true);
    }

    // ========== DASHBOARD TAB ==========
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header dengan Filter dan Search
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelHeader.add(new JLabel("Filter Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "menunggu", "disetujui", "ditolak", "dibatalkan"});
        cmbFilterStatus.addActionListener(e -> filterPesanan());
        panelHeader.add(cmbFilterStatus);

        panelHeader.add(new JLabel("Cari:"));
        txtSearch = new JTextField(20);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterPesanan(); }
            public void removeUpdate(DocumentEvent e) { filterPesanan(); }
            public void changedUpdate(DocumentEvent e) { filterPesanan(); }
        });
        panelHeader.add(txtSearch);

        JButton btnRefresh = new JButton("Refresh Manual");
        btnRefresh.addActionListener(e -> loadDataPesanan());
        panelHeader.add(btnRefresh);

        panel.add(panelHeader, BorderLayout.NORTH);

        // Tabel Pesanan
        String[] columns = {"No", "Tanggal Masak", "Menu", "Jumlah Porsi", "Status"};
        modelPesanan = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePesanan = new JTable(modelPesanan);
        tablePesanan.setRowHeight(25);
        tablePesanan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePesanan.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    toggleExpandRow(row);
                }
            }
        });

        panel.add(new JScrollPane(tablePesanan), BorderLayout.CENTER);

        // Info label
        JLabel lblInfo = new JLabel("Klik baris untuk melihat detail bahan yang diminta");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo, BorderLayout.SOUTH);

        loadDataPesanan();
        return panel;
    }

    private void loadDataPesanan() {
        try {
            allPesanan.clear();
            List<Permintaan> list = permintaanDao.getByPemohonId((int) loggedInUser.getId());
            allPesanan.addAll(list);
            filterPesanan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat pesanan: " + e.getMessage());
        }
    }

    private void filterPesanan() {
        try {
            modelPesanan.setRowCount(0);
            expandedRow = -1;

            String selectedStatus = (String) cmbFilterStatus.getSelectedItem();
            String searchText = txtSearch.getText().toLowerCase().trim();

            for (Permintaan p : allPesanan) {
                boolean statusMatch = selectedStatus.equals("Semua") || p.getStatus().equals(selectedStatus);
                boolean searchMatch = p.getMenuMakan().toLowerCase().contains(searchText)
                        || String.valueOf(p.getId()).contains(searchText);

                if (statusMatch && searchMatch) {
                    int no = modelPesanan.getRowCount() + 1;
                    modelPesanan.addRow(new Object[]{
                            no,
                            p.getTglMasak(),
                            p.getMenuMakan(),
                            p.getJumlahPorsi(),
                            getStatusLabel(p.getStatus())
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleExpandRow(int row) {
        if (expandedRow == row) {
            expandedRow = -1;
            filterPesanan();
        } else {
            expandedRow = row;
            showDetailBahan(row);
        }
    }

    private void showDetailBahan(int row) {
        try {
            Integer pesananId = (Integer) modelPesanan.getValueAt(row, 0);
            Permintaan pesanan = permintaanDao.getById(pesananId);

            List<PermintaanDetail> details = detailDao.getByPermintaanId(pesananId);

            // Buat panel untuk detail
            JPanel panelDetail = new JPanel(new BorderLayout(5, 5));
            panelDetail.setBorder(BorderFactory.createTitledBorder("Detail Bahan Baku yang Diminta"));
            panelDetail.setBackground(new Color(240, 240, 240));

            // Tabel detail bahan
            String[] colDetail = {"No", "Nama Bahan", "Jumlah Diminta"};
            DefaultTableModel modelDetail = new DefaultTableModel(colDetail, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (PermintaanDetail d : details) {
                BahanBaku bahan = bahanDao.getById(d.getBahanId());
                int no = modelDetail.getRowCount() + 1;
                modelDetail.addRow(new Object[]{
                        no,
                        bahan != null ? bahan.getNama() : "N/A",
                        d.getJumlahDiminta() + " " + (bahan != null ? bahan.getSatuan() : "")
                });
            }

            JTable tableDetail = new JTable(modelDetail);
            tableDetail.setRowHeight(20);
            panelDetail.add(new JScrollPane(tableDetail), BorderLayout.CENTER);

            // Tombol aksi (Edit/Cancel) - hanya jika status "menunggu"
            if ("menunggu".equals(pesanan.getStatus())) {
                JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnEdit = new JButton("Edit Pesanan");
                btnEdit.addActionListener(e -> editPesanan(pesanan, details));
                JButton btnCancel = new JButton("Batalkan Pesanan");
                btnCancel.setBackground(new Color(200, 0, 0));
                btnCancel.setForeground(Color.WHITE);
                btnCancel.addActionListener(e -> batalkanPesanan(pesanan));
                panelAksi.add(btnEdit);
                panelAksi.add(btnCancel);
                panelDetail.add(panelAksi, BorderLayout.SOUTH);
            }

            // Tampilkan detail dalam dialog
            JDialog dialog = new JDialog(this, "Detail Pesanan #" + pesananId, true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.add(panelDetail);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menampilkan detail: " + e.getMessage());
        }
    }

    private void editPesanan(Permintaan pesanan, List<PermintaanDetail> details) {
        JDialog dlgEdit = new JDialog(this, "Edit Pesanan #" + pesanan.getId(), true);
        dlgEdit.setSize(700, 500);
        dlgEdit.setLocationRelativeTo(this);

        JPanel panelEdit = new JPanel(new BorderLayout(10, 10));
        panelEdit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Header
        JPanel panelHeader = new JPanel(new GridLayout(2, 2, 10, 10));
        panelHeader.setBorder(BorderFactory.createTitledBorder("Detail Pesanan"));

        panelHeader.add(new JLabel("Menu Masakan:"));
        JTextField txtEditMenu = new JTextField(pesanan.getMenuMakan());
        panelHeader.add(txtEditMenu);

        panelHeader.add(new JLabel("Jumlah Porsi:"));
        JTextField txtEditPorsi = new JTextField(String.valueOf(pesanan.getJumlahPorsi()));
        panelHeader.add(txtEditPorsi);

        panelEdit.add(panelHeader, BorderLayout.NORTH);

        // Tabel bahan edit
        JPanel panelBahan = new JPanel(new BorderLayout(5, 5));
        panelBahan.setBorder(BorderFactory.createTitledBorder("Daftar Bahan"));

        String[] colEditBahan = {"ID Bahan", "Nama Bahan", "Jumlah Lama", "Jumlah Baru"};
        DefaultTableModel modelEditBahan = new DefaultTableModel(colEditBahan, 0);

        for (PermintaanDetail d : details) {
            try {
                BahanBaku bahan = bahanDao.getById(d.getBahanId());
                modelEditBahan.addRow(new Object[]{
                        d.getBahanId(),
                        bahan != null ? bahan.getNama() : "N/A",
                        d.getJumlahDiminta(),
                        d.getJumlahDiminta()
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JTable tableEditBahan = new JTable(modelEditBahan);
        tableEditBahan.setRowHeight(25);
        panelBahan.add(new JScrollPane(tableEditBahan), BorderLayout.CENTER);

        panelEdit.add(panelBahan, BorderLayout.CENTER);

        // Tombol Simpan/Batal
        JPanel panelBtnEdit = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.addActionListener(e -> {
            try {
                pesanan.setMenuMakan(txtEditMenu.getText());
                pesanan.setJumlahPorsi(Integer.parseInt(txtEditPorsi.getText()));
                permintaanDao.update(pesanan);

                // Update detail bahan
                detailDao.deleteByPermintaanId(pesanan.getId());
                for (int i = 0; i < modelEditBahan.getRowCount(); i++) {
                    PermintaanDetail d = new PermintaanDetail();
                    d.setPermintaanId(pesanan.getId());
                    d.setBahanId((Integer) modelEditBahan.getValueAt(i, 0));
                    d.setJumlahDiminta((int) modelEditBahan.getValueAt(i, 3));
                    detailDao.save(d);
                }

                JOptionPane.showMessageDialog(dlgEdit, "Pesanan berhasil diupdate!");
                dlgEdit.dispose();
                loadDataPesanan();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlgEdit, "Gagal menyimpan: " + ex.getMessage());
            }
        });
        panelBtnEdit.add(btnSimpan);
        panelBtnEdit.add(new JButton("Batal") {{
            addActionListener(e -> dlgEdit.dispose());
        }});

        panelEdit.add(panelBtnEdit, BorderLayout.SOUTH);

        dlgEdit.add(panelEdit);
        dlgEdit.setVisible(true);
    }

    private void batalkanPesanan(Permintaan pesanan) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin membatalkan pesanan ini?",
                "Konfirmasi Pembatalan",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pesanan.setStatus("dibatalkan");
                permintaanDao.update(pesanan);
                JOptionPane.showMessageDialog(this, "Pesanan berhasil dibatalkan!");
                loadDataPesanan();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal membatalkan: " + e.getMessage());
            }
        }
    }

    // ========== INPUT PESANAN TAB ==========
    private JPanel createInputPesananPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Header (Atas)
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

        // Input bahan
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

        // Tabel keranjang
        String[] colKeranjang = {"No", "Nama Bahan", "Jumlah Diminta", "Satuan"};
        modelKeranjang = new DefaultTableModel(colKeranjang, 0);
        tableKeranjang = new JTable(modelKeranjang);
        panelTengah.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);

        panel.add(panelTengah, BorderLayout.CENTER);

        // 3. Tombol Aksi (Bawah)
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReset = new JButton("Reset Form");
        btnReset.addActionListener(e -> resetForm());
        JButton btnKirim = new JButton("Kirim Permintaan");
        btnKirim.setBackground(new Color(0, 128, 0)); // warna dasar
        btnKirim.setForeground(Color.WHITE);
        btnKirim.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnKirim.setFocusPainted(false);      // hilangkan border fokus default
        btnKirim.setBorderPainted(false);     // hilangkan border default
        btnKirim.setOpaque(true);             // pastikan tombol tidak transparan
        btnKirim.setContentAreaFilled(true);  // isi area tombol dengan warna background

        // Hover effect
        btnKirim.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKirim.setBackground(new Color(0, 160, 0)); // lebih terang saat hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKirim.setBackground(new Color(0, 128, 0)); // kembali ke warna semula
            }
        });

        btnKirim.addActionListener(e -> kirimPermintaan());


        panelBawah.add(btnReset);
        panelBawah.add(btnKirim);

        panel.add(panelBawah, BorderLayout.SOUTH);

        loadDataBahanCombo();
        return panel;
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

            PermintaanDetail detail = new PermintaanDetail();
            detail.setBahanId(selected.id);
            detail.setJumlahDiminta(qty);
            keranjangPermintaan.add(detail);

            modelKeranjang.addRow(new Object[]{selected.id, selected.nama, qty, selected.satuan});

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
            p.setPemohonId((int) loggedInUser.getId());
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
            loadDataPesanan();

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

    // ========== AUTO-REFRESH ==========
    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> {
            JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
            if (tabbedPane.getSelectedIndex() == 0) {
                loadDataPesanan();
            }
        });
        refreshTimer.start();
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }

    private String getStatusLabel(String status) {
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    // Helper class
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