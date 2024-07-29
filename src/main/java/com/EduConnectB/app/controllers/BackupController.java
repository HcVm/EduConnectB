package com.EduConnectB.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduConnectB.app.services.BackupService;

@RestController
@RequestMapping("/mantenimiento")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping("/backup")
    public String triggerBackup() {
        backupService.performBackup();
        return "Backup en proceso";
    }
}
