package com.performance.gui.controller;

import com.performance.gui.model.SystemData;
import com.performance.gui.service.SystemDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * ReceiverController
 * log4j2 used for logging
 * dataList is a list to store 10 minutes of data to send performance GUI
 *
 */
@RestController
public class ReceiverController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiverController.class);
    private final SystemDataService systemDataService;
    private List<SystemData> dataList;

    public ReceiverController(SystemDataService systemDataService) {
        this.systemDataService = systemDataService;
    }


    /**
     *
     * @param data
     * the data is a String that needs to be parsed to process
     * it includes CPU,Memory, Disk, and Network datas
     * after it converted to SystemData object it sends to be processed and saved.
     * @return systemData object to debug sending systemData object
     */
    @PostMapping("/api/performance")
    public ResponseEntity<SystemData> receiveData(@RequestBody String data) {
        // Delegate data processing and saving to the service
        logger.trace("Data is received");
        SystemData systemData = systemDataService.convertStringToData(data);
        logger.trace("Data is converted");
        dataList = systemDataService.processAndSaveData(systemData);
        return ResponseEntity.ok(systemData);
    }

    /**
     * Performance GUI receives the dataList from the determined URL
     * @return
     */
    @GetMapping("/api/show/data")
    public ResponseEntity<List<SystemData>> getGraphData() {
        return ResponseEntity.ok(dataList);
    }
}
