package com.example.myapplication.model;

import java.sql.Timestamp;

public class Transaction extends BaseModel{
    private long amount;
    private String category;
    private Timestamp date = new Timestamp(System.currentTimeMillis());
    private String wallet;
    private String content;
    private boolean report_exclusion;
    private long day, month, year;
    public Transaction(long amount, String category, String wallet){
        setAmount(amount);
        setCategory(category);
        setWallet(wallet);
    }

    public long getDay() {
        return day;
    }

    public long getMonth() {
        return month;
    }

    public long getYear() {
        return year;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setReport_exclusion(boolean report_exclusion) {
        this.report_exclusion = report_exclusion;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public long getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getWallet() {
        return wallet;
    }

    public Timestamp getDate() {
        return date;
    }
    public String getContent(){return content;}
    public boolean getReport_exclusion(){return report_exclusion;}
}
