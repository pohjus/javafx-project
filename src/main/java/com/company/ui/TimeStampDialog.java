package com.company.ui;

import com.company.timestamp.Time;
import com.company.timestamp.TimeHandler;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

public class TimeStampDialog {
    public static Dialog generateDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Time Spent with this App");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        StackPane fp = new StackPane(generateChart());

        dialog.getDialogPane().setPrefSize(800, 400);
        dialog.getDialogPane().setContent(fp);


        return dialog;
    }

    private static BarChart generateChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Time Summary");
        xAxis.setLabel("Day");
        yAxis.setLabel("Seconds");
        XYChart.Series series1 = new XYChart.Series();

        var times = TimeHandler.getInstance().getStamps();
        for(var time : times) {
            series1.getData().add(new XYChart.Data(time.getDay(), time.getTime()));
        }

        bc.getData().addAll(series1);
        bc.setLegendVisible(false);
        return bc;
    }
}
