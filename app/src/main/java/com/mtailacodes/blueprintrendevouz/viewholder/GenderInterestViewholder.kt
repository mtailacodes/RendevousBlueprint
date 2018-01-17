package com.mtailacodes.blueprintrendevouz.viewholder

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.R.id.*

/**
 * Created by matthewtaila on 1/14/18.
 */
class GenderInterestViewholder: ParentSearchSettingsViewholder {

    var selectMale: TextView
    var selectFemale: TextView

    constructor(itemView: View?) : super(itemView){

        selectMale = itemView!!.findViewById(R.id.tv_SelectMale)
        selectFemale = itemView!!.findViewById(R.id.tv_SelectFemale)
        container = itemView!!.findViewById(R.id.container)
        subContainer = itemView!!.findViewById(R.id.subContainer)
    }
}