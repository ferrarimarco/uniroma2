package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import android.os.AsyncTask;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginAsyncTask extends AsyncTask<Void, Void, Boolean> {

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[]{
			"foo@example.com:hello", "bar@example.com:world"
	};

	private final String mEmail;
	private final String mPassword;

	private AsyncCaller caller;

	public UserLoginAsyncTask(AsyncCaller caller, String email, String password) {
		this.caller = caller;
		mEmail = email;
		mPassword = password;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO: attempt authentication against a network service.

		try {
			// Simulate network access.
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			return false;
		}

		for (String credential : DUMMY_CREDENTIALS) {
			String[] pieces = credential.split(":");
			if (pieces[0].equals(mEmail)) {
				// Account exists, return true if the password matches.
				return pieces[1].equals(mPassword);
			}
		}

		// TODO: register the new account here.
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean result) {
		caller.onBackgroundTaskCompleted(result);
	}

	@Override
	protected void onCancelled() {
		caller.onBackgroundTaskCancelled();
	}
}
