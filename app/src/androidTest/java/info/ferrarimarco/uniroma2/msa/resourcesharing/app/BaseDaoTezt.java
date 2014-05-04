package info.ferrarimarco.uniroma2.msa.resourcesharing.app;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;

public abstract class BaseDaoTezt extends BaseTezt {

    public BaseDaoTezt() {
        super();
    }

    @Override
    public void initObjectGraph() {
        objectGraph = ObjectGraph.create(new DaoModuleImpl());
        objectGraph.inject(this);
    }
}
