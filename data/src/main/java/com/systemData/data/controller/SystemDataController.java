package com.systemData.data.controller;

import com.systemData.data.model.NetworkUsage;
import com.systemData.data.service.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemDataController {

    private final SystemDataService systemDataService;

    private NetworkUsage networkUsage = new NetworkUsage(0, 0);
    @Autowired
    public SystemDataController(SystemDataService systemDataService) {
        this.systemDataService = systemDataService;
    }

    @Scheduled(fixedRate = 1000) // Execute every 1000 milliseconds
    public String triggerDataSending() {
        systemDataService.sendSystemData(networkUsage);
        return "Data sending triggered";
    }
}


