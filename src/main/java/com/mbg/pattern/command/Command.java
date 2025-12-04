package com.mbg.pattern.command;

/**
 * Command Interface - Behavioral Pattern
 * Mendefinisikan kontrak untuk semua command yang akan di-execute
 * Setiap command merepresentasikan satu aksi dari pengguna
 */
public interface Command {
    
    /**
     * Mengeksekusi command
     * Merupakan aksi utama yang dilakukan oleh command
     * 
     * @throws Exception jika terjadi error saat eksekusi
     */
    void execute() throws Exception;
    
    /**
     * Membatalkan command yang telah di-execute
     * Mengembalikan aplikasi ke state sebelum command dijalankan
     * 
     * @throws Exception jika terjadi error saat undo
     */
    void undo() throws Exception;
    
    /**
     * Mengembalikan deskripsi singkat tentang command
     * Berguna untuk logging atau tampilan di GUI
     * 
     * @return Deskripsi command
     */
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}