package vishtechno.bkm.quickscreenshotcapture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.internal.view.SupportMenu;
import java.io.File;
import java.io.FileOutputStream;

import vishtechno.bkm.quickscreenshotcapture.BlurActivity;

public class TouchImageView extends AppCompatImageView {
    static final int CLICK = 3;
    public static final int DRAG = 1;
    public static final int NONE = 0;
    static final int ZOOM = 2;
    static final int ZOOMDRAG = 3;
    static float resRatio;
    BitmapShader bitmapShader;
    Path brushPath;
    public Canvas canvas;
    Canvas canvasPreview;
    Paint circlePaint;
    Path circlePath;
    public boolean coloring = true;
    Context context;
    PointF curr = new PointF();
    public int currentImageIndex = 0;
    boolean draw = false;
    Paint drawPaint;
    Path drawPath;
    public Bitmap drawingBitmap;
    Rect dstRect;
    float[] f109m;
    float f110x;
    float f111y;
    PointF last = new PointF();
    Paint logPaintColor;
    Paint logPaintGray;
    ScaleGestureDetector mScaleDetector;
    Matrix matrix;
    float maxScale = 5.0f;
    float minScale = 1.0f;
    public int mode = 0;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    float oldX = 0.0f;
    float oldY = 0.0f;
    boolean onMeasureCalled = false;
    public int opacity = 25;
    protected float origHeight;
    protected float origWidth;
    int pCount1 = -1;
    int pCount2 = -1;
    public boolean prViewDefaultPosition;
    Paint previewPaint;
    public float radius = 150.0f;
    public float saveScale = 1.0f;
    public Bitmap splashBitmap;
    PointF start = new PointF();
    Paint tempPaint;
    Bitmap tempPreviewBitmap;
    int viewHeight;
    int viewWidth;

    private class MyAnimationListener implements AnimationListener {
        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }

        private MyAnimationListener() {
        }

        public void onAnimationEnd(Animation animation) {
            if (TouchImageView.this.prViewDefaultPosition) {
                LayoutParams layoutParams = new LayoutParams(BlurActivity.prView.getWidth(), BlurActivity.prView.getHeight());
                layoutParams.setMargins(0, 0, 0, 0);
                BlurActivity.prView.setLayoutParams(layoutParams);
                return;
            }
            LayoutParams layoutParams2 = new LayoutParams(BlurActivity.prView.getWidth(), BlurActivity.prView.getHeight());
            layoutParams2.setMargins(0, TouchImageView.this.viewHeight - BlurActivity.prView.getWidth(), 0, 0);
            BlurActivity.prView.setLayoutParams(layoutParams2);
        }
    }

    private class SaveCanvasLog extends AsyncTask<String, Integer, String> {
        private SaveCanvasLog() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... strArr) {
            TouchImageView.this.currentImageIndex++;
            File file = BlurActivity.tempDrawPathFile;
            StringBuilder sb = new StringBuilder();
            String str = "canvasLog";
            sb.append(str);
            sb.append(TouchImageView.this.currentImageIndex);
            String str2 = ".jpg";
            sb.append(str2);
            File file2 = new File(file, sb.toString());
            if (file2.exists()) {
                file2.delete();
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                TouchImageView.this.drawingBitmap.compress(CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TouchImageView.this.currentImageIndex > 5) {
                File file3 = BlurActivity.tempDrawPathFile;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(TouchImageView.this.currentImageIndex - 5);
                sb2.append(str2);
                File file4 = new File(file3, sb2.toString());
                if (file4.exists()) {
                    file4.delete();
                }
            }
            return "this string is passed to onPostExecute";
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            super.onPostExecute(str);
        }
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            BlurActivity.prView.setVisibility(4);
            if (TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3) {
                TouchImageView.this.mode = 3;
            } else {
                TouchImageView.this.mode = 2;
            }
            TouchImageView.this.draw = false;
            return true;
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float f = TouchImageView.this.saveScale;
            TouchImageView.this.saveScale *= scaleFactor;
            if (TouchImageView.this.saveScale > TouchImageView.this.maxScale) {
                TouchImageView touchImageView = TouchImageView.this;
                touchImageView.saveScale = touchImageView.maxScale;
                scaleFactor = TouchImageView.this.maxScale / f;
            } else {
                float f2 = TouchImageView.this.saveScale;
                float f3 = TouchImageView.this.minScale;
            }
            if (TouchImageView.this.origWidth * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewWidth) || TouchImageView.this.origHeight * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewHeight)) {
                TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, (float) (TouchImageView.this.viewWidth / 2), (float) (TouchImageView.this.viewHeight / 2));
            } else {
                TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            }
            TouchImageView.this.matrix.getValues(TouchImageView.this.f109m);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            TouchImageView.this.radius = ((float) (BlurActivity.radiusBar.getProgress() + 50)) / TouchImageView.this.saveScale;
            BlurActivity.brushView.setShapeRadiusRatio(((float) (BlurActivity.radiusBar.getProgress() + 50)) / TouchImageView.this.saveScale);
            TouchImageView.this.updatePreviewPaint();
        }
    }

    /* access modifiers changed from: 0000 */
    public float getFixTrans(float f, float f2, float f3) {
        float f4;
        float f5;
        if (f3 <= f2) {
            f4 = f2 - f3;
            f5 = 0.0f;
        } else {
            f5 = f2 - f3;
            f4 = 0.0f;
        }
        if (f < f5) {
            return (-f) + f5;
        }
        if (f > f4) {
            return (-f) + f4;
        }
        return 0.0f;
    }

    public TouchImageView(Context context2) {
        super(context2);
        this.context = context2;
        sharedConstructing(context2);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    public TouchImageView(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        this.context = context2;
        sharedConstructing(context2);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    public void initDrawing() {
        try {
            this.splashBitmap = BlurActivity.bitmapClear;
            this.drawingBitmap = BlurActivity.bitmapBlur.copy(Config.ARGB_8888, true);
            setImageBitmap(this.drawingBitmap);
            this.canvas = new Canvas(this.drawingBitmap);
            this.circlePath = new Path();
            this.drawPath = new Path();
            this.brushPath = new Path();
            this.circlePaint = new Paint(1);
            this.circlePaint.setColor(SupportMenu.CATEGORY_MASK);
            this.circlePaint.setStyle(Style.STROKE);
            this.circlePaint.setStrokeWidth(5.0f);
            this.drawPaint = new Paint(1);
            this.drawPaint.setStyle(Style.STROKE);
            this.drawPaint.setStrokeWidth(this.radius);
            this.drawPaint.setStrokeCap(Cap.ROUND);
            this.drawPaint.setStrokeJoin(Join.ROUND);
            setLayerType(1, null);
            this.tempPaint = new Paint();
            this.tempPaint.setStyle(Style.FILL);
            this.tempPaint.setColor(-1);
            this.previewPaint = new Paint();
            this.previewPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            this.tempPreviewBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
            this.canvasPreview = new Canvas(this.tempPreviewBitmap);
            this.dstRect = new Rect(0, 0, 100, 100);
            this.logPaintGray = new Paint(this.drawPaint);
            this.logPaintGray.setShader(new BitmapShader(BlurActivity.bitmapBlur, TileMode.CLAMP, TileMode.CLAMP));
            this.bitmapShader = new BitmapShader(this.splashBitmap, TileMode.CLAMP, TileMode.CLAMP);
            this.drawPaint.setShader(this.bitmapShader);
            this.logPaintColor = new Paint(this.drawPaint);
            new SaveCanvasLog().execute(new String[0]);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void updatePaintBrush() {
        try {
            this.drawPaint.setStrokeWidth(this.radius * resRatio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updatePreviewPaint();
    }

    public void changeShaderBitmap() {
        this.bitmapShader = new BitmapShader(this.splashBitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.drawPaint.setShader(this.bitmapShader);
        updatePreviewPaint();
    }

    public void updatePreviewPaint() {
        try {
            if (BlurActivity.bitmapClear.getWidth() > BlurActivity.bitmapClear.getHeight()) {
                resRatio = ((float) BlurActivity.displayWidth) / ((float) BlurActivity.bitmapClear.getWidth());
                resRatio *= this.saveScale;
            } else {
                resRatio = this.origHeight / ((float) BlurActivity.bitmapClear.getHeight());
                resRatio *= this.saveScale;
            }
            this.drawPaint.setStrokeWidth(this.radius * resRatio);
            this.drawPaint.setMaskFilter(new BlurMaskFilter(resRatio * 30.0f, Blur.NORMAL));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void sharedConstructing(Context context2) {
        super.setClickable(true);
        this.context = context2;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener());
        this.matrix = new Matrix();
        this.f109m = new float[9];
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TouchImageView.this.mScaleDetector.onTouchEvent(motionEvent);
                TouchImageView.this.pCount2 = motionEvent.getPointerCount();
                TouchImageView.this.curr = new PointF(motionEvent.getX(), motionEvent.getY() - (((float) BlurActivity.offsetBar.getProgress()) * 3.0f));
                TouchImageView touchImageView = TouchImageView.this;
                touchImageView.f110x = (touchImageView.curr.x - TouchImageView.this.f109m[2]) / TouchImageView.this.f109m[0];
                TouchImageView touchImageView2 = TouchImageView.this;
                touchImageView2.f111y = (touchImageView2.curr.y - TouchImageView.this.f109m[5]) / TouchImageView.this.f109m[4];
                int action = motionEvent.getAction();
                if (action == 0) {
                    TouchImageView.this.drawPaint.setStrokeWidth(TouchImageView.this.radius * TouchImageView.resRatio);
                    TouchImageView.this.drawPaint.setMaskFilter(new BlurMaskFilter(TouchImageView.resRatio * 30.0f, Blur.NORMAL));
                    TouchImageView.this.drawPaint.getShader().setLocalMatrix(TouchImageView.this.matrix);
                    TouchImageView touchImageView3 = TouchImageView.this;
                    touchImageView3.oldX = 0.0f;
                    touchImageView3.oldY = 0.0f;
                    touchImageView3.last.set(TouchImageView.this.curr);
                    TouchImageView.this.start.set(TouchImageView.this.last);
                    if (!(TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3)) {
                        TouchImageView.this.draw = true;
                        BlurActivity.prView.setVisibility(0);
                    }
                    TouchImageView.this.circlePath.reset();
                    TouchImageView.this.circlePath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                    TouchImageView.this.circlePath.addCircle(TouchImageView.this.curr.x, TouchImageView.this.curr.y, (TouchImageView.this.radius * TouchImageView.resRatio) / 2.0f, Direction.CW);
                    TouchImageView.this.drawPath.moveTo(TouchImageView.this.f110x, TouchImageView.this.f111y);
                    TouchImageView.this.brushPath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                    TouchImageView.this.showBoxPreview();
                } else if (action == 1) {
                    if (TouchImageView.this.mode == 1) {
                        TouchImageView.this.matrix.getValues(TouchImageView.this.f109m);
                    }
                    int abs = (int) Math.abs(TouchImageView.this.curr.y - TouchImageView.this.start.y);
                    if (((int) Math.abs(TouchImageView.this.curr.x - TouchImageView.this.start.x)) < 3 && abs < 3) {
                        TouchImageView.this.performClick();
                    }
                    if (TouchImageView.this.draw) {
                        TouchImageView.this.drawPaint.setStrokeWidth(TouchImageView.this.radius);
                        TouchImageView.this.drawPaint.setMaskFilter(new BlurMaskFilter(30.0f, Blur.NORMAL));
                        TouchImageView.this.drawPaint.getShader().setLocalMatrix(new Matrix());
                        TouchImageView.this.canvas.drawPath(TouchImageView.this.drawPath, TouchImageView.this.drawPaint);
                        new SaveCanvasLog().execute(new String[0]);
                    }
                    BlurActivity.prView.setVisibility(4);
                    TouchImageView.this.circlePath.reset();
                    TouchImageView.this.drawPath.reset();
                    TouchImageView.this.brushPath.reset();
                    TouchImageView.this.draw = false;
                } else if (action != 2) {
                    if (action == 6 && TouchImageView.this.mode == 2) {
                        TouchImageView.this.mode = 0;
                    }
                } else if (TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3 || !TouchImageView.this.draw) {
                    if (TouchImageView.this.pCount1 == 1 && TouchImageView.this.pCount2 == 1) {
                        TouchImageView.this.matrix.postTranslate(TouchImageView.this.curr.x - TouchImageView.this.last.x, TouchImageView.this.curr.y - TouchImageView.this.last.y);
                    }
                    TouchImageView.this.last.set(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                } else {
                    TouchImageView.this.circlePath.reset();
                    TouchImageView.this.circlePath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                    TouchImageView.this.circlePath.addCircle(TouchImageView.this.curr.x, TouchImageView.this.curr.y, (TouchImageView.this.radius * TouchImageView.resRatio) / 2.0f, Direction.CW);
                    TouchImageView.this.drawPath.lineTo(TouchImageView.this.f110x, TouchImageView.this.f111y);
                    TouchImageView.this.brushPath.lineTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                    TouchImageView.this.showBoxPreview();
                    double width = (double) BlurActivity.prView.getWidth();
                    Double.isNaN(width);
                    int i = (int) (width * 1.3d);
                    float f = (float) i;
                    if ((TouchImageView.this.curr.x > f || TouchImageView.this.curr.y > f || !TouchImageView.this.prViewDefaultPosition) && TouchImageView.this.curr.x <= f && TouchImageView.this.curr.y >= ((float) (TouchImageView.this.viewHeight - i)) && !TouchImageView.this.prViewDefaultPosition) {
                        TouchImageView touchImageView4 = TouchImageView.this;
                        touchImageView4.prViewDefaultPosition = true;
                        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-(touchImageView4.viewHeight - BlurActivity.prView.getWidth())));
                        translateAnimation.setDuration(500);
                        translateAnimation.setFillAfter(false);
                        translateAnimation.setAnimationListener(new MyAnimationListener());
                        BlurActivity.prView.startAnimation(translateAnimation);
                    } else {
                        TouchImageView touchImageView5 = TouchImageView.this;
                        touchImageView5.prViewDefaultPosition = false;
                        TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (touchImageView5.viewHeight - BlurActivity.prView.getWidth()));
                        translateAnimation2.setDuration(500);
                        translateAnimation2.setFillAfter(false);
                        translateAnimation2.setAnimationListener(new MyAnimationListener());
                        BlurActivity.prView.startAnimation(translateAnimation2);
                    }
                }
                TouchImageView touchImageView6 = TouchImageView.this;
                touchImageView6.pCount1 = touchImageView6.pCount2;
                TouchImageView touchImageView7 = TouchImageView.this;
                touchImageView7.setImageMatrix(touchImageView7.matrix);
                TouchImageView.this.invalidate();
                return true;
            }
        });
    }

    public void updateRefMetrix() {
        this.matrix.getValues(this.f109m);
    }

    /* access modifiers changed from: 0000 */
    public void showBoxPreview() {
        buildDrawingCache();
        Bitmap createBitmap = Bitmap.createBitmap(getDrawingCache());
        this.canvasPreview.drawRect(this.dstRect, this.tempPaint);
        this.canvasPreview.drawBitmap(createBitmap, new Rect(((int) this.curr.x) - 100, ((int) this.curr.y) - 100, ((int) this.curr.x) + 100, ((int) this.curr.y) + 100), this.dstRect, this.previewPaint);
        BlurActivity.prView.setImageBitmap(this.tempPreviewBitmap);
        destroyDrawingCache();
    }

    public void onDraw(Canvas canvas2) {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        int i = (int) fArr[2];
        int i2 = (int) fArr[5];
        super.onDraw(canvas2);
        float f = this.origHeight;
        float f2 = this.saveScale;
        float f3 = (float) i2;
        float f4 = (f * f2) + f3;
        if (i2 < 0) {
            float f5 = (float) i;
            float f6 = (this.origWidth * f2) + f5;
            int i3 = this.viewHeight;
            if (f4 > ((float) i3)) {
                f4 = (float) i3;
            }
            canvas2.clipRect(f5, 0.0f, f6, f4);
        } else {
            float f7 = (float) i;
            float f8 = (this.origWidth * f2) + f7;
            int i4 = this.viewHeight;
            if (f4 > ((float) i4)) {
                f4 = (float) i4;
            }
            canvas2.clipRect(f7, f3, f8, f4);
        }
        if (this.draw) {
            canvas2.drawPath(this.brushPath, this.drawPaint);
            canvas2.drawPath(this.circlePath, this.circlePaint);
        }
    }

    /* access modifiers changed from: 0000 */
    public void fixTrans() {
        this.matrix.getValues(this.f109m);
        float[] fArr = this.f109m;
        float f = fArr[2];
        float f2 = fArr[5];
        float fixTrans = getFixTrans(f, (float) this.viewWidth, this.origWidth * this.saveScale);
        float fixTrans2 = getFixTrans(f2, (float) this.viewHeight, this.origHeight * this.saveScale);
        if (!(fixTrans == 0.0f && fixTrans2 == 0.0f)) {
            this.matrix.postTranslate(fixTrans, fixTrans2);
        }
        this.matrix.getValues(this.f109m);
        updatePreviewPaint();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (!this.onMeasureCalled) {
            this.viewWidth = MeasureSpec.getSize(i);
            this.viewHeight = MeasureSpec.getSize(i2);
            int i3 = this.oldMeasuredHeight;
            if (i3 != this.viewWidth || i3 != this.viewHeight) {
                int i4 = this.viewWidth;
                if (i4 != 0) {
                    int i5 = this.viewHeight;
                    if (i5 != 0) {
                        this.oldMeasuredHeight = i5;
                        this.oldMeasuredWidth = i4;
                        if (this.saveScale == 1.0f) {
                            fitScreen();
                        }
                        this.onMeasureCalled = true;
                    }
                }
            }
        }
    }

    public void fitScreen() {
        Drawable drawable = getDrawable();
        if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            float f = (float) intrinsicWidth;
            float intrinsicHeight = (float) drawable.getIntrinsicHeight();
            float min = Math.min(((float) this.viewWidth) / f, ((float) this.viewHeight) / intrinsicHeight);
            this.matrix.setScale(min, min);
            float f2 = (((float) this.viewHeight) - (intrinsicHeight * min)) / 2.0f;
            float f3 = (((float) this.viewWidth) - (f * min)) / 2.0f;
            this.matrix.postTranslate(f3, f2);
            this.origWidth = ((float) this.viewWidth) - (f3 * 2.0f);
            this.origHeight = ((float) this.viewHeight) - (f2 * 2.0f);
            setImageMatrix(this.matrix);
            this.matrix.getValues(this.f109m);
            fixTrans();
        }
    }
}
