package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.RegisterNewUserAsyncTask;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterNewUserActivity extends Activity implements LoaderCallbacks<Cursor>, AsyncCaller {

    @InjectView(R.id.email)
    AutoCompleteTextView mEmailView;

    @InjectView(R.id.password)
    EditText mPasswordView;

    @InjectView(R.id.login_progress)
    View mProgressView;

    @InjectView(R.id.register_new_user_form)
    View mRegisterNewUserFormView;

    private String email;
    private String password;

    private ObjectGraph objectGraph;

    private RegisterNewUserAsyncTask registerNewUserAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        ButterKnife.inject(this);

        // Check if there is already a defined user
        objectGraph = ObjectGraph.create(new ContextModuleImpl(this.getApplicationContext()), new DaoModuleImpl());
        objectGraph.inject(this);

        // Set up the registration form (populate auto complete)
        getLoaderManager().initLoader(0, null, this);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register_new_user || id == EditorInfo.IME_NULL) {
                    registerNewUser();
                    return true;
                }
                return false;
            }
        });

    }

    @OnClick(R.id.register_new_user_button)
    public void registerNewUserButtonClickListener() {
        registerNewUser();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void registerNewUser() {

        if (registerNewUserAsyncTask != null) {
            return;
        }

        if (areFieldsValid()) {
            registerNewUserAsyncTask = objectGraph.get(RegisterNewUserAsyncTask.class);
            registerNewUserAsyncTask.initTask(this, this.getApplicationContext());
            executeTask(registerNewUserAsyncTask);
        }
    }

    private boolean areFieldsValid() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Reset fields
        email = "";
        password = "";

        // Store values at the time of the registration attempt.
        if (mEmailView.getText() != null) {
            email = mEmailView.getText().toString();
        }

        if (mPasswordView.getText() != null) {
            password = mPasswordView.getText().toString();
        }

        View focusView = null;
        boolean result = true;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            result = false;
        }

        // Check for a valid email address.
        if (result && TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            result = false;
        }

        // Check if the user wrote a valid email
        if (result && !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            result = false;
        }

        if (!result) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return result;
    }

    private void executeTask(AsyncTask<String, Void, UserTaskResult> task) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        task.execute(email, password);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterNewUserFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterNewUserFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterNewUserFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {
        registerNewUserAsyncTask = null;
        showProgress(false);

        UserTaskResult taskResult = (UserTaskResult) result;

        if (taskResult.getUserTaskResultType().equals(UserTaskResult.UserTaskResultType.SUCCESS)) {
            Intent intent = new Intent(this, ShowResourcesActivity.class);
            startActivity(intent);
            finish();
        } else {
            // TODO: handle this error condition
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    @Override
    public void onBackgroundTaskCancelled() {
        registerNewUserAsyncTask = null;
        showProgress(false);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterNewUserActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


}



