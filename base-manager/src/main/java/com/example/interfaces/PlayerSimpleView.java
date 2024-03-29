package com.example.interfaces;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface PlayerSimpleView {

    UUID getId();

    String getUsername();

    int getTotalScore();

    @Value("#{target.baseList.size()}")
    int getNumberOfBases();

}
