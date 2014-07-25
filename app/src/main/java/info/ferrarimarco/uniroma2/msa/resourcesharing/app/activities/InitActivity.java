package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ServiceModuleImpl;

public class InitActivity extends Activity{

    @Inject
    GenericDao<User> userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        if(this.getActionBar() != null){
            this.getActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        // Check if there is already a defined user
        ObjectGraph objectGraph = ObjectGraph.create(new ContextModuleImpl(this.getApplicationContext()), new DaoModuleImpl(), new ServiceModuleImpl());
        objectGraph.inject(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
        try{
            userDao.open(User.class);
        }catch(SQLException e){
            e.printStackTrace();
        }

        User registeredUser = new User();
        Long registered_user_id = Long.parseLong(getResources().getString(R.string.registered_user_id));
        registeredUser.setId(registered_user_id);

        List<User> users = null;

        try{
            users = userDao.read(registeredUser);
        }catch(SQLException e){
            e.printStackTrace();
        }

        userDao.close();

        // Check if there is a registered user
        if(users != null && !users.isEmpty()){
            Intent intent = new Intent(this, ShowResourcesActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, RegisterNewUserActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.init, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
