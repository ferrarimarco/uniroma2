package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public abstract class AbstractPersistenceService {

    protected ObjectGraph objectGraph;

    @Inject
    Bus bus;

    @Inject
    public AbstractPersistenceService(Context context) {
        objectGraph = ObjectGraphUtils.getObjectGraph(context);
    }
}
