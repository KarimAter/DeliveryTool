package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {
    private Button loadDeliveryTemplate, loadStockBalance;
    private HashMap<String, HashMap<String, Integer>> stringHashMapHashMap;
    private List<StockBalanceLine> prioritisedBalance;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
        initializeButtons(scene);

        loadDeliveryTemplate.setOnAction(event -> {
            String excelFilePath = Utils.loadExcelFile(primaryStage);
            Importer importer = new Importer();
            stringHashMapHashMap = importer.importCurrentTarget(excelFilePath);
        });

        loadStockBalance.setOnAction(event -> {
            String excelFilePath = Utils.loadExcelFile(primaryStage);
            Importer importer = new Importer();

            List<StockBalanceLine> prioritisedBalance = importer.importStockBalance(excelFilePath).stream()
                    .filter(stockBalanceLine -> stockBalanceLine.getAvailableBalance() > 5)
                    .sorted(Comparator.comparing(StockBalanceLine::getPriority))
                    .collect(Collectors.toList());
            int x = 1;
        });
    }

    private void initializeButtons(Scene scene) {
        loadDeliveryTemplate = (Button) scene.lookup("#loadDeliveryTemplate");
        loadStockBalance = (Button) scene.lookup("#loadStockBalance");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
