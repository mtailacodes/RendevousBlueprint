package com.mtailacodes.blueprintrendevouz.login

import android.content.Context
import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.support.v4.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.SpinnerAdapter
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.GenderSelectionBinding

/**
 * Created by mtaila on 5/5/18.
 */

class GenderSpinnerAdapter (
        private var adapter: SpinnerAdapter,
        private var nothingSelectedLayout: Int,
        private var nothingSelectedDropdownLayout: Int,
        private var context: Context) : SpinnerAdapter, ListAdapter {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    constructor(
            spinnerAdapter: SpinnerAdapter,
            nothingSelectedLayout: Int, context: Context) : this(spinnerAdapter, nothingSelectedLayout, -1, context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return when (position) {
            0 -> getNothingSelectedView(parent)
            else -> return dropdownView(parent, position, true)
        }
    }

    private fun getNothingSelectedView(parent: ViewGroup): View {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return if (position == 0) {
            if (nothingSelectedDropdownLayout == -1)
                View(context)
            else
                getNothingSelectedDropdownView(parent)
        } else {
            var active = false
            dropdownView(parent, position, active)
        }
    }

    private fun dropdownView(parent: ViewGroup, position: Int, active: Boolean): View {
        var binding = DataBindingUtil.inflate<GenderSelectionBinding>(layoutInflater,
                R.layout.gender_selection, parent, false)
        if (position == 1){
            binding.tv.text = "Male"
            binding.divider.visibility = View.VISIBLE
        } else if (position == 2){
            binding.tv.text = "Female"
            binding.divider.visibility = View.GONE
        }

        if (active){
            binding.divider.visibility = View.GONE
            binding.tv.setPadding(0, 0 , 0 , 2)
        } else {
            binding.tv.setTextColor(ContextCompat.getColor(context, R.color.black100))
        }
        var view = binding.root
        return view
    }

    private fun getNothingSelectedDropdownView(parent: ViewGroup): View {
        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false)
    }

    override fun getCount(): Int {
        val count = adapter.count
        return if (count == 0) 0 else count + EXTRA
    }

    override fun getItem(position: Int): Any? {
        return if (position == 0) null else adapter.getItem(position - EXTRA)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return if (position >= EXTRA) adapter.getItemId(position - EXTRA) else (position - EXTRA).toLong()
    }

    override fun hasStableIds(): Boolean {
        return adapter.hasStableIds()
    }

    override fun isEmpty(): Boolean {
        return adapter.isEmpty
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        adapter.registerDataSetObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        adapter.unregisterDataSetObserver(observer)
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0 // Don't allow the 'nothing selected'
        // item to be picked.
    }

    companion object {
        private val EXTRA = 1
    }

}
