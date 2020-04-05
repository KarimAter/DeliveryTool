package sample;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Importer {
    static String deliveryTemplateName = "C:/Ater/Development/Delivery Tool/WorkOrder Template.xlsx";
    static String deliveryOutputName = "C:/Ater/Development/Delivery Tool/WorkOrder.xlsx";
    XSSFWorkbook wb;


    HashMap<String, HashMap<String, Integer>> importCurrentTarget(String excelFilePath) {
        HashMap<String, HashMap<String, Integer>> sitesDelivery = new HashMap<>();

        if (excelFilePath != null) {
            try {
                FileInputStream excelFile = new FileInputStream(new File(excelFilePath));
                XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
                XSSFSheet sheet = workbook.getSheet("Current-Target");
                Reader reader = new Reader();
                List<Delivery> deliveries = reader.readCurrentTarget(sheet);
                deliveries.forEach(delivery -> {
                    Map<String, List<DeliveryLine>> actionDelivery = delivery.getActionDelivery();
                    actionDelivery.entrySet().stream().map(stringListEntry ->
                            stringListEntry.getValue().stream().map(DeliveryLine::getNumberOfAddedSectors).reduce(0, Integer::sum));

                });
                int z = 2;

                export(deliveries);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sitesDelivery;
    }

    private void export(List<Delivery> deliveries) throws IOException {
        wb = prepareWorkbook(deliveryTemplateName);
        ZipSecureFile.setMinInflateRatio(0);
        int numOfColumns = 7;
        XSSFSheet sheet = wb.getSheet("Sheet1");
        final int[] r = {1};

        deliveries.forEach(siteDelivery -> {

                    List<WorkOrderLine> deliveryWorkOrderLines = siteDelivery.getWorkOrderLines();
                    deliveryWorkOrderLines.forEach(workOrderLine -> {
                        ArrayList<XSSFCell> cells = new ArrayList<>();
                        XSSFRow row = sheet.createRow(r[0]);
                        //iterating c number of columns
                        for (int i = 0; i < numOfColumns; i++) {
                            XSSFCell cell = row.createCell(i);
                            cells.add(i, cell);
                        }
                        cells.get(0).setCellValue(workOrderLine.getName());
                        cells.get(1).setCellValue(workOrderLine.getCode());
                        cells.get(2).setCellValue(workOrderLine.getDeliveryItem());
                        cells.get(3).setCellValue(workOrderLine.getQuantity());
                        cells.get(4).setCellValue(workOrderLine.getpO());
                        cells.get(5).setCellValue(workOrderLine.getPartNumber());
                        cells.get(6).setCellValue(workOrderLine.getTech());
                        r[0]++;

                    });
                });


        FileOutputStream fileOut = new FileOutputStream(deliveryOutputName);
        //write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        wb.close();
    }

    private XSSFWorkbook prepareWorkbook(String path) {
        File file = new File(path);
        InputStream ExcelFileToRead;
        XSSFWorkbook workbook = null;
        if (file.exists()) {
            ZipSecureFile.setMinInflateRatio(0);
            try {
                ExcelFileToRead = new FileInputStream(file);
                workbook = new XSSFWorkbook(ExcelFileToRead);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            workbook = new XSSFWorkbook();
        }
        return workbook;
    }

    ArrayList<StockBalanceLine> importStockBalance(String excelFilePath) {

        ArrayList<StockBalanceLine> balance = new ArrayList<>();
        if (excelFilePath != null) {
            try {
                FileInputStream excelFile = new FileInputStream(new File(excelFilePath));
                XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
                XSSFSheet sheet = workbook.getSheet("Priorities_HW_NOKIA");
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row currentRow = sheet.getRow(i);
                    balance.add(stockLineReader(currentRow));
                    int z = 1;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return balance;
    }

    private StockBalanceLine stockLineReader(Row currentRow) {
        StockBalanceLine stockBalanceLine = new StockBalanceLine();
        stockBalanceLine.setPo(currentRow.getCell(0).getStringCellValue());
        stockBalanceLine.setItemCode(currentRow.getCell(1).getStringCellValue());
        stockBalanceLine.setItemDescription(currentRow.getCell(2).getStringCellValue());
        stockBalanceLine.setAvailableBalance((int) currentRow.getCell(3).getNumericCellValue());
        stockBalanceLine.setPriority(currentRow.getCell(4).getStringCellValue());
        return stockBalanceLine;
    }


}

// String cellNumber = cell.getReference();
