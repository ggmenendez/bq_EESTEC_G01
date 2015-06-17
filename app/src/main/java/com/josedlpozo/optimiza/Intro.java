package com.josedlpozo.optimiza;

import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.josedlpozo.sliders.Slide1;
import com.josedlpozo.sliders.Slide2;
import com.josedlpozo.sliders.Slide3;
import com.josedlpozo.sliders.Slide5;


public class Intro extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(new Slide1(), getApplicationContext());
        addSlide(new Slide2(), getApplicationContext());
        addSlide(new Slide3(), getApplicationContext());
        addSlide(new Slide5(), getApplicationContext());


        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        finish();
    }

    private void loadMainActivity() {
        finish();
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        loadMainActivity();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
