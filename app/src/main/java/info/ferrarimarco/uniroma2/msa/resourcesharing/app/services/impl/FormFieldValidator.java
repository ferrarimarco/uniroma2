package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import android.text.TextUtils;
import android.widget.EditText;

import javax.inject.Inject;


public class FormFieldValidator {

    @Inject
    public FormFieldValidator(){}

    public Boolean validateNonEmptyTextField(String text){
        return !TextUtils.isEmpty(text);
    }

    public Boolean validatePasswordField(String password){
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public Boolean validateEmailField(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
