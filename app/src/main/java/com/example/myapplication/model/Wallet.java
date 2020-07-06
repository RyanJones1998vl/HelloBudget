package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;

public class Wallet extends BaseModel {
    private String name;
    private String currency;
    private int initial_balance=0;
    private boolean total_exclusion=false;
    private List<Transaction> transactionList=new ArrayList<>();
    private String icon;
    private boolean deleted = false;


    public Wallet(){
        super();
        deleted=false;
    }
    public void setTotal_exclusion(boolean total_exclusion) {
        this.total_exclusion = total_exclusion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInitial_balance(int initial_balance) {
        this.initial_balance = initial_balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTotalExclusion(boolean exclusion){this.total_exclusion=exclusion;}

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public int getInitial_balance() {
        return initial_balance;
    }
    public boolean getTotalExclusion(){return this.total_exclusion;}

    public void addTrannsaction(Transaction transaction){
        transactionList.add(transaction);
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }
    public String getIcon(){return icon;}
    public void setIcon(String ic){this.icon = ic;}
    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
