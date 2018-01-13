package com.mtailacodes.blueprintrendevouz.viewholder

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 1/13/18.
 */
class SettingsViewholder : RecyclerView.ViewHolder{

    var settingsTitle: TextView
    var container: ConstraintLayout

    constructor(itemView: View?) : super(itemView){
        settingsTitle = itemView!!.findViewById(R.id.tv_SettingsTitle)
        container = itemView!!.findViewById(R.id.parentContainer)
    }
}