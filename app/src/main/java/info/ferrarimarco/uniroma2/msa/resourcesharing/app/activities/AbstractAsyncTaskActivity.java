package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;

public abstract class AbstractAsyncTaskActivity extends Activity implements AsyncCaller {

    private View progressBarView;
    private View mainView;

    protected ObjectGraph objectGraph;

    protected void defaultInitialization(View progressBarView, View mainView){
        this.progressBarView = progressBarView;
        this.mainView = mainView;
    }

    /**
     * Shows the progress UI and hides the form.
     */
    public void showProgress(final boolean show) {

        if(mainView == null || progressBarView == null){
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
}
