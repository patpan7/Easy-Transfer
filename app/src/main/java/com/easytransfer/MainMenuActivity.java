package com.easytransfer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    Button btnCreateVoucher, btnViewVouchers, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnCreateVoucher = findViewById(R.id.btnCreateVoucher);
        btnViewVouchers = findViewById(R.id.btnViewVouchers);
        btnSettings = findViewById(R.id.btnSettings);

        btnCreateVoucher.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        btnViewVouchers.setOnClickListener(v ->
                startActivity(new Intent(this, VoucherListActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }
}
