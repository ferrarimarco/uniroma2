package info.ferrarimarco.uniroma2.msa.resourcesharing.app.util;

import android.content.Context;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;

public class ObjectGraphUtils{

    private static ObjectGraph objectGraph;

    private ObjectGraphUtils(){
    }

    private static void initObjectGraph(Context applicationContext){
        if(objectGraph != null){
            objectGraph = ObjectGraph.create(new ContextModuleImpl(applicationContext), new DaoModuleImpl());
        }
    }

    public static ObjectGraph getObjectGraph(Context applicationContext){
        if(objectGraph == null){
            initObjectGraph(applicationContext);
        }

        return objectGraph;
    }
}
