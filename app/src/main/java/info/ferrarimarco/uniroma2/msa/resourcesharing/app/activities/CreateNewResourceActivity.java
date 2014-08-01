package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.joda.time.DateTime;

import java.sql.SQLException;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.ResourceType;

public class CreateNewResourceActivity extends Activity {

    @Inject
    GenericDao<Resource> resDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_resource);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_new_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_save_new_resource) {
            // Test the insertion of a new resource
            try {
                resDao.open(Resource.class);

                Resource newRes = new Resource("Title", "Desc", "LOC", DateTime.now(), "ACQ", "CREATOR", ResourceType.CREATED_BY_ME);

                resDao.save(newRes);

                // TODO: update this only when returning from a correct insert
//                if(getActionBar() != null && getActionBar().getSelectedNavigationIndex() == 1){
//                    resourceArrayAdapter.add(newRes);
//                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                resDao.close();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
