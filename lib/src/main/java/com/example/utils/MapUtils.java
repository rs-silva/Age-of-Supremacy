package com.example.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class MapUtils {

    public static Map<String, Integer> shuffleMapValues(Map<String, Integer> originalMap) {
        // Get the values as a list
        List<Integer> valuesList = new ArrayList<>(originalMap.values());

        // Shuffle the values
        Collections.shuffle(valuesList);

        // Reconstruct the map with shuffled values
        Iterator<Integer> shuffledValuesIterator = valuesList.iterator();
        Map<String, Integer> shuffledMap = new HashMap<>();

        originalMap.keySet().forEach(key -> shuffledMap.put(key, shuffledValuesIterator.next()));

        return shuffledMap;
    }

    public static Map<String, Double> addValuesToMap(Map<String, Double> currentMap, String unitName, double valueToAdd) {
        if (currentMap.containsKey(unitName)) {
            double currentUnitDamage = currentMap.get(unitName);

            double updatedUnitDamage = currentUnitDamage + valueToAdd;

            currentMap.put(unitName, updatedUnitDamage);
        }
        else {
            currentMap.put(unitName, valueToAdd);
        }

        return currentMap;
    }

}
