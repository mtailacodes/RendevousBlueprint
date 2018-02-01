package com.mtailacodes.blueprintrendevouz.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mtailacodes.blueprintrendevouz.fragments.UserCardFragment


/**
 * Created by matthewtaila on 1/22/18.
 */
class UserCardAdapter(context: Context, resource: Int): ArrayAdapter<RendevouzUserModel>(context, resource){

    var mContext: Context

    init {
        mContext = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder : ViewHolder
        var convView : View
        var inflater = LayoutInflater.from(mContext)

        if (convertView == null){
            convView = inflater.inflate(R.layout.item_list_user_card, parent, false)
            holder = ViewHolder(convView)
            convView.tag = holder
        } else {
            convView = inflater.inflate(R.layout.item_list_user_card, parent, false)
            holder = ViewHolder(convView)
            convView.tag = holder
//            holder = (convView.tag as? ViewHolder)!!
        }

        var mUser = getItem(position)
        when(mUser.stub){
            1 ->{Glide.with(context).load("https://i.pinimg.com/originals/dc/9c/e9/dc9ce9f6156657a9e6ee52e1ebda3234.jpg").into(holder.imageView)}
            2 ->{Glide.with(context).load("https://i.pinimg.com/originals/43/89/fa/4389fa1278825de135fc3fc4d99331bc.jpg").into(holder.imageView)}
            3 ->{Glide.with(context).load("https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/17494899_1241264039284000_3810822330238631936_n.jpg").into(holder.imageView)}
            4 ->{Glide.with(context).load("https://i.pinimg.com/originals/87/91/04/879104b59a8977815e74338bbc6c3d70.jpg").into(holder.imageView)}
        }
        return convView
    }

    inner class ViewHolder(itemView: View) {
        var imageView: ImageView
        var like: ImageView
        var dislike: ImageView

        init {
            imageView = itemView.findViewById(R.id.iv_ProfilePic)
            like = itemView.findViewById(R.id.like)
            dislike = itemView.findViewById(R.id.dislike)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }




}