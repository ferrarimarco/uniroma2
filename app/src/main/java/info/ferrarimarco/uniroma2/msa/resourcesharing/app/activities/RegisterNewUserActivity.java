package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.UserDeletionCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.UserLocalRegistrationCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack.UserIdCheckAckAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack.UserSavedAckAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.FormFieldValidatorImpl;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterNewUserActivity extends AbstractAsyncTaskActivity {

    @Inject
    FormFieldValidatorImpl formFieldValidatorImpl;

    @InjectView(R.id.email)
    AutoCompleteTextView mEmailView;

    @InjectView(R.id.password)
    EditText mPasswordView;

    @InjectView(R.id.register_new_user_progress)
    View mProgressView;

    @InjectView(R.id.register_new_user_form)
    View mRegisterNewUserFormView;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        ButterKnife.inject(this);

        this.defaultInitialization(mProgressView, mRegisterNewUserFormView);

        objectGraph.inject(this);

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
        if (areFieldsValid()) {
            showProgress(true);
            userService.registerNewUser(email, password);
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
        if (formFieldValidatorImpl.validateNonEmptyTextField(password) && !formFieldValidatorImpl.validatePasswordField(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            result = false;
        }

        // Check for a valid email address.
        if (result && formFieldValidatorImpl.validateNonEmptyTextField(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            result = false;
        }

        // Check if the user wrote a valid email
        if (result && !formFieldValidatorImpl.validateEmailField(email)) {
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

    @Subscribe
    public void userIdCheckAckAvailable(UserIdCheckAckAvailableEvent event) {
        if (TaskResultType.USER_ID_OK.equals(event.getResult())) {
            gcmMessagingService.registerNewUser(userService.readRegisteredUserSync());
        } else if (TaskResultType.USER_ID_NOT_FREE.equals(event.getResult())) {
            userService.deleteRegisteredUser();
            mEmailView.setError(getString(R.string.error_user_id_already_taken));
            mEmailView.requestFocus();
        }
    }

    @Subscribe
    public void userSavedAckAvailable(UserSavedAckAvailableEvent event) {
        if (TaskResultType.USER_SAVED.equals(event.getResult())) {
            Intent intent = new Intent(this, ShowResourcesActivity.class);
            startActivity(intent);
            finish();
        } else if (TaskResultType.USER_NOT_SAVED.equals(event.getResult())) {
            // TODO: handle this error condition
        }
    }

    @Subscribe
    public void userLocalRegistrationCompletedAvailable(UserLocalRegistrationCompletedEvent event) {
        UserTaskResult result = event.getResult();

        if (TaskResultType.SUCCESS.equals(result.getTaskResultType())) {
            User registeredUser = userService.readRegisteredUserSync();
            gcmMessagingService.checkUserIdValidity(registeredUser.getEmail());
        } else {
            showProgress(false);
            // TODO: handle this error condition
        }
    }

    @Subscribe
    public void userDeletionCompletedAvailable(UserDeletionCompletedEvent event) {
        UserTaskResult result = event.getResult();

        showProgress(false);

        if (TaskResultType.SUCCESS.equals(result.getTaskResultType())) {
            // TODO: user successfully deleted from local DB. Anything else to do?
        } else {
            // TODO: handle this error condition
        }
    }
}



