package com.mtailacodes.blueprintrendevouz.viewholder

import android.view.View
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 1/17/18.
 */
class AgeRangeViewholder: ParentSearchSettingsViewholder {


    constructor(itemView: View) : super(itemView){
        container = itemView.findViewById(R.id.ageRangecontainer)
        subContainer = itemView.findViewById(R.id.ageRangeSubContainer)
    }
}