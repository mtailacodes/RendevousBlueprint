package com.mtailacodes.blueprintrendevouz.customViews

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DialogFragment
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.fragments.onboarding.OnBoardingAboutMe
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import java.util.*

/**
 * Created by matthewtaila on 2/13/18.
 */
@SuppressLint("ValidFragment")
open class DatePickerFragment(textView: TextView, userModel: RendevouzUserModel, activity: Activity) : DialogFragment(), DatePickerDialog.OnDateSetListener{

    var tv : TextView = textView
    var mUserModel = userModel
    var targetFrag = activity

    override fun onCreateDialog(savedInstanceState: Bundle?): DatePickerDialog {
        val c : Calendar = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        lateinit var datePickerDialog : DatePickerDialog
        if (Build.VERSION.SDK_INT < 23){
            datePickerDialog = DatePickerDialog(activity, AlertDialog.THEME_HOLO_LIGHT,this, year, month, day)
        } else {
            datePickerDialog = DatePickerDialog(activity, R.style.Theme_Holo_Light_Dialog, this, year, month, day)
        }
        return datePickerDialog
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        mUserModel.birthYear  = p1
        mUserModel.birthMonth  = p2 + 1
        mUserModel.birthDay  = p3
        tv.text = "$p3/$p2/$p1"
        sendBackResult()
    }

    interface EditNameDialogListener {
        fun onFinishEditDialog(mUserModel: RendevouzUserModel)
    }

    open fun sendBackResult() {
        var listener = targetFrag as EditNameDialogListener
            listener.onFinishEditDialog(mUserModel)
            dismiss()
    }



}