package com.easytransfer;

public class Voucher {
    private int id;
    private String name, email, type, date, time;
    private int adults, children;
    private String pickupLocation, dropoffLocation, notes;


    public Voucher(int id, String name, String email, String date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.date = date;
    }

    public Voucher(String name, String email, String transferType, String date, String time,
                   int adults, int children, String pickupLocation, String dropoffLocation, String notes) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.date = date;
        this.time = time;
        this.adults = adults;
        this.children = children;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
