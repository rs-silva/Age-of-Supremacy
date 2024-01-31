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

}
