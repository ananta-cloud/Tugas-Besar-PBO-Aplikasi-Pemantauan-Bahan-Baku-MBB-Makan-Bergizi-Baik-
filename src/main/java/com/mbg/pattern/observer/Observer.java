package com.mbg.pattern.observer;

/**
  Observer Interface - Behavioral Pattern
  Mendefinisikan kontrak untuk semua observer yang ingin menerima notifikasi
  dari subject ketika ada perubahan data.
**/
public interface Observer {
    
    /**
     * Method yang dipanggil ketika subject melakukan notifikasi
     * 
     * @param eventType Tipe event yang terjadi (PERMINTAAN_BARU, PERMINTAAN_DIUPDATE, dll)
     * @param data Data yang berkaitan dengan event
     */
    void update(String eventType, Object data);
}