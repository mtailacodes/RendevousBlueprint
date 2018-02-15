package com.mtailacodes.blueprintrendevouz.fragments.onboarding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.mtailacodes.blueprintrendevouz.Activity.RxUserUtil
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.FragmentOnboardingSearchSettingsBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 2/14/18.
 */
class OnBoardingSearchSettings : Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentOnboardingSearchSettingsBinding
    private lateinit var mSearchSettings : UserSearchSettings

    companion object {
        fun newInstance(): OnBoardingSearchSettings {
            var mFragment = OnBoardingSearchSettings()
            return mFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_search_settings, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvNext.setOnClickListener(this)
        setupRangeBar()
    }

    private fun setupRangeBar() {
        mBinding.ageRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            mBinding.tvMinAge.text = minValue.toString()
            mBinding.tvMaxAge.text = maxValue.toString()
        }
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.tv_Next ->{

                mSearchSettings = UserSearchSettings()

                mSearchSettings.ageRangeMin = Integer.parseInt(mBinding.tvMinAge.text.toString())
                mSearchSettings.ageRangeMax= Integer.parseInt(mBinding.tvMaxAge.text.toString())
                mSearchSettings.sexIntereset = mBinding.spGenderSpinner.selectedItem.toString()
                mSearchSettings.settingsCompleted = true


                var uuID = FirebaseAuth.getInstance().currentUser!!.uid
                var mFirestore = RxUserUtil().UserSettingsCollectionReference(uuID)
                mFirestore .document(uuID).set(mSearchSettings)
                        .addOnSuccessListener{
                            (activity.application as MyApplication)
                                    .bus()
                                    .send("USER_SEARCH_SETTINGS_STORED")
                        }
                }
            }
        }
}

