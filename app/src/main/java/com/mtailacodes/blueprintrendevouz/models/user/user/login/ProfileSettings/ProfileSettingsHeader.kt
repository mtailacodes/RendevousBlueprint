package com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings

/**
 * Created by matthewtaila on 1/12/18.
 */
open class ProfileSettingsHeader{

    var headerType: Int = -1
    var description = "default"

    constructor(headerType: Int) {
        this.headerType = headerType
    }
}