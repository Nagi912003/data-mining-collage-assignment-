package com.example.application.services;

import com.example.application.data.AssociationRule;
import com.example.application.data.TransactionItem;
import com.example.application.data.Transactions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AprioriAlgorithm {
    private final Transactions transactions = new Transactions();

    public void printTransactionsInfo() {
        System.out.println("TransactionItems length: " + transactions.transactionsItems.size());
        System.out.println("Transactions length: " + transactions.transactions.size());
//        System.out.println("Transactions: " + transactions.transactions);
//        System.out.println("item counts: " + transactions.itemsCount);
        System.out.println("item counts length: " + transactions.itemsCount.size());
        int supportCount = (int)(transactions.transactions.size() * 0.005);
        System.out.println("Items With Count Greater Than "+supportCount+": " + transactions.getItemsWithCountGreaterThan(supportCount));
    }

    public void readTransactionsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int transactionNo = Integer.parseInt(parts[0].trim());
                String items = parts[1].trim();
                String dateTime = parts[2].trim();
                String dayPart = parts[3].trim();
                String dayType = parts[4].trim();

                // Create a transaction object or process the data as needed
                TransactionItem transactionItem = new TransactionItem(transactionNo, items, dateTime, dayPart, dayType);
                transactions.addItem(transactionItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Set<String>, Integer> apriori(double minSupport) {
//    public List<Set<String>> apriori(double minSupport) {

        // Generate frequent 1-itemsets
        Map<Set<String>, Integer> candidateItemsets = generateCandidateItemsetsOfSize1();
        Map<Set<String>, Integer> frequent1Itemsets = filterCandidateItemsets(candidateItemsets, minSupport);

        Map<Set<String>, Integer> frequentItemsets = new HashMap<>(frequent1Itemsets);
//        List<Set<String>> frequentItemsets = new ArrayList<>(frequent1Itemsets.keySet());

        int k = 2;
        while (!frequent1Itemsets.isEmpty()) {
            Map<Set<String>, Integer> candidateKItemsets = generateCandidateItemsetsOfSizeK(frequent1Itemsets.keySet(), k);
            Map<Set<String>, Integer> frequentKItemsets = filterCandidateItemsets(candidateKItemsets, minSupport);

            frequentItemsets.putAll(frequentKItemsets);
//            frequentItemsets.addAll(frequentKItemsets.keySet());

            frequent1Itemsets = frequentKItemsets;
            k++;
        }

        return frequentItemsets;
    }

    private Map<Set<String>, Integer> generateCandidateItemsetsOfSize1() {
        Map<Set<String>, Integer> candidateItemsets = new HashMap<>();

        for (String item : transactions.itemsCount.keySet()) {
            Set<String> itemset = new HashSet<>();
            itemset.add(item);
            candidateItemsets.put(itemset, transactions.itemsCount.get(item));
        }

        return candidateItemsets;
    }

    private Map<Set<String>, Integer> generateCandidateItemsetsOfSizeK(Set<Set<String>> frequentItemsetsOfSizeKMinus1, int k) {
        Map<Set<String>, Integer> candidateItemsets = new HashMap<>();

        List<Set<String>> frequentItemsetsList = new ArrayList<>(frequentItemsetsOfSizeKMinus1);
        int n = frequentItemsetsList.size();

        for (int i = 0; i < n; i++) {
            Set<String> itemset1 = frequentItemsetsList.get(i);
            for (int j = i + 1; j < n; j++) {
                Set<String> itemset2 = frequentItemsetsList.get(j);
                Set<String> newCandidateItemset = new HashSet<>(itemset1);
                newCandidateItemset.addAll(itemset2);

                if (newCandidateItemset.size() == k) {
                    candidateItemsets.put(newCandidateItemset, 0);
                }
            }
        }

        return candidateItemsets;
    }

    private Map<Set<String>, Integer> filterCandidateItemsets(Map<Set<String>, Integer> candidateItemsets, double minSupport) {
        Map<Set<String>, Integer> frequentItemsets = new HashMap<>();

        for (Map.Entry<Set<String>, Integer> entry : candidateItemsets.entrySet()) {
            Set<String> itemset = entry.getKey();
            int count = 0;
            for (List<TransactionItem> transactionList : transactions.transactions.values()) {
                boolean containsAllItems = true;
                for (String item : itemset) {
                    boolean containsItem = false;
                    for (TransactionItem transaction : transactionList) {
                        if (transaction.items.contains(item)) {
                            containsItem = true;
                            break;
                        }
                    }
                    if (!containsItem) {
                        containsAllItems = false;
                        break;
                    }
                }
                if (containsAllItems) {
                    count++;
                }
            }
            double support = (double) count / transactions.transactions.size();
            if (support >= minSupport) {
                frequentItemsets.put(itemset, count);
            }
        }

        return frequentItemsets;
    }

    public List<AssociationRule> generateAssociationRules(Map<Set<String>, Integer> frequentItemsets, double minConfidence) {
        List<AssociationRule> associationRules = new ArrayList<>();
        for (Set<String> itemset : frequentItemsets.keySet()) {
            if (itemset.size() > 1) {
                Set<Set<String>> subsets = generateSubsets(itemset);
                for (Set<String> subset : subsets) {
                    Set<String> remaining = new HashSet<>(itemset);
                    remaining.removeAll(subset);
                    // -----------------------------------------
                    Integer itemCountItemset = frequentItemsets.get(itemset);
                    Integer itemCountSubset = frequentItemsets.get(subset);
                    if (itemCountItemset != null && itemCountSubset != null && itemCountSubset != 0) {
                        double confidence = (double) itemCountItemset / itemCountSubset;
                        if (confidence >= minConfidence) {
                            // cut the confidence to 3 decimal places
                            confidence = Math.round(confidence * 100000.0) / 1000.0;
                            AssociationRule associationRule = new AssociationRule();
                            associationRule.setSubset(subset);
                            associationRule.setRemaining(remaining);
                            associationRule.setConfidence(confidence);
                            associationRules.add(associationRule);
//                            System.out.println(subset + " => " + remaining + ", Confidence: " + confidence + "%");
                        }
                    }
                    // -----------------------------------------
                }
            }
        }
        return associationRules;
    }


    private Set<Set<String>> generateSubsets(Set<String> itemset) {
        Set<Set<String>> subsets = new HashSet<>();
        List<String> items = new ArrayList<>(itemset);
        int n = items.size();
        for (int i = 1; i < (1 << n) - 1; i++) {
            Set<String> subset = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subset.add(items.get(j));
                }
            }
            subsets.add(subset);
        }
        return subsets;
    }


    public static void main(String[] args) {
        AprioriAlgorithm apriori = new AprioriAlgorithm();
        apriori.readTransactionsFromFile("E:\\FCAI\\4th Grade\\2nd\\BigData\\Apiriori_algorithm\\src\\Bakery.csv");
        apriori.printTransactionsInfo();

        Map<Set<String>, Integer> frequentItemsets = apriori.apriori(0.05); // Minimum support = 0.005
        // Display frequent itemsets
        System.out.println();
        System.out.println("Frequent itemsets: ");
        for (Set<String> itemset : frequentItemsets.keySet())
        {
            if(itemset.size() > 1)
                System.out.println( itemset + ": " + frequentItemsets.get(itemset));
        }
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter minimum support (0 to 1): ");
//        double minSupport = scanner.nextDouble();
//
//        System.out.print("Enter minimum confidence (0 to 1): ");
//        double minConfidence = scanner.nextDouble();
//        List<Set<String>> frequentItemsets = apriori.apriori(minSupport);

        System.out.println();
        System.out.println("Association rules: ");
        apriori.generateAssociationRules(frequentItemsets, 0.1);

//        System.out.println("generate subsets out of [Tea, Cake, Coffee]: " + apriori.generateSubsets(Set.of("Tea", "Cake", "Coffee")));
        System.out.println("Done");
    }
}
