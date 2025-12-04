package com.mbg.pattern.command;

import java.util.List;

import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.model.Permintaan;
import com.mbg.model.PermintaanDetail;

/**
  EditRequestCommand - Command untuk mengedit permintaan yang sudah ada

**/
public class EditRequestCommand implements Command {

    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;
    private Permintaan permintaan;
    private Permintaan oldPermintaan; 
    private List<PermintaanDetail> newDetails;
    private List<PermintaanDetail> oldDetails; 

    // Constructor
    public EditRequestCommand(PermintaanDao permintaanDao, PermintaanDetailDao detailDao,
                               Permintaan permintaan, Permintaan oldPermintaan,
                               List<PermintaanDetail> newDetails) {
        this.permintaanDao = permintaanDao;
        this.detailDao = detailDao;
        this.permintaan = permintaan;
        this.oldPermintaan = oldPermintaan;
        this.newDetails = newDetails;
    }

    // Mengeksekusi update permintaan
    @Override
    public void execute() throws Exception {
        System.out.println("[EditRequestCommand] Executing edit on request ID " + permintaan.getId());
        
        // Backup detail lama untuk undo
        this.oldDetails = detailDao.getByPermintaanId(permintaan.getId());
        
        // Update permintaan
        permintaanDao.update(permintaan);
        
        // Hapus detail lama
        detailDao.deleteByPermintaanId(permintaan.getId());
        
        // Simpan detail baru
        for (PermintaanDetail d : newDetails) {
            d.setPermintaanId(permintaan.getId());
            detailDao.save(d);
        }
        
        System.out.println("[EditRequestCommand] Permintaan berhasil diupdate");
    }

    // Membatalkan edit permintaan
    @Override
    public void undo() throws Exception {
        System.out.println("[EditRequestCommand] Undoing edit on request ID " + permintaan.getId());
        
        // Restore permintaan ke state lama
        permintaanDao.update(oldPermintaan);
        
        // Hapus detail baru
        detailDao.deleteByPermintaanId(oldPermintaan.getId());
        
        // Restore detail lama
        if (oldDetails != null) {
            for (PermintaanDetail d : oldDetails) {
                detailDao.save(d);
            }
        }
        
        System.out.println("[EditRequestCommand] Perubahan berhasil dibatalkan");
    }

    @Override
    public String getDescription() {
        return "Edit Permintaan - " + permintaan.getMenuMakan();
    }
}