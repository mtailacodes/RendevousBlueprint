package com.mtailacodes.blueprintrendevouz.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.FragmentPersonalSettingsBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 1/21/18.
 */
class PersonalSettingsFragment: Fragment(){

    private lateinit var mUserModel : RendevouzUserModel
    private lateinit var mBinding: FragmentPersonalSettingsBinding

    companion object {
        fun newInstance(): PersonalSettingsFragment{
            val mFragment = PersonalSettingsFragment()
//            var args = Bundle()
//            args.putParcelable("SearchSettings", rendevouzUserModel)
//            mFragment.arguments = args
            return mFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_settings, container, false)
        return mBinding.root
    }
}