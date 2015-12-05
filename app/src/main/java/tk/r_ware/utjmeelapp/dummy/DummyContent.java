package tk.r_ware.utjmeelapp.dummy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.r_ware.utjmeelapp.Communication.containers.Transaction;
import tk.r_ware.utjmeelapp.TransactionsFragment;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent implements TransactionsFragment.OnListFragmentInteractionListener {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Transaction> ITEMS = new ArrayList<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Transaction item) {


        ITEMS.add(item);
    }

    private static Transaction createDummyItem(int position) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")//todo inportant define date time format for gson deserialize
                .create();
        return gson.fromJson("{\n" +
                "\t\"id\":0,\n" +
                "\t\"sourceName\":\"source\",\n" +
                "\t\"targetName\":\"target\",\n" +
                "\t\"amount\":0.5,\n" +
                "\t\"date\":\"2015-12-04 12:00:00\",\n" +
                "\t\"itemName\":\"Munt\",\n" +
                "\t\"itemCount\":1,\n" +
                "\t\"description\":\"N/A\"\n" +
                "}",Transaction.class);
    }

    @Override
    public void onListFragmentInteraction(Transaction item) {

    }
}
