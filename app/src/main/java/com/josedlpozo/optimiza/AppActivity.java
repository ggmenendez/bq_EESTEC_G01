package com.josedlpozo.optimiza;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.josedlpozo.fragments.RecyclerViewFragmentPermisos;

/**
 * Created by josedlpozo on 17/5/15.
 *
 * Activity para vista detallada de aplicación.
 *
 * Utiliza el fragment permisos
 *
 */
public class AppActivity extends ActionBarActivity {

    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    private ImageView image;


    private String nombre;

    // Detector para swipe
    private GestureDetector gestureDetector;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_permisos);


        gestureDetector = new GestureDetector(this, gestureListener);


        // Recogemos el intent para poner nombre a la pestaña y el icono del tema el icono de la aplicacion
        Intent intent = getIntent();


        final Bundle bundle = intent.getExtras();

        nombre = bundle.getString("NOMBRE");

        image = (ImageView) findViewById(R.id.app_img);
        try {
            image.setImageDrawable(getPackageManager().getApplicationIcon(bundle.getString("PAQUETE")));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setTitle("");

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager2);

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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.setDrawerListener(mDrawerToggle);

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            int oldPosition = -1;

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    default:
                        RecyclerViewFragmentPermisos r = new RecyclerViewFragmentPermisos();
                        r.setArguments(bundle);
                        return r;
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
                }

                final int fadeDuration = 400;
                mViewPager.setImageUrl(imageUrl, fadeDuration);
                mViewPager.setColor(color, fadeDuration);

            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return nombre;
                }
                return "";
            }
        });
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Animaciones de entrada y salida de activity
        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT > 21) {
            Explode explode = new Explode();
            explode.setDuration(1000);
            getWindow().setEnterTransition(explode);

            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setReturnTransition(fade);
        }
    }

    // Detector de swipe para hacer back
    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final int SWIPE_MIN_DISTANCE = 120;
            final int SWIPE_MAX_OFF_PATH = 250;
            final int SWIPE_THRESHOLD_VELOCITY = 200;
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("Tag", "Right to Left");
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("Tag", "Left to Right");
                    onBackPressed();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}

