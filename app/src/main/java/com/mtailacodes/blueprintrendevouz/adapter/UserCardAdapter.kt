package com.mtailacodes.blueprintrendevouz.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mtailacodes.blueprintrendevouz.fragments.UserCardFragment


/**
 * Created by matthewtaila on 1/22/18.
 */
class UserCardAdapter(var context: Context, var userList: ArrayList<RendevouzUserModel>, listener: OnItemClickListener): RecyclerView.Adapter<UserCardAdapter.ViewHolder>(){


    var mUserList = ArrayList<RendevouzUserModel>()
    lateinit var mContext: Context
    lateinit var mListener : OnItemClickListener


    init {
        mUserList = userList
        mContext = context
        mListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var like: ImageView
        var dislike: ImageView

        init {
            imageView = itemView.findViewById(R.id.iv_ProfilePic)
            like = itemView.findViewById(R.id.like)
            dislike = itemView.findViewById(R.id.dislike)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var mUser = mUserList[position]
        var image = holder.imageView

        holder.like.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_like_user))
        holder.dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_dislike_user))

        holder.like.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mListener.onItemClick(position)
            }
        })

        when(mUser.stub){
            1 ->{Glide.with(context).load("https://i.pinimg.com/originals/dc/9c/e9/dc9ce9f6156657a9e6ee52e1ebda3234.jpg").into(image)}
            2 ->{Glide.with(context).load("https://i.pinimg.com/originals/43/89/fa/4389fa1278825de135fc3fc4d99331bc.jpg").into(image)}
            3 ->{Glide.with(context).load("https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/17494899_1241264039284000_3810822330238631936_n.jpg").into(holder.imageView)}
            4 ->{Glide.with(context).load("https://i.pinimg.com/originals/87/91/04/879104b59a8977815e74338bbc6c3d70.jpg").into(holder.imageView)}
        }
    }



    override fun getItemCount(): Int {
        return mUserList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val userCard = inflater.inflate(R.layout.item_list_user_card, parent, false)
        return ViewHolder(userCard)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}