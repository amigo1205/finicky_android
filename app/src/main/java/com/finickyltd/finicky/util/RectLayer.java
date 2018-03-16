package com.finickyltd.finicky.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RectLayer extends AppCompatImageView {
    private Rect rect;

    public RectLayer(Context context) {
        this(context, null);
    }

    public RectLayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectLayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = getWidth();
        int height = getHeight();
        this.rect = new Rect(width / 12, height / 5, (width * 11) / 12, (height * 7) / 10);
        Paint p = new Paint();
        p.setColor(ViewCompat.MEASURED_STATE_MASK);
        p.setStyle(Style.FILL);
        p.setAlpha(76);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) this.rect.top, p);
        canvas.drawRect(0.0f, (float) this.rect.bottom, (float) width, (float) height, p);
        canvas.drawRect(0.0f, (float) this.rect.top, (float) this.rect.left, (float) this.rect.bottom, p);
        canvas.drawRect((float) this.rect.right, (float) this.rect.top, (float) width, (float) this.rect.bottom, p);
        p.setColor(SupportMenu.CATEGORY_MASK);
        p.setAlpha(255);
        p.setStrokeWidth(3.0f);
        int lineVerticalPos = this.rect.top + (this.rect.height() >> 1);
        canvas.drawLine((float) (this.rect.left + 10), (float) lineVerticalPos, (float) (this.rect.right - 10), (float) lineVerticalPos, p);
        p.setColor(-1);
        p.setStrokeWidth(3.0f);
        canvas.drawLines(new float[]{(float) this.rect.left, (float) this.rect.top, (float) this.rect.right, (float) this.rect.top, (float) this.rect.right, (float) this.rect.top, (float) this.rect.right, (float) this.rect.bottom, (float) this.rect.right, (float) this.rect.bottom, (float) this.rect.left, (float) this.rect.bottom, (float) this.rect.left, (float) this.rect.top, (float) this.rect.left, (float) this.rect.bottom}, p);
        p.setARGB(255, 255, 141, 22);
        p.setStrokeWidth(5.0f);
        canvas.drawLine((float) this.rect.left, (float) this.rect.top, (float) (this.rect.left + 40), (float) this.rect.top, p);
        canvas.drawLine((float) (this.rect.right - 40), (float) this.rect.top, (float) this.rect.right, (float) this.rect.top, p);
        canvas.drawLine((float) this.rect.right, (float) this.rect.top, (float) this.rect.right, (float) (this.rect.top + 40), p);
        canvas.drawLine((float) this.rect.right, (float) (this.rect.bottom - 40), (float) this.rect.right, (float) this.rect.bottom, p);
        canvas.drawLine((float) (this.rect.right - 40), (float) this.rect.bottom, (float) this.rect.right, (float) this.rect.bottom, p);
        canvas.drawLine((float) this.rect.left, (float) this.rect.bottom, (float) (this.rect.left + 40), (float) this.rect.bottom, p);
        canvas.drawLine((float) this.rect.left, (float) (this.rect.bottom - 40), (float) this.rect.left, (float) this.rect.bottom, p);
        canvas.drawLine((float) this.rect.left, (float) this.rect.top, (float) this.rect.left, (float) (this.rect.top + 40), p);
    }
}
