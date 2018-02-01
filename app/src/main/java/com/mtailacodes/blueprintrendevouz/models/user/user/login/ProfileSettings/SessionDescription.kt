package com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings

/**
 * Created by matthewtaila on 1/13/18.
 * Used to hold briefly highlight what the user is up to for the given session
 * Will be used in the profileActivity settings RecyclerView
 */
class SessionDescription: ProfileSettingsHeader{

    constructor(title: String) : super(2){
        description = title
    }

}