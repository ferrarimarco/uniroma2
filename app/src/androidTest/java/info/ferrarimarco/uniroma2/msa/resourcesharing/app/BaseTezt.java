package info.ferrarimarco.uniroma2.msa.resourcesharing.app;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.TestDaoModuleImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

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

    public void initObjectGraph() {
        objectGraph = ObjectGraph.create(new ContextModuleImpl(context), new DaoModuleImpl(), new TestDaoModuleImpl());
        objectGraph.inject(this);
    }

    @Test
    public void contextTest() {
        assertThat(context, notNullValue());
    }

    @Test
    public void objectGraphTest() {
        assertThat(objectGraph, notNullValue());
    }

    @Test
    public abstract void dependencyInjectionTest();

    protected void singleDependencyCheck(Object dependency) {
        assertThat(dependency, notNullValue());
    }

    @BeforeClass
    public static void setup() {
        System.setProperty("robolectric.logging", "stdout");
    }
}
