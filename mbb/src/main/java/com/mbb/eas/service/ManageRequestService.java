package com.mbb.eas.service;

import com.mbb.eas.enums.PermintaanStatus;

public class ManageRequestService {

    public PermintaanStatus approve() {
        return PermintaanStatus.DISETUJUI;
    }

    public PermintaanStatus reject() {
        return PermintaanStatus.DITOLAK;
    }
}
