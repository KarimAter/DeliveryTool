package sample;

public class KeyData {

    private int quantity;
    private String tech, po, itemDescription, longItemCode, output;
    private String partNumber;

     KeyData(int quantity) {
        this.quantity = quantity;
    }

     KeyData(int quantity, String tech) {
        this.quantity = quantity;
        this.tech = tech;
    }

     KeyData(String tech) {
        this.tech = tech;
    }

    // for merging
    static KeyData sum(KeyData q1, KeyData q2) {
        return new KeyData(q1.getQuantity() + q2.getQuantity(),q1.getTech());
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

     String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

     String getItemDescription() {
        return itemDescription;
    }

     void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getLongItemCode() {
        return longItemCode;
    }

    public void setLongItemCode(String longItemCode) {
        this.longItemCode = longItemCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

     void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

     String getPartNumber() {
        return partNumber;
    }
}
