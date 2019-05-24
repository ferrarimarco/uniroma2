package info.ferrarimarco.uniroma2.msa.resourcesharing.app.util;

import android.content.Context;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;

public class ObjectGraphUtils{

    private static ObjectGraph objectGraph;

    public static ObjectGraph getObjectGraph(Context context){

        if (objectGraph == null) {
            if (context != null) {
                // Check if we are inside the application
                if (context.getApplicationContext() != null) {
                    objectGraph = ObjectGraph.create(new ContextModuleImpl(context.getApplicationContext()));
                } else {
                    // Do not cache this instance as we want to cache only when inside the application
                    return ObjectGraph.create(new ContextModuleImpl(context));
                }
            } else {
                throw new IllegalArgumentException("Context cannot be null");
            }
        }

        return objectGraph;
    }
}
