package com.yy.codex.uikit.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Scroller;

import com.yy.codex.uikit.CGPoint;
import com.yy.codex.uikit.UIGestureRecognizer;
import com.yy.codex.uikit.UIView;

/**
 * Created by it on 17/1/10.
 */

public class MyMainActivity extends AppCompatActivity {

    UIView view = null;
    Button button;
    Button moveButton;

    Scroller mScroller;

    TestLayout testLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new UIView(this);
        setContentView(R.layout.activity_main);
//        view = (UIView)findViewById(R.id.testUIView);
//        button = (Button)findViewById(R.id.testButton);
//        moveButton = (Button)findViewById(R.id.testMoveButton);
//
//        mScroller=new Scroller(this);
//
//        testLayout = (TestLayout)findViewById(R.id.testMyLayout);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                testLayout.beginScroll();
//            }
//        });
//
//        UITapGestureRecognizer tap = new UITapGestureRecognizer(this, "ttt:");
//        view.addGestureRecognizer(tap);
    }

    public void ttt(UIGestureRecognizer gestureRecognizer) {
        CGPoint point = gestureRecognizer.locationInView(view);
        moveButton.scrollBy(30, 0);
    }
}
