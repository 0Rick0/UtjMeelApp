package tk.r_ware.utjmeelapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;

/**
 * Created by Rick on 2-2-2016.
 */
public class ImageGetter implements Html.ImageGetter {
    private final Context context;
    private int size;

    public void setHeight(int size){
        this.size = size;
    }

    public ImageGetter(Context context, int size) {
        this.context = context;
        this.size = size;
    }

    public Drawable getDrawable(String source) {
        int id;

        if (source.equals("icon_coin.png")) {
            id = R.drawable.icon_coin;
        }
        else {
            return null;
        }

        Drawable d = context.getResources().getDrawable(id);
        if(d == null)return null;
        d.setBounds(0,0,size,size);
        return d;
    }
}
