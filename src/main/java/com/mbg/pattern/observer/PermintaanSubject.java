package com.mbg.pattern.observer;

import java.util.ArrayList;
import java.util.List;

import com.mbg.dao.PermintaanDao;
import com.mbg.model.Permintaan;

/**
  PermintaanSubject - Subject dalam Observer Pattern
  Berfungsi untuk:
  - Menyimpan daftar observers (dashboard yang berlangganan)
  - Memberitahu observers ketika ada perubahan data permintaan
  - Mengelola data permintaan dari database
**/
public class PermintaanSubject {

    private List<Observer> observers;
    private PermintaanDao permintaanDao;
    private List<Permintaan> permintaanData;

    // Constructor
    public PermintaanSubject(PermintaanDao permintaanDao) {
        this.observers = new ArrayList<>();
        this.permintaanDao = permintaanDao;
        this.permintaanData = new ArrayList<>();
    }

    // Menambahkan observer ke daftar
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observer ditambahkan: " + observer.getClass().getSimpleName());
        }
    }

    // Menghapus observer dari daftar
    public void detach(Observer observer) {
        if (observers.remove(observer)) {
            System.out.println("Observer dihapus: " + observer.getClass().getSimpleName());
        }
    }

    // Memberitahu semua observers tentang perubahan data
    public void notifyObservers(String eventType, Object data) {
        System.out.println("\n[PermintaanSubject] Notifying " + observers.size() + " observers...");
        System.out.println("[PermintaanSubject] Event: " + eventType);
        
        for (Observer observer : observers) {
            observer.update(eventType, data);
        }
    }

    // Menambahkan permintaan baru dan notify observers
    public void addPermintaan(Permintaan permintaan) throws Exception {
        Permintaan saved = permintaanDao.save(permintaan);
        permintaanData.add(saved);
        notifyObservers("PERMINTAAN_BARU", saved);
    }

    // Mengupdate permintaan dan notify observers    
    public void updatePermintaan(Permintaan permintaan) throws Exception {
        permintaanDao.update(permintaan);
        notifyObservers("PERMINTAAN_DIUPDATE", permintaan);
    }

    // Menghapus permintaan dan notify observers
    public void deletePermintaan(Integer permintaanId) throws Exception {
        Permintaan permintaan = permintaanDao.getById(permintaanId);
        if (permintaan != null) {
            permintaanDao.remove(permintaanId);
            permintaanData.remove(permintaan);
            notifyObservers("PERMINTAAN_DIHAPUS", permintaanId);
        }
    }

    // Mengambil data permintaan dari database
    public List<Permintaan> getPermintaanData() throws Exception {
        permintaanData.clear();
        permintaanData.addAll(permintaanDao.getAll());
        return permintaanData;
    }

    // Mendapatkan jumlah observers yang aktif
    public int getObserverCount() {
        return observers.size();
    }
}