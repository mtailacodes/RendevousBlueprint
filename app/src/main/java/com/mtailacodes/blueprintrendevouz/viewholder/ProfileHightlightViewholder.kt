package com.mtailacodes.blueprintrendevouz.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.R.id.tv_userAge

/**
 * Created by matthewtaila on 1/12/18.
 */

class ProfileHightlightViewholder : RecyclerView.ViewHolder {

    var tv_userName: TextView
    var tv_userAge: TextView
    var tv_userLocation: TextView

    constructor(itemView: View?) : super(itemView){
        tv_userName = itemView!!.findViewById(R.id.tv_userName)
        tv_userAge = itemView!!.findViewById(R.id.tv_userAge)
        tv_userLocation = itemView!!.findViewById(R.id.tv_userLocation)
    }


}