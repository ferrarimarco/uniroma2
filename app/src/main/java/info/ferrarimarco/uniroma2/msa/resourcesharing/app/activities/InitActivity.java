package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.GcmRegistrationCompletedEvent;

public class InitActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    protected Class getRedirectActivityClass() {
        return ShowResourcesActivity.class;
    }

    @Override
    protected boolean terminateActivityAfterRedirect() {
        return true;
    }

    @Override
    @Subscribe
    public void gcmRegistrationCompletedEventListener(GcmRegistrationCompletedEvent gcmRegistrationCompletedEvent){
        super.gcmRegistrationCompletedEvent(gcmRegistrationCompletedEvent);
    }
}
