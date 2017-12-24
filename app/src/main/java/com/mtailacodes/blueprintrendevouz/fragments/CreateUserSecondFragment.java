package com.mtailacodes.blueprintrendevouz.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mtailacodes.blueprintrendevouz.MyApplication;
import com.mtailacodes.blueprintrendevouz.R;
import com.mtailacodes.blueprintrendevouz.Util.Tags;
import com.mtailacodes.blueprintrendevouz.databinding.FragmentCreateUserSecondBinding;
import com.mtailacodes.blueprintrendevouz.models.user.user.ParentUser;

import io.reactivex.functions.Consumer;

/**
 * Created by matthewtaila on 12/11/17.
 */

public class CreateUserSecondFragment extends Fragment implements View.OnClickListener{

    private FirebaseUser mFirebaseUser;
    FragmentCreateUserSecondBinding mBinding;
    FirebaseAuth mAuth;


    public static CreateUserSecondFragment newInstance(FirebaseUser currentUser) {
        CreateUserSecondFragment mFragment = new CreateUserSecondFragment();
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        ((MyApplication) getActivity().getApplication())
                .bus()
                .toObservable()
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (object instanceof ParentUser){
                            Log.i("Second Parent", String.valueOf(((ParentUser) object).getmFirebaseUser().getEmail()));
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_user_second, container, false);
        setOnClickListeners();
        setRangeBarListener();
        return mBinding.getRoot();
    }

    private void setOnClickListeners() {
        mBinding.btnCreateUserFinishButton.setOnClickListener(this);
    }

    private void setRangeBarListener() {
        mBinding.rbAgeRangeBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mBinding.tvMinimumAge.setText(String.valueOf(minValue));
                mBinding.tvMaximumAge.setText(String.valueOf(maxValue));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_CreateUserFinishButton:
                ((MyApplication) getActivity().getApplication())
                        .bus()
                        .send(Tags.CREATE_USER_STEP_TWO_SUCCESS);
                break;
        }
    }
}
