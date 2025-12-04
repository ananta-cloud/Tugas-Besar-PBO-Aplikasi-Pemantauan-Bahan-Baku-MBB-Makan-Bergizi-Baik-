package com.mbg.pattern.command;

import com.mbg.dao.PermintaanDao;
import com.mbg.model.Permintaan;

/**
 * CancelRequestCommand - Command untuk membatalkan permintaan
 * Mengimplementasikan Command interface untuk menangani aksi pembatalan permintaan
 * Mengubah status permintaan menjadi "dibatalkan" dan mendukung undo
 */
public class CancelRequestCommand implements Command {

    private PermintaanDao permintaanDao;
    private Permintaan permintaan;
    private String previousStatus; // Backup status sebelumnya untuk undo

    /**
     * Constructor
     * 
     * @param permintaanDao DAO untuk permintaan
     * @param permintaan Permintaan yang akan dibatalkan
     */
    public CancelRequestCommand(PermintaanDao permintaanDao, Permintaan permintaan) {
        this.permintaanDao = permintaanDao;
        this.permintaan = permintaan;
        this.previousStatus = permintaan.getStatus(); // Backup status lama
    }

    /**
     * Mengeksekusi pembatalan permintaan
     * Ubah status menjadi "dibatalkan" dan simpan ke database
     */
    @Override
    public void execute() throws Exception {
        System.out.println("[CancelRequestCommand] Executing cancel on request ID " + permintaan.getId());
        
        // Ubah status menjadi dibatalkan
        permintaan.setStatus("dibatalkan");
        permintaanDao.update(permintaan);
        
        System.out.println("[CancelRequestCommand] Permintaan ID " + permintaan.getId() + 
                           " berhasil dibatalkan (status sebelumnya: " + previousStatus + ")");
    }

    /**
     * Membatalkan aksi pembatalan
     * Kembalikan status ke status sebelumnya
     */
    @Override
    public void undo() throws Exception {
        System.out.println("[CancelRequestCommand] Undoing cancel on request ID " + permintaan.getId());
        
        // Restore status ke status sebelumnya
        permintaan.setStatus(previousStatus);
        permintaanDao.update(permintaan);
        
        System.out.println("[CancelRequestCommand] Status permintaan dikembalikan ke: " + previousStatus);
    }

    @Override
    public String getDescription() {
        return "Batalkan Permintaan - " + permintaan.getMenuMakan();
    }
}