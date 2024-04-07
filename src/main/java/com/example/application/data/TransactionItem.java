package com.example.application.data;

public class TransactionItem {
    public int transactionNo;
    public String items;
//    public String dateTime;
    public String dayPart;
    public String dayType;

    public TransactionItem(int transactionNo, String items, String dateTime, String dayPart, String dayType) {
        this.transactionNo = transactionNo;
        this.items = items;
//        this.dateTime = dateTime;
        this.dayPart = dayPart;
        this.dayType = dayType;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionNo=" + transactionNo +
                ", items='" + items + '\'' +
//                ", dateTime='" + dateTime + '\'' +
                ", dayPart='" + dayPart + '\'' +
                ", dayType='" + dayType + '\'' +
                '}';
    }
}
