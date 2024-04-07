package com.example.application.data;

import java.util.Set;

public class AssociationRule {
    //subset + " => " + remaining + ", Confidence: " + confidence + "%");
    Set<String> subset;
    Set<String> remaining;
    double confidence;

    public Set<String> getSubset() {
        return subset;
    }

    public void setSubset(Set<String> subset) {
        this.subset = subset;
    }

    public Set<String> getRemaining() {
        return remaining;
    }

    public void setRemaining(Set<String> remaining) {
        this.remaining = remaining;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
