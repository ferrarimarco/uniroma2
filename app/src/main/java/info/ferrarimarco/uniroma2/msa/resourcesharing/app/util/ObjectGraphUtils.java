package info.ferrarimarco.uniroma2.msa.resourcesharing.app.util;

import android.content.Context;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;

public class ObjectGraphUtils{

    private static ObjectGraph objectGraph;

    private ObjectGraphUtils(){
    }

    private static void initObjectGraph(Context context){
        if(objectGraph != null){
            objectGraph = ObjectGraph.create(new ContextModuleImpl(context));
        }
    }

    public static ObjectGraph getObjectGraph(Context context){
        if(objectGraph == null){
            initObjectGraph(context.getApplicationContext());
        }

        return objectGraph;
    }
}
