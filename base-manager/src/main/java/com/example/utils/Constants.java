package com.example.utils;

public abstract class Constants {

    public static final String PLAYER_ALREADY_EXISTS = "Player with id %s and/or username %s already exists!";

    public static final String PLAYER_ID_FROM_REQUEST_DOES_NOT_MATCH_TOKEN_PLAYER_ID = "Player ID from request is different from the Player ID in the token";

    public static final String PLAYER_NOT_FOUND = "Player with id %s was not found";

    public static final String BASE_NOT_FOUND = "Base with id %s was not found";

    public static final String BUILDING_NOT_FOUND = "Building with id %s was not found";

    public static final String BUILDING_UPGRADE_NOT_FOUND_ERROR = "There was an error while retrieving the building's upgrade configuration.";

    public static final String BUILDING_SCORE_NOT_FOUND_ERROR = "There was an error while retrieving the building's score configuration.";

    public static final String BUILDING_IS_ALREADY_MAX_LEVEL = "Cannot upgrade a building that is already at the maximum level.";

    public static final String NOT_ENOUGH_RESOURCES_TO_UPGRADE_BUILDING = "There are not enough resources to upgrade this building.";

    public static final String BASE_DOES_NOT_BELONG_TO_THE_LOGGED_IN_PLAYER = "This base does not belong to the player logged in. Cannot perform operation.";

    public static final String BUILDING_DOES_NOT_BELONG_TO_THE_LOGGED_IN_PLAYER = "This building does not belong to the player logged in. Cannot perform operation.";

    public static final String BUILDING_ALREADY_EXISTS = "Attempted to create a building that already exists in the base.";

    public static final String INVALID_BUILDING_NAME = "The building you are trying to create does not exist. Please provide a valid name.";

    public static final String UNIT_CONFIG_NOT_FOUND_ERROR = "There was an error while retrieving the unit's recruitment configuration.";

    public static final String INVALID_UNIT_NAME = "The unit %s does not exist. Please provide a valid name.";

    public static final String NOT_ENOUGH_RESOURCES_TO_RECRUIT_UNITS = "There are not enough resources to recruit these units.";

    public static final String BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS = "Recruiting %s requires %s (lv.%d)";
}
