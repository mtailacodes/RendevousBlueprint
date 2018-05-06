package com.mtailacodes.blueprintrendevouz.login

import android.animation.AnimatorSet
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityCreateUserBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class CreateUserActivity :AppCompatActivity(), View.OnClickListener{

    lateinit var mBinding: ActivityCreateUserBinding

    // credential flags
    var emailPassed = false
    var emailErrorReason : String = ""
    var passwordPassed = false
    var passwordErrorReason : String = ""

    // views
    var onboardingFirstViewList = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_user)

        generateViewList()
        setOnClickListener()

    }

    private fun generateViewList() {
        onboardingFirstViewList.add(mBinding.createUserEtName)
        onboardingFirstViewList.add(mBinding.createUserClSpinnerContainer)
    }

    private fun checkPasswordInput() : Boolean {
        if (mBinding.createUserEtPassword.text.length >= 6){
            return true
        } else {
            if (mBinding.createUserEtPassword.text.isEmpty() || mBinding.createUserEtPassword.text.isNullOrBlank()){
                passwordErrorReason = resources.getString(R.string.createUser_password_empty)
                return false
            } else if (mBinding.createUserEtPassword.text.length < 6){
                passwordErrorReason = resources.getString(R.string.createUser_password_too_short)
                return false
            }
            return false
        }
    }

    private fun checkEmailInput() : Boolean {
        if (android.util.Patterns
                        .EMAIL_ADDRESS
                        .matcher(mBinding.createUserEtEmail.text)
                        .matches()) {
            return true
        } else {
            if (mBinding.createUserEtEmail.text.isEmpty() || mBinding.createUserEtEmail.text.isNullOrBlank() ){
                emailErrorReason = resources.getString(R.string.createUser_no_email_input)
                return false
            } else if (!android.util.Patterns
                            .EMAIL_ADDRESS
                            .matcher(mBinding.createUserEtEmail.text)
                            .matches()){
                emailErrorReason = resources.getString(R.string.createUser_invalide_email_input)
                return false
            }
            return false
        }
    }

    private fun setOnClickListener() {
        mBinding.createUserBtnGetStarted.setOnClickListener(this)
        mBinding.createUserSpnAge.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 1){
                    Log.i("Gender", "Male")
                } else if (position == 2){
                    Log.i("Gender", "Female")
                }
            }

        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.createUser_btn_getStarted -> {
                emailPassed = checkEmailInput()
                passwordPassed = checkPasswordInput()
                handleInput()
            }
        }
    }

    private fun handleInput() {
        if (emailPassed && passwordPassed){
            createUser()
        }else {
            if (!emailPassed){
                showErrorState(emailErrorReason)
                return
            }
            if (!passwordPassed){
                showErrorState(passwordErrorReason)
                return
            }
        }
    }

    private fun createUser() {
        var observable = Observable.create<Any> {
            it -> FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                // todo show loading animation
                "${mBinding.createUserEtEmail.text}", "${mBinding.createUserEtPassword.text}")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // todo - hide loading animation
                            showOnBoardingViews()

                        } else {
                            showErrorState("We could not register your account at the moment, please try again shortly")
                        }
                    }
            }
        observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

    private fun showOnBoardingViews() {

        var adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, R.layout.gender_select_item_list)
        adapter.setDropDownViewResource(R.layout.gender_select_item_list)

        mBinding.createUserSpnAge.adapter = GenderSpinnerAdapter(
                adapter,
                R.layout.gender_select_item_list_selected,
                this)

        mBinding.createUserEtEmail.visibility = View.GONE
        mBinding.createUserEtPassword.visibility = View.GONE

        var onboardingFirstList = AnimatorSet()

        for (v in onboardingFirstViewList){
            v.visibility = View.VISIBLE
            onboardingFirstList.play(AnimationUtil.staggeredAnimatorSet(
                    v,
                    onboardingFirstViewList.indexOf(v),
                    -30f))
        }
        onboardingFirstList.start()
    }

    private fun showErrorState(errorString: String) {

        mBinding.createUserTvErrorMessage.text = errorString

        var mErrorViewAnimatorSet = AnimatorSet()
        var translateErrorViewUp = AnimationUtil.translateY(
                mBinding.createUserClErrorContainer,
                interpolator = AccelerateDecelerateInterpolator(),
                translationYValue = -(resources.getDimension(R.dimen.createUser_error_container_height) - 20))

        var translateErrorViewDown = AnimationUtil.translateY(
                mBinding.createUserClErrorContainer,
                interpolator = AccelerateDecelerateInterpolator(),
                startDelay = 3000,
                translationYValue = 0f)

        var translateButtonViewUp = AnimationUtil.translateY(
                mBinding.createUserBtnGetStarted,
                interpolator = AccelerateDecelerateInterpolator(),
                translationYValue = -(resources.getDimension(R.dimen.createUser_error_container_height) - 20))

        var translateEButtonViewDown = AnimationUtil.translateY(
                mBinding.createUserBtnGetStarted,
                interpolator = AccelerateDecelerateInterpolator(),
                startDelay = 3000,
                translationYValue = 0f)

        mErrorViewAnimatorSet.playTogether(
                translateErrorViewDown,
                translateErrorViewUp,
                translateButtonViewUp,
                translateEButtonViewDown)

        mErrorViewAnimatorSet.duration = 300
        mErrorViewAnimatorSet.interpolator = OvershootInterpolator(1.5f)
        mErrorViewAnimatorSet.start()

    }

}

