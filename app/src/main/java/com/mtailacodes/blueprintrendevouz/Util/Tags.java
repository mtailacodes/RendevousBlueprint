package com.mtailacodes.blueprintrendevouz.Util;

/**
 * Created by matthewtaila on 12/10/17.
 */

public class Tags {

    // create user tags
    private static final String NO_EMAIL_INPUT = "Please enter a valid email address";
    private static final String EMAIL_FORMAT_INCORRECT = "Please enter a valid email format";
    private static final String NO_USERNAME_INPUT = "Please fill in the username field";
    public static final String NO_PASSWORD_INPUT = "Please fill in the username field";
    private static final String NO_GENDER_INPUT = "Please select a gender";
    public  final static String CREATE_USER_STEP_ONE_SUCCESS = "Create User Step One Success";
    public final static String CREATE_USER_STEP_TWO_SUCCESS = "Create User Step Two Success";


    public static String getNoEmailInput() {
        return NO_EMAIL_INPUT;
    }
    public static String getNoUsernameInput() {
        return NO_USERNAME_INPUT;
    }
    public static String getNoGenderInput() {
        return NO_GENDER_INPUT;
    }
    public static String getEmailFormatIncorrect() {
        return EMAIL_FORMAT_INCORRECT;
    }
}
