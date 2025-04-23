package com.easytransfer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    EditText etCompanyName, etCompanyEmail, etPhone;
    Button btnSaveSettings;
    DatabaseHelper dbHelper;
    private static final int PICK_LOGO_REQUEST = 1;
    private Uri logoUri;
    Button btnChooseLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyEmail = findViewById(R.id.etCompanyEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        dbHelper = new DatabaseHelper(this);

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnChooseLogo = findViewById(R.id.btnChooseLogo);
        btnChooseLogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Επιλογή Λογότυπου"), PICK_LOGO_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOGO_REQUEST && resultCode == RESULT_OK && data != null) {
            logoUri = data.getData();
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
            etCompanyName.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_name")));
            etCompanyEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("company_email")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            cursor.close();
        }
    }

    private void saveSettings() {
        String name = etCompanyName.getText().toString().trim();
        String email = etCompanyEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        boolean saved = dbHelper.saveSettings(name, email, phone);

        if (saved) {
            Toast.makeText(this, "Οι ρυθμίσεις αποθηκεύτηκαν", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Αποτυχία αποθήκευσης", Toast.LENGTH_SHORT).show();
        }
    }
}
