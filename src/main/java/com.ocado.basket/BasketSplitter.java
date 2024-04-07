package com.ocado.basket;
import java.util.*; // For collections
import com.fasterxml.jackson.core.type.TypeReference; // For reading json file
import com.fasterxml.jackson.databind.ObjectMapper; // For reading json file
import java.io.File; // For reading file
import java.io.IOException; // For catching errors

public class BasketSplitter {

    public Map<String, List<Byte>> allProductsDictionary = new HashMap<>();
    public Map<Byte, String> dynamicEnumOfDelivery = new HashMap<>(10);

    public BasketSplitter(String absolutePathToConfigFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File configFile = new File(absolutePathToConfigFile);
            Map<String, List<String>> productsMap = objectMapper.readValue(configFile, new TypeReference<Map<String, List<String>>>(){});

            // create dynamicEnumOfDelivery
            for (List<String> deliveryOptions : productsMap.values()) {
                for (String option : deliveryOptions) {
                    if (!dynamicEnumOfDelivery.containsValue(option)) {
                        dynamicEnumOfDelivery.put((byte)dynamicEnumOfDelivery.size(), option);
                    }
                }
            }
            // create allProductsDictionary
            for (Map.Entry<String, List<String>> entry : productsMap.entrySet()) {
                String productKey = entry.getKey();
                List<String> deliveryOptions = entry.getValue();
                List<Byte> deliveryByteValues = new ArrayList<>();

                for (String option : deliveryOptions) {
                    for (Map.Entry<Byte, String> enumEntry : dynamicEnumOfDelivery.entrySet()) {
                        if (enumEntry.getValue().equals(option)) {
                            deliveryByteValues.add(enumEntry.getKey());
                            break;
                        }
                    }
                }

                allProductsDictionary.put(productKey, deliveryByteValues);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> split(List<String> items) {
        // Creating dictionary with customer basket and table with necessary delivery type
        Map<String, List<Byte>> copiedDictionary = new HashMap<>();
        Byte[] uniqueDeliveries = {};
        for (String item : items) {
            if (allProductsDictionary.containsKey(item)) {
                List<Byte> values = allProductsDictionary.get(item);
                copiedDictionary.put(item, new ArrayList<>(values));
                if (values.size() == 1) {
                    Byte firstValue = values.get(0);
                    if (!containsValue(uniqueDeliveries, firstValue)) {
                        uniqueDeliveries = addValue(uniqueDeliveries, firstValue);
                    }
                }
            }
        }
        // Calculate which delivery types we need
        List<Byte> deliveriesType = minimalByteList(copiedDictionary,uniqueDeliveries);

        // Group deliveries (with replicating values)
        Map<Byte,List<String>> groupedDeliveries = mapProductsToDeliveries(deliveriesType,copiedDictionary);

        // Delete duplicates
        groupedDeliveries = removeDuplicates(groupedDeliveries);

        // Translate to returning value
        Map<String, List<String>> returner = new HashMap<>();
        for (Map.Entry<Byte, String> entry : dynamicEnumOfDelivery.entrySet()) {
            Byte keyByte = entry.getKey();
            String deliveryType = entry.getValue();
            List<String> products = groupedDeliveries.getOrDefault(keyByte, new ArrayList<>());
            if (!products.isEmpty()) {
                returner.put(deliveryType, products);
            }
        }
        return returner;
    }

    // Is value in array?
    private static boolean containsValue(Byte[] array, Byte value) {
        for (Byte element : array) {
            if (element != null && element.equals(value)) {
                return true;
            }
        }
        return false;
    }

    // Add value to the array
    private static Byte[] addValue(Byte[] array, Byte value) {
        Byte[] newArray = new Byte[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = value;
        return newArray;
    }

    // Check table of answers can be enough for this basket
    private boolean checkAnswers(Map<String, List<Byte>> dictionary, Byte[] answers) {
        for (List<Byte> values : dictionary.values()) {
            boolean containsAnswer = false;
            for (Byte answer : answers) {
                if (values.contains(answer)) {
                    containsAnswer = true;
                    break;
                }
            }
            if (!containsAnswer) {
                return false;
            }
        }
        return true;
    }

    // Change array to byte list
    private List<Byte> byteArrayToList(Byte[] byteArray) {
        List<Byte> byteList = new ArrayList<>();
        for (byte b : byteArray) {
            byteList.add(b);
        }
        return byteList;
    }

    // Change list to byte array
    private Byte[] listToByteArray(List<Byte> byteList) {
        Byte[] byteArray = new Byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
    }

    // Generate list of every possible combination
    private List<List<Byte>> generateCombinations(List<Byte> list, int k) {
        List<List<Byte>> combinations = new ArrayList<>();

        if (k == 0) {
            combinations.add(new ArrayList<>());
            return combinations;
        } else if (list.isEmpty()) {
            return combinations;
        } else {

            Byte head = list.get(0);
            List<Byte> tail = list.subList(1, list.size());
            List<List<Byte>> combinationsWithHead = new ArrayList<>();

            for (List<Byte> combination : generateCombinations(tail, k - 1)) {
                List<Byte> comboWithHead = new ArrayList<>();
                comboWithHead.add(head);
                comboWithHead.addAll(combination);
                combinationsWithHead.add(comboWithHead);
            }

            List<List<Byte>> combinationsWithoutHead = generateCombinations(tail, k);

            combinations.addAll(combinationsWithHead);
            combinations.addAll(combinationsWithoutHead);

            return combinations;
        }
    }

    // Are all unique delivers in testing items?
    private boolean containsAllValues(List<Byte> testingItems, Byte[] uniqueDeliveries) {
        for (Byte value : uniqueDeliveries) {
            if (!testingItems.contains(value)) {
                return false;
            }
        }
        return true;
    }

    // Return list with no. of delivery type, which are necessary to use to deliver products to the customer
    private List<Byte> minimalByteList(Map<String, List<Byte>> dictionary, Byte[] uniqueDeliveries) {
        List<Byte> result = new ArrayList<>();
        if(checkAnswers(dictionary,uniqueDeliveries)){
            return byteArrayToList(uniqueDeliveries);
        }
        else {
            List<Byte> allKeys = new ArrayList<>();
            byte numOfDeliveriesType = (byte)dynamicEnumOfDelivery.size();
            while (allKeys.size()<numOfDeliveriesType){
                allKeys.add((byte)allKeys.size());
            }
            int numOfLoop = uniqueDeliveries.length +1;
            while (numOfLoop<numOfDeliveriesType) {
                List<List<Byte>> allCombinations = generateCombinations(allKeys, numOfLoop);
                for (List<Byte> testingItems : allCombinations) {
                    if (containsAllValues(testingItems, uniqueDeliveries)) {
                        if (checkAnswers(dictionary, listToByteArray(testingItems))) {
                            return testingItems;
                        }
                    }
                }
                numOfLoop++;
            }
        }
        return result;
    }

    // Translating map where keys are products to keys with type of deliver
    private Map<Byte, List<String>> mapProductsToDeliveries(List<Byte> deliveriesType, Map<String, List<Byte>> dictionaryWithProducts) {
        Map<Byte, List<String>> resultMap = new HashMap<>();

        // Iterating over deliveriesType
        for (byte deliveryType : deliveriesType) {
            // List to hold products associated with the current delivery type
            List<String> productsForDeliveryType = new ArrayList<>();

            // Iterating over dictionaryWithProducts to find products associated with the current delivery type
            for (Map.Entry<String, List<Byte>> entry : dictionaryWithProducts.entrySet()) {
                // If the current product list contains the current delivery type, add it to the productsForDeliveryType list
                if (entry.getValue().contains(deliveryType)) {
                    productsForDeliveryType.add(entry.getKey());
                }
            }

            // Adding the list of products to the resultMap with deliveryType as the key
            resultMap.put(deliveryType, productsForDeliveryType);
        }
        return resultMap;
    }

    // Remove duplicated values. First duplicated values are deleting from smaller collections
    private Map<Byte, List<String>> removeDuplicates(Map<Byte, List<String>> inputMap) {
        Map<Byte, List<String>> resultMap = new HashMap<>(inputMap);

        List<Map.Entry<Byte, List<String>>> entries = new ArrayList<>(resultMap.entrySet());
        entries.sort(Comparator.comparingInt(entry -> -entry.getValue().size()));

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<Byte, List<String>> currentEntry = entries.get(i);
            List<String> currentList = currentEntry.getValue();

            for (int j = i + 1; j < entries.size(); j++) {
                List<String> nextList = entries.get(j).getValue();
                nextList.removeAll(currentList);
            }
        }
        return resultMap;
    }
}
