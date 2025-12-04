package com.mbg.pattern.command;

import com.mbg.dao.PermintaanDao;
import com.mbg.dao.PermintaanDetailDao;
import com.mbg.model.Permintaan;
import com.mbg.model.PermintaanDetail;
import java.util.List;

/**
 * CreateRequestCommand - Command untuk membuat permintaan baru
 * Mengimplementasikan Command interface untuk menangani aksi pembuatan permintaan
 * Mendukung undo untuk menghapus permintaan yang baru dibuat
 */
public class CreateRequestCommand implements Command {

    private PermintaanDao permintaanDao;
    private PermintaanDetailDao detailDao;
    private Permintaan permintaan;
    private List<PermintaanDetail> details;
    private Integer savedPermintaanId; // Untuk undo

    /**
     * Constructor
     * 
     * @param permintaanDao DAO untuk permintaan
     * @param detailDao DAO untuk detail permintaan
     * @param permintaan Permintaan yang akan dibuat
     * @param details List detail bahan yang diminta
     */
    public CreateRequestCommand(PermintaanDao permintaanDao, PermintaanDetailDao detailDao,
                                 Permintaan permintaan, List<PermintaanDetail> details) {
        this.permintaanDao = permintaanDao;
        this.detailDao = detailDao;
        this.permintaan = permintaan;
        this.details = details;
    }

    /**
     * Mengeksekusi pembuatan permintaan
     * Simpan permintaan ke database, kemudian simpan setiap detail bahan
     */
    @Override
    public void execute() throws Exception {
        System.out.println("[CreateRequestCommand] Executing...");
        
        // Simpan permintaan utama
        Permintaan savedPermintaan = permintaanDao.save(permintaan);
        this.savedPermintaanId = savedPermintaan.getId();
        
        // Simpan detail bahan
        for (PermintaanDetail d : details) {
            d.setPermintaanId(savedPermintaanId);
            detailDao.save(d);
        }
        
        System.out.println("[CreateRequestCommand] Permintaan ID " + savedPermintaanId + " berhasil dibuat");
    }

    /**
     * Membatalkan pembuatan permintaan
     * Hapus semua detail bahan, kemudian hapus permintaan
     */
    @Override
    public void undo() throws Exception {
        if (savedPermintaanId != null) {
            System.out.println("[CreateRequestCommand] Undoing... Deleting request ID " + savedPermintaanId);
            
            // Hapus semua detail permintaan
            List<PermintaanDetail> savedDetails = detailDao.getByPermintaanId(savedPermintaanId);
            for (PermintaanDetail d : savedDetails) {
                detailDao.remove(d.getId());
            }
            
            // Hapus permintaan utama
            permintaanDao.remove(savedPermintaanId);
            
            System.out.println("[CreateRequestCommand] Permintaan berhasil dihapus");
        }
    }

    @Override
    public String getDescription() {
        return "Buat Permintaan Baru - " + permintaan.getMenuMakan();
    }
}