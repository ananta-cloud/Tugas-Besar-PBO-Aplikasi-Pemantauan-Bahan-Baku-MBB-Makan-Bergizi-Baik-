package com.mbg.dao;

import java.util.List;

import com.codeway.daoTemplate.dao.GenericDaoImpl;
import com.mbg.config.MBGDataSource;
import com.mbg.model.PermintaanDetail;

public class PermintaanDetailDao extends GenericDaoImpl<Integer, PermintaanDetail> {
    public PermintaanDetailDao() {
        super(PermintaanDetail.class, MBGDataSource.getInstance());
    }

    public List<PermintaanDetail> getByPermintaanId(Integer permintaanId) throws Exception {
        return getList("permintaan_id = ?", permintaanId);
    }

    public void deleteByPermintaanId(Integer permintaanId) throws Exception {
        try {
            List<PermintaanDetail> details = getByPermintaanId(permintaanId);
            for (PermintaanDetail detail : details) {
                remove(detail.getId());
            }
        } catch (Exception e) {
            System.err.println("Error deleting detail permintaan: " + e.getMessage());
            throw e;
        }
    }
}