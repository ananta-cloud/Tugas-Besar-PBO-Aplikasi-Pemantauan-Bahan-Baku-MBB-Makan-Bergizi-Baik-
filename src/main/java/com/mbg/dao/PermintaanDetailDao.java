package com.mbg.dao;

import com.codeway.daoTemplate.dao.GenericDaoImpl;
import com.mbg.config.MBGDataSource;
import com.mbg.model.PermintaanDetail;
import java.util.List;

public class PermintaanDetailDao extends GenericDaoImpl<Integer, PermintaanDetail> {
    public PermintaanDetailDao() {
        super(PermintaanDetail.class, new MBGDataSource());
    }

    public List<PermintaanDetail> getByPermintaanId(Integer permintaanId) throws Exception {
        return getList("permintaan_id = ?", permintaanId);
    }
}