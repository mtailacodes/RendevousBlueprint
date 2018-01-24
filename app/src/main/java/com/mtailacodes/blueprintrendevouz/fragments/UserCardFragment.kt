package com.mtailacodes.blueprintrendevouz.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.adapter.UserCardAdapter
import com.mtailacodes.blueprintrendevouz.databinding.FragmentUserCardsBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.AccelerateInterpolator


/**
 * Created by matthewtaila on 1/22/18.
 */
class UserCardFragment: Fragment(), UserCardAdapter.OnItemClickListener{


    private var mUserList = ArrayList<RendevouzUserModel>()
    private lateinit var mBinding : FragmentUserCardsBinding
    lateinit var mLayoutManager: LinearLayoutManager

    companion object {
        fun newInstance(): UserCardFragment{
            val mFragment = UserCardFragment()
            return mFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateStubUserData()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_cards, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mAdapter = UserCardAdapter(this@UserCardFragment.context, mUserList, this)
        mLayoutManager = LinearLayoutManager(this@UserCardFragment.context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvUserCards.adapter = mAdapter
        mBinding.rvUserCards.layoutManager = mLayoutManager
        mBinding.rvUserCards.setOnTouchListener { view, motionEvent -> true }

    }

    private fun generateStubUserData() {
        var user = RendevouzUserModel()
        user.stub = 1
        var user1 = RendevouzUserModel()
        user1.stub = 2
        var user2 = RendevouzUserModel()
        user2.stub = 3
        var user3 = RendevouzUserModel()
        user3.stub = 4
        var user4 = RendevouzUserModel()
        user4.stub = 1
        var user5 = RendevouzUserModel()
        user5.stub = 2
        var user6 = RendevouzUserModel()
        user6.stub = 3
        var user7 = RendevouzUserModel()
        user7.stub = 4
        var user8 = RendevouzUserModel()
        user8.stub = 2

        mUserList.add(user)
        mUserList.add(user1)
        mUserList.add(user2)
        mUserList.add(user3)
        mUserList.add(user4)
        mUserList.add(user5)
        mUserList.add(user6)
        mUserList.add(user7)
        mUserList.add(user8)
    }

    inner class CustomLayoutManager: LinearLayoutManager{

        var scollable = false;

        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)

        override fun canScrollHorizontally(): Boolean {
            return scollable
        }
    }

    override fun onItemClick(position: Int) {
        mBinding.rvUserCards.smoothScrollBy(1000, 0, AccelerateInterpolator())
    }

}