package com.mbb.eas.controller;

import com.mbb.eas.service.ManageRequestService;

public class GudangRequestController {

    private final ManageRequestService service = new ManageRequestService();

    public void approveRequest() {
        System.out.println(service.approve());
    }

    public void rejectRequest() {
        System.out.println(service.reject());
    }
}
