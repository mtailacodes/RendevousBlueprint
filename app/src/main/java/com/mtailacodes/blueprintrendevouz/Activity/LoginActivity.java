package com.mtailacodes.blueprintrendevouz.Activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.mtailacodes.blueprintrendevouz.R;
import com.mtailacodes.blueprintrendevouz.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Activity Variables
    private static final String TAG = "Login Activity Main";
    ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBinding.tvCreateNewUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_CreateNewUser:
                showCreateUserContainer();
                break;
        }
    }

    private void showCreateUserContainer() {

        float translationHeight = getResources().getDimension(R.dimen.createUserContainerHeight) - (mBinding.parentContainer.getBottom() - mBinding.tvCreateNewUser.getBottom());

        ObjectAnimator showUserContainer = ObjectAnimator.ofFloat(mBinding.clCreateNewUserContainer, View.TRANSLATION_Y, 0, - translationHeight);
        showUserContainer.setDuration(300);
        showUserContainer.setInterpolator(new AccelerateDecelerateInterpolator());
        showUserContainer.start();

    }
}
