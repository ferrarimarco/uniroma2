package info.ferrarimarco.uniroma2.msa.resourcesharing.app;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.TestDaoModuleImpl;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public abstract class BaseDaoTezt extends BaseTezt{

    public BaseDaoTezt(){
        super();
    }

    @Override
    public void initObjectGraph(){
        objectGraph = ObjectGraph.create(new ContextModuleImpl(context), new DaoModuleImpl(), new TestDaoModuleImpl());
        objectGraph.inject(this);
    }
}
