package com.mtailacodes.blueprintrendevouz.fragments.onboarding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtailacodes.blueprintrendevouz.Activity.RxUserUtil
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.customViews.DatePickerFragment
import com.mtailacodes.blueprintrendevouz.databinding.FragmentOnboardingAboutMeBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import java.util.*

/**
 * Created by matthewtaila on 2/10/18.
 */
class OnBoardingAboutMe : Fragment(), View.OnClickListener, DatePickerFragment.EditNameDialogListener{


    private lateinit var mBinding : FragmentOnboardingAboutMeBinding
    private lateinit var mUser : RendevouzUserModel
    private var userNamePassed = false
    private var birthdatePassed  = false

    companion object {
        fun newInstance(): OnBoardingAboutMe {
            var mFragment = OnBoardingAboutMe()
            return mFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUser = RxUserUtil().getUserModel()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_about_me, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.etAgeDay.setOnClickListener(this)
        mBinding.tvNext.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id){
            R.id.et_AgeDay ->{
                var datePickerFragment = DatePickerFragment(mBinding.etAgeYear, mUser, this)
                datePickerFragment.show(activity.fragmentManager, "DatePicker")
            }
            R.id.tv_Next ->{
                mUser.gender = mBinding.spGenderSpinner.selectedItem.toString()
                userNamePassed = mBinding.etUserNameInput.text.isNotEmpty() // check username field

                if (checkBirthdatePassed() == 1){
                    // todo error - you must be 18 to use this app
                } else if (checkBirthdatePassed() == 2){
                    // todo error - need to enter a valid birthday
                }

                if (userNamePassed && birthdatePassed){
                    mUser.username = mBinding.etUserNameInput.text.toString()
                    var mFireStore = RxUserUtil().GlobalUserCollectionReference().document(mUser.uuID)
                    mFireStore.update("requiresOnboarding", false,
                            "username", mUser.username,
                                "birthDay", mUser.birthDay,
                                        "birthMonth", mUser.birthMonth,
                                        "birthYear", mUser.birthYear,
                                        "gender", mUser.gender).addOnSuccessListener{ _ ->
                        (activity.application as MyApplication)
                                .bus()
                                .send("USER_DATA_STORED")
                    }.addOnFailureListener { e ->
                        Log.d("FirestoreFailure", e.localizedMessage)
                    }

                }
            }
        }
    }

    private fun checkBirthdatePassed(): Int {

        var userBday = GregorianCalendar(mUser.birthYear, mUser.birthMonth, mUser.birthDay)
        var today  = GregorianCalendar()
        today.add(Calendar.YEAR, -18)
        return if (today.before(userBday)){
            1
        } else {
             if (mUser.birthDay > 0 && mUser.birthMonth > 0 && mUser.birthYear > 0){
                 birthdatePassed = true
                 3
            } else 2
        }
    }


    override fun onFinishEditDialog(mUserModel: RendevouzUserModel) {
        mUser = mUserModel
    }


}