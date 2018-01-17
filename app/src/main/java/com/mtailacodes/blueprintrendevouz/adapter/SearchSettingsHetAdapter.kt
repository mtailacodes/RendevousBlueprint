package com.mtailacodes.blueprintrendevouz.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings.ProfileSettings
import com.mtailacodes.blueprintrendevouz.models.user.user.login.searchSettings.SearchSettings
import com.mtailacodes.blueprintrendevouz.viewholder.GenderInterestViewholder
import com.mtailacodes.blueprintrendevouz.viewholder.ParentSearchSettingsViewholder
import com.mtailacodes.blueprintrendevouz.viewholder.ProfileHightlightViewholder
import com.mtailacodes.blueprintrendevouz.viewholder.ProfileSettingsBreakViewholer

/**
 * Created by matthewtaila on 1/14/18.
 */
class SearchSettingsHetAdapter(context: Context, mSettingsList: ArrayList<SearchSettings>, listener: OnItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){



    var mListener: OnItemClickListener = listener
    var mSearchSettingsItems : ArrayList<SearchSettings> = mSettingsList
    var mContext : Context = context

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType){
            SearchSettings.GENDER_INTEREST -> {
                var genderInterestViewholder = holder as GenderInterestViewholder
                configureGenderInterestViewholder(genderInterestViewholder, position)
            }
            SearchSettings.BREAK -> {
                var breakViewholder = holder as ProfileSettingsBreakViewholer
            }
        }
    }

    private fun configureGenderInterestViewholder(genderInterestViewholder: GenderInterestViewholder, position: Int) {
        genderInterestViewholder.container.setOnClickListener({ view ->
            mListener.onItemClick(view, position, genderInterestViewholder)
        } )
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var mViewHolder: RecyclerView.ViewHolder
        var inflator = LayoutInflater.from(parent.context)

        when (viewType) {
            SearchSettings.GENDER_INTEREST -> {
                var genderInterestLayout = inflator.inflate(R.layout.item_list_gender_interest,
                        parent, false)
                mViewHolder = GenderInterestViewholder(genderInterestLayout)
                return mViewHolder
            }
            SearchSettings.BREAK -> {
                var breakLayout = inflator.inflate(R.layout.item_list_break,
                        parent, false)
                mViewHolder = ProfileSettingsBreakViewholer(breakLayout)
                return mViewHolder
            }
        }
        return mViewHolder
    }

    override fun getItemCount(): Int {
        return mSearchSettingsItems.size
    }

    override fun getItemViewType(position: Int): Int {
        when (mSearchSettingsItems[position].type){
            SearchSettings.GENDER_INTEREST ->{
                return SearchSettings.GENDER_INTEREST
            }
            SearchSettings.BREAK->{
                return SearchSettings.BREAK
            }
        }
        return -1
    }

    interface OnItemClickListener{
        fun onItemClick(itemView: View, position: Int, viewHolder: ParentSearchSettingsViewholder)
    }


}