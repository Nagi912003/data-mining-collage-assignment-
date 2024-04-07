package com.example.application.data;

import com.example.application.data.TransactionItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {
    public Map<Integer, List<TransactionItem>> transactions = new HashMap<>();
    public Map<String, Integer> itemsCount = new HashMap<>();
    public List<TransactionItem> transactionsItems = new ArrayList<>();

    public void addItem(TransactionItem transaction){
        transactionsItems.add(transaction);

        if (transactions.containsKey(transaction.transactionNo)){
            transactions.get(transaction.transactionNo).add(transaction);
        } else {
            List<TransactionItem> transactionItems = new ArrayList<>();
            transactionItems.add(transaction);
            transactions.put(transaction.transactionNo, transactionItems);
        }

        if(itemsCount.containsKey(transaction.items)){
            itemsCount.put(transaction.items, itemsCount.get(transaction.items) + 1);
        } else {
            itemsCount.put(transaction.items, 1);
        }

//        System.out.println("Transaction: " + transaction.toString());
//        System.out.println("TransactionItems length: " + transactionsItems.size());
//        System.out.println("Transactions length: " + transactions.size());
    }

    public int getItemsWithCountGreaterThan(int count){
        int itemsCountGreaterThan = 0;
        for (Map.Entry<String, Integer> entry : itemsCount.entrySet()){
            if (entry.getValue() > count){
                itemsCountGreaterThan++;
            }
        }
        return itemsCountGreaterThan;
    }
}
