package sample;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Reader {
    List<Delivery> readCurrentTarget(XSSFSheet sheet) {
        ArrayList<DeliveryLine> deliveryLines = new ArrayList<>();
        List<Delivery> deliveries;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            deliveryLines.add(deliveryLineReader(currentRow));

            int z = 1;
        }
        deliveries = deliveryLines.stream()
                .collect(Collectors.groupingBy(DeliveryLine::getCode, Collectors.groupingBy(DeliveryLine::getActionType)))
                .entrySet().stream().map(stringMapEntry -> new Delivery(stringMapEntry.getKey(),
                        stringMapEntry.getValue())).collect(Collectors.toList());
        return deliveries;
    }


    private DeliveryLine deliveryLineReader(Row currentRow) {
        DeliveryLine deliveryLine = new DeliveryLine();
        deliveryLine.setActionType(currentRow.getCell(0).getStringCellValue());
        deliveryLine.setCode(currentRow.getCell(1).getStringCellValue());
        deliveryLine.setName(currentRow.getCell(2).getStringCellValue());
        deliveryLine.setTech(currentRow.getCell(3).getStringCellValue());
        deliveryLine.setBand(String.valueOf(currentRow.getCell(4).getNumericCellValue()));
        deliveryLine.setFeederType(currentRow.getCell(5).getStringCellValue());
        deliveryLine.setCurrentRf(currentRow.getCell(6).getStringCellValue());
        deliveryLine.setTargetRf(currentRow.getCell(7).getStringCellValue());
        deliveryLine.setCurrentSm(currentRow.getCell(8).getStringCellValue());
        deliveryLine.setTargetSm(currentRow.getCell(9).getStringCellValue());
        deliveryLine.setU9Soln(currentRow.getCell(10).getStringCellValue());
        deliveryLine.setTxMode(currentRow.getCell(11).getStringCellValue());
        deliveryLine.setAntennaType(currentRow.getCell(12).getStringCellValue());
        deliveryLine.setNumberOfAddedSectors((int) currentRow.getCell(13).getNumericCellValue());
        deliveryLine.finish();
        return deliveryLine;
    }

}
