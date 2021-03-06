package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.GenericEntity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application
    public static final String DATABASE_NAME = "resourceSharing.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Resource.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    public <T extends GenericEntity> Dao<T, ?> getEntityDao(final Class<T> entityClass) throws SQLException {
        return getDao(entityClass);
    }

    /**
     * Close the database connections.
     */
    @Override
    public void close() {
        super.close();
    }
}
