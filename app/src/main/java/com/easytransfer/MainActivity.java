package com.easytransfer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etName, etEmail, etAdults, etChildren;
    RadioGroup rgTransferType;
    DatePicker datePicker;
    TimePicker timePicker;
    Button btnSave, btnSendPdf;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAdults = findViewById(R.id.etAdults);
        etChildren = findViewById(R.id.etChildren);
        rgTransferType = findViewById(R.id.rgTransferType);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        btnSave = findViewById(R.id.btnSave);
        btnSendPdf = findViewById(R.id.btnSendPdf);

        btnSave.setOnClickListener(v -> saveVoucher());
        btnSendPdf.setOnClickListener(v -> sendVoucherAsPdf());
    }

    private void saveVoucher() {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String adultsStr = etAdults.getText().toString().trim();
            String childrenStr = etChildren.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || adultsStr.isEmpty() || childrenStr.isEmpty()) {
                Toast.makeText(this, "Συμπλήρωσε όλα τα πεδία", Toast.LENGTH_SHORT).show();
                return;
            }

            int adults = Integer.parseInt(adultsStr);
            int children = Integer.parseInt(childrenStr);

            int checkedId = rgTransferType.getCheckedRadioButtonId();
            String transferType = (checkedId == R.id.rbOneWay) ? "One Way" : "Return";

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String date = day + "/" + month + "/" + year;

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = hour + ":" + (minute < 10 ? "0" + minute : minute);

            boolean inserted = dbHelper.insertVoucher(name, email, adults, children, transferType, date, time);

            if (inserted) {
                Toast.makeText(this, "Το voucher αποθηκεύτηκε!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(this, "Αποτυχία αποθήκευσης", Toast.LENGTH_SHORT).show();
            }
        }

    private void clearForm() {
        etName.setText("");
        etEmail.setText("");
        etAdults.setText("");
        etChildren.setText("");
        rgTransferType.check(R.id.rbOneWay);
    }


    private void sendVoucherAsPdf() {
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
        } else {
            Toast.makeText(this, "Αποτυχία δημιουργίας PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailWithAttachment(String toEmail, File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Το Voucher σας");
        intent.putExtra(Intent.EXTRA_TEXT, "Αγαπητέ πελάτη, επισυνάπτουμε το voucher μεταφοράς σας.");
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Αποστολή με..."));
    }


}