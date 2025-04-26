package com.easytransfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class MainMenuActivity extends AppCompatActivity {

    MaterialCardView cardCreateVoucher , cardViewVouchers , cardSettings ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        cardCreateVoucher  = findViewById(R.id.cardCreateVoucher);
        cardViewVouchers = findViewById(R.id.cardViewVouchers);
        cardSettings = findViewById(R.id.cardSettings);

        setCardTouchAnimation(cardCreateVoucher);
        setCardTouchAnimation(cardViewVouchers);
        setCardTouchAnimation(cardSettings);

        cardCreateVoucher.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        cardViewVouchers.setOnClickListener(v ->
                startActivity(new Intent(this, VoucherListActivity.class)));

        cardSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void setCardTouchAnimation(MaterialCardView cardView) {
        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.card_scale_down);
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.card_scale_up);

        cardView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                cardView.startAnimation(scaleDown);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                cardView.startAnimation(scaleUp);
            }
            return false;
        });
    }
}
