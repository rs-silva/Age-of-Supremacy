package org.example.config;

public abstract class ServerConfig {

    /* Resource Production */
    /* Formula is SERVER_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    public static final Double SERVER_GROWING_FACTOR = 1.0;

    public static final Double BASE = 30.0;

    public static final Double EXPONENTIAL = 1.1659144;

}
