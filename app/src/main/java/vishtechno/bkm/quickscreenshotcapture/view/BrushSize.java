package vishtechno.bkm.quickscreenshotcapture.view;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;

public class BrushSize {
    private Paint paintInner;
    private Paint paintOuter = new Paint();
    private Path path;

    BrushSize() {
        this.paintOuter.setColor(-1);
        this.paintOuter.setStrokeWidth(8.0f);
        this.paintOuter.setStyle(Style.STROKE);
        this.paintInner = new Paint();
        this.paintInner.setColor(-7829368);
        this.paintInner.setStrokeWidth(8.0f);
        this.paintInner.setStyle(Style.FILL);
        this.path = new Path();
    }

    /* access modifiers changed from: 0000 */
    public void setCircle(float f, float f2, float f3, Direction direction) {
        this.path.reset();
        this.path.addCircle(f, f2, f3, direction);
    }

    public Path getPath() {
        return this.path;
    }

    /* access modifiers changed from: 0000 */
    public Paint getPaint() {
        return this.paintOuter;
    }

    /* access modifiers changed from: 0000 */
    public Paint getInnerPaint() {
        return this.paintInner;
    }

    public void setPaintOpacity(int i) {
        this.paintInner.setAlpha(i);
    }
}
