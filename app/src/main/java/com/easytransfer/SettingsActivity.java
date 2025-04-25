package com.easytransfer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    EditText etCompanyTitle, etCompanyName, etCompanyAfm, etCompanyAddress, etCompanyCity, etCompanyPostcode, etCompanyEmail, etCompanyMobile, etCompanyPhone;
    Button btnSaveSettings;
    DatabaseHelper dbHelper;
    private static final int PICK_LOGO_REQUEST = 1;
    private Uri logoUri;
    Button btnChooseLogo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etCompanyTitle = findViewById(R.id.etCompanyTitle);
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyAfm = findViewById(R.id.etCompanyAfm);
        etCompanyAddress = findViewById(R.id.etCompanyAddress);
        etCompanyCity = findViewById(R.id.etCompanyCity);
        etCompanyPostcode = findViewById(R.id.etCompanyPostcode);
        etCompanyEmail = findViewById(R.id.etCompanyEmail);
        etCompanyMobile = findViewById(R.id.etCompanyMobile);
        etCompanyPhone = findViewById(R.id.etCompanyPhone);

        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        dbHelper = new DatabaseHelper(this);

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnChooseLogo = findViewById(R.id.btnChooseLogo);
        btnChooseLogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_LOGO_REQUEST);
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOGO_REQUEST && resultCode == RESULT_OK && data != null) {
            logoUri = data.getData();

            // 🔐 Πάρε δικαίωμα πρόσβασης
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(logoUri, takeFlags);

            // 💾 Αποθήκευση URI
            getSharedPreferences("company_info", MODE_PRIVATE)
                    .edit()
                    .putString("logo_uri", logoUri.toString())
                    .apply();

            Toast.makeText(this, "Αποθηκεύτηκε το λογότυπο", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSettings() {
        Cursor cursor = dbHelper.getSettings();
        if (cursor != null && cursor.moveToFirst()) {
            etCompanyTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_title")));
            etCompanyName.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_name")));
            etCompanyAfm.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_afm")));
            etCompanyAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_address")));
            etCompanyCity.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_city")));
            etCompanyPostcode.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_postcode")));
            etCompanyEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_email")));
            etCompanyMobile.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_mobile")));
            etCompanyPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_phone")));
            cursor.close();
        }
    }

    private void saveSettings() {
        String title = etCompanyTitle.getText().toString().trim();
        String name = etCompanyName.getText().toString().trim();
        String afm = etCompanyAfm.getText().toString().trim();
        String address = etCompanyAddress.getText().toString().trim();
        String city = etCompanyCity.getText().toString().trim();
        String postalcode = etCompanyPostcode.getText().toString().trim();
        String email = etCompanyEmail.getText().toString().trim();
        String mobile = etCompanyMobile.getText().toString().trim();
        String phone = etCompanyPhone.getText().toString().trim();


        boolean saved = dbHelper.saveSettings(title, name, afm, address, city, postalcode, email, mobile, phone);

        if (saved) {
            Toast.makeText(this, "Οι ρυθμίσεις αποθηκεύτηκαν", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Αποτυχία αποθήκευσης", Toast.LENGTH_SHORT).show();
        }
    }
}
