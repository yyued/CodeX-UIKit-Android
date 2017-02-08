package com.yy.codex.uikit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.yy.codex.foundation.NSLog
import com.yy.codex.foundation.lets

/**
 * Created by PonyCui_Home on 2017/2/7.
 */
class UIMenuController {

    enum class ArrowDirection {
        Default, // up or down based on screen location
        Up,
        Down,
        Left,
        Right,
    }

    private constructor()

    var menuVisible = false

    fun setMenuVisible(visible: Boolean, animated: Boolean) {
        menuVisible = visible
        lets(targetView, menuView, maskView) { targetView, menuView, maskView ->
            if (menuVisible) {
                val rootView = UIViewHelpers.findRootView(targetView) ?: return@lets
                resetLayouts(rootView)
                rootView.addSubview(maskView)
                rootView.addSubview(menuView)
                maskView.alpha = 1.0f
                menuView.alpha = 1.0f
            }
            else {
                maskView.alpha = 0.0f
                menuView.alpha = 0.0f
            }
        }
    }

    fun setTargetWithRect(targetRect: CGRect, targetView: UIView) {
        this.targetRect = targetRect
        this.targetView = targetView
        createMenuView()
    }

    var arrowDirection: ArrowDirection = ArrowDirection.Default
        set(value) {
            field = value
            createMenuView()
        }

    var menuItems: List<UIMenuItem> = listOf()
        set(value) {
            field = value
            createMenuView()
        }

    private var targetRect: CGRect? = null
    private var targetView: UIView? = null
    private var menuView: UIView? = null
    private var triangleView: UIView? = null
    private var maskView: UIView? = null

    private fun createMenuView() {
        targetView?.let {
            val targetView = it
            if (menuView == null) {
                menuView = UIView(it.context)
                menuView?.wantsLayer = true
                menuView?.layer?.backgroundColor = UIColor.blackColor.colorWithAlpha(0.75)
                menuView?.layer?.cornerRadius = 6.0
            }
            if (maskView == null) {
                maskView = UIView(it.context)
                maskView?.setBackgroundColor(UIColor.clearColor)
                maskView?.frame = CGRect(0.0, 0.0, UIScreen.mainScreen.bounds().width, UIScreen.mainScreen.bounds().height)
                maskView?.addGestureRecognizer(UITapGestureRecognizer(this, "onMaskViewTouched:"))
            }
            lets(menuView, maskView) { menuView, maskView ->
                menuView.subviews.forEach(UIView::removeFromSuperview)
                var x: Double = 0.0
                var height: Double = 36.0
                menuItems.map {
                    val button = UIButton(targetView.context)
                    button.addTarget(it.target, it.selector, UIControl.Event.TouchUpInside)
                    button.tintColor = UIColor.whiteColor
                    button.font = UIFont(14.0f)
                    button.setTitle(it.title, UIControl.State.Normal)
                    button.contentEdgeInsets = UIEdgeInsets(0.0, 12.0, 0.0, 12.0)
                    return@map button
                }.forEachIndexed { index, button ->
                    val contentSize = button.intrinsicContentSize()
                    button.tag = index
                    button.frame = CGRect(x, 0.0, contentSize.width, height)
                    x += contentSize.width
                    if (index < menuItems.size - 1) {
                        val separator = UIPixelLine(targetView.context)
                        separator.color = UIColor.whiteColor.colorWithAlpha(0.25)
                        separator.vertical = true
                        separator.frame = CGRect(x, 0.0, 1.0, height)
                        menuView.addSubview(separator)
                    }
                    menuView.addSubview(button)
                }
                menuView.frame = CGRect(0.0, 0.0, x, height)
            }
        }
    }

    private fun resetLayouts(absoluteView: UIView, targetDirection: ArrowDirection? = null) {
        lets(menuView, maskView, targetRect, targetView) { menuView, maskView, targetRect, targetView ->
            maskView.frame = CGRect(0.0, 0.0, absoluteView.frame.width, absoluteView.frame.height)
            when (targetDirection ?: arrowDirection) {
                ArrowDirection.Default -> {
                    triangleView?.removeFromSuperview()
                    val targetPoint = targetView.convertPoint(CGPoint((targetRect.x + targetRect.width) / 2.0, (targetRect.y + targetRect.height) / 2.0), absoluteView)
                    val x = Math.min(absoluteView.frame.width - menuView.frame.width, Math.max(0.0, targetPoint.x - menuView.frame.width / 2.0))
                    val y = Math.min(absoluteView.frame.height - menuView.frame.height, Math.max(0.0, targetPoint.y - menuView.frame.height))
                    if (y - 9.0 < 0.0) {
                        resetLayouts(absoluteView, ArrowDirection.Up)
                    }
                    else {
                        resetLayouts(absoluteView, ArrowDirection.Down)
                    }
                }
                ArrowDirection.Down -> {
                    triangleView?.removeFromSuperview()
                    val targetPoint = targetView.convertPoint(CGPoint((targetRect.x + targetRect.width) / 2.0, targetRect.y), absoluteView)
                    val x = Math.min(absoluteView.frame.width - menuView.frame.width, Math.max(0.0, targetPoint.x - menuView.frame.width / 2.0))
                    val y = Math.min(absoluteView.frame.height - menuView.frame.height, Math.max(0.0, targetPoint.y - menuView.frame.height))
                    menuView.frame = menuView.frame.setX(x).setY(y - 9.0)
                    triangleView = UIMenuViewTriangleView(targetView.context, ArrowDirection.Down)
                    triangleView?.let {
                        it.frame = CGRect(menuView.frame.x + menuView.frame.width / 2.0 - 8.0, menuView.frame.y + menuView.frame.height, 16.0, 9.0)
                        maskView.addSubview(it)
                    }
                }
                ArrowDirection.Up -> {
                    triangleView?.removeFromSuperview()
                    val targetPoint = targetView.convertPoint(CGPoint((targetRect.x + targetRect.width) / 2.0, targetRect.y + targetRect.height), absoluteView)
                    val x = Math.min(absoluteView.frame.width - menuView.frame.width, Math.max(0.0, targetPoint.x - menuView.frame.width / 2.0))
                    val y = Math.min(absoluteView.frame.height - menuView.frame.height, Math.max(0.0, targetPoint.y))
                    menuView.frame = menuView.frame.setX(x).setY(y + 9.0)
                    triangleView = UIMenuViewTriangleView(targetView.context, ArrowDirection.Up)
                    triangleView?.let {
                        it.frame = CGRect(menuView.frame.x + menuView.frame.width / 2.0 - 8.0, menuView.frame.y - 8.0, 16.0, 9.0)
                        maskView.addSubview(it)
                    }
                }
                ArrowDirection.Left -> {

                }
                ArrowDirection.Right -> {

                }
            }
        }
    }

    private fun onMaskViewTouched(sender: UITapGestureRecognizer) {
        setMenuVisible(false, true)
    }

    companion object {

        var sharedMenuController = UIMenuController()

    }

    inner private class UIMenuViewTriangleView(context: Context, val arrowDirection: ArrowDirection): UIView(context) {

        val drawingPaint = Paint()

        fun requestPath(): Path {
            val scale = UIScreen.mainScreen.scale().toFloat()
            when (arrowDirection) {
                ArrowDirection.Down -> {
                    val d = Path()
                    d.moveTo(0.0f * scale, 0.0f * scale)
                    d.lineTo(16.0f * scale, 0.0f * scale)
                    d.lineTo(8.0f * scale, 9.0f * scale)
                    d.lineTo(0.0f * scale, 0.0f * scale)
                    return d
                }
                ArrowDirection.Up -> {
                    val d = Path()
                    d.moveTo(8.0f * scale, 0.0f * scale)
                    d.lineTo(16.0f * scale, 9.0f * scale)
                    d.lineTo(0.0f * scale, 9.0f * scale)
                    d.lineTo(8.0f * scale, 0.0f * scale)
                    return d
                }
            }
            return Path()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            drawingPaint.color = UIColor.blackColor.colorWithAlpha(0.75).toInt()
            drawingPaint.isAntiAlias = true
            canvas.drawPath(requestPath(), drawingPaint)
        }

    }

}