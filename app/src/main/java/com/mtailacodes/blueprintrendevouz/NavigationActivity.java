package com.mtailacodes.blueprintrendevouz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mtailacodes.blueprintrendevouz.Activity.LoginActivity;
import com.mtailacodes.blueprintrendevouz.Activity.MapSearchActivity;
import com.mtailacodes.blueprintrendevouz.Activity.SignInActivity;
import com.mtailacodes.blueprintrendevouz.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Navigation Activity";
    static final int REQUEST_IMAGE_CAPTURE = 1;

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
        mBinding.startActivityForREsils.setOnClickListener(this);
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
            case R.id.startActivityForREsils:
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }

                Intent intent2 = new Intent(this, MapSearchActivity.class);
                startActivity(intent2);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");



                int newWidth = mBinding.pictureExample.getMeasuredWidth();
                int newHeight = mBinding.pictureExample.getMeasuredHeight();
                Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

                float ratioX = newWidth / (float) imageBitmap.getWidth();
                float ratioY = newHeight / (float) imageBitmap.getHeight();
                float middleX = newWidth / 2.0f;
                float middleY = newHeight / 2.0f;

                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(imageBitmap, middleX - imageBitmap.getWidth() / 2, middleY - imageBitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            mBinding.pictureExample.setImageBitmap(scaledBitmap);

        }
    }
}
