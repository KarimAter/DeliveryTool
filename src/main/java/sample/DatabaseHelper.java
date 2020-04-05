package sample;

import javafx.util.Pair;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

 class DatabaseHelper {
    private Connection connection;
    private static final String TABLE_NAME = "MainModulesDriver";

     DatabaseHelper(String path) {
        this.connection = getConnection(path);
    }

    private Connection getConnection(String path) {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + path, "r", "s");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    private Pair<String, Integer> fetchHardwareItems(String driver, List<KeyData> keyDataList, KeyData keyData) {
        Statement statement;
        ResultSet resultSet;
        String output;
        Pair<String, Integer> pair = null;

        try {
            statement = connection.createStatement();
            String query = "Select * from " + TABLE_NAME + " where driver = '" + driver + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                KeyData tempKeyData = new KeyData(keyData.getQuantity());
                tempKeyData.setTech(keyData.getTech());
                tempKeyData.setPartNumber(resultSet.getString("longItemCode"));
                String itemDescription = resultSet.getString("itemDescription");
                int totalCount;
                tempKeyData.setItemDescription(itemDescription);
                // Rectifying FPKC count
                if (itemDescription.contains("FPKC"))
                    totalCount = ((int) (Math.ceil((float) tempKeyData.getQuantity() / 2)) * resultSet.getInt("quantity"));
                else totalCount = tempKeyData.getQuantity() * resultSet.getInt("quantity");
                tempKeyData.setQuantity(totalCount);
                output = resultSet.getString("output");
                if (output != null) {
                    // getting driver output and count
                    pair = new Pair<>(output, totalCount);
                }
                keyDataList.add(tempKeyData);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pair;
    }

    List<WorkOrderLine> generateWorkOrderLines(DeliveryLine deliveryLine) {

        return deliveryLine.getFilteredKeysMap().entrySet().stream().map(stringIntegerEntry -> {
            String driver, tech;
            String[] split = stringIntegerEntry.getKey().split("_");
            driver = split[0];
            KeyData keyData = stringIntegerEntry.getValue();
            // for U9 accessories
            if (split.length == 2) {
                tech = split[1];
                keyData.setTech(tech);
            } else tech = deliveryLine.getTech();
            Pair<String, Integer> primaryOutput;
            List<KeyData> keyDataList = new ArrayList<>();
            // get primary data from database
            primaryOutput = fetchHardwareItems(driver, keyDataList, keyData);
            if (primaryOutput != null) {
                String key = primaryOutput.getKey();
                String[] splitterDrivers;
                // to generate RF drivers in case of Rfs
                if (key.contains("RF")) {
                    splitterDrivers = Stream.of(key.split(",")).map(s -> deliveryLine.getFeederType() + s).toArray(String[]::new);
                } else splitterDrivers = key.split(",");

                for (String splitDriver : splitterDrivers) {
                    // looping for different splitted drivers
                    Pair<String, Integer> secondaryOutput;
                    do {
                        KeyData secondaryKeyData = new KeyData(primaryOutput.getValue());
                        secondaryKeyData.setTech(tech);
                        // getting the second data from database
                        secondaryOutput = fetchHardwareItems(splitDriver, keyDataList, secondaryKeyData);
                        if (secondaryOutput != null)
                            splitDriver = secondaryOutput.getKey();
                    }
                    while (secondaryOutput != null);
                }
            }
            return keyDataList.stream().map(data -> {
                WorkOrderLine workOrderLine = new WorkOrderLine();
                workOrderLine.setTech(data.getTech());
                workOrderLine.setPartNumber(data.getPartNumber());
                workOrderLine.setDeliveryItem(data.getItemDescription());
                workOrderLine.setQuantity(data.getQuantity());
                workOrderLine.setpO(data.getPo());
                workOrderLine.setCode(deliveryLine.getCode());
                workOrderLine.setName(deliveryLine.getName());
                return workOrderLine;
            }).collect(Collectors.toList());

        }).flatMap(List::stream).collect(Collectors.toList());

    }

}
