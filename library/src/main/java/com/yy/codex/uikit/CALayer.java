package com.yy.codex.uikit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;


/**
 * Created by cuiminghui on 2017/1/3.
 */

public class CALayer {

    private CGRect frame = new CGRect(0, 0, 0, 0);

    private int backgroundColor;

    private float cornerRadius; // default 0
    private float borderWidth;  // default 0
    private int borderColor = Color.BLACK;  // default #000

    private float shadowX = 2.0f;
    private float shadowY = 2.0f;
    private float shadowRadius;
    private int shadowColor = Color.BLACK;

    private Bitmap bitmap;
    private int bitmapGravity = GRAVITY_SCALE_TO_FILL;

    private CALayer superLayer;
    private ArrayList<CALayer> subLayers = new ArrayList<CALayer>();

    /* const imageGravity */

    public static final int GRAVITY_SCALE_TO_FILL = 0x01;
    public static final int GRAVITY_SCALE_ASCEPT_FIT = 0x02;
    public static final int GRAVITY_SCALE_ASCEPT_FILL = 0x03;
    public static final int GRAVITY_TOP = 0x04;
    public static final int GRAVITY_LEFT = 0x05;
    public static final int GRAVITY_BOTTOM = 0x06;
    public static final int GRAVITY_RIGHT = 0x07;
    public static final int GRAVITY_CENRER = 0x08;

    /* category CALayer Constructor */

    public CALayer() {}

    public CALayer(CGRect frame) {
        this.frame = frame;
    }

    /* category CALayer Hierarchy */

    public CALayer getSuperLayer() {
        return superLayer;
    }

    public ArrayList<CALayer> getSubLayers() {
        return subLayers;
    }

    public void removeFromSuperView(){

    }

    public void addSubLayer(CALayer layer){
        layer.superLayer = this;
        subLayers.add(layer);
    }

    public void insertSubLayerAtIndex(CALayer subLayer, int index){

    }

    public void insertBelowSubLayer(CALayer subLayer, CALayer siblingSubview){
        int idx = subLayers.indexOf(siblingSubview);
        if (idx > -1){
            subLayers.add(idx, subLayer);
        }
    }

    public void insertAboveSubLayer(CALayer subLayer, CALayer siblingSubview){
        int idx = subLayers.indexOf(siblingSubview);
        if (idx > -1){
            subLayers.add(idx + 1, subLayer);
        }
    }

    public void bringSubLayerToFront(){

    }

    public void bringSubLayerToBack(){

    }

    /* category CALayer Appearance */

    private void drawLayer(Canvas canvas, CGRect rect){
        // normalize layer's prop
        CGRect frame = this.frame;
        Float halfBorderW = borderWidth / 2.0f;

        // background & shadow
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);

        // border
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);

        if (bitmap != null){
//            canvas.drawBitmap(bitmap, (float) frame.origin.getX(), (float) frame.origin.getY(), paint);
            canvas.drawBitmap(bitmap, calcBitmapRect(bitmap, bitmapGravity), frame.toRectF(), paint);
            return;
        }

        if (cornerRadius > 0){
            if (shadowRadius > 0){
                paint.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
            }
            canvas.drawRoundRect(frame.toRectF(), cornerRadius, cornerRadius, paint);
            canvas.drawRoundRect(halfBorderW, halfBorderW, canvas.getWidth()-halfBorderW, canvas.getHeight()-halfBorderW, cornerRadius, cornerRadius, borderPaint);
        }
        else {
            if (shadowRadius > 0){
                paint.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
            }
            canvas.drawRect(frame.toRectF(), paint);
            canvas.drawRect(frame.toRectF(), borderPaint);
        }
    }

    private Rect calcBitmapRect(Bitmap bitmap, int bitmapGravity){
        // @Td
        double imageW = bitmap.getWidth();
        double imageH = bitmap.getHeight();
        double frameW = frame.getSize().getWidth();
        double frameH = frame.getSize().getHeight();
        double frameRatio = frameW / frameH;
        switch (bitmapGravity){
            case GRAVITY_SCALE_ASCEPT_FIT:
            case GRAVITY_SCALE_ASCEPT_FILL:
            case GRAVITY_SCALE_TO_FILL:
        }
        // return new Rect(bitmap.getWidth() / 3, bitmap.getHeight() / 3, bitmap.getWidth() / 3 * 2, bitmap.getHeight() / 3 * 2);
        return null;
    }

    public void drawRect(Canvas canvas, CGRect rect){
        drawLayer(canvas, rect);
        for (CALayer item : subLayers){ // @Td unnecessary draw
            item.drawLayer(canvas, rect);
        }
    }

    /* category CALayer Getter&Setter */

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public CALayer setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public CALayer setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        return this;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public CALayer setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public CALayer setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public float getShadowX() {
        return shadowX;
    }

    public float getShadowY() {
        return shadowY;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public CALayer setShadowX(float shadowX) {
        this.shadowX = shadowX;
        return this;
    }

    public CALayer setShadowY(float shadowY) {
        this.shadowY = shadowY;
        return this;
    }

    public CALayer setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    public CALayer setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public CGRect getFrame() {
        return frame;
    }

    public CALayer setFrame(CGRect frame) {
        this.frame = frame;
        return this;
    }

    public CALayer setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public CALayer setBitmapGravity(int bitmapGravity) {
        this.bitmapGravity = bitmapGravity;
        return this;
    }
}


