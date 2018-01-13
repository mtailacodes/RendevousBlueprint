package com.mtailacodes.blueprintrendevouz.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 1/13/18.
 */
class SessionsDescriptionViewholder : RecyclerView.ViewHolder {
    var sessionDescription: TextView
    constructor(itemView: View?) : super(itemView){
        sessionDescription = itemView!!.findViewById(R.id.tv_sessionDescription)
    }
}