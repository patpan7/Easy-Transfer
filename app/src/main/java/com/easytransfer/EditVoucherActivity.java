package com.easytransfer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class EditVoucherActivity extends AppCompatActivity {

    LinearLayout mainLayout;
    EditText etName, etEmail, etAdults, etChildren;
    RadioGroup rgTransferType;
    RadioButton rbOneWay, rbReturn;
    DatePicker datePicker;
    TimePicker timePicker;
    AutoCompleteTextView etPickup;
    AutoCompleteTextView etDropoff;
    EditText etNotes;
    String createdAt;
    Button btnUpdate, btnResendPdf, btnDelete;

    DatabaseHelper dbHelper;
    int voucherId;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_voucher);

        mainLayout = findViewById(R.id.linearLayoutMain);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mainLayout.startAnimation(fadeIn);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAdults = findViewById(R.id.etAdults);
        etChildren = findViewById(R.id.etChildren);
        rgTransferType = findViewById(R.id.rgTransferType);
        rbOneWay = findViewById(R.id.rbOneWay);
        rbReturn = findViewById(R.id.rbReturn);
        etPickup = findViewById(R.id.etPickup);
        etDropoff = findViewById(R.id.etDropoff);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        etNotes = findViewById(R.id.etNotes);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnResendPdf = findViewById(R.id.btnResendPdf);
        btnDelete = findViewById(R.id.btnDelete);

        String[] locations = {"Airport", "Port", "City Center", "Hotel"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);

        etPickup.setAdapter(adapter);
        etPickup.setLongClickable(false);
        etPickup.setTextIsSelectable(false);
        etDropoff.setAdapter(adapter);
        etDropoff.setLongClickable(false);
        etDropoff.setTextIsSelectable(false);
        etPickup.setOnClickListener(v -> etPickup.showDropDown());
        etDropoff.setOnClickListener(v -> etDropoff.showDropDown());

        // Προαιρετικό: Ενεργοποίηση άμεσης εμφάνισης dropdown
        etPickup.setThreshold(1);
        etDropoff.setThreshold(1);

        timePicker.setIs24HourView(true);
        dbHelper = new DatabaseHelper(this);

        voucherId = getIntent().getIntExtra("voucher_id", -1);
        if (voucherId != -1) loadVoucherData(voucherId);

        btnUpdate.setOnClickListener(v -> updateVoucher());
        btnResendPdf.setOnClickListener(v -> resendVoucher());
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Επιβεβαίωση")
                    .setMessage("Θέλεις σίγουρα να διαγράψεις αυτό το voucher;")
                    .setPositiveButton("Ναι", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteVoucher(voucherId);
                        if (deleted) {
                            Toast.makeText(this, "Διαγράφηκε!", Toast.LENGTH_SHORT).show();
                            finish(); // Επιστροφή στη λίστα
                        } else {
                            Toast.makeText(this, "Αποτυχία διαγραφής", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Άκυρο", null)
                    .show();
        });
    }

    private void loadVoucherData(int id) {
        Cursor cursor = dbHelper.getVoucherById(id);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            etAdults.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("adults"))));
            etChildren.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("children"))));

            String transferType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            if ("One Way".equalsIgnoreCase(transferType)) {
                rgTransferType.check(R.id.rbOneWay);
            } else {
                rgTransferType.check(R.id.rbReturn);
            }

            etPickup.setText(cursor.getString(cursor.getColumnIndexOrThrow("pickup")));
            etDropoff.setText(cursor.getString(cursor.getColumnIndexOrThrow("dropoff")));

            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String[] parts = date.split("/");
            datePicker.updateDate(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));

            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String[] t = time.split(":");
            timePicker.setHour(Integer.parseInt(t[0]));
            timePicker.setMinute(Integer.parseInt(t[1]));

            etNotes.setText(cursor.getString(cursor.getColumnIndexOrThrow("note")));
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
            cursor.close();
        }
    }

    private void updateVoucher() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int adults = Integer.parseInt(etAdults.getText().toString().trim());
        int children = Integer.parseInt(etChildren.getText().toString().trim());
        String transferType = (rgTransferType.getCheckedRadioButtonId() == R.id.rbOneWay) ? "One Way" : "Return";
        String pickupLocation = etPickup.getText().toString().trim();
        String dropoffLocation = etDropoff.getText().toString().trim();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String date = day + "/" + month + "/" + year;

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String time = hour + ":" + (minute < 10 ? "0" + minute : minute);

        String notes = etNotes.getText().toString().trim();

        boolean updated = dbHelper.updateVoucher(voucherId, name, email, adults, children, transferType,pickupLocation,dropoffLocation, date, time, notes);

        if (updated) {
            Toast.makeText(this, "Voucher ενημερώθηκε!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Σφάλμα ενημέρωσης", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    private void resendVoucher() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int adults = Integer.parseInt(etAdults.getText().toString().trim());
        int children = Integer.parseInt(etChildren.getText().toString().trim());
        String type = (rgTransferType.getCheckedRadioButtonId() == R.id.rbOneWay) ? "One Way" : "Return";
        String pickupLocation = etPickup.getText().toString().trim();
        String dropoffLocation = etDropoff.getText().toString().trim();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String date = day + "/" + month + "/" + year;

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String time = hour + ":" + (minute < 10 ? "0" + minute : minute);
        String notes = etNotes.getText().toString().trim();
        Log.e("Type", type);
        Voucher voucher = new Voucher(name, email, type, date, time, adults, children, pickupLocation, dropoffLocation, notes);
        voucher.setCreatedAt(createdAt);
        Cursor settingsCursor = dbHelper.getSettings();
        File pdf = PdfGenerator.generateVoucherPdf(this, voucher, settingsCursor);
        if (pdf != null) {
            sendEmailWithAttachment(email, pdf);
        }
    }

    private void sendEmailWithAttachment(String toEmail, File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Transfer Voucher");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear customer, we are attaching your transfer voucher..");
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Αποστολή με..."));
    }
}
