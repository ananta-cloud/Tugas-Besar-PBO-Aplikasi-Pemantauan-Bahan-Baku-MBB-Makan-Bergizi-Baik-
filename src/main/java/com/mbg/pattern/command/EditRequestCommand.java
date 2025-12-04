package com.mbg.pattern.command;

import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.model.Permintaan;
import com.mbg.model.PermintaanDetail;
import java.util.List;

/**
 * EditRequestCommand - Command untuk mengedit permintaan yang sudah ada
 * Mengimplementasikan Command interface untuk menangani aksi edit permintaan
 * Mendukung undo untuk mengembalikan data ke state sebelumnya
 */
public class EditRequestCommand implements Command {

    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;
    private Permintaan permintaan;
    private Permintaan oldPermintaan; // Backup untuk undo
    private List<PermintaanDetail> newDetails;
    private List<PermintaanDetail> oldDetails; // Backup untuk undo

    /**
     * Constructor
     * 
     * @param permintaanDao DAO untuk permintaan
     * @param detailDao DAO untuk detail permintaan
     * @param permintaan Permintaan yang sudah diupdate (state baru)
     * @param oldPermintaan Backup permintaan sebelum edit (state lama)
     * @param newDetails List detail bahan yang baru
     */
    public EditRequestCommand(PermintaanDao permintaanDao, PermintaanDetailDao detailDao,
                               Permintaan permintaan, Permintaan oldPermintaan,
                               List<PermintaanDetail> newDetails) {
        this.permintaanDao = permintaanDao;
        this.detailDao = detailDao;
        this.permintaan = permintaan;
        this.oldPermintaan = oldPermintaan;
        this.newDetails = newDetails;
    }

    /**
     * Mengeksekusi update permintaan
     * Update data permintaan dan hapus detail lama, kemudian simpan detail baru
     */
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

    /**
     * Membatalkan edit permintaan
     * Kembalikan data permintaan ke state lama dan restore detail yang lama
     */
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