package info.ferrarimarco.uniroma2.msa.resourcesharing.app;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import dagger.ObjectGraph;

import static org.junit.Assert.assertNotNull;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public abstract class BaseTezt {

	protected Context context;

	protected ObjectGraph objectGraph;

	public BaseTezt() {
		initContext();
		initObjectGraph();
	}

	public void initContext() {
		context = Robolectric.application.getApplicationContext();
	}

	public abstract void initObjectGraph();

	@Test
	public void contextTest() {
		assertNotNull(context);
	}

	@Test
	public void objectGraphTest() {
		assertNotNull(objectGraph);
	}

	@Test
	public abstract void daoInjectionTest();

	@BeforeClass
	public static void setup() {
		System.setProperty("robolectric.logging", "stdout");
	}
}
