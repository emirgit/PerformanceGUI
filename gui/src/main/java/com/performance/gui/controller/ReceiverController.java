package com.performance.gui.controller;

import com.performance.gui.model.SystemData;
import com.performance.gui.service.SystemDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReceiverController {

    private final SystemDataService systemDataService;
    private List<SystemData> dataList;

    public ReceiverController(SystemDataService systemDataService) {
        this.systemDataService = systemDataService;
    }

    @PostMapping("/api/performance")
    public ResponseEntity<SystemData> receiveData(@RequestBody String data) {
        // Delegate data processing and saving to the service
        SystemData systemData = systemDataService.convertStringToData(data);
        dataList = systemDataService.processAndSaveData(systemData);
        return ResponseEntity.ok(systemData);
    }

    @GetMapping("/api/show/data")
    public ResponseEntity<List<SystemData>> getGraphData() {

        return ResponseEntity.ok(dataList);
    }
}
