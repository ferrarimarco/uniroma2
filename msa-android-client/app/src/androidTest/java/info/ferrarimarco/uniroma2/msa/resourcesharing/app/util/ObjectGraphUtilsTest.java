package info.ferrarimarco.uniroma2.msa.resourcesharing.app.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseTezt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ObjectGraphUtilsTest extends BaseTezt{

    public ObjectGraphUtilsTest(){
        super();
    }

    @Test
    public void getObjectGraphTest(){
        assertThat(ObjectGraphUtils.getObjectGraph(context), notNullValue());
    }

    @Override
    public void dependencyInjectionTest(){
    }
}
