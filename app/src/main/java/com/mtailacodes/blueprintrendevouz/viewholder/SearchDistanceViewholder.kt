package com.mtailacodes.blueprintrendevouz.viewholder

import android.view.View
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 1/17/18.
 */
class SearchDistanceViewholder: ParentSearchSettingsViewholder {

    constructor(itemView: View) : super(itemView){
        container = itemView.findViewById(R.id.container)
        subContainer = itemView.findViewById(R.id.subContainer)
    }
}