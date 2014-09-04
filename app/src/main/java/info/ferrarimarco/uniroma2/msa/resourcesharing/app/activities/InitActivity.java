package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.os.Bundle;
import android.view.View;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;

public class InitActivity extends AbstractAsyncTaskActivity {

    @InjectView(R.id.init_progress)
    View progressView;

    @InjectView(R.id.init_content)
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    public View getProgressView() {
        return progressView;
    }

    @Override
    public View getContentView() {
        return contentView;
    }
}
