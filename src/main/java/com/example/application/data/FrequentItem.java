package com.example.application.data;

import java.util.Set;

public class FrequentItem {
    Set<String> itemset;
    Integer supportCount;

    public Set<String> getItemset() {
        return itemset;
    }

    public void setItemset(Set<String> itemset) {
        this.itemset = itemset;
    }

    public Integer getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(Integer supportCount) {
        this.supportCount = supportCount;
    }
}
