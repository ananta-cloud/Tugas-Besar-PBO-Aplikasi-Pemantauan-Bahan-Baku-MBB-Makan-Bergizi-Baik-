package com.mbg.dao;

import com.codeway.daoTemplate.dao.GenericDaoImpl;
import com.mbg.config.MBGDataSource;
import com.mbg.model.BahanBaku;

public class BahanBakuDao extends GenericDaoImpl<Integer, BahanBaku> {
    public BahanBakuDao() {
        super(BahanBaku.class, MBGDataSource.getInstance());
    }

    public BahanBaku getById(Integer id) throws Exception {
        return getSingleEntity("id = ?", id);
    }
}