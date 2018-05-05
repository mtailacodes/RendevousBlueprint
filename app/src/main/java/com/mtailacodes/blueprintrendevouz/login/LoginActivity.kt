package com.mtailacodes.blueprintrendevouz.login

import android.animation.AnimatorSet
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import com.mtailacodes.blueprintrendevouz.Activity.MapSearchActivity
import com.mtailacodes.blueprintrendevouz.Activity.RxUserUtil
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityLoginBinding
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoginActivity : AppCompatActivity(), View.OnClickListener, LoginContract.View{

    //animation variables
    var onEnterAnimationViewsList = ArrayList<View>()
    var signInViewsList = ArrayList<View>()

    lateinit var mBinding: ActivityLoginBinding
    var readyForSignIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setOnClickListeners()
        LoginPresenter().attachView(this)

        // hide navigationBar
        var decorView = window.decorView
        var screenOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
        decorView.systemUiVisibility = screenOptions
    }

    private fun setOnClickListeners() {
        mBinding.loginBtnGetStarted.setOnClickListener(this)
        mBinding.loginTvCreateUser.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id){
            R.id.login_btn_getStarted ->{
                if (!readyForSignIn){
                    showSignInViews()
                } else {
                    signInWithEmail()
                }
            }
            R.id.login_tv_createUser ->{
                var intent = Intent(this@LoginActivity, CreateUserActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showSignInViews() {

        var hideAppTitleAnimatorSet = AnimatorSet()

        // translate App Title
        var translateAppTitle = AnimationUtil.translateY(
                view = mBinding.loginTvAppTitle,
                translationYValue =  getTranslationDistance(getBottom(mBinding.loginTvAppTitle), mBinding.loginGd20),
                duration = 200)
        hideAppTitleAnimatorSet.play(translateAppTitle)

        // alpha out motto
        var alphaOutMotto = AnimationUtil.alpha(
                view = mBinding.loginTvAppMotto,
                alphaValue = 0f,
                interpolator = AccelerateInterpolator(),
                duration = 100)
        hideAppTitleAnimatorSet.play(alphaOutMotto)
        hideAppTitleAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        hideAppTitleAnimatorSet.start()

        // change button textview
        mBinding.loginBtnGetStarted.text = "Login"

        // staggered login views
        var showSignInAnimatorSet = AnimatorSet()

        for (view in signInViewsList){
            view.visibility = View.VISIBLE
            showSignInAnimatorSet.play(AnimationUtil.staggeredAnimatorSet(
                    view,
                    signInViewsList.indexOf(view),
                    start = -50f ))
        }

        var mFinalAnimatorSet = AnimatorSet()
        mFinalAnimatorSet.play(showSignInAnimatorSet).after(translateAppTitle)
        mFinalAnimatorSet.start()
        readyForSignIn = true
    }

    override val actions: Observable<LoginAction>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    private fun getTranslationDistance(bottom: Float, view2: View): Float {
        return (view2.bottom - bottom)
    }

    private fun getBottom(view: View): Float {
        return view.bottom.toFloat()
    }

    override fun generateViewsList() {
        onEnterAnimationViewsList.add(mBinding.loginTvAppTitle)
        onEnterAnimationViewsList.add(mBinding.loginTvAppMotto)
        onEnterAnimationViewsList.add(mBinding.loginBtnGetStarted)

        signInViewsList.add(mBinding.loginEtEmail)
        signInViewsList.add(mBinding.loginEtPassword)
        signInViewsList.add(mBinding.loginTvCreateUser)
    }

    override fun onEnterAnimation() {
        var onEnterAnimatorSet = AnimatorSet()
        for (view in onEnterAnimationViewsList){
            view.visibility = View.VISIBLE
            onEnterAnimatorSet.play(AnimationUtil.staggeredAnimatorSet(view,
                    onEnterAnimationViewsList.indexOf(view), -100f))
        }
        onEnterAnimatorSet.duration = 1000
        onEnterAnimatorSet.start()
    }

    private fun signInWithEmail() {
        val single = RxUserUtil().loginUserWithEmailAndPassword(email = mBinding.loginEtEmail.text.toString(),
                password = mBinding.loginEtPassword.text.toString())
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
