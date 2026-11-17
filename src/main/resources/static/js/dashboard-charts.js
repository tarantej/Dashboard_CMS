
(function () {
  'use strict';

  // Ensure Chart.js is loaded first.
  if (typeof Chart === 'undefined') {
    console.error('Chart.js is not loaded.');
    return;
  }

  // Original SB Admin typography.
  Chart.defaults.global.defaultFontFamily =
      'Nunito, -apple-system, system-ui, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif';

  Chart.defaults.global.defaultFontColor = '#858796';

  // Data supplied by Spring Boot / Thymeleaf.
  var chartData = window.dashboardChartData || {};

  function getArray(value) {
    return Array.isArray(value) ? value : [];
  }

  function renderDoughnut(canvasId, labels, values, colors) {
    var canvas = document.getElementById(canvasId);

    if (!canvas) {
      console.warn('Canvas not found: ' + canvasId);
      return;
    }

    labels = getArray(labels);
    values = getArray(values);
    colors = getArray(colors);

    if (labels.length === 0 || values.length === 0) {
      console.warn('No data available for: ' + canvasId);
      return;
    }

    new Chart(canvas, {
      type: 'doughnut',

      data: {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: colors,
          hoverBackgroundColor: colors,
          hoverBorderColor: 'rgba(234,236,244,1)',
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },

      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutoutPercentage: 72,

        tooltips: {
          backgroundColor: 'rgb(255,255,255)',
          titleFontColor: '#5a5c69',
          bodyFontColor: '#858796',
          borderColor: '#dddfeb',
          borderWidth: 1,
          xPadding: 15,
          yPadding: 15,
          displayColors: true,
          caretPadding: 10
        },

        legend: {
          display: false
        },

        animation: {
          animateScale: true,
          animateRotate: true
        }
      }
    });
  }

  // Project Type chart.
  renderDoughnut(
      'projectTypeChart',
      chartData.projectTypeLabels,
      chartData.projectTypeCounts,
      chartData.projectTypeColors
  );

  // Project Status chart.
  // Includes Archived and Maintenance when supplied by the controller.
  renderDoughnut(
      'projectStatusChart',
      chartData.projectStatusLabels,
      chartData.projectStatusCounts,
      chartData.projectStatusColors
  );

})();