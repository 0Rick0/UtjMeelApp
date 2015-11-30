package tk.r_ware.utjmeelapp.Communication.containers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Rick on 28-11-2015.
 */
public class Statistics {
    private int error;
    private Date begin;
    private Date end;
    private List<StatItem> items;
    private int totalCoinsOut;
    private int totalCoinsIn;

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public List<StatItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalCoinsOut() {
        return totalCoinsOut;
    }

    public int getTotalCoinsIn() {
        return totalCoinsIn;
    }
}
