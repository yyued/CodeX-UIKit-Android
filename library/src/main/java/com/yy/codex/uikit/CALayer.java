package com.yy.codex.uikit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;


/**
 * Created by cuiminghui on 2017/1/3.
 */

public class CALayer {

    /* layoutProps */

    @NonNull
    private CGRect frame = new CGRect(0, 0, 0, 0);

    /* styleProps */

    private UIColor backgroundColor = UIColor.clearColor;
    private double cornerRadius = 0.0;
    private double borderWidth = 0.0;
    private UIColor borderColor = UIColor.blackColor;
    private double shadowX = 2.0;
    private double shadowY = 2.0;
    private double shadowRadius = 0.0;
    private UIColor shadowColor = UIColor.blackColor;
    @Nullable
    private Bitmap bitmap = null;
    private int bitmapGravity = GRAVITY_SCALE_ASCEPT_FIT;
    private boolean clipToBounds = false;
    private boolean hidden = false;

    /* renderProps */

    private boolean needDisplay = false;
    private boolean newCanvasContext = false;
    @Nullable
    private CALayer mask = null; // not support

    /* hierarchyProps */

    private UIView view;
    private CALayer superLayer;
    @NonNull
    private ArrayList<CALayer> subLayers = new ArrayList<CALayer>();

    /* transformProp */
    private CGTransform[] transforms = null;

    /* scaledDensityProp */

    public static float scaledDensity = (float) UIScreen.mainScreen.scale();

    /* imageGravity const */

    public static final int GRAVITY_SCALE_TO_FILL = 0x01;
    public static final int GRAVITY_SCALE_ASCEPT_FIT = 0x02;
    public static final int GRAVITY_SCALE_ASCEPT_FILL = 0x03;
    public static final int GRAVITY_CENRER = 0x04;
    public static final int GRAVITY_TOP = 0x05;
    public static final int GRAVITY_TOP_LEFT = 0x06;
    public static final int GRAVITY_TOP_RIGHT = 0x07;
    public static final int GRAVITY_BOTTOM = 0x08;
    public static final int GRAVITY_BOTTOM_LEFT = 0x09;
    public static final int GRAVITY_BOTTOM_RIGHT = 0x0a;
    public static final int GRAVITY_LEFT = 0x0b;
    public static final int GRAVITY_RIGHT = 0x0c;

    /* category CALayer Constructor */

    public CALayer() {}

    public CALayer(@NonNull CGRect frame) {
        float x = (float) (frame.origin.getX());
        float y = (float) (frame.origin.getY());
        float w = (float) (frame.size.getWidth());
        float h = (float) (frame.size.getHeight());
        this.frame = new CGRect(x, y, w, h);
    }

    /* category CALayer Hierarchy */

    public CALayer getSuperLayer() {
        return superLayer;
    }

    @NonNull
    public CALayer[] getSubLayers() { return subLayers.toArray(new CALayer[subLayers.size()]); }

    public void removeFromSuperLayer(){
        if (this.superLayer != null){
            this.superLayer.subLayers.remove(this);
        }
    }

    public void addSubLayer(@NonNull CALayer layer){
        layer.removeFromSuperLayer();
        layer.superLayer = this;
        subLayers.add(layer);
    }

    public void insertSubLayerAtIndex(@NonNull CALayer subLayer, int index){
        subLayer.removeFromSuperLayer();
        if (index < 1){
            this.subLayers.add(0, subLayer);
        }
        else if (index > this.subLayers.size() - 1){
            this.subLayers.add(subLayer);
        }
        else {
            this.subLayers.add(index, subLayer);
        }
    }

    public void insertBelowSubLayer(@NonNull CALayer subLayer, CALayer siblingSubview){
        int idx = subLayers.indexOf(siblingSubview);
        if (idx > -1){
            subLayer.removeFromSuperLayer();
            subLayers.add(idx, subLayer);
        }
    }

    public void insertAboveSubLayer(@NonNull CALayer subLayer, CALayer siblingSubview){
        int idx = subLayers.indexOf(siblingSubview);
        if (idx > -1){
            subLayer.removeFromSuperLayer();
            subLayers.add(idx + 1, subLayer);
        }
    }

    public void replaceSubLayer(@NonNull CALayer subLayer, @NonNull CALayer newLayer){
        int idx = subLayers.indexOf(subLayer);
        if (idx > -1){
            subLayer.removeFromSuperLayer();
            insertSubLayerAtIndex(newLayer, idx);
        }
    }

    /* category CALayer Appearance */

    public void drawRect(@NonNull Canvas canvas, CGRect rect){
        if(this.askIfNeedDispaly()){
            this.resetNeedDisplayToFalse();
            drawAllLayers(canvas, rect);
        }
    }

    private void drawAllLayers(@NonNull Canvas canvas, CGRect rect){
        if (hidden){
            return;
        }
        if (isNewCanvasContext()){
            drawLayer(canvas, rect, true);
        }
        else {
            drawLayer(canvas, rect, false);
            for (CALayer item : subLayers){
                item.drawAllLayers(canvas, rect);
            }
        }
    }

    protected void drawLayer(@NonNull Canvas canvas, CGRect rect, boolean isDrawInNewCanvas){
        if (isDrawInNewCanvas){
            // create srcBitmap
            CGPoint origin = calcOrigin(this);
            Bitmap srcBitmap = Bitmap.createBitmap((int)(scaledDensity * (frame.size.getWidth()+origin.getX())), (int)(scaledDensity * (frame.size.getHeight()+origin.getY())), Bitmap.Config.ARGB_8888);
            Canvas canvasA = new Canvas(srcBitmap);
            drawLayersInCanvas(canvasA);

            // create maskBitmap
            Bitmap maskBitmap = Bitmap.createBitmap((int)(scaledDensity * (frame.size.getWidth()+origin.getX())), (int)(scaledDensity * (frame.size.getHeight()+origin.getY())), Bitmap.Config.ARGB_8888);
            Canvas canvasB = new Canvas(maskBitmap);
            Paint p3 = new Paint(Paint.ANTI_ALIAS_FLAG);
            canvasB.drawRoundRect(new CGRect(scaledDensity * origin.getX(), scaledDensity *origin.getY(), scaledDensity * frame.size.getWidth(), scaledDensity * frame.size.getHeight()).toRectF(), (float) cornerRadius, (float) cornerRadius, p3);

            // draw srcBitmap, and apply maskBitmap ifNeed
            Paint p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (this.transforms != null & this.transforms.length > 0){
                Matrix matrix = createMatrixFromTransforms(this.transforms);
                if (this.clipToBounds){
                    canvas.drawBitmap(maskBitmap, matrix, p2);
                    p2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                }
                canvas.drawBitmap(srcBitmap, matrix, p2);
            }
            else {
                canvas.drawBitmap(maskBitmap, 0, 0, p2);
                p2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(srcBitmap, 0, 0, p2);
            }
        }
        else {
            drawInCanvas(canvas);
        }
    }
    private Matrix createMatrixFromTransforms(CGTransform[] transforms){
        Matrix matrix = new Matrix();
        if (transforms == null || transforms.length == 0){
            return matrix;
        }
        RectF rectF = frame.toRectF();
        for (CGTransform transform : transforms){
            if (!transform.enable){
                continue;
            }
            if (transform instanceof CGTransformRotation){
                matrix.preRotate((float) ((CGTransformRotation) transform).angle, rectF.centerX() * (float) scaledDensity, rectF.centerY() * (float) scaledDensity);
            }
            else if (transform instanceof CGTransformTranslation){
                CGTransformTranslation translation = (CGTransformTranslation) transform;
                matrix.postTranslate((float) translation.tx, (float) translation.ty);
            }
            else if (transform instanceof CGTransformScale){
                CGTransformScale scale = (CGTransformScale)transform;
                matrix.postScale((float) scale.sx, (float) scale.sy, rectF.centerX() * (float) scaledDensity, rectF.centerY() * (float) scaledDensity);
            }
            else if (transform instanceof CGTransformMatrix){
                // @TODO
            }
        }
        return matrix;
    }

    private void drawLayersInCanvas(@NonNull Canvas canvas){
        drawInCanvas(canvas);
        for (CALayer item : subLayers){
            item.drawLayersInCanvas(canvas);
        }
    }

    protected void drawInCanvas(@NonNull Canvas canvas){
        // normalize layer's prop
        CGPoint calculatedOrigin = calcOrigin(this);
        CGPoint originScaled = new CGPoint(calculatedOrigin.getX() * scaledDensity, calculatedOrigin.getY() * scaledDensity);
        CGRect frameScaled = new CGRect(this.frame.getX() * scaledDensity, this.frame.getY() * scaledDensity, this.frame.getWidth() * scaledDensity, this.frame.getHeight() * scaledDensity);
        float halfBorderW = (float) borderWidth * scaledDensity / 2.0f;

        // background & shadow
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.backgroundColor.toInt());

        // visible
        if (hidden){
            return;
        }

        // background bitmap border
        if (cornerRadius > 0){
            if (shadowRadius > 0){
                paint.setShadowLayer((float) shadowRadius * scaledDensity, (float) shadowX * scaledDensity, (float) shadowY * scaledDensity, shadowColor.toInt());
            }
            canvas.drawRoundRect(frameScaled.shrinkToRectF(halfBorderW, originScaled), (float) cornerRadius * scaledDensity, (float) cornerRadius * scaledDensity, paint);

            if (bitmap != null){
                CGRect newFrame = new CGRect(originScaled.getX(), originScaled.getY(), frameScaled.size.getWidth(), frameScaled.size.getHeight());
                Paint p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                Bitmap maskBitmap = createRadiusMask(newFrame, cornerRadius * scaledDensity);
                canvas.drawBitmap(maskBitmap, 0, 0, p2);
                p2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                drawBitmap(canvas, newFrame, bitmap, bitmapGravity, p2);
            }

            if (borderWidth > 0){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) borderWidth * scaledDensity);
                paint.setColor(borderColor.toInt());
                canvas.drawRoundRect(frameScaled.shrinkToRectF(halfBorderW, originScaled), (float) cornerRadius * scaledDensity, (float) cornerRadius * scaledDensity, paint);
            }
        }
        else {
            if (shadowRadius > 0){
                paint.setShadowLayer((float) shadowRadius * scaledDensity, (float) shadowX * scaledDensity, (float) shadowY * scaledDensity, shadowColor.toInt());
            }
            canvas.drawRect(frameScaled.toRectF(originScaled), paint);

            if (bitmap != null){
                CGRect newFrame = new CGRect(originScaled.getX(), originScaled.getY(), frameScaled.size.getWidth(), frameScaled.size.getHeight());
                drawBitmap(canvas, newFrame, bitmap, bitmapGravity, paint);
            }

            if (borderWidth > 0){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) borderWidth * scaledDensity);
                paint.setColor(borderColor.toInt());
                canvas.drawRect(frameScaled.shrinkToRectF(halfBorderW, originScaled), paint);
            }
        }

        if (mask != null){
            // @TODO
        }
    }

    private Bitmap createRadiusMask(@NonNull CGRect rect, double radius){
        Bitmap maskBitmap = Bitmap.createBitmap((int)(rect.size.getWidth()+rect.origin.getX()), (int)(rect.size.getHeight()+rect.origin.getY()), Bitmap.Config.ARGB_8888);
        Canvas canvasB = new Canvas(maskBitmap);
        Paint p3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvasB.drawRoundRect(rect.toRectF(), (float) radius, (float) radius, p3);
        return maskBitmap;
    }

    private Bitmap createMask(@NonNull CALayer layer){
        CGRect rect = layer.getFrame();
        Bitmap maskBitmap = Bitmap.createBitmap((int)(rect.size.getWidth()+rect.origin.getX()), (int)(rect.size.getHeight()+rect.origin.getY()), Bitmap.Config.ARGB_8888);
        Canvas canvasB = new Canvas(maskBitmap);
        layer.drawInCanvas(canvasB);
        return maskBitmap;
    }

    private boolean askIfNeedDispaly(){
        return true;
    }

    private void resetNeedDisplayToFalse(){
        this.needDisplay = false;
        for (CALayer item : subLayers){
            item.resetNeedDisplayToFalse();
        }
    }

    private void drawBitmap(@NonNull Canvas canvas, @NonNull CGRect rect, @NonNull Bitmap bitmap, int bitmapGravity, Paint paint){
        double imageW = bitmap.getWidth();
        double imageH = bitmap.getHeight();
        double imageRatio = imageW / imageH;
        double frameW = rect.size.getWidth();
        double frameH = rect.size.getHeight();
        double frameRatio = frameW / frameH;
        double frameX = rect.origin.getX();
        double frameY = rect.origin.getY();
        CGRect imageRect = new CGRect(0, 0, imageW, imageH);
        CGRect frameRect = rect;

        switch (bitmapGravity){
            case GRAVITY_SCALE_TO_FILL:
                break;
            case GRAVITY_SCALE_ASCEPT_FIT:
                if (frameRatio > imageRatio){
                    double scaledFrameW = frameH * imageRatio;
                    frameRect = new CGRect(frameX + (frameW - scaledFrameW) / 2, frameY, scaledFrameW, frameH);
                }
                else {
                    double scaledH = frameW / imageRatio;
                    frameRect = new CGRect(frameX, frameY + (frameH - scaledH)/2, frameW, scaledH);
                }
                break;
            case GRAVITY_SCALE_ASCEPT_FILL:
                if (frameRatio > imageRatio){
                    double clipedImageH = imageW / frameRatio;
                    imageRect = new CGRect(0, (imageH - clipedImageH)/2, imageW, clipedImageH);
                }
                else {
                    double clipedImageW = imageH * frameRatio;
                    imageRect = new CGRect((imageW - clipedImageW) / 2, 0, clipedImageW, imageH);
                }
                break;
            case GRAVITY_CENRER:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX + (frameW - imageW) / 2, frameY + (frameH - imageH) / 2, imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect((imageW - frameW)/2, 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY+(frameH - imageH)/2, frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, (imageH - frameH)/2, imageW, frameH);
                    frameRect = new CGRect(frameX+(frameW - imageW)/2, frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect((imageW - frameW)/2, (imageH - frameH)/2, frameW, frameH);
                }
                break;
            case GRAVITY_TOP:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX+(frameW-imageW)/2, frameY, imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect((imageW-frameW)/2, 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY, frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, 0, imageW, frameH);
                    frameRect = new CGRect(frameX+(imageW-frameW)/2, frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect((imageW-frameW)/2, 0, frameW, frameH);
                }
                break;
            case GRAVITY_TOP_LEFT:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX, frameY, imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect(0, 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY, frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, 0, imageW, frameH);
                    frameRect = new CGRect(frameX, frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect(0, 0, frameW, frameH);
                }
                break;
            case GRAVITY_TOP_RIGHT:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX+(frameW-imageW), frameY, imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect((imageW-frameW), 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY, frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, 0, imageW, frameH);
                    frameRect = new CGRect(frameX+(frameW-imageW), frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect((imageW-frameW), 0, frameW, frameH);
                }
                break;
            case GRAVITY_BOTTOM:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX+(frameW-imageW)/2, frameY+(frameH-imageH), imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect((imageW-frameW)/2, 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH), frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, (imageH-frameH), imageW, frameH);
                    frameRect = new CGRect(frameX+(frameW-imageW)/2, frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect((imageW-frameW)/2, (imageH-frameH), frameW, frameH);
                }
                break;
            case GRAVITY_BOTTOM_LEFT:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH), imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect(0, 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH), frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, (imageH-frameH), imageW, frameH);
                    frameRect = new CGRect(frameX, frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect(0, (imageH-frameH), frameW, frameH);
                }
                break;
            case GRAVITY_BOTTOM_RIGHT:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX+(frameW-imageW), frameY+(frameH-imageH), imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect((imageW-frameW), 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH), frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, (imageH-frameH), imageW, frameH);
                    frameRect = new CGRect(frameX+(frameW-imageW), frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect((imageW-frameW), (imageH-frameH), frameW, frameH);
                }
                break;
            case GRAVITY_LEFT:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH)/2, imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect(0, 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH)/2, frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, (imageH-frameH)/2, imageW, frameH);
                    frameRect = new CGRect(frameX, frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect(0, 0, frameW, frameH);
                }
                break;
            case GRAVITY_RIGHT:
                if (frameW >= imageW && frameH >= imageH){
                    frameRect = new CGRect(frameX+(frameW-imageW), frameY+(frameH-imageH)/2, imageW, imageH);
                }
                else if (frameW < imageW && frameH >= imageH ){
                    imageRect = new CGRect((imageW-frameW), 0, frameW, imageH);
                    frameRect = new CGRect(frameX, frameY+(frameH-imageH)/2, frameW, imageH);
                }
                else if (frameH < imageH && frameW >= imageW) {
                    imageRect = new CGRect(0, (imageH-frameH)/2, imageW, frameH);
                    frameRect = new CGRect(frameX+(frameW-imageW), frameY, imageW, frameH);
                }
                else {
                    imageRect = new CGRect((imageW-frameW), (imageH-frameH)/2, frameW, frameH);
                }
                break;
        }

        canvas.drawBitmap(bitmap, imageRect.toRect(), frameRect.toRect(), paint);
    }

    /* category CALayer Getter&Setter */

    public void bindView(UIView view){
        this.view = view;
    }

    public UIColor getBackgroundColor() {
        return backgroundColor;
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public UIColor getBorderColor() {
        return borderColor;
    }

    public double getShadowX() {
        return shadowX;
    }

    public double getShadowY() {
        return shadowY;
    }

    public double getShadowRadius() {
        return shadowRadius;
    }

    public UIColor getShadowColor() {
        return shadowColor;
    }

    @NonNull
    public CGRect getFrame() {
        return frame;
    }

    public Boolean getClipToBounds() {
        return clipToBounds;
    }

    /*
        以下情况，在新画布绘制。
        1. 有 transform 属性时
        2. 有子节点 且 clipToBounds 时
     */
    public boolean isNewCanvasContext() {
        boolean result = (this.transforms != null && this.transforms.length > 0)
                || (this.getSubLayers().length > 0 && this.clipToBounds);
        return result;
    }

    public CGTransform[] getTransforms() {
        return transforms;
    }

    public void setNewCanvasContext(boolean newCanvasContext) {
        this.newCanvasContext = newCanvasContext;
    }

    @NonNull
    public CALayer setFrame(@NonNull CGRect frame) {
        float x = (float) frame.origin.getX();
        float y = (float) frame.origin.getY();
        float w = (float) frame.size.getWidth();
        float h = (float) frame.size.getHeight();
        CGRect newValue = new CGRect(x, y, w, h);
        if (!this.frame.equals(newValue)){
            this.frame = newValue;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setBitmap(Bitmap bitmap) {
        if (this.bitmap != bitmap){
            this.bitmap = bitmap;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setBitmapGravity(int bitmapGravity) {
        if (this.bitmapGravity != bitmapGravity){
            this.bitmapGravity = bitmapGravity;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setClipToBounds(Boolean clipToBounds) {
        if (this.clipToBounds != clipToBounds){
            this.clipToBounds = clipToBounds;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setHidden(boolean hidden) {
        if (this.hidden != hidden){
            this.hidden = hidden;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setBorderWidth(double borderWidth) {
        double oldValue = this.borderWidth;
        if (!doubleEqual(this.borderWidth, borderWidth)){
            this.borderWidth = borderWidth;
            this.setNeedDisplay(true);
            if (this.requestRootLayer().view != null) {
                UIView.animator.addAnimationState(this.requestRootLayer().view, "layer.borderWidth", oldValue, borderWidth);
            }
        }
        return this;
    }

    @NonNull
    public CALayer setBorderColor(UIColor borderColor) {
        if (this.borderColor != borderColor){
            this.borderColor = borderColor;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setCornerRadius(double cornerRadius) {
        if (!doubleEqual(this.cornerRadius, cornerRadius)){
            double oldValue = this.cornerRadius;
            this.cornerRadius = cornerRadius;
            this.setNeedDisplay(true);
            if (this.requestRootLayer().view != null) {
                UIView.animator.addAnimationState(this.requestRootLayer().view, "layer.cornerRadius", oldValue, cornerRadius);
            }
        }
        return this;
    }

    @NonNull
    public CALayer setBackgroundColor(UIColor backgroundColor) {
        if (this.backgroundColor != backgroundColor){
            this.backgroundColor = backgroundColor;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setShadowX(double shadowX) {
        if (!doubleEqual(this.shadowX, shadowX)){
            this.shadowX = shadowX;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setShadowY(double shadowY) {
        if (!doubleEqual(this.shadowY, shadowY)){
            this.shadowY = shadowY;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setShadowRadius(double shadowRadius) {
        if (!doubleEqual(this.shadowRadius, shadowRadius)){
            this.shadowRadius = shadowRadius;
            this.setNeedDisplay(true);
        }
        return this;
    }

    @NonNull
    public CALayer setShadowColor(UIColor shadowColor) {
        if (this.shadowColor != shadowColor){
            this.shadowColor = shadowColor;
            this.setNeedDisplay(true);
        }
        return this;
    }

    public void setTransforms(CGTransform[] transforms) {
        this.transforms = transforms;
    }

    public void setTransform(CGTransform a) {
        CGTransform[] tf = {a};
        this.transforms = tf;
    }

    public void setNeedDisplay(boolean needDisplay) {
        this.needDisplay = needDisplay;
        if (needDisplay){
            UIView view = this.requestRootLayer().view;
            if (view != null){
                view.invalidate();
            }
        }
    }

    @NonNull
    public CALayer setMask(CALayer mask) {
        if (this.mask != mask){
            this.mask = mask;
            this.setNeedDisplay(true);
        }
        return this;
    }

    /* category CALayer support method */

    @NonNull
    private CGPoint calcOrigin(@NonNull CALayer layer){
        double oriX = layer.frame.origin.getX();
        double oriY = layer.frame.origin.getY();
        CALayer p = layer.getSuperLayer();
        while (p != null){
//            if (p.isNewCanvasContext()) {
//                break;
//            }
            oriX += p.frame.origin.getX();
            oriY += p.frame.origin.getY();
            p = p.getSuperLayer();
        }
        return new CGPoint(oriX, oriY);
    }

    private CALayer requestRootLayer(){
        CALayer root = this;
        while (root.superLayer!=null){
            root = root.superLayer;
            if (root == null){
                return root;
            }
        }
        return root;
    }

    private boolean doubleEqual(Double a, Double b){
        if (Math.abs(a - b) < 0.01) {
            return true;
        }
        return false;
    }

    /* Animation */

    public void animate(@NonNull String aKey, float aValue) {
        if (aKey.equalsIgnoreCase("layer.cornerRadius")) {
            setCornerRadius(aValue);
        }
        else if (aKey.equalsIgnoreCase("layer.borderWidth")){
            setBorderWidth(aValue);
        }
    }

}


