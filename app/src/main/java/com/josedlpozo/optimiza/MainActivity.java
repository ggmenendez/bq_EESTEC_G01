package com.josedlpozo.optimiza;

import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.josedlpozo.database.AppDbAdapter;
import com.josedlpozo.database.AppsDbHelper;
import com.josedlpozo.fragments.BatteryFragment;
import com.josedlpozo.fragments.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ActionBarActivity {


    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("");

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        toolbar = mViewPager.getToolbar();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "HOLA", Toast.LENGTH_LONG).show();
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.setDrawerListener(mDrawerToggle);


        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            int oldPosition = -1;

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return RecyclerViewFragment.newInstance();
                    case 1:
                        return BatteryFragment.newInstance();
                    //case 2:
                    //    return ListViewFragment.newInstance();
                    //case 3:
                    //    return WebViewFragment.newInstance();
                    default:
                        return RecyclerViewFragment.newInstance();
                }
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);

                //only if position changed
                if (position == oldPosition)
                    return;
                oldPosition = position;

                int color = 0;
                String imageUrl = "";
                switch (position) {
                    case 0:
                        imageUrl = "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg";
                        color = getResources().getColor(R.color.blue);
                        break;
                    case 1:
                        imageUrl = "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg";
                        color = getResources().getColor(R.color.green);
                        break;
                    case 2:
                        imageUrl = "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg";
                        color = getResources().getColor(R.color.cyan);
                        break;
                    case 3:
                        imageUrl = "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg";
                        color = getResources().getColor(R.color.red);
                        break;
                }

                final int fadeDuration = 400;
                mViewPager.setImageUrl(imageUrl, fadeDuration);
                mViewPager.setColor(color, fadeDuration);

            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Permisos";
                    case 1:
                        return "Batería";
                }
                return "";
            }
        });
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        /*
        * Declaramos el controlador de la BBDD y accedemos en modo escritura
        */

        AppsDbHelper dbHelper = new AppsDbHelper(getBaseContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        AppDbAdapter dbAdapter = new AppDbAdapter(getBaseContext());


        Cursor mCursor = db.rawQuery("SELECT * FROM " + "Permisos_App", null);

        if (!mCursor.moveToFirst()) {
            Log.d("JOSE", "ENTRA");
            Toast.makeText(this, "I AM EMPTY", Toast.LENGTH_LONG).show();
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            ArrayList<AppsPermisos> apps = new ArrayList<>();

            for (ApplicationInfo applicationInfo : packages) {
                Log.d("test", "App: " + pm.getApplicationLabel(applicationInfo) + " Package: " + applicationInfo.packageName);

                try {
                    PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                    //Get Permissions
                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if (requestedPermissions == null) continue;
                    AppsPermisos app = new AppsPermisos(pm.getApplicationIcon(packageInfo.packageName), pm.getApplicationLabel(applicationInfo).toString(), requestedPermissions, applicationInfo.packageName);

                    apps.add(app);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(apps, new Comparator<AppsPermisos>() {
                @Override
                public int compare(AppsPermisos lhs, AppsPermisos rhs) {
                    return rhs.getNumPermisos() - lhs.getNumPermisos();
                }
            });

            for (AppsPermisos app : apps) {
                ContentValues registro = new ContentValues();
                registro.put(AppDbAdapter.COLUMNA_NOMBRE, app.getNombre());
                registro.put(AppDbAdapter.COLUMNA_PAQUETES, app.getNombrePaquete());
                dbAdapter.insert(registro);
            }
            db.close();
        }
        db.close();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
