package tk.r_ware.utjmeelapp.Communication.containers;

import java.util.Date;

/**
 * Created by Rick on 28-11-2015.
 */
public class Transaction {
    private int id;
    private String sourceName;
    private String targetName;
    private double amount;
    private Date date;
    private String itemName;
    private int itemCount;
    private String description;

    public int getId() {
        return id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getDescription() {
        return description;
    }
}
