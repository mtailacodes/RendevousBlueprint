package com.mtailacodes.blueprintrendevouz.login

import android.app.Activity
import android.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mtailacodes.blueprintrendevouz.databinding.GenderSelectItemListBinding
import com.mtailacodes.blueprintrendevouz.databinding.GenderSelectItemListSelectedBinding

class GenderAdapter(var activity: Activity, var genderList: ArrayList<String>) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val binding = DataBindingUtil.inflate<GenderSelectItemListSelectedBinding>(activity.layoutInflater,
                    R.layout.gender_select_item_list_selected, null, false)
            binding.tv.text = genderList[position]
            view = binding.root
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val binding = DataBindingUtil.inflate<GenderSelectItemListBinding>(activity.layoutInflater,
                    R.layout.gender_select_item_list, null, false)
            binding.tv.text = genderList[position]
            view = binding.root
        }
        return view
    }

    override fun getItem(p0: Int): String {
        return genderList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return genderList.size
    }

    fun updateData(updatedList: ArrayList<String>) {
        genderList = updatedList
        notifyDataSetChanged()
    }

}