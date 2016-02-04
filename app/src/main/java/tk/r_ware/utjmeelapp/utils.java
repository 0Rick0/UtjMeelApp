package tk.r_ware.utjmeelapp;

import android.content.res.Resources;

/**
 * Created by Rick on 4-2-2016.
 */
public class utils {

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


}
