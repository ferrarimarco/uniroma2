package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.os.Bundle;
import android.view.View;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;

public class InitActivity extends AbstractAsyncTaskActivity {

    @InjectView(R.id.init_progress)
    View mProgressView;

    @InjectView(R.id.init_content)
    View mInitContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_init);

        super.onCreate(savedInstanceState);

        this.defaultInitialization(mProgressView, mInitContentView);
    }
}
