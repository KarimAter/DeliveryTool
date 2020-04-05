package sample;

import java.util.Objects;

public class StockBalanceLine {

    private String po, itemCode, itemDescription, priority;
    private int availableBalance;


    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(int availableBalance) {
        this.availableBalance = availableBalance;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(priority);
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//        StockBalanceLine stockBalanceLine = (StockBalanceLine) obj;
//        return Objects.equals(priority, stockBalanceLine.getPriority());
//    }
}
