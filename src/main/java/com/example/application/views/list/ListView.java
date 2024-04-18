package com.example.application.views.list;

import com.example.application.data.AssociationRule;
import com.example.application.data.FrequentItem;
import com.example.application.services.AprioriAlgorithm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PageTitle("Apiriori algorithm")
@Route(value = "")
public class ListView extends VerticalLayout {
    Grid<FrequentItem> frequentItemsGrid = new Grid<>(FrequentItem.class);
    Grid<AssociationRule> associationRulesGrid = new Grid<>(AssociationRule.class);
    AprioriAlgorithm apriori = new AprioriAlgorithm();
    public ListView() {
        addClassName("list-view");
        setSizeFull();

//        initConfigurations();
        configureGrid();

        HorizontalLayout inputLayout = new HorizontalLayout();
        NumberField minSupportInput = new NumberField("Min Support Count");
        NumberField minConfidenceInput = new NumberField("Min Confidence");
        NumberField InputFilePercentage = new NumberField("Percentage");
//        Div Suffix = new Div();
//        Suffix.setText("%");
        Div Suffix2 = new Div();
        Suffix2.setText("%");
        Div Suffix3 = new Div();
        Suffix3.setText("%");

//        minSupportInput.setSuffixComponent(Suffix);
        minSupportInput.setMin(0);
//        minSupportInput.setMax(100);
        minSupportInput.setValue(100.0);

        minConfidenceInput.setSuffixComponent(Suffix2);
        minConfidenceInput.setMin(0);
        minConfidenceInput.setMax(100);
        minConfidenceInput.setValue(10.0);

        InputFilePercentage.setSuffixComponent(Suffix3);
        InputFilePercentage.setMin(0);
        InputFilePercentage.setMax(100);
        InputFilePercentage.setValue(70.0);

        Button generateButton = new Button("Generate", event -> {
            if(InputFilePercentage.isEmpty()){
                InputFilePercentage.setValue(100.0);
            }
            if (InputFilePercentage.getValue() <= 0 || InputFilePercentage.getValue() > 100) {
                Notification.show("Please enter a valid Input File Percentage value").setThemeName("error");
                return;
            }
            if (minSupportInput.isEmpty() || minConfidenceInput.isEmpty()) {
                Notification.show("Please enter the minimum support and confidence values").setThemeName("error");
                return;
            }
            if (minSupportInput.getValue() < 0) {
                Notification.show("Please enter a valid minimum support value").setThemeName("error");
                return;
            }
            if (minConfidenceInput.getValue() < 0 || minConfidenceInput.getValue() > 100) {
                Notification.show("Please enter a valid minimum confidence value").setThemeName("error");
                return;
            }
            List<FrequentItem> frequentItems = new ArrayList<>();
            List<AssociationRule> associationRules = new ArrayList<>();
            apriori.readTransactionsFromFile("E:\\FCAI\\4th Grade\\2nd\\BigData\\Apiriori_algorithm\\src\\Bakery.csv", InputFilePercentage.getValue());
            apriori.printTransactionsInfo();
            Map<Set<String>, Integer> frequentItemsets = apriori.apriori(minSupportInput.getValue());
            frequentItemsets.forEach((key, value) -> {
                if(key.size() == 1) return;
                FrequentItem frequentItem = new FrequentItem();
                frequentItem.setItemset(key);
                frequentItem.setSupportCount(value);
                frequentItems.add(frequentItem);
            });
            // sort the frequent items by support count then the length of the itemset in descending order
            frequentItems.sort((f1, f2) -> f2.getSupportCount() - f1.getSupportCount());
            frequentItems.sort((f1, f2) -> f2.getItemset().size() - f1.getItemset().size());

            frequentItemsGrid.setItems(frequentItems);
            associationRules = apriori.generateAssociationRules(frequentItemsets, minConfidenceInput.getValue()/100);
            // sort the association rules by confidence in descending order
            associationRules.sort((a1, a2) -> Double.compare(a2.getConfidence(), a1.getConfidence()));
            associationRules.sort((a1, a2) -> a2.getSubset().size() - a1.getSubset().size());
            associationRules.sort((a1, a2) -> a1.getRemaining().size() - a2.getRemaining().size());
            associationRulesGrid.setItems(associationRules);

        });
        minSupportInput.getStyle().set("margin-left", "1rem");

        inputLayout.add(
                InputFilePercentage,
                minSupportInput,
                minConfidenceInput,
                generateButton
        );
        // Apply layout alignment
        inputLayout.setSpacing(true);
        inputLayout.setAlignItems(Alignment.BASELINE);

        add(inputLayout);

        Div gridLayout = new Div();
        gridLayout.add(frequentItemsGrid, associationRulesGrid);
        setGridStyles(frequentItemsGrid);
        setGridStyles(associationRulesGrid);
        setContainerStyles(gridLayout);
        add(gridLayout);
    }
    private static void setGridStyles(Grid grid) {
        grid.setHeightFull();
        grid.getStyle()
//                .set("width", "300px")
//                .set("height", "550px")
                .set("margin-left", "1rem")
                .set("margin-right", "1rem")
                .set("align-self", "unset");
    }

    private static void setContainerStyles(Div container) {
        container.setWidthFull();
        container.setHeightFull();
        container.getStyle().set("display", "flex").set("flex-direction", "row")
                .set("flex-wrap", "wrap");
    }
//    private void initConfigurations() {
////        apriori.readTransactionsFromFile("E:\\FCAI\\4th Grade\\2nd\\BigData\\Apiriori_algorithm\\src\\Bakery.csv");
////        apriori.printTransactionsInfo();
//    }
    private void configureGrid() {
        frequentItemsGrid.addClassName("frequent-items-grid");
        frequentItemsGrid.setSizeUndefined();
        frequentItemsGrid.setColumns("itemset", "supportCount");
        frequentItemsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
//        frequentItemsGrid.sort(GridSortOrder.desc(frequentItemsGrid.getColumnByKey("supportCount")).build());

        associationRulesGrid.addClassName("association-rules-grid");
        associationRulesGrid.setSizeUndefined();
        associationRulesGrid.setColumns("subset", "remaining", "confidence");
        associationRulesGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        // the grid is sorted by confidence in descending order
//        associationRulesGrid.sort(GridSortOrder.desc(associationRulesGrid.getColumnByKey("confidence")).build());
        frequentItemsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        associationRulesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

}
