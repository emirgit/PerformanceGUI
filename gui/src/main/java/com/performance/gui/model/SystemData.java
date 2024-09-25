package com.performance.gui.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SystemData class to store CPU, Memory, Disk, and Network Usage
 * it also includes the logs
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private double cpuUsage;
    private double ramUsage;
    private double diskUsage;
    private long networkReceivedKB;
    private long networkSentKB;
    private String log;

}
