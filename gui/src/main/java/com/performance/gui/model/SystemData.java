package com.performance.gui.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private long ramUsedMB;
    private long ramTotalMB;
    private long diskUsedMB;
    private long diskTotalMB;
    private long networkReceivedKB;
    private long networkSentKB;

}
