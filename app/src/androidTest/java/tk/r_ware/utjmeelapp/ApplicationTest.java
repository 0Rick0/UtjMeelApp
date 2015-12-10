package tk.r_ware.utjmeelapp;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Test;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.Info;
import tk.r_ware.utjmeelapp.Communication.containers.Statistics;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void test(){
        Communication instance = Communication.getInstance();
        //test login method
        Assert.assertFalse(instance.login("UtjMeel", "notAdmin"));
        Assert.assertTrue(instance.login("UtjMeel", "admin"));

        Info info = instance.info();//get info
        Info cInfo = instance.getCachedInfo();
        Assert.assertSame(info,cInfo);//check if chased info is the same as normal info

        Statistics stats = instance.statistics();
    }
}