package com.josedlpozo.optimiza;

import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.josedlpozo.adapters.NavigationAdapter;
import com.josedlpozo.database.AppDbAdapter;
import com.josedlpozo.database.AppsDbHelper;
import com.josedlpozo.fragments.BatteryFragment;
import com.josedlpozo.fragments.MemoryFragment;
import com.josedlpozo.fragments.RecyclerViewFragment;
import com.josedlpozo.fragments.RecyclerViewProcessFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ActionBarActivity {


    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    private ListView mNavList;
    private ArrayList<Item_objct> mNavItms;
    private TypedArray mNavIcons;
    NavigationAdapter mNavAdapter;

    private String[] titulos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Lista
        mNavList = (ListView) findViewById(R.id.lista);
        //Declaramos el header el caul sera el layout de header.xml
        View header = getLayoutInflater().inflate(R.layout.header, null);
        //Establecemos header
        mNavList.addHeaderView(header);
        //Tomamos listado  de imgs desde drawable
        mNavIcons = getResources().obtainTypedArray(R.array.navigation_iconos);
        //Tomamos listado  de titulos desde el string-array de los recursos @string/nav_options
        titulos = getResources().getStringArray(R.array.nav_options);
        //Listado de titulos de barra de navegacion
        mNavItms = new ArrayList<Item_objct>();
        //Agregamos objetos Item_objct al array
        //Perfil
        mNavItms.add(new Item_objct(titulos[0], mNavIcons.getResourceId(0, -1)));
        //Favoritos
        mNavItms.add(new Item_objct(titulos[1], mNavIcons.getResourceId(1, -1)));
        //Eventos
        mNavItms.add(new Item_objct(titulos[2], mNavIcons.getResourceId(2, -1)));
        //Lugares
        mNavItms.add(new Item_objct(titulos[3], mNavIcons.getResourceId(3, -1)));

        //Declaramos y seteamos nuestrp adaptador al cual le pasamos el array con los titulos
        mNavAdapter = new NavigationAdapter(this, mNavItms);
        mNavList.setAdapter(mNavAdapter);
        //Siempre vamos a mostrar el mismo titulo

        mNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Item_objct item = (Item_objct) mNavList.getItemAtPosition(position);

                switch (item.getTitulo()) {
                    case "Permisos":
                        mViewPager.getViewPager().setCurrentItem(0);
                        mDrawer.closeDrawers();
                        break;
                    case "Batería":
                        mViewPager.getViewPager().setCurrentItem(1);
                        mDrawer.closeDrawers();
                        break;
                    case "Procesos":
                        mViewPager.getViewPager().setCurrentItem(2);
                        mDrawer.closeDrawers();
                        break;
                    case "Memoria":
                        mViewPager.getViewPager().setCurrentItem(3);
                        mDrawer.closeDrawers();
                        break;
                    default:
                        mViewPager.getViewPager().setCurrentItem(3);
                }

            }
        });


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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
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
                    case 2:
                        return RecyclerViewProcessFragment.newInstance();
                    case 3:
                        return MemoryFragment.newInstance();
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
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Permisos";
                    case 1:
                        return "Batería";
                    case 2:
                        return "Procesos";
                    case 3:
                        return "Memoria";
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
                registro.put(AppDbAdapter.COLUMNA_IGNORADA, 0);
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


}
