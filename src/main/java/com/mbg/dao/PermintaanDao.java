package com.mbg.dao;

import java.util.List;

import com.codeway.daoTemplate.dao.GenericDaoImpl;
import com.mbg.config.MBGDataSource;
import com.mbg.model.Permintaan;

public class PermintaanDao extends GenericDaoImpl<Integer, Permintaan> {
    public PermintaanDao() {
        super(Permintaan.class, MBGDataSource.getInstance());
    }
    
    public List<Permintaan> getPendingRequests() throws Exception {
        // Mengambil semua permintaan yang statusnya 'menunggu'
        return getList("status = ?", "menunggu");
    }

    public List<Permintaan> getByStatus(String status) throws Exception {
        return getList("status = ?", status);
    }

    public List<Permintaan> getByPemohonId(Integer pemohonId) throws Exception {
        return getList("pemohon_id = ?", pemohonId);
    }

    public Permintaan getById(Integer id) throws Exception {
        return getSingleEntity("id = ?", id);
    }

}