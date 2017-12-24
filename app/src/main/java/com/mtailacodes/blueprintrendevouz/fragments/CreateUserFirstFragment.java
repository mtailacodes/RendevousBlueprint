package com.mtailacodes.blueprintrendevouz.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mtailacodes.blueprintrendevouz.MyApplication;
import com.mtailacodes.blueprintrendevouz.R;
import com.mtailacodes.blueprintrendevouz.Util.Tags;
import com.mtailacodes.blueprintrendevouz.databinding.FragmentCreateUserSourceBinding;
import com.mtailacodes.blueprintrendevouz.models.user.user.ParentUser;

/**
 * Created by matthewtaila on 12/2/17.
 */

public class CreateUserFirstFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static final String TAG = "CREATE_USER_FRAG";
    private static FirebaseAuth mAuth;

    FragmentCreateUserSourceBinding mBinding;

    // create user credentials
    private String gender, emailCredential, nameCredential, userPassword;
    private boolean emailFieldPass = false;
    private boolean nameFieldEmpty = true;
    private boolean passwordFieldEmpty = true;
    private boolean genderFieldEmpty = true;
    private ParentUser mParentUser = null;

    public static CreateUserFirstFragment newInstance(FirebaseUser mCurrentUser) {
        CreateUserFirstFragment mFragment = new CreateUserFirstFragment();
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("mAuth", String.valueOf(mAuth));
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_user_source, container, false);
        setupOnclickListeners();
        // todo - use rxJava to subscribe to the email address text field - listen out for the text entry and see if it matches the credentials of an email address - if it does, then animate the editText view
        return mBinding.getRoot();
    }

    private void setupOnclickListeners() {
        mBinding.tvCreateUserMale.setOnClickListener(this);
        mBinding.tvCreateUserFemale.setOnClickListener(this);
        mBinding.btnCreateUserStepOneButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_CreateUserMale:
                mBinding.tvCreateUserMale.setBackground(getResources().getDrawable(R.drawable.create_user_application_button));
                mBinding.tvCreateUserFemale.setBackground(getResources().getDrawable(R.drawable.login_rounded_edit_text_fields));
                gender = "Male";
                break;
            case R.id.tv_CreateUserFemale:
                mBinding.tvCreateUserFemale.setBackground(getResources().getDrawable(R.drawable.create_user_application_button));
                mBinding.tvCreateUserMale.setBackground(getResources().getDrawable(R.drawable.login_rounded_edit_text_fields));
                gender = "Female";
                break;
            case R.id.btn_CreateUserStepOneButton:
                emailCredential = mBinding.etCreateUserEmailAddress.getText().toString(); // todo need to also include if correct email format is included
                nameFieldEmpty = checkNameCredentials(mBinding.etCreateUserName.getText().toString());
                passwordFieldEmpty = checkPasswordCredentials(mBinding.etCreateUserName.getText().toString());
                genderFieldEmpty = checkGenderCredentials(gender);


                if (!emailFieldPass){
                    if (!emailCredential.isEmpty()){
                        if (!isEmailValid(emailCredential)){
                            Toast.makeText(getContext(), Tags.getEmailFormatIncorrect(), Toast.LENGTH_SHORT).show();
                            emailFieldPass = false;
                            break;
                        } else {
                            emailFieldPass = true;
                        }
                    } else {
                        emailFieldPass = false;
                        Toast.makeText(getContext(), Tags.getNoEmailInput(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (nameFieldEmpty){
                    Toast.makeText(getContext(), Tags.getNoUsernameInput(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, Tags.getNoEmailInput());
                    break;
                }

                if (passwordFieldEmpty){
                    Toast.makeText(getContext(), Tags.NO_PASSWORD_INPUT, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, Tags.NO_PASSWORD_INPUT);
                    break;
                }

                if (genderFieldEmpty){
                    Toast.makeText(getContext(), Tags.getNoGenderInput(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, Tags.getNoGenderInput());
                    break;
                }

                if (emailFieldPass && !genderFieldEmpty && !nameFieldEmpty && !passwordFieldEmpty){
                    mParentUser = setParentUserCredentials(nameCredential, gender);
                    createUser(emailCredential, mBinding.etCreateUserPassword.getText().toString());
                    break;
            }
        }
    }

    private void createUser(String emailCredential, String userPassword) {
        mAuth.createUserWithEmailAndPassword(emailCredential, userPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i("Create User", "Success");
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            mParentUser.setmFirebaseUser(mUser);
                            String uuId = mParentUser.getmFirebaseUser().getUid();
                            Log.i("Parent", uuId);
                            // todo need to send user to the login activity (i think?)
                                ((MyApplication) getActivity().getApplication()).bus().send(mParentUser);
                        } else {
                            Log.i("Create User", "Failed");
                            Log.i("Create User", task.getException().toString());
                        }
                    }
                });
    }
    private boolean checkPasswordCredentials(String s) {
        nameCredential = s;
        return nameCredential.isEmpty();
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private ParentUser setParentUserCredentials(String nameCredential,  String gender) {
        ParentUser mParentUser = new ParentUser(nameCredential, gender);
        return mParentUser;
    }

    private boolean checkGenderCredentials(String gender) {
        if (gender != null){
            return gender.isEmpty();
        }
        return true;
    }

    private boolean checkNameCredentials(String s) {
        nameCredential = s;
        return nameCredential.isEmpty();
    }


}
