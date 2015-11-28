package tk.r_ware.utjmeelapp.Communication.containers;

import java.util.Collections;
import java.util.List;

/**
 * Created by Rick on 28-11-2015.
 */
public class Transactions {
    private int offset;
    private int amount;
    private List<Transaction> transactions;

    public int getOffset() {
        return offset;
    }

    public int getAmount() {
        return amount;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
