package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import javax.inject.Inject;

/**
 * Created by Marco on 01/03/14.
 */
public class DatabaseHelperManager {

	@Inject
	public DatabaseHelperManager() {
	}

	public DatabaseHelper getHelper(Context context) {
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	public void releaseHelper() {
		OpenHelperManager.releaseHelper();
	}
}
