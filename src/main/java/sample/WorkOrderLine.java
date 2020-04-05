package sample;

public class WorkOrderLine {

    private String code, Name, deliveryItem, pO, partNumber, tech;
    private int quantity;

    WorkOrderLine() {
    }

    // for cloning
    WorkOrderLine(WorkOrderLine workOrderLine) {
        this.quantity = workOrderLine.getQuantity();
        this.code = workOrderLine.getCode();
        this.Name = workOrderLine.getName();
        this.deliveryItem = workOrderLine.getDeliveryItem();
        this.pO = workOrderLine.getpO();
        this.partNumber = workOrderLine.getPartNumber();
        this.tech = workOrderLine.getTech();
    }

    // for merging
    WorkOrderLine merge(WorkOrderLine another) {
        if (another.getDeliveryItem().equals(this.getDeliveryItem()))
            this.setQuantity(quantity + another.getQuantity());
        return WorkOrderLine.this;
    }

     String getDeliveryItem() {
        return deliveryItem;
    }

     void setDeliveryItem(String deliveryItem) {
        this.deliveryItem = deliveryItem;
    }

     String getpO() {
        return pO;
    }

     void setpO(String pO) {
        this.pO = pO;
    }

     String getPartNumber() {
        return partNumber;
    }

     void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

     int getQuantity() {
        return quantity;
    }

     void setQuantity(int quantity) {
        this.quantity = quantity;
    }

     String getTech() {
        return tech;
    }

     void setTech(String tech) {
        this.tech = tech;
    }

     String getCode() {
        return code;
    }

     void setCode(String code) {
        this.code = code;
    }

     String getName() {
        return Name;
    }

     void setName(String name) {
        Name = name;
    }


}
