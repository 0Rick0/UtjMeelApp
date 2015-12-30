package tk.r_ware.utjmeelapp.Communication.containers;

import java.util.List;

/**
 * Created by Rick on 28-11-2015.
 */
public class Info {
    private int error;
    private List<InfoItem> items;
    private List<Notification> notifications;
    private double balance;
    private int user_type;
    private double price;
    private List<String> usernames;

    public List<InfoItem> getItems() {
        return items;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public double getBalance() {
        return balance;
    }

    public int getUser_type() {
        return user_type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getUsernames(){
        return usernames;
    }
}
