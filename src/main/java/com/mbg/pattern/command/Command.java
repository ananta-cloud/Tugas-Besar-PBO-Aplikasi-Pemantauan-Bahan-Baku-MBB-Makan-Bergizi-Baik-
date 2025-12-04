package com.mbg.pattern.command;

/**
  Command Interface - Behavioral Pattern
  Mendefinisikan kontrak untuk semua command yang akan di-execute
  Setiap command merepresentasikan satu aksi dari pengguna
**/
public interface Command {
    
    // Mengeksekusi command
    void execute() throws Exception;
    
    // Undo command yang telah di-execute
    void undo() throws Exception;
    
    // Mengembalikan deskripsi singkat tentang command  
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}