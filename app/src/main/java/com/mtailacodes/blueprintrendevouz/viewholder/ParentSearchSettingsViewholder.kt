package com.mtailacodes.blueprintrendevouz.viewholder

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by matthewtaila on 1/15/18.
 */
abstract class ParentSearchSettingsViewholder: RecyclerView.ViewHolder{

    open lateinit var subContainer: ConstraintLayout
    open lateinit var container: ConstraintLayout

    constructor(itemView: View?) : super(itemView)
}