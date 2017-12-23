package com.mtailacodes.blueprintrendevouz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mtailacodes.blueprintrendevouz.Activity.LoginActivity;
import com.mtailacodes.blueprintrendevouz.Activity.SignInActivity;
import com.mtailacodes.blueprintrendevouz.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Navigation Activity";

    ActivityNavigationBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_navigation);
        setOnclickListener();

    }

    private void setOnclickListener() {
        mBinding.signInActivityButton.setOnClickListener(this);
        mBinding.signInActivityButtonKotlin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.signInActivityButton:
                Log.i(TAG, "Sign in Activity");
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.signInActivityButtonKotlin:
                Log.i(TAG, "Sign in Activity");
                Intent newIntent = new Intent(this, SignInActivity.class);
                startActivity(newIntent);
                break;

        }
    }
}
