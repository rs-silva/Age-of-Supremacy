package com.example.interfaces;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface BaseSimpleView {
    UUID getId();

    String getName();

    int getScore();

    @Value("#{target.x_coordinate}")
    int getX_coordinate();

    @Value("#{target.y_coordinate}")
    int getY_coordinate();
}
