const points = [
  { x: "2025-12-01T00:00:00Z", y: 12 },
  { x: "2025-12-03T00:00:00Z", y: 18 },
  { x: "2025-12-06T00:00:00Z", y: 9 },
  { x: "2025-12-10T00:00:00Z", y: 22 },
];

let chart = null;

function buildChart(canvas) {
  return new Chart(canvas, {
    type: "line",
    data: {
      datasets: [
        {
          label: "Value over time",
          data: [...points], 
          tension: 0.25,
          pointRadius: 2,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      parsing: false,
      scales: {
        x: {
          type: "time",
          time: { tooltipFormat: "DD T" },
          title: { display: true, text: "Date" },
        },
        y: {
          title: { display: true, text: "Value" },
        },
      },
    },
  });
}

function destroyExistingChart(canvas) {

  if (typeof Chart !== "undefined" && Chart.getChart) {
    const existing = Chart.getChart(canvas);
    if (existing) existing.destroy();
  }

  if (chart) {
    chart.destroy();
    chart = null;
  }
}

function initChart() {
  const canvas = document.getElementById("tsChart");
  if (!canvas) return;
  if (typeof Chart === "undefined") return;

  destroyExistingChart(canvas);
  chart = buildChart(canvas);
}

export { initChart };
