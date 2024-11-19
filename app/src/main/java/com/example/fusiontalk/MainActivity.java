package com.example.fusiontalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageView logo;
    TextView t1, t2, t3;
    Animation topAni, bottomAni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);
        t1 = findViewById(R.id.textWord);
        t2 = findViewById(R.id.fromOwner);
        t3 = findViewById(R.id.owner);

        topAni = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAni = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo.setAnimation(topAni);
        t1.setAnimation(bottomAni);
        t2.setAnimation(bottomAni);
        t3.setAnimation(bottomAni);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}