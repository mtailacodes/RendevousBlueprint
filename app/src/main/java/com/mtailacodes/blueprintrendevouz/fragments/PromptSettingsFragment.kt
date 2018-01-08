package com.mtailacodes.blueprintrendevouz.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.mtailacodes.blueprintrendevouz.Activity.RxUserUtil
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.FragmentPromptSettingsBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import android.widget.AdapterView.OnItemSelectedListener



/**
 * Created by matthewtaila on 1/7/18.
 */
class PromptSettingsFragment: android.support.v4.app.Fragment(), View.OnClickListener{

    lateinit var mBinding: FragmentPromptSettingsBinding
    var mSearchSettings =  UserSearchSettings()
    var mUser = RendevouzUserModel()
    var mSearchSettingsListener : UserSearchSettingsListener? = null

    companion object {
        fun newInstance(): PromptSettingsFragment {
            return PromptSettingsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_prompt_settings, container, false)
        setupUser()
        setOnClickListeners()
        return mBinding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is UserSearchSettingsListener) {
            mSearchSettingsListener = context
        } else {
            throw ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    private fun setupUser() {
        mUser.uuID = FirebaseAuth.getInstance().currentUser!!.uid
    }

    private fun setOnClickListeners() {
        mBinding.tvUserGenderMale.setOnClickListener(this)
        mBinding.tvUserGenderFemale.setOnClickListener(this)
        mBinding.tvUserInterestMale.setOnClickListener(this)
        mBinding.tvUserInterestFemale.setOnClickListener(this)
        mBinding.tvSave.setOnClickListener(this)
        mBinding.rbAgeRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            mBinding.tvAgeRangeMin.text = minValue.toString()
            mBinding.tvAgeRangeMax.text = maxValue.toString()
            // todo  need to change drawable for the rings
        }
    }

    override fun onClick(view: View) {

        when(view.id){
        R.id.tv_UserGenderMale -> {
            resetView(mBinding.tvUserGenderFemale)
            applySelectionHighlight(mBinding.tvUserGenderMale)
            mSearchSettings.gender = "Male"
            return
        }
        R.id.tv_UserGenderFemale -> {
            resetView(mBinding.tvUserGenderMale)
            applySelectionHighlight(mBinding.tvUserGenderFemale)
            mSearchSettings.gender = "Female"
            return
            }
        R.id.tv_UserInterestMale -> {
            resetView(mBinding.tvUserInterestFemale)
            applySelectionHighlight(mBinding.tvUserInterestMale)
            mSearchSettings.sexIntereset= "Male"
            return
            }
        R.id.tv_UserInterestFemale -> {
            resetView(mBinding.tvUserInterestMale)
            applySelectionHighlight(mBinding.tvUserInterestFemale)
            mSearchSettings.sexIntereset= "Female"
            return
            }
        R.id.tv_Save-> {
            mSearchSettingsListener!!.searchSettingsValid(checkSettingsInput(mSearchSettings))
            return
            }
        }
    }

    private fun applySelectionHighlight(view: TextView) {
        view.setTextColor(ContextCompat.getColor(activity, R.color.green))
    }

    private fun resetView(view: TextView) {
        view.setTextColor(ContextCompat.getColor(activity, R.color.black100))
    }

    private fun checkSettingsInput(searchSettings: UserSearchSettings): Boolean {
        var pass = false
        // todo maybe add animations to show which fields are set correctly - not necesary right now but when the UI is done we can think about that
        pass = searchSettings.gender == "Male" || searchSettings.gender == "Female"
        pass = searchSettings.sexIntereset ==  "Male" || searchSettings.sexIntereset == "Female"

        if (mBinding.etUserAgeInput.toString().isEmpty()){
            pass = false
        }  else {
            if (Integer.parseInt(mBinding.etUserAgeInput.text.toString()) >= 18){
                pass = true
            }
        }

        mSearchSettings.currentAge = Integer.parseInt(mBinding.etUserAgeInput.text.toString())
        mSearchSettings.ageRangeMin = Integer.parseInt(mBinding.tvAgeRangeMin.text.toString())
        mSearchSettings.ageRangeMax = Integer.parseInt(mBinding.tvAgeRangeMax.text.toString())
        mSearchSettings.settingsCompleted = pass

        if (mSearchSettings.settingsCompleted){
            var mFirestore = RxUserUtil().UserSettingsCollectionReference(mUser.uuID)
            mFirestore .document(mUser.uuID).set(mSearchSettings)
                .addOnSuccessListener{ _ ->

                }.addOnFailureListener { e ->
            Log.d("FirestoreFailure", e.localizedMessage)
            }
        }
        return mSearchSettings.settingsCompleted
        // todo - add on success listener - onSuccess - hide cardview and show map
    }

    interface UserSearchSettingsListener {
        fun searchSettingsValid(passed: Boolean)
    }
}