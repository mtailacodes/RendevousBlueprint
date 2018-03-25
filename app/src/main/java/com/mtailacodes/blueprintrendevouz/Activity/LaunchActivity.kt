package com.mtailacodes.blueprintrendevouz.Activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.ActivityLaunchBinding
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by matthewtaila on 3/24/18.
 */
class LaunchActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mBinding : ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_launch)
        mBinding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id){
            R.id.btn_login ->{
                signInWithEmail()
            }
        }
    }

    private fun signInWithEmail() {
        val single = RxUserUtil().loginUserWithEmailAndPassword(email = mBinding.etUsernameInput.text.toString(),
                password = mBinding.etPasswordInput.text.toString())
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleSignInWithEmailAndPassword()
                }, { throwable ->
                    Log.d("SignInActivity", "Create user failed: ${throwable.message}")
                })
    }

    private fun handleSignInWithEmailAndPassword() {
        val intent =  Intent(this, MapSearchActivity::class.java)
        startActivity(intent)
        finish()
    }
}
