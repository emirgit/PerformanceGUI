
# System Performance Monitoring and Visualization

## Overview

This project involves creating a basic system for monitoring and visualizing computer performance metrics. The system collects, processes, and displays various performance indicators such as CPU usage, RAM usage, disk usage, and network traffic. The project is built using Spring Boot for the backend and Chart.js for frontend visualization.

## Features

- **Real-time Data Collection:** Collects performance data every second, including CPU usage, RAM usage, disk usage, and network traffic.
- **Historical Data Visualization:** Displays historical performance data on a chart, providing insights into system performance trends over time.
- **Responsive Design:** Ensures the chart is displayed correctly on various devices and screen sizes.

## Technologies Used

- **Backend:** 
  - Spring Boot
  - H2 Database
- **Frontend:** 
  - HTML
  - CSS
  - JavaScript
  - Chart.js

## Backend

### Data Collection and Processing

- **Microservices:** A Spring Boot microservice collects system performance data every second.
- **Data Handling:** Data is processed and stored using an H2 database. The `SystemDataService` class manages data parsing and storage, while the `ReceiverController` exposes endpoints for data processing.

### Endpoints

- **POST `/api/performance`**: Accepts system performance data in POST requests and processes it.
- **GET `/api/graph/data`**: Provides historical performance data in JSON format for visualization.

## Frontend

### Data Visualization

- **Charting Library:** Utilizes Chart.js to render performance data on a chart.
- **Dynamic Updates:** Fetches data from the backend and updates the chart in real-time to display up to 10 minutes of historical data.


## Getting Started

1. **Clone the Repository**

   ```bash
   git clone https://github.com/emirgit/PerformanceGUI
   cd PerformanceGUI


## Running Tests

To run tests, run the following command

Go To **gui**
```bash
  mvn clean install
  mvn spring-boot:run
```

Go To **data**
```bash
  mvn clean install
  mvn spring-boot:run
```
