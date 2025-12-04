package com.mbg.gui.dapur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.mbg.pattern.command.Command;
import com.mbg.pattern.command.CreateRequestCommand;
import com.mbg.pattern.command.EditRequestCommand;
import com.mbg.pattern.command.CancelRequestCommand;
import com.mbg.pattern.command.CommandInvoker;

import com.mbg.dao.BahanBakuDao;
import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.model.BahanBaku;
import com.mbg.model.Permintaan;
import com.mbg.model.PermintaanDetail;
import com.mbg.model.User;
import com.mbg.helper.DateLabelFormatter;

public class dapur extends JFrame {

    private User loggedInUser;
    private BahanBakuDao bahanDao;
    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;
    private JDatePickerImpl datePicker;
    private CommandInvoker commandInvoker;

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
        this.commandInvoker = new CommandInvoker();

        initComponents();
        startAutoRefresh();
    }

    private void initComponents() {
        setTitle("Aplikasi Dapur - " + (loggedInUser != null ? loggedInUser.getName() : "User"));
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gunakan BorderLayout untuk Frame utama
        setLayout(new BorderLayout());

        // 1. Tambahkan Header (Info User & Tombol Logout)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Setup TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Tab 1: Dashboard
        JPanel panelDashboard = createDashboardPanel();
        tabbedPane.addTab("Dashboard Pesanan", panelDashboard);

        // Tab 2: Input Pesanan
        JPanel panelInputPesanan = createInputPesananPanel();
        tabbedPane.addTab("Buat Pesanan Baru", panelInputPesanan);

        // --- Logika Pewarnaan Tab ---
        Runnable updateTabColors = () -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == selectedIndex) {
                    // Tab Aktif: Biru, Teks Putih
                    tabbedPane.setBackgroundAt(i, new Color(0, 166, 255));
                    tabbedPane.setForegroundAt(i, new Color(104, 1, 1));
                } else {
                    // Tab Tidak Aktif: Putih, Teks Hitam
                    tabbedPane.setBackgroundAt(i, Color.WHITE);
                    tabbedPane.setForegroundAt(i, Color.BLACK);
                }
            }
        };

        tabbedPane.addChangeListener(e -> updateTabColors.run());
        updateTabColors.run(); // Jalankan sekali di awal

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ========== HEADER & LOGOUT ==========
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245)); // Background abu muda
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Info User (Kiri)
        JLabel lblUser = new JLabel("Selamat Datang, " + (loggedInUser != null ? loggedInUser.getName() : "Chef") + " (Dapur)");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblUser.setForeground(Color.DARK_GRAY);
        lblUser.setIcon(UIManager.getIcon("FileView.computerIcon"));
        panel.add(lblUser, BorderLayout.WEST);

        // Tombol Logout (Kanan)
        JButton btnLogout = new JButton("Logout");
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.setBackground(new Color(220, 53, 69)); // Merah
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect Logout
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnLogout.setBackground(new Color(200, 35, 51)); }
            public void mouseExited(MouseEvent evt) { btnLogout.setBackground(new Color(220, 53, 69)); }
        });

        btnLogout.addActionListener(e -> handleLogout());
        panel.add(btnLogout, BorderLayout.EAST);

        return panel;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Tutup window saat ini
            new com.mbg.MainApp().setVisible(true); // Kembali ke Login
        }
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
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePesanan = new JTable(modelPesanan);

        // --- STYLE TABLE ---
        styleTable(tablePesanan);

        tablePesanan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePesanan.rowAtPoint(e.getPoint());
                if (row >= 0) toggleExpandRow(row);
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
                            no, p.getTglMasak(), p.getMenuMakan(), p.getJumlahPorsi(), getStatusLabel(p.getStatus())
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
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
            Integer pesananId = (Integer) modelPesanan.getValueAt(row, 0); // No sebenarnya bukan ID, tapi untuk simplifikasi kita anggap urutan
            // NOTE: Logic pengambilan ID di atas mungkin perlu disesuaikan jika kolom 'No' bukan ID Database.
            // Ambil objek asli dari list terfilter untuk keamanan ID
            // ... (Kode simplified, asumsi ID tersimpan)
            // Untuk memastikan, mari ambil Permintaan dari database berdasarkan kriteria unik atau simpan ID hidden.
            // Di sini kita ambil dari allPesanan yang cocok (simplified approach):
            Permintaan pesanan = allPesanan.stream().filter(p ->
                    p.getMenuMakan().equals(modelPesanan.getValueAt(row, 2)) &&
                            p.getJumlahPorsi().toString().equals(modelPesanan.getValueAt(row, 3).toString())
            ).findFirst().orElse(null);

            if(pesanan == null) return;

            List<PermintaanDetail> details = detailDao.getByPermintaanId(pesanan.getId());

            JPanel panelDetail = new JPanel(new BorderLayout(5, 5));
            panelDetail.setBorder(BorderFactory.createTitledBorder("Detail Bahan Baku yang Diminta"));
            panelDetail.setBackground(new Color(240, 240, 240));

            String[] colDetail = {"No", "Nama Bahan", "Jumlah Diminta"};
            DefaultTableModel modelDetail = new DefaultTableModel(colDetail, 0) {
                public boolean isCellEditable(int row, int column) { return false; }
            };

            int no = 1;
            for (PermintaanDetail d : details) {
                BahanBaku bahan = bahanDao.getById(d.getBahanId());
                modelDetail.addRow(new Object[]{
                        no++,
                        bahan != null ? bahan.getNama() : "N/A",
                        d.getJumlahDiminta() + " " + (bahan != null ? bahan.getSatuan() : "")
                });
            }

            JTable tableDetail = new JTable(modelDetail);
            styleTable(tableDetail);
            panelDetail.add(new JScrollPane(tableDetail), BorderLayout.CENTER);

            if ("menunggu".equals(pesanan.getStatus())) {
                JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnEdit = new JButton("Edit Pesanan");
                btnEdit.addActionListener(e -> editPesanan(pesanan, details));
                JButton btnCancel = new JButton("Batalkan Pesanan");
                btnCancel.setBackground(new Color(220, 53, 69));
                btnCancel.setForeground(Color.WHITE);
                btnCancel.addActionListener(e -> batalkanPesanan(pesanan));
                panelAksi.add(btnEdit);
                panelAksi.add(btnCancel);
                panelDetail.add(panelAksi, BorderLayout.SOUTH);
            }

            JDialog dialog = new JDialog(this, "Detail Pesanan", true);
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
        JDialog dlgEdit = new JDialog(this, "Edit Pesanan", true);
        dlgEdit.setSize(700, 500);
        dlgEdit.setLocationRelativeTo(this);

        JPanel panelEdit = new JPanel(new BorderLayout(10, 10));
        panelEdit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelHeader = new JPanel(new GridLayout(2, 2, 10, 10));
        panelHeader.setBorder(BorderFactory.createTitledBorder("Detail Pesanan"));

        panelHeader.add(new JLabel("Menu Masakan:"));
        JTextField txtEditMenu = new JTextField(pesanan.getMenuMakan());
        panelHeader.add(txtEditMenu);

        panelHeader.add(new JLabel("Jumlah Porsi:"));
        JTextField txtEditPorsi = new JTextField(String.valueOf(pesanan.getJumlahPorsi()));
        panelHeader.add(txtEditPorsi);

        panelEdit.add(panelHeader, BorderLayout.NORTH);

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
            } catch (Exception e) { e.printStackTrace(); }
        }

        JTable tableEditBahan = new JTable(modelEditBahan);
        styleTable(tableEditBahan);
        panelBahan.add(new JScrollPane(tableEditBahan), BorderLayout.CENTER);

        panelEdit.add(panelBahan, BorderLayout.CENTER);

        JPanel panelBtnEdit = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.addActionListener(e -> {
            try {
                // Buat copy permintaan lama untuk undo
                Permintaan oldPermintaan = new Permintaan();
                oldPermintaan.setId(pesanan.getId());
                oldPermintaan.setMenuMakan(pesanan.getMenuMakan());
                oldPermintaan.setJumlahPorsi(pesanan.getJumlahPorsi());

                // Update data baru
                pesanan.setMenuMakan(txtEditMenu.getText());
                pesanan.setJumlahPorsi(Integer.parseInt(txtEditPorsi.getText()));

                // Buat list detail baru dari tabel
                List<PermintaanDetail> newDetails = new ArrayList<>();
                for (int i = 0; i < modelEditBahan.getRowCount(); i++) {
                    PermintaanDetail d = new PermintaanDetail();
                    d.setPermintaanId(pesanan.getId());
                    d.setBahanId((Integer) modelEditBahan.getValueAt(i, 0));
                    d.setJumlahDiminta((int) modelEditBahan.getValueAt(i, 3));
                    newDetails.add(d);
                }

                Command editCommand = new EditRequestCommand(
                        permintaanDao, 
                        detailDao, 
                        pesanan, 
                        oldPermintaan,
                        newDetails
                );
                commandInvoker.executeCommand(editCommand);

                JOptionPane.showMessageDialog(dlgEdit, "Pesanan berhasil diupdate!");
                dlgEdit.dispose();
                loadDataPesanan();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlgEdit, "Gagal menyimpan: " + ex.getMessage());
            }
        });

        // TOMBOL UNDO/REDO
        JButton btnUndo = new JButton("↶ Undo");
        btnUndo.addActionListener(e -> {
            try {
                commandInvoker.undoCommand();
                JOptionPane.showMessageDialog(dlgEdit, "Perubahan dibatalkan!");
                dlgEdit.dispose();
                loadDataPesanan();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlgEdit, "Tidak ada aksi yang dapat di-undo");
            }
        });

        panelBtnEdit.add(btnUndo);
        panelBtnEdit.add(btnSimpan);
        panelBtnEdit.add(new JButton("Batal") {{ addActionListener(e -> dlgEdit.dispose()); }});

        panelEdit.add(panelBtnEdit, BorderLayout.SOUTH);
        dlgEdit.add(panelEdit);
        dlgEdit.setVisible(true);
    }

    private void batalkanPesanan(Permintaan pesanan) {
        int confirm = JOptionPane.showConfirmDialog(this, "Batalkan pesanan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Command cancelCommand = new CancelRequestCommand(permintaanDao, pesanan);
                commandInvoker.executeCommand(cancelCommand);

                JOptionPane.showMessageDialog(this, "Pesanan berhasil dibatalkan!");
                loadDataPesanan();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
            }
        }
    }

    // ========== INPUT PESANAN TAB (GRID BAG LAYOUT) ==========
    private JPanel createInputPesananPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Header (Form Detail)
        JPanel panelHeader = new JPanel(new GridBagLayout());
        panelHeader.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Detail Masakan ",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Menu
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelHeader.add(new JLabel("Nama Menu Masakan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtMenuMasakan = new JTextField();
        txtMenuMasakan.setPreferredSize(new Dimension(200, 30));
        panelHeader.add(txtMenuMasakan, gbc);

        // Porsi
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelHeader.add(new JLabel("Jumlah Porsi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtJumlahPorsi = new JTextField();
        txtJumlahPorsi.setPreferredSize(new Dimension(200, 30));
        panelHeader.add(txtJumlahPorsi, gbc);

        // Tanggal
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panelHeader.add(new JLabel("Tanggal Masak:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        UtilDateModel model = new UtilDateModel();
        model.setSelected(true);
        Properties dateProps = new Properties();
        dateProps.put("text.today", "Hari Ini");
        dateProps.put("text.month", "Bulan");
        dateProps.put("text.year", "Tahun");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, dateProps);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.getJFormattedTextField().setPreferredSize(new Dimension(200, 30));
        panelHeader.add(datePicker, gbc);

        panel.add(panelHeader, BorderLayout.NORTH);

        // 2. Input Bahan (Tengah)
        JPanel panelTengah = new JPanel(new BorderLayout(10, 10));
        panelTengah.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Daftar Bahan yang Dibutuhkan ",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)));

        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelInput.add(new JLabel("Pilih Bahan:"));
        cmbBahanBaku = new JComboBox<>();
        cmbBahanBaku.setPreferredSize(new Dimension(250, 30));
        panelInput.add(cmbBahanBaku);

        panelInput.add(new JLabel("Qty:"));
        txtQtyBahan = new JTextField(5);
        txtQtyBahan.setPreferredSize(new Dimension(60, 30));
        panelInput.add(txtQtyBahan);

        JButton btnTambah = new JButton("tambah bahan");
        btnTambah.setPreferredSize(new Dimension(140, 30));
        btnTambah.setBackground(new Color(40, 167, 152));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnTambah.setFocusPainted(false);
        btnTambah.setBorderPainted(false);
        btnTambah.setOpaque(true);
        btnTambah.setContentAreaFilled(true);
        btnTambah.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnTambah.setBackground(new Color(25, 103, 110)); }
            public void mouseExited(MouseEvent evt) { btnTambah.setBackground(new Color(40, 167, 167)); }
        });
        btnTambah.addActionListener(e -> tambahBahanKeList());
        panelInput.add(btnTambah);

        panelTengah.add(panelInput, BorderLayout.NORTH);

        String[] colKeranjang = {"No", "Nama Bahan", "Jumlah Diminta", "Satuan"};
        modelKeranjang = new DefaultTableModel(colKeranjang, 0);
        tableKeranjang = new JTable(modelKeranjang);
        styleTable(tableKeranjang);
        panelTengah.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);

        panel.add(panelTengah, BorderLayout.CENTER);

        // 3. Tombol Aksi (Bawah - Flat Style)
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnReset = new JButton("Reset Form");
        btnReset.setPreferredSize(new Dimension(140, 40));
        btnReset.setBackground(new Color(220, 53, 69));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setOpaque(true);
        btnReset.setContentAreaFilled(true);
        btnReset.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnReset.setBackground(new Color(200, 35, 51)); }
            public void mouseExited(MouseEvent evt) { btnReset.setBackground(new Color(220, 53, 69)); }
        });
        btnReset.addActionListener(e -> resetForm());

        JButton btnKirim = new JButton("Kirim Permintaan");
        btnKirim.setPreferredSize(new Dimension(180, 40));
        btnKirim.setBackground(new Color(40, 167, 69));
        btnKirim.setForeground(Color.WHITE);
        btnKirim.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnKirim.setFocusPainted(false);
        btnKirim.setBorderPainted(false);
        btnKirim.setOpaque(true);
        btnKirim.setContentAreaFilled(true);
        btnKirim.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnKirim.setBackground(new Color(33, 136, 56)); }
            public void mouseExited(MouseEvent evt) { btnKirim.setBackground(new Color(40, 167, 69)); }
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
        } catch (Exception e) { e.printStackTrace(); }
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

            modelKeranjang.addRow(new Object[]{modelKeranjang.getRowCount() + 1, selected.nama, qty, selected.satuan});
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
            JOptionPane.showMessageDialog(this, "Daftar bahan masih kosong.");
            return;
        }

        try {
            Permintaan p = new Permintaan();
            p.setPemohonId((int) loggedInUser.getId());
            p.setMenuMakan(txtMenuMasakan.getText());
            p.setJumlahPorsi(Integer.parseInt(txtJumlahPorsi.getText()));

            Date selectedDate = (Date) datePicker.getModel().getValue();
            if (selectedDate != null) {
                p.setTglMasak(selectedDate);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih tanggal masak!");
                return;
            }

            p.setStatus("menunggu");
            p.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            Command createCommand = new CreateRequestCommand(
                    permintaanDao, 
                    detailDao, 
                    p, 
                    new ArrayList<>(keranjangPermintaan)
            );
            commandInvoker.executeCommand(createCommand);

            JOptionPane.showMessageDialog(this, "Sukses! Permintaan berhasil dikirim.");
            resetForm();
            loadDataPesanan();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }

    private void resetForm() {
        txtMenuMasakan.setText("");
        txtJumlahPorsi.setText("");
        txtQtyBahan.setText("");
        modelKeranjang.setRowCount(0);
        keranjangPermintaan.clear();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> {
            // Cek apakah tab aktif adalah dashboard (index 0)
            JTabbedPane tabbedPane = null;
            // Cari tabbedPane di komponen
            for(java.awt.Component comp : getContentPane().getComponents()) {
                if(comp instanceof JTabbedPane) {
                    tabbedPane = (JTabbedPane) comp;
                    break;
                }
            }

            if (tabbedPane != null && tabbedPane.getSelectedIndex() == 0) {
                loadDataPesanan();
            }
        });
        refreshTimer.start();
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) refreshTimer.stop();
        super.dispose();
    }

    private String getStatusLabel(String status) {
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    // --- HELPER METHOD: STYLE TABEL (ISI & HEADER RATA TENGAH + FONT 12) ---
    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(30);

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setHorizontalAlignment(JLabel.CENTER);
                setBackground(new Color(230, 230, 230));
                setForeground(Color.BLACK);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private static class BahanBakuItem {
        int id;
        String nama;
        String satuan;
        public BahanBakuItem(int id, String nama, String satuan) { this.id = id; this.nama = nama; this.satuan = satuan; }
        @Override public String toString() { return nama + " (" + satuan + ")"; }
    }
}