package com.mtailacodes.blueprintrendevouz.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mtailacodes.blueprintrendevouz.viewpagerAdapter.CreateUserViewPagerAdapter;
import com.mtailacodes.blueprintrendevouz.MyApplication;
import com.mtailacodes.blueprintrendevouz.R;
import com.mtailacodes.blueprintrendevouz.Util.Tags;
import com.mtailacodes.blueprintrendevouz.databinding.ActivityLoginBinding;

import io.reactivex.functions.Consumer;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Activity Variables
    private static final String TAG = "Login Activity Main";
    ActivityLoginBinding mBinding;
    private int createUserContainerHeight;

    // User variables
    private FirebaseAuth mAuthUser;
    FirebaseUser currentUser;

    //Create user variables
    private boolean createUserContainerRevealed = false;
    CreateUserViewPagerAdapter mCreateUserViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuthUser = FirebaseAuth.getInstance();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        createUserContainerHeight = metrics.heightPixels;

        mBinding.clCreateNewUserContainer.animate().translationY(createUserContainerHeight - 160).setDuration(0).start();

        setOnClickListeners();
        setupCreateUserViewPager();

        ((MyApplication) getApplication())
                .bus()
                .toObservable()
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {


                        if (object instanceof String) {
                            switch (String.valueOf(object)){
                                case Tags.CREATE_USER_STEP_ONE_SUCCESS:

                                    break;
                                case Tags.CREATE_USER_STEP_TWO_SUCCESS:
                                    mBinding.vpCreateUserViewPager.setCurrentItem(2);
                                    mBinding.siCreateUserStepIndicator.setCurrentStep(2);
                            }

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuthUser.getCurrentUser();
        // todo - if mAuth user is null - show the sign in views and the create user views
        // todo - if mAuth user is not null - show animation sign in / sign out option / hide the create account option

    }

    private void setupCreateUserViewPager() {
        // disable Scrolling
        mBinding.vpCreateUserViewPager.setPagingEnabled(false);

        // viewpager adapter
        mCreateUserViewPagerAdapter = new CreateUserViewPagerAdapter(getSupportFragmentManager(), currentUser);
        mBinding.vpCreateUserViewPager.setAdapter(mCreateUserViewPagerAdapter);

        // viewpager step indicator setup
        mBinding.siCreateUserStepIndicator.setViewPager(mBinding.vpCreateUserViewPager);
        mBinding.siCreateUserStepIndicator.setStepCount(2);
        mBinding.siCreateUserStepIndicator.setCurrentStep(0);
    }


    private void setOnClickListeners() {

        mBinding.tvCreateNewUser.setOnClickListener(this);
        mBinding.ivCreateUserArrowReveal.setOnClickListener(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_CreateNewUser:
                if (!createUserContainerRevealed){
                    showCreateUserContainer();
                }
                break;
            case R.id.iv_CreateUserArrowReveal:
                if (createUserContainerRevealed){
                    hideCreateUserContainer();
                }
        }
    }

    private void hideCreateUserContainer() {
        ObjectAnimator hideUserContainer = ObjectAnimator.ofFloat(mBinding.clCreateNewUserContainer, View.TRANSLATION_Y, createUserContainerHeight - 160);
        hideUserContainer.setDuration(300);
        hideUserContainer.setInterpolator(new AccelerateDecelerateInterpolator());
        hideUserContainer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                createUserContainerRevealed = false;
                mBinding.ivCreateUserArrowReveal.setImageResource(R.drawable.create_user_up_arrow); //todo think about transforming the path for this animation
                Log.i(TAG, String.valueOf(createUserContainerRevealed));
            }
        });
        hideUserContainer.start();
    }

    private void showCreateUserContainer() {
        mBinding.ivCreateUserArrowReveal.setOnClickListener(this);
        ObjectAnimator showUserContainer = ObjectAnimator.ofFloat(mBinding.clCreateNewUserContainer, View.TRANSLATION_Y,  0);
        showUserContainer.setDuration(300);
        showUserContainer.setInterpolator(new AccelerateDecelerateInterpolator());
        showUserContainer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBinding.ivCreateUserArrowReveal.setImageResource(R.drawable.createa_user_down_arrow); //todo think about transforming the path for this animation
                createUserContainerRevealed = true;
                Log.i(TAG, String.valueOf(createUserContainerRevealed));
            }
        });
        showUserContainer.start();



    }
}
