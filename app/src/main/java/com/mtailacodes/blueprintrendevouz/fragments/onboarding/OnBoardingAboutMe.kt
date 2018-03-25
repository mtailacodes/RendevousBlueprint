package com.mtailacodes.blueprintrendevouz.fragments.onboarding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private var birthDatePassed = false
    var birthdateDialogueHandled = false

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

        // set on click listeners
        mBinding.etAgeDay.setOnClickListener(this)
        mBinding.tvNext.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id){
            R.id.et_AgeDay ->{ // show birthDate picker
                if (!birthdateDialogueHandled){
//                    DatePickerFragment(mBinding.etAgeYear, mUser, this@).show(activity.fragmentManager, "DatePicker")
                    birthdateDialogueHandled = true
                }
            }
            R.id.tv_Next ->{
                var onBoardingAboutMeStatus = checkOnBoardingCredentialsPassed()
                when (onBoardingAboutMeStatus){
                    1 ->{ Toast.makeText(context, "Username field empty", Toast.LENGTH_SHORT).show()}
                    2 ->{ Toast.makeText(context, "Please select your birthday", Toast.LENGTH_SHORT).show()}
                    3 ->{ Toast.makeText(context, "You must be over 18 YOA", Toast.LENGTH_SHORT).show()}
                    4 ->{ nextOnBoardingProcess()}
                }
            }
        }
    }

    private fun nextOnBoardingProcess() {
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

    private fun checkOnBoardingCredentialsPassed() : Int {
        return if (mBinding.etUserNameInput.text.isNotEmpty()){
            mUser.gender = mBinding.spGenderSpinner.selectedItem.toString()
            mUser.username = mBinding.etUserNameInput.text.toString()
            checkBirthDatePassed()
        } else 1
    }

    private fun checkBirthDatePassed(): Int {
        val userBday = GregorianCalendar(mUser.birthYear, mUser.birthMonth, mUser.birthDay)
        val today  = GregorianCalendar()
        today.add(Calendar.YEAR, -18)
        return if (today.before(userBday)){
            3
        } else {
             if (mUser.birthDay > 0 && mUser.birthMonth > 0 && mUser.birthYear > 0){
                 birthDatePassed = true
                 4
            } else 2
        }
    }

    override fun onFinishEditDialog(mUserModel: RendevouzUserModel) {
        mUser = mUserModel
        birthdateDialogueHandled = false
    }

}