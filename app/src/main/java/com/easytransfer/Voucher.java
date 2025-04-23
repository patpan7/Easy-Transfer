package com.easytransfer;

public class Voucher {
    private int id;
    private String name, date, email;

    public Voucher(int id, String name, String email, String date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getEmail() { return email; }
}
