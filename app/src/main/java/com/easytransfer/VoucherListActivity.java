package com.easytransfer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class VoucherListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Voucher> voucherList;
    DatabaseHelper dbHelper;
    VoucherAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);

        listView = findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);
        voucherList = new ArrayList<>();
        searchView = findViewById(R.id.searchView);

        loadVouchers();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Voucher selected = voucherList.get(position);
            Intent intent = new Intent(VoucherListActivity.this, EditVoucherActivity.class);
            intent.putExtra("voucher_id", selected.getId());
            startActivity(intent);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void loadVouchers() {
        Cursor cursor = dbHelper.getAllVouchers();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                voucherList.add(new Voucher(id, name, email, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new VoucherAdapter(this, voucherList);
        listView.setAdapter(adapter);
    }
}
