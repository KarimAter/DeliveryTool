package sample;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Delivery {

    private String code;
    private Map<String, List<DeliveryLine>> actionDelivery;
    List<WorkOrderLine> workOrderLines;

    public Delivery(String code, Map<String, List<DeliveryLine>> actionDelivery) {
        this.code = code;
        List<DeliveryLine> deliveryLines;
        int deltaFMFAs = getDeltaFMFAs(actionDelivery);
        List<String> techs = actionDelivery.values().stream().flatMap(List::stream).map(DeliveryLine::getTech).sorted().collect(Collectors.toList());


        deliveryLines = actionDelivery.values().stream().flatMap(List::stream).map(deliveryLine -> {
            if (deliveryLine.getTech().equals(techs.get(0)) && deltaFMFAs > 0) {
                Map<String, KeyData> filteredKeysMap = deliveryLine.getFilteredKeysMap();
                filteredKeysMap.put("FMFA", new KeyData(deltaFMFAs, techs.get(0)));
                deliveryLine.setFilteredKeysMap(filteredKeysMap);
                return deliveryLine;
            } else return deliveryLine;

        }).collect(Collectors.toList());

        DatabaseHelper databaseHelper = new DatabaseHelper(Constants.databasePath);
        workOrderLines = deliveryLines.stream().map(databaseHelper::generateWorkOrderLines).flatMap(List::stream).collect
                (Collectors.toList());

        this.actionDelivery = actionDelivery;
    }

    private int getDeltaFMFAs(Map<String, List<DeliveryLine>> actionDelivery) {
        Integer currentIndoorModules = actionDelivery.entrySet().stream().flatMap(stringListEntry ->
                stringListEntry.getValue().stream().map(DeliveryLine::getCurrentNumberOfIndoorModules)).reduce(0, Integer::sum);

        Integer targetIndoorModules = actionDelivery.entrySet().stream().flatMap(stringListEntry ->
                stringListEntry.getValue().stream().map(DeliveryLine::getTargetNumberOfIndoorModules)).reduce(0, Integer::sum);
        double currentFMFAs = Math.ceil((float) currentIndoorModules / 7);
        double targetFMFAs = Math.ceil((float) targetIndoorModules / 7);

        return (int) (targetFMFAs - currentFMFAs);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, List<DeliveryLine>> getActionDelivery() {
        return actionDelivery;
    }

    public void setActionDelivery(HashMap<String, List<DeliveryLine>> actionDelivery) {
        this.actionDelivery = actionDelivery;
    }

    public List<WorkOrderLine> getWorkOrderLines() {

////        ArrayList<WorkOrderLine> collect = workOrderLines.stream().collect(Collectors.collectingAndThen
////                (Collectors.toMap(WorkOrderLine::getTech, Function.identity(), (left, right) -> {
////                    if (left.getDeliveryItem().equals(right.getDeliveryItem()))
////                        left.setQuantity(left.getQuantity() + (right.getQuantity()));
////                    return left;
////                }), m -> new ArrayList<>(m.values())));
////        ArrayList<WorkOrderLine> collect = new ArrayList<>(this.workOrderLines.stream().collect(Collectors.toMap(WorkOrderLine::getTech, WorkOrderLine::new, WorkOrderLine::merge)).values());
//
//        Map<String, List<WorkOrderLine>> collect = workOrderLines.stream().collect(Collectors.groupingBy(WorkOrderLine::getTech));
//        collect.values().stream().collect(Collectors.toMap(WorkOrderLine::getDeliveryItem,Function.identity(),(a,b)->{
//
//
//        })
        Map<String, Map<String, List<WorkOrderLine>>> collect = workOrderLines.stream().collect(Collectors.groupingBy(WorkOrderLine::getTech,
                Collectors.groupingBy(WorkOrderLine::getDeliveryItem)));
        List<WorkOrderLine> collect1 = collect.entrySet().stream().map(stringMapEntry -> stringMapEntry.getValue().entrySet().stream().map(stringListEntry -> new ArrayList<>(stringListEntry.getValue().stream().collect(Collectors.toMap(
                WorkOrderLine::getDeliveryItem,
                WorkOrderLine::new,
                WorkOrderLine::merge)).values())).flatMap(List::stream).collect(Collectors.toList())).flatMap(List::stream).collect(Collectors.toList());

        return collect1;
    }

    public void setWorkOrderLines(List<WorkOrderLine> workOrderLines) {
        this.workOrderLines = workOrderLines;
    }

    void method() {

//        actionDelivery.entrySet().stream().filter(stringListEntry ->
//                stringListEntry.getValue().stream().filter(deliveryLine -> deliveryLine.getTech().equals("2G"))
//                        .map()

    }
}
