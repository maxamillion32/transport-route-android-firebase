package com.fcorcino.transportroute.utils;

public class Constants {

    // TCP server services codes
    public static final int SERVICE_CODE_CREATE_USER = 1;
    public static final int SERVICE_CODE_LOG_IN_USER = 2;
    public static final int SERVICE_CODE_GET_ROUTES = 3;
    public static final int SERVICE_CODE_GET_STOPS_BY_ROUTE = 4;
    public static final int SERVICE_CODE_GET_TURNS_BY_ROUTE = 5;
    public static final int SERVICE_CODE_GET_CURRENT_RESERVATION = 6;
    public static final int SERVICE_CODE_MAKE_RESERVATION = 7;
    public static final int SERVICE_CODE_GET_PENDING_STOPS_BY_TURN = 8;
    public static final int SERVICE_CODE_GET_TURN_BY_DRIVER = 9;
    public static final int SERVICE_CODE_UPDATE_RESERVATION = 10;
    public static final int SERVICE_CODE_UPDATE_TURN_LOCATION = 11;
    public static final int SERVICE_CODE_GET_TURN = 12;
    public static final int SERVICE_CODE_UPDATE_TURN_LAST_STOP = 13;
    public static final int SERVICE_CODE_VALIDATE_USERNAME = 14;

    // User types
    public static final String USER_TYPE_USER = "1";
    public static final String USER_TYPE_DRIVER = "2";

    // Database status field values
    public static final String STATUS_VALUE_ACTIVE = "A";
    public static final String STATUS_VALUE_PICK_UP = "P";
    public static final String STATUS_VALUE_DELIVERED = "D";

    // Intent data keys
    public static final String ROUTE_ID_KEY = "ROUTE_ID_KEY";
    public static final String ROUTE_PRICE_KEY = "ROUTE_PRICE_KEY";
    public static final String ROUTE_RESERVATION_KEY = "ROUTE_RESERVATION_KEY";
    public static final String LAST_KNOWN_LOCATION_KEY = "LAST_KNOWN_LOCATION_KEY";

    // Request codes
    public static final int REQUEST_CODE_CHOOSE_STOPS = 1;
    public static final int REQUEST_CODE_SELECT_ROUTE = 2;

    // Scanner keys
    public static final String SCANNER_SCAN_WIDTH_KEY = "SCAN_WIDTH";
    public static final String SCANNER_SCAN_HEIGHT_KEY = "SCAN_HEIGHT";
    public static final String SCANNER_PROMPT_MESSAGE_KEY = "PROMPT_MESSAGE";

    // SharedPreferences keys
    public static final String SHARED_PREF_USER_ID_KEY = "SHARED_PREF_USER_ID_KEY";
    public static final String SHARED_PREF_USER_NAME_KEY = "SHARED_PREF_USER_NAME_KEY";
    public static final String SHARED_PREF_USER_TYPE_KEY = "SHARED_PREF_USER_TYPE_KEY";
    public static final String SHARED_PREF_USER_TURN_ID_KEY = "SHARED_PREF_USER_TURN_ID_KEY";
    public static final String SHARED_PREF_ROUTE_ID_KEY = "SHARED_PREF_ROUTE_ID_KEY";
    public static final String SHARED_PREF_RESERVATION_ID_KEY = "SHARED_PREF_RESERVATION_ID_KEY";
}
