package info.ferrarimarco.uniroma2.msa.resourcesharing.app.util;

import android.content.Context;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;

public class ObjectGraphUtils{

    private static ObjectGraph objectGraph;

    public static ObjectGraph getObjectGraph(Context context){
        if(objectGraph == null && context != null){
            objectGraph = ObjectGraph.create(new ContextModuleImpl(context.getApplicationContext()));
        }
        return objectGraph;
    }
}
