package com.yy.codex.uikit.sample

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.yy.codex.foundation.NSLog
import com.yy.codex.uikit.*

/**
 * Created by adi on 17/2/13.
 */
class TestProgressView : UIView {
    constructor(context: Context, view: View) : super(context, view)
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun init() {
        super.init()

        // simple
        val s1 = UIProgressView(context)
        s1.frame = CGRect(10.0, 100.0, 300.0, 32.0)
        addSubview(s1)

        // configure appearance
        val s2 = UIProgressView(context)
        s2.frame = CGRect(10.0, 200.0, 200.0, 32.0)
        s2.tintColor = UIColor.greenColor
        addSubview(s2)

        // configure status
        val s3 = UIProgressView(context)
        s3.frame = CGRect(10.0, 300.0, 200.0, 32.0)
        s3.value = 0.1
        addSubview(s3)

        // onValueChanged
        s3.addBlock(Runnable {
            NSLog.log(s3.value)
        }, UIControl.Event.ValueChanged)
        postDelayed({
            s3.value = 0.8
        }, 1000)
        postDelayed({
            s3.value = 0.2
        }, 2000)
    }
}