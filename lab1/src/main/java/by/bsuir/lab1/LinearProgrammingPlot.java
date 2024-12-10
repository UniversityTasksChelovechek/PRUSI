package by.bsuir.lab1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;

public class LinearProgrammingPlot {
    public static void main(String[] args) {
        double[] F = {12, 18};
        double[][] left = {
                {48, 12},
                {24, 21},
                {15, 27}
        };
        double[] right = {600, 840, 1350};

        double xMin = 0, xMax = 50;
        double yMin = 0, yMax = 60;
        double step = 0.5;

        XYSeriesCollection dataset = new XYSeriesCollection();
        addLine(dataset, left, right, xMin, xMax);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Linear Programming Visualization",
                "X", "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        addContours(plot, F, xMin, xMax, yMin, yMax, step);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setRange(xMin, xMax);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(yMin, yMax);

        JFrame frame = new JFrame("Linear Programming Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    private static void addLine(XYSeriesCollection dataset, double[][] left, double[] right, double xMin, double xMax) {
        for (int i = 0; i < left.length; i++) {
            XYSeries series = new XYSeries("Line " + (i + 1));
            for (double x = xMin; x <= xMax; x += 0.5) {
                double y = (right[i] - left[i][0] * x) / left[i][1];
                if (y >= 0) {
                    series.add(x, y);
                }
            }
            dataset.addSeries(series);
        }
    }

    private static void addContours(XYPlot plot, double[] F, double xMin, double xMax, double yMin, double yMax, double step) {
        BiFunction<Double, Double, Double> zFunction = (x, y) -> F[0] * x + F[1] * y;

        for (double level = 100; level <= 1000; level += 200) {
            XYSeries contour = new XYSeries("Z = " + level);
            for (double x = xMin; x <= xMax; x += step) {
                for (double y = yMin; y <= yMax; y += step) {
                    if (Math.abs(zFunction.apply(x, y) - level) < 10) {
                        contour.add(x, y);
                    }
                }
            }
            plot.setDataset(plot.getDatasetCount(), new XYSeriesCollection(contour));
        }
    }
}
