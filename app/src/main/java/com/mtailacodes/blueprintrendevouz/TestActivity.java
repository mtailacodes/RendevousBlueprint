package com.mtailacodes.blueprintrendevouz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by matthewtaila on 1/5/18.
 */
public class TestActivity extends AppCompatActivity {

    TextView texture;
    Button button;
    int x = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        texture = (TextView) findViewById(R.id.textView);
        button = findViewById(R.id.button);



        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = MotionEventCompat.getActionMasked(motionEvent);

                if (motionEvent.getPointerCount() == 2){
                    Log.d("number", String.valueOf(motionEvent.getPointerCount()));

                    switch(action) {
                        case (MotionEvent.ACTION_POINTER_DOWN):
                        case (MotionEvent.ACTION_MOVE) :
                            break;
                        case (MotionEvent.ACTION_POINTER_UP):
                            x --;
                            texture.setText(String.valueOf(x));

                            return true;
                        case (MotionEvent.ACTION_UP) :
                        case (MotionEvent.ACTION_CANCEL) :
                        case (MotionEvent.ACTION_OUTSIDE) :
                            break;
                        case (MotionEvent.ACTION_DOWN) :
                            break;
                    }
                } else if (motionEvent.getPointerCount() == 1){
                    switch(action) {
                        case (MotionEvent.ACTION_DOWN) :
                            x ++;
                            texture.setText(String.valueOf(x));
                            return true;
                        case (MotionEvent.ACTION_POINTER_1_DOWN):
                        case (MotionEvent.ACTION_MOVE) :
                        case (MotionEvent.ACTION_POINTER_1_UP):
                        case (MotionEvent.ACTION_UP) :
                        case (MotionEvent.ACTION_CANCEL) :
                        case (MotionEvent.ACTION_OUTSIDE) :
                            break;
                    }
                }
                return false;
            }
        });

    }

    private void updateText(int i) {
    }

}
