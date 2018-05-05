package com.mtailacodes.blueprintrendevouz.fragments

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
import com.mtailacodes.blueprintrendevouz.databinding.FragmentSearchSettingsBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 1/20/18.
 */
class SearchSettingsFragment: Fragment(){

    private lateinit var mBinding : FragmentSearchSettingsBinding
    private lateinit var mSearchSettings: UserSearchSettings

    companion object {
        fun newInstance(searchSettings: UserSearchSettings): SearchSettingsFragment{
            val mFragment = SearchSettingsFragment()
            var args = Bundle()
            args.putParcelable("SearchSettings", searchSettings)
            mFragment.arguments = args
            return mFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSearchSettings = arguments!!.getParcelable("SearchSettings")

        (activity!!.application as MyApplication)
                .bus()
                .toObservable()
                .subscribe { `object` ->
                    if (`object` is String) {
                        when (`object`.toString()) {
                            "SAVE SEARCH SETTINGS" ->{
                                saveSearchSettings()
                            }
                        }
                    }
                }
    }

    private fun saveSearchSettings() {
        when{
            mBinding.rbMale.isChecked ->{
                mSearchSettings.sexIntereset = "Male"
            }
            mBinding.rbFemale.isChecked ->{
                mSearchSettings.sexIntereset = "Female"
            }
        }

        mSearchSettings.ageRangeMin = Integer.parseInt(mBinding.tvMinAge.text.toString())
        mSearchSettings.ageRangeMax= Integer.parseInt(mBinding.tvMaxAge.text.toString())

        storeSearchSettings()
    }

    private fun storeSearchSettings() {
        var uuID = FirebaseAuth.getInstance().currentUser!!.uid
        var mFirestore = RxUserUtil().UserSettingsCollectionReference(uuID)
        mFirestore .document(uuID).set(mSearchSettings)
                .addOnSuccessListener{ _ ->
                    (activity!!.application as MyApplication)
                            .bus()
                            .send("User Search Settings Stored")
                }.addOnFailureListener { e ->
            Log.d("FirestoreFailure", e.localizedMessage)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_settings, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRangeBar() // age range bar listener
        setupSearchSettingsStates()
    }

    private fun setupSearchSettingsStates() {
        when (mSearchSettings.sexIntereset){
            "Male" ->{
                mBinding.rbMale.isChecked = true
                mBinding.rbFemale.isChecked = false
            }
            "Female" ->{
                mBinding.rbMale.isChecked = false
                mBinding.rbFemale.isChecked = true
            }
        }
        mBinding.ageRangeBar.setMinStartValue(mSearchSettings.ageRangeMin.toFloat()).apply()
        mBinding.ageRangeBar.setMaxStartValue(mSearchSettings.ageRangeMax.toFloat()).apply()
    }

    // age range bar listener
    private fun setupRangeBar() {
        mBinding.ageRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            mBinding.tvMinAge.text = minValue.toString()
            mBinding.tvMaxAge.text = maxValue.toString()
        }
    }
}