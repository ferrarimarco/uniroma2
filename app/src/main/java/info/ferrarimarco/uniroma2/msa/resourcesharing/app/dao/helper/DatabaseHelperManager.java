package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Marco on 01/03/14.
 */
public class DatabaseHelperManager {

	public DatabaseHelper getHelper(Context context) {
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	public void releaseHelper() {
		OpenHelperManager.releaseHelper();
	}
}
