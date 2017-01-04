package com.yy.codex.uikit;

import android.graphics.RectF;

/**
 * Created by cuiminghui on 2017/1/3.
 */

public final class CGRect {

    public CGPoint origin = new CGPoint(0, 0);
    public CGSize size = new CGSize(0, 0);

    public CGRect(double x, double y, double width, double height) {
        this.origin = new CGPoint(x, y);
        this.size = new CGSize(width, height);
    }

    public RectF toRectF() {
        return new RectF((float) origin.getX(), (float) origin.getY(), (float) (origin.getX() + size.getWidth()), (float) (origin.getY() + size.getHeight()));
    }

    public CGRect setX(double x) {
        return new CGRect(x, this.origin.getY(), this.size.getWidth(), this.size.getHeight());
    }

    public CGRect setY(double y) {
        return new CGRect(this.origin.getX(), y, this.size.getWidth(), this.size.getHeight());
    }

    public CGRect setWidth(double width) {
        return new CGRect(this.origin.getX(), this.origin.getY(), width, this.size.getHeight());
    }

    public CGRect setHeight(double height) {
        return new CGRect(this.origin.getX(), this.origin.getY(), this.size.getWidth(), height);
    }

    public CGSize getSize(){
        return size;
    }

}
