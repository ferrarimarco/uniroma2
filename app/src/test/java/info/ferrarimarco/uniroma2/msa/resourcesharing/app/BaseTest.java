package info.ferrarimarco.uniroma2.msa.resourcesharing.app;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Marco on 26/02/14.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class BaseTest {

    protected Context context;

    public void initContext() {
        context = Robolectric.application.getApplicationContext();
    }

    @Test
	public void initContextTest() {
        initContext();
        assertNotNull(context);
	}

    @BeforeClass
    public static void setup(){
        System.setProperty("robolectric.logging", "stdout");
    }
}
