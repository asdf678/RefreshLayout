package tzy.refreshlayout.header;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;


public class HeaderImageDrawable extends BitmapDrawable {
    private float mRotation;

    private void setRotation(float rotation) {
        mRotation = rotation;
    }

    private float getRotation() {
        return mRotation;
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        canvas.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        super.draw(canvas);
    }
}
