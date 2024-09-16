async function fetchData() {
    const response = await fetch('/api/show/data');
    const data = await response.json();
    console.log("Fetched Data:", data); // Debugging output
    return data;
}

async function updateFooter(logs) {
    const logElement = document.getElementById('log');

    if (logs && logs.length > 0) {
        const latestLog = logs[logs.length - 1];
        const timestamp = new Date(latestLog.timestamp).toLocaleString();
        logElement.textContent = `Last Update: ${timestamp} - ${latestLog.log}`;
    } else {
        logElement.textContent = 'No logs available';
    }
}

let cpuChart, ramChart, diskChart, networkChart;

async function createCharts() {
    const data = await fetchData();

    const cpuCtx = document.getElementById('cpuChart').getContext('2d');
    const ramCtx = document.getElementById('ramChart').getContext('2d');
    const diskCtx = document.getElementById('diskChart').getContext('2d');
    const networkCtx = document.getElementById('networkChart').getContext('2d');

    // CPU Usage Chart
    cpuChart = new Chart(cpuCtx, {
        type: 'line',
        data: {
            labels: data.map(d => new Date(d.timestamp).toLocaleTimeString()),
            datasets: [{
                label: 'CPU Usage (%)',
                data: data.map(d => d.cpuUsage),
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                fill: false,
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    min: 0,
                    max: 100,
                    title: {
                        display: true,
                        text: 'CPU Usage (%)'
                    }
                }
            }
        }
    });

    // RAM Usage Chart
    ramChart = new Chart(ramCtx, {
        type: 'line',
        data: {
            labels: data.map(d => new Date(d.timestamp).toLocaleTimeString()),
            datasets: [{
                label: 'RAM Usage (%)',
                data: data.map(d => d.ramUsage),
                borderColor: 'rgba(54, 162, 235, 1)',
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                fill: false,
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    min: 0,
                    max: 100,
                    title: {
                        display: true,
                        text: 'RAM Usage (%)'
                    }
                }
            }
        }
    });

    // Disk Usage Chart
    diskChart = new Chart(diskCtx, {
        type: 'line',
        data: {
            labels: data.map(d => new Date(d.timestamp).toLocaleTimeString()),
            datasets: [{
                label: 'Disk Usage (%)',
                data: data.map(d => d.diskUsage),
                borderColor: 'rgba(255, 206, 86, 1)',
                backgroundColor: 'rgba(255, 206, 86, 0.2)',
                fill: false,
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    min: 0,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Disk Usage (%)'
                    }
                }
            }
        }
    });

    // Network Usage Chart
    networkChart = new Chart(networkCtx, {
        type: 'line',
        data: {
            labels: data.map(d => new Date(d.timestamp).toLocaleTimeString()),
            datasets: [{
                label: 'Network Received (Kb)',
                data: data.map(d => d.networkReceivedKB),
                borderColor: 'rgba(153, 102, 255, 1)',
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                fill: false,
            }, {
                label: 'Network Sent (Kb)',
                data: data.map(d => d.networkSentKB),
                borderColor: 'rgba(255, 99, 132, 1)',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                fill: false,
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    title: {
                        display: true,
                        text: 'Network Usage (Kb)'
                    }
                }
            }
        }
    });
}

async function updateCharts() {
    const data = await fetchData();
    console.log("Updating Charts with Data:", data); // Debugging output

    // Update CPU Chart
    cpuChart.data.labels = data.map(d => new Date(d.timestamp).toLocaleTimeString());
    cpuChart.data.datasets[0].data = data.map(d => d.cpuUsage);
    cpuChart.update();

    // Update RAM Chart
    ramChart.data.labels = data.map(d => new Date(d.timestamp).toLocaleTimeString());
    ramChart.data.datasets[0].data = data.map(d => d.ramUsage);
    ramChart.update();

    // Update Disk Chart
    diskChart.data.labels = data.map(d => new Date(d.timestamp).toLocaleTimeString());
    diskChart.data.datasets[0].data = data.map(d => d.diskUsage);
    diskChart.update();

    // Update Network Chart
    networkChart.data.labels = data.map(d => new Date(d.timestamp).toLocaleTimeString());
    networkChart.data.datasets[0].data = data.map(d => d.networkReceivedKB);
    networkChart.data.datasets[1].data = data.map(d => d.networkSentKB);
    networkChart.update();

    updateFooter()
}

// Initialize the charts
createCharts();
setInterval(updateCharts, 1000);