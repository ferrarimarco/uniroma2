package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.accounts.Account;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.AccountUtils;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public abstract class AbstractAsyncTaskActivity extends Activity implements AsyncCaller {

    private View progressBarView;
    private View mainView;

    protected ObjectGraph objectGraph;

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1002;

    protected void defaultInitialization(View progressBarView, View mainView) {
        this.progressBarView = progressBarView;
        this.mainView = mainView;
    }

    /**
     * Shows the progress UI and hides the form.
     */
    public void showProgress(final boolean show) {

        if (mainView == null || progressBarView == null) {
            throw new IllegalStateException("The activity is not correctly initialized. Main view or progress bar view is null");
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        mainView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBarView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectGraph = ObjectGraphUtils.getObjectGraph(this.getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPlayServices()) {
            // Then we're good to go!
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Google Play Services must be installed.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    private boolean checkUserAccount() {
        String accountName = AccountUtils.getAccountName(this);
        if (accountName == null) {
            // Then the user was not found in the SharedPreferences. Either the
            // application deliberately removed the account, or the application's
            // data has been forcefully erased.
            showAccountPicker();
            return false;
        }

        Account account = AccountUtils.getGoogleAccountByName(this, accountName);
        if (account == null) {
            // Then the account has since been removed.
            AccountUtils.removeAccount(this);
            showAccountPicker();
            return false;
        }

        return true;
    }

    private void showAccountPicker() {
        Intent pickAccountIntent = AccountPicker.newChooseAccountIntent(
                null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                true, null, null, null, null);
        startActivityForResult(pickAccountIntent, REQUEST_CODE_PICK_ACCOUNT);
    }
}
