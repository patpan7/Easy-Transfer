package com.easytransfer;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class EditVoucherActivity extends AppCompatActivity {

    EditText etName, etEmail, etAdults, etChildren;
    RadioGroup rgTransferType;
    RadioButton rbOneWay, rbReturn;
    DatePicker datePicker;
    TimePicker timePicker;
    Button btnUpdate, btnResendPdf, btnDelete;

    DatabaseHelper dbHelper;
    int voucherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_voucher);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAdults = findViewById(R.id.etAdults);
        etChildren = findViewById(R.id.etChildren);
        rgTransferType = findViewById(R.id.rgTransferType);
        rbOneWay = findViewById(R.id.rbOneWay);
        rbReturn = findViewById(R.id.rbReturn);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnResendPdf = findViewById(R.id.btnResendPdf);
        btnDelete = findViewById(R.id.btnDelete);

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

            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String[] parts = date.split("/");
            datePicker.updateDate(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));

            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String[] t = time.split(":");
            timePicker.setHour(Integer.parseInt(t[0]));
            timePicker.setMinute(Integer.parseInt(t[1]));

            cursor.close();
        }
    }

    private void updateVoucher() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int adults = Integer.parseInt(etAdults.getText().toString().trim());
        int children = Integer.parseInt(etChildren.getText().toString().trim());
        String transferType = (rgTransferType.getCheckedRadioButtonId() == R.id.rbOneWay) ? "One Way" : "Return";

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String date = day + "/" + month + "/" + year;

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String time = hour + ":" + (minute < 10 ? "0" + minute : minute);

        boolean updated = dbHelper.updateVoucher(voucherId, name, email, adults, children, transferType, date, time);

        if (updated) {
            Toast.makeText(this, "Voucher ενημερώθηκε!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Σφάλμα ενημέρωσης", Toast.LENGTH_SHORT).show();
        }
    }

    private void resendVoucher() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int adults = Integer.parseInt(etAdults.getText().toString().trim());
        int children = Integer.parseInt(etChildren.getText().toString().trim());
        String type = (rgTransferType.getCheckedRadioButtonId() == R.id.rbOneWay) ? "One Way" : "Return";

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String date = day + "/" + month + "/" + year;

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String time = hour + ":" + (minute < 10 ? "0" + minute : minute);

        File pdf = PdfGenerator.generateVoucherPDF(this, name, email, adults, children, type, date, time, dbHelper);
        if (pdf != null) {
            sendEmailWithAttachment(email, pdf);
        }
    }

    private void sendEmailWithAttachment(String toEmail, File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Voucher Ενημέρωση");
        intent.putExtra(Intent.EXTRA_TEXT, "Επισυνάπτουμε το ενημερωμένο voucher σας.");
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Αποστολή με..."));
    }
}
