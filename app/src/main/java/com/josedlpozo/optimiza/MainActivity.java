package com.josedlpozo.optimiza;

import android.content.ContentValues;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.github.florent37.materialviewpager.HeaderDesign;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.josedlpozo.adapters.NavigationAdapter;
import com.josedlpozo.database.AppDbAdapter;
import com.josedlpozo.database.AppsDbHelper;
import com.josedlpozo.fragments.AppsPermissionsFragment;
import com.josedlpozo.fragments.BatteryFragment;
import com.josedlpozo.fragments.MemoryFragment;
import com.josedlpozo.fragments.ProcessesFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by josedlpozo on 16/5/15.
 * <p/>
 * MainActivity con las vistas de pestañas de permisos, bateria, procesos y memoria
 * <p/>
 * Se encarga de crear la base de datos de aplicaciones la primera vez que instalas
 */

public class MainActivity extends ActionBarActivity {


    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    // Necesario para drawer
    private ListView mNavList;
    private ArrayList<DrawerItem> mNavItms;
    private TypedArray mNavIcons;
    NavigationAdapter mNavAdapter;
    private String[] titulos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        setTitle("OptimizApp");

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
                Fabric.with(new Fabric.Builder(MainActivity.this).kits(new Crashlytics()).debuggable(true).build());
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
                        return AppsPermissionsFragment.newInstance();
                    case 1:
                        return ProcessesFragment.newInstance();
                    case 2:
                        return MemoryFragment.newInstance();
                    case 3:
                        return BatteryFragment.newInstance();
                    default:
                        return AppsPermissionsFragment.newInstance();
                }
            }

            /*@Override
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

            }*/

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Apps";
                    case 1:
                        return "Procesos";
                    case 2:
                        return "Memoria";
                    case 3:
                        return "Batería";
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.MaterialViewPagerListener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(R.color.blue, "https://dl.dropbox.com/s/1p3vdntb2orog9d/apps.jpg?dl=0");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(R.color.red, "https://dl.dropbox.com/s/5hg69147404tt7i/processes.jpg?dl=0");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(R.color.yellow, "https://dl.dropbox.com/s/9tz50qecw8udr5d/memory.jpg?dl=0");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(R.color.green, "https://dl.dropbox.com/s/lcphfvky4nm0zg5/batt.jpg?dl=0");
                }

                //execute others actions if needed (ex : modify your header logo)


                return null;
            }
        });


        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        /*
        * Declaramos el controlador de la BBDD y accedemos en modo escritura
        *
        * para cargar todas las aplicaciones la primera vez que se instala
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

            // Salta la INTRO la primera vez que se ejecuta esta app
            Intent intent = new Intent(this, Intro.class);
            startActivity(intent);

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

    private void initDrawer() {//Lista
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
        mNavItms = new ArrayList<DrawerItem>();
        for (int i = 0; i < titulos.length; i++) {
            mNavItms.add(new DrawerItem(titulos[i], mNavIcons.getResourceId(i, -1)));
        }

        mNavAdapter = new NavigationAdapter(this, mNavItms);
        mNavList.setAdapter(mNavAdapter);

        mNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DrawerItem item = (DrawerItem) mNavList.getItemAtPosition(position);

                if (position == 0) {

                } else {
                    switch (item.getTitulo()) {
                        case "Apps":
                            mViewPager.getViewPager().setCurrentItem(0);
                            mDrawer.closeDrawers();
                            break;
                        case "Procesos":
                            mViewPager.getViewPager().setCurrentItem(1);
                            mDrawer.closeDrawers();
                            break;
                        case "Memoria":
                            mViewPager.getViewPager().setCurrentItem(2);
                            mDrawer.closeDrawers();
                            break;
                        case "Batería":
                            mViewPager.getViewPager().setCurrentItem(3);
                            mDrawer.closeDrawers();
                            break;
                        case "Invitar amigos":
                            shareApp();
                            mDrawer.closeDrawers();
                            break;
                        case "Acerca de":
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title(R.string.app_name)
                                    .content("Aplicación desarrollada para concurso aplicaciones Android Cátedra BQ.\n\nDesarrollado por josedlpozo.\n\nVersion: 1.0")
                                    .positiveText("OK")
                                    .positiveColorRes(R.color.red)
                                    .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                                    .show();
                            mDrawer.closeDrawers();
                            break;
                        default:
                            mViewPager.getViewPager().setCurrentItem(3);
                    }
                }

            }
        });
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "OptimizApp");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "#OptimizApp Nueva app, compartelo! \n\n " + "https://play.google.com/store/apps/details?id=com.josedlpozo.optimiza");
        startActivity(Intent.createChooser(sharingIntent, "Compartir vía:"));
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
