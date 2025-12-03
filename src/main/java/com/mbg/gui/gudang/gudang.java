package com.mbg.gui.gudang;

import com.mbg.model.User;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class gudang extends JFrame {
    public gudang(User user) {
        setTitle("Gudang Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new JLabel("Selamat Datang di Gudang, " + user.getName(), JLabel.CENTER));
    }
}