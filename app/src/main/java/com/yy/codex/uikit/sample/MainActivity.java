package com.yy.codex.uikit.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yy.codex.uikit.CALayer;
import com.yy.codex.uikit.CGRect;
import com.yy.codex.uikit.NSLog;
import com.yy.codex.uikit.UIGestureRecognizerState;
import com.yy.codex.uikit.UILabel;
import com.yy.codex.uikit.UILongPressGestureRecognizer;
import com.yy.codex.uikit.UITapGestureRecognizer;
import com.yy.codex.uikit.UIView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TestView(this));
    }

}

class TestView extends UIView {

    public TestView(Context context, View view) {
        super(context, view);
        init();
    }

    public TestView(Context context) {
        super(context);
        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        final UIView redView = new UIView(getContext());
        redView.setWantsLayer(true);
        redView.setFrame(new CGRect(88, 88, 88, 88));
        redView.getLayer().setBackgroundColor(Color.BLACK);
        redView.getLayer().setCornerRadius(44.0);
        redView.setUserInteractionEnabled(true);

//        UITapGestureRecognizer tapGestureRecognizer = new UITapGestureRecognizer(this, "onTap:");
//        redView.addGestureRecognizer(tapGestureRecognizer);

        UILongPressGestureRecognizer longPressGestureRecognizer = new UILongPressGestureRecognizer(this, "onLongPressed:");
        redView.addGestureRecognizer(longPressGestureRecognizer);

        redView.addGestureRecognizer(new UITapGestureRecognizer(new Runnable() {
            @Override
            public void run() {
                redView.getLayer().setBackgroundColor(Color.RED);
            }
        }));

        addSubview(redView);
    }

    public void onTap(UITapGestureRecognizer tapGestureRecognizer) {
        final UIView redView = tapGestureRecognizer.getView();
        if (redView != null) {
                UIView.animateWithSpring(new Runnable() {
                    @Override
                    public void run() {
                        redView.setFrame(new CGRect(44, 44, 88 * 2, 88 * 2));
                        redView.getLayer().setCornerRadius(88.0);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        NSLog.log(redView);
                    }
                });
        }
    }

    public void onLongPressed(UILongPressGestureRecognizer longPressGestureRecognizer) {
        final UIView redView = longPressGestureRecognizer.getView();
        if (redView != null) {
            if (longPressGestureRecognizer.getState() == UIGestureRecognizerState.Began) {
                redView.getLayer().setBackgroundColor(Color.GRAY);
            }
            else if (longPressGestureRecognizer.getState() == UIGestureRecognizerState.Changed) {
                redView.getLayer().setBackgroundColor(Color.GREEN);
                NSLog.log(longPressGestureRecognizer.location());
            }
            else if (longPressGestureRecognizer.getState() == UIGestureRecognizerState.Ended) {
                redView.getLayer().setBackgroundColor(Color.YELLOW);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println(event);
        return super.onTouchEvent(event);
    }

}
