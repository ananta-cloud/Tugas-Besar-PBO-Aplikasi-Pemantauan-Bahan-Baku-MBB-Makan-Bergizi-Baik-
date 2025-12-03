package com.mbg.dao;

import com.codeway.daoTemplate.dao.GenericDaoImpl;
import com.mbg.config.MBGDataSource;
import com.mbg.model.Permintaan;
import java.util.List;

public class PermintaanDao extends GenericDaoImpl<Integer, Permintaan> {
    public PermintaanDao() {
        super(Permintaan.class, new MBGDataSource());
    }
    
    public List<Permintaan> getPendingRequests() throws Exception {
        // Mengambil semua permintaan yang statusnya 'menunggu'
        return getList("status = ?", "menunggu");
    }

    public List<Permintaan> getByStatus(String status) throws Exception {
        return getList("status = ?", status);
    }
}