package com.example.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ListUtils {

    public static <T> List<T> concatenateLists(List<T> list1, List<T> list2, List<T> list3) {
        return Stream.of(list1, list2, list3)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
