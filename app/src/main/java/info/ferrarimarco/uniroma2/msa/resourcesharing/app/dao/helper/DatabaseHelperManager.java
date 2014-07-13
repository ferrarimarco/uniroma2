package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import javax.inject.Inject;


public class DatabaseHelperManager{

    @Inject
    public DatabaseHelperManager(){
    }

    public DatabaseHelper getHelper(Context context){
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public void releaseHelper(){
        OpenHelperManager.releaseHelper();
    }
}
