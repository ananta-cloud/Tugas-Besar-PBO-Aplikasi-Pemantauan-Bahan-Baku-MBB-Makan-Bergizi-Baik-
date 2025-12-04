package com.mbg;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.codeway.daoTemplate.utils.TemplateLogger;
import com.mbg.dao.UserDao;
import com.mbg.model.User;

public class MainApp extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private UserDao userDao;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        TemplateLogger.shouldLog = false;

        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }

    public MainApp() {
        userDao = new UserDao();
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - MBG System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Aplikasi Makan Bergizi Baik");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(253, 253, 253));
        btnLogin.setForeground(new Color(30, 60, 180));
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(this::handleLogin);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(btnLogin, gbc);

        getRootPane().setDefaultButton(btnLogin);
    }

    private void handleLogin(ActionEvent e) {
        String email = txtEmail.getText().trim();

        // Validasi input kosong
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap isi Email!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Ambil user berdasarkan email
            User user = userDao.getByEmail(email);

            if (user != null) {
                System.out.println("User ditemukan: " + user.getName());
                System.out.println("Bypass password check...");

                bukaDashboard(user);

            } else {
                JOptionPane.showMessageDialog(this, "Email tidak ditemukan!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage());
        }
    }

    private void bukaDashboard(@org.jetbrains.annotations.NotNull User user) {
        this.dispose();

        String role = user.getRole();
        if (role == null) role = "";

        // Arahkan ke Dashboard sesuai Role
        if (role.equalsIgnoreCase("dapur")) {
            new com.mbg.gui.dapur.dapur(user).setVisible(true);
        } else if (role.equalsIgnoreCase("gudang")) {
            new com.mbg.gui.gudang.gudang(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Role user tidak valid: " + role);
        }
    }
}