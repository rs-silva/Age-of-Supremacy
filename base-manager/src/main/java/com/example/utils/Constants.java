package com.example.utils;

public abstract class Constants {

    public static final String PLAYER_ALREADY_EXISTS = "Player with id %s and/or username %s already exists!";

    public static final String PLAYER_ID_FROM_REQUEST_DOES_NOT_MATCH_TOKEN_PLAYER_ID = "Player ID from request is different from the Player ID in the token";

    public static final String BASE_NOT_FOUND = "Base with id %s was not found";

    public static final String BUILDING_NOT_FOUND = "Building with id %s was not found";

    public static final String BUILDING_UPGRADE_NOT_FOUND_ERROR = "There was an error while retrieving the building's upgrade configuration.";

    public static final String BUILDING_IS_ALREADY_MAX_LEVEL = "Cannot upgrade a building is already at the maximum level";

    public static final String NOT_ENOUGH_RESOURCES_TO_UPGRADE_BUILDING = "There are not enough resources to upgrade this building";

    public static final String BASE_NO_INFORMATION_ABOUT_RESOURCE_AMOUNT = "The base does not contain information about this resource amount";
}
