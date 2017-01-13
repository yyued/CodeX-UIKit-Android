package com.yy.codex.uikit;

import android.support.annotation.NonNull;

/**
 * Created by cuiminghui on 2017/1/11.
 */

public class UITapGestureRecognizer extends UIGestureRecognizer {

    private UITouch[] startTouches;

    public int numberOfTapsRequired = 1;
    public int numberOfTouchesRequired = 1;

    public UITapGestureRecognizer(@NonNull Object target, @NonNull String selector) {
        super(target, selector);
    }

    public UITapGestureRecognizer(@NonNull Runnable triggerBlock) {
        super(triggerBlock);
    }

    @Override
    public void touchesBegan(@NonNull UITouch[] touches, @NonNull UIEvent event) {
        super.touchesBegan(touches, event);
        int acceptedTouches = 0;
        for (int i = 0; i < touches.length; i++) {
            if (touches[i].getTapCount() == numberOfTapsRequired) {
                acceptedTouches++;
            }
        }
        if (acceptedTouches >= numberOfTouchesRequired) {
            startTouches = touches;
        }
        else {
            mState = UIGestureRecognizerState.Failed;
        }
    }

    @Override
    public void touchesMoved(@NonNull UITouch[] touches, @NonNull UIEvent event) {
        super.touchesMoved(touches, event);
        if (moveOutOfBounds(touches)) {
            mState = UIGestureRecognizerState.Failed;
        }
    }

    @Override
    public void touchesEnded(@NonNull UITouch[] touches, @NonNull UIEvent event) {
        super.touchesEnded(touches, event);
        if (!moveOutOfBounds(touches)) {
            mState = UIGestureRecognizerState.Ended;
            markOtherGestureRecognizersFailed(this);
            sendActions();
        }
    }

    private boolean moveOutOfBounds(@NonNull UITouch[] touches) {
        if (startTouches == null) {
            return true;
        }
        UIView view = getView();
        if (view == null) {
            return true;
        }
        double allowableMovement = 22.0;
        int accepted = 0;
        for (int i = 0; i < touches.length; i++) {
            CGPoint p0 = touches[i].locationInView(view);
            for (int j = 0; j < startTouches.length; j++) {
                CGPoint p1 = startTouches[j].locationInView(view);
                if (p0.inRange(allowableMovement, allowableMovement, p1)) {
                    accepted++;
                    break;
                }
            }
        }
        return accepted < startTouches.length;
    }

}
