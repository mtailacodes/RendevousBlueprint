package com.mtailacodes.blueprintrendevouz.adapter

import android.provider.Settings
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings.*
import com.mtailacodes.blueprintrendevouz.viewholder.ProfileHightlightViewholder
import com.mtailacodes.blueprintrendevouz.viewholder.ProfileSettingsBreakViewholer
import com.mtailacodes.blueprintrendevouz.viewholder.SessionsDescriptionViewholder
import com.mtailacodes.blueprintrendevouz.viewholder.SettingsViewholder

/**
 * Created by matthewtaila on 1/12/18.
 */
class HeterogenousProfileSettingsAdapter(settingsList: ArrayList<ProfileSettingsHeader>, mItemListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mSettingsList = ArrayList<ProfileSettingsHeader>()
    var mItemListener: OnItemClickListener
    private var PROFILE_HIGHLIGHT = 1
    private var SESSION_DESCRIPTION = 2
    private var PROFILE_SETTINGS = 3
    private var SETTINGS_BREAK = 4

    init {
        this.mSettingsList = settingsList
        this.mItemListener = mItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var mViewHolder: RecyclerView.ViewHolder
        var inflator = LayoutInflater.from(parent.context)

        when (viewType){
            PROFILE_HIGHLIGHT ->{
                var profileHighlightLayout = inflator.inflate(R.layout.item_list_profile_highlight,
                        parent, false)
                mViewHolder = ProfileHightlightViewholder(profileHighlightLayout)
                return mViewHolder
            }
            SESSION_DESCRIPTION ->{
                var sessionDescriptionLayout = inflator.inflate(R.layout.item_list_session_description,
                        parent, false)
                mViewHolder = SessionsDescriptionViewholder(sessionDescriptionLayout)
                return mViewHolder
            }
            PROFILE_SETTINGS ->{
                var profileSettingsLayout = inflator.inflate(R.layout.item_list_setting,
                        parent, false)
                mViewHolder = SettingsViewholder(profileSettingsLayout)
                return mViewHolder
            }
            SETTINGS_BREAK ->{
                var settingsBreakLayout = inflator.inflate(R.layout.item_list_break,
                        parent, false)
                mViewHolder = ProfileSettingsBreakViewholer(settingsBreakLayout)
                return mViewHolder
            }
        }
        return mViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder!!.itemViewType) {
            PROFILE_HIGHLIGHT -> {
                var profileHighlightViewholder = holder as ProfileHightlightViewholder
                configureProfileHightlightViewholder(profileHighlightViewholder, position)
            }
            SESSION_DESCRIPTION -> {
                var sessionDescriptionViewholder = holder as SessionsDescriptionViewholder
                configureSessionDescriptionViewholder(sessionDescriptionViewholder, position)
            }
            PROFILE_SETTINGS -> {
                var profileSettingsViewholder = holder as SettingsViewholder
                configureProfileSettingsViewholder(profileSettingsViewholder, position)
            }
            SETTINGS_BREAK -> {
                var breakSettingsViewholder = holder as ProfileSettingsBreakViewholer
            }
        }
    }

    private fun configureProfileSettingsViewholder(profileSettingsViewholder: SettingsViewholder, position: Int) {
        var profileSettings = mSettingsList[position] as ProfileSettings
        profileSettingsViewholder.settingsTitle.text = profileSettings.description
        profileSettingsViewholder.container.setOnClickListener { _ ->
                if (mItemListener != null){
                    mItemListener.onItemClick(profileSettings, profileSettingsViewholder, position)
                }

        }
    }

    private fun configureSessionDescriptionViewholder(sessionDescriptionViewholder: SessionsDescriptionViewholder, position: Int) {
        var sessionsDescription = mSettingsList.get(position) as SessionDescription
        sessionDescriptionViewholder.sessionDescription.text = sessionsDescription.description
    }

    private fun configureProfileHightlightViewholder(profileHighlightViewholder: ProfileHightlightViewholder,
                                                     position: Int) {
        profileHighlightViewholder.tv_userName.text = "Tom, "
        profileHighlightViewholder.tv_userAge.text = " 26"
        profileHighlightViewholder.tv_userLocation.text = "Southfield, MI"

    }

    override fun getItemCount(): Int {
        return mSettingsList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (mSettingsList[position] is ProfileHightlight){
            return PROFILE_HIGHLIGHT
        } else if (mSettingsList[position] is SessionDescription) {
            return SESSION_DESCRIPTION
        } else if (mSettingsList[position] is ProfileSettings){
            return PROFILE_SETTINGS
        } else if (mSettingsList[position] is ProfileSettingsBreak){
            return SETTINGS_BREAK
        }else {
            return -1
        }
    }

    interface OnItemClickListener{
        fun onItemClick(profileSetting: ProfileSettingsHeader, viewHolder: RecyclerView.ViewHolder, position: Int)
    }

}