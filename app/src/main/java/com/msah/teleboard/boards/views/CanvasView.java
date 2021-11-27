package com.msah.teleboard.boards.views;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.msah.teleboard.R;
import com.msah.teleboard.boards.models.PhotoRecord;
import com.msah.teleboard.boards.models.SketchData;
import com.msah.teleboard.utils.BitmapUtils;
import com.msah.teleboard.utils.ScreenUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static com.msah.teleboard.utils.BitmapUtils.createBitmapThumbnail;

public class CanvasView extends View implements View.OnTouchListener {

    public static final int PIXEL_SIZE = 1;
    public static final int EDIT_STROKE = 1;
    private Paint mPaint;
    private int mLastX;
    private int mLastY;
    private Canvas mBuffer;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private DatabaseReference mFirebaseRef;
    private ChildEventListener mListener;
    private int mCurrentColor = 0xFFFF0000;
    private int mCurrentStrokeSize = 10;
    private Path mPath, dBPath;
    private Set<String> mOutstandingSegments;
    private ArrayList<Segment> incomingSegments;
    private Segment mCurrentSegment;
    private float mScale = 1.0f;
    private int mCanvasWidth;
    private int mCanvasHeight;

    public int counter = 0;

    ObjectAnimator animator = ObjectAnimator.ofFloat(CanvasView.this, "phase", 1.0f, 0.0f);

    private float length;




    public static final int EDIT_PHOTO = 2;
    public static final int DEFAULT_STROKE_SIZE = 3;
    public static final int DEFAULT_STROKE_ALPHA = 100;
    public static final int DEFAULT_ERASER_SIZE = 50;
    public static final float TOUCH_TOLERANCE = 4;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_DRAG = 1;
    public static final int ACTION_SCALE = 2;
    public static final int ACTION_ROTATE = 3;
    //    public int curSketchData.editMode = EDIT_STROKE;
    public static float SCALE_MAX = 4.0f;
    public static float SCALE_MIN = 0.2f;
    public static float SCALE_MIN_LEN;
    public final String TAG = getClass().getSimpleName();
    public Paint boardPaint;

    public Bitmap mirrorMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.ic_copy);
    public Bitmap deleteMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.ic_remove);
    public Bitmap rotateMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.ic_croprotate);
    public Bitmap resetMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.ic_copy);
    //    Bitmap rotateMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.test);
    public RectF markerCopyRect = new RectF(0, 0, 50, 50);//镜像标记边界
    public RectF markerDeleteRect = new RectF(0, 0, 50, 50);//删除标记边界
    public RectF markerRotateRect = new RectF(0, 0, 50, 50);//旋转标记边界
    public RectF markerResetRect = new RectF(0, 0, 50, 50);//旋转标记边界
    public SketchData curSketchData;
    public Rect backgroundSrcRect = new Rect();
    public Rect backgroundDstRect = new Rect();
    public PhotoRecord curPhotoRecord;
    public int actionMode;
    public float simpleScale = 0.5f;//图片载入的缩放倍数
    public float strokeSize = DEFAULT_STROKE_SIZE;
    public int strokeRealColor = Color.BLACK;//画笔实际颜色
    public int strokeColor = Color.BLACK;//画笔颜色
    public int strokeAlpha = 255;//画笔透明度
    public float eraserSize = DEFAULT_ERASER_SIZE;
    public Path strokePath;
    public float downX, downY, preX, preY, curX, curY;
    public int mWidth, mHeight;
    public Context mContext;
    public int drawDensity = 2;//绘制密度,数值越高图像质量越低、性能越好
    public ScaleGestureDetector mScaleGestureDetector = null;
    public OnDrawChangedListener onDrawChangedListener;


    public CanvasView(Context context, DatabaseReference ref) {

        this(context, ref, 10);
    }

    public CanvasView(Context context, DatabaseReference ref, int width, int height) {
        this(context, ref);
        mCanvasWidth = width;
        mCanvasHeight = height;

        this.mContext = context;
        setSketchData(new SketchData());
        initParams(context);
        this.setOnTouchListener(this);


    }

    public CanvasView(Context context, DatabaseReference ref, float scale) {
        super(context);

        mOutstandingSegments = new HashSet<String>();
        incomingSegments = new ArrayList<Segment>();
        mPath = new Path();
        dBPath = new Path();
        this.mFirebaseRef = ref;
        this.mScale = scale;
        addListener(ref);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setShadowLayer(0.2f, 0, 0, Color.RED);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setPhase(float phase){
        mPaint.setPathEffect(createPathEffect(length, phase, 0.0f));
        invalidate();
    }
    private static PathEffect createPathEffect(float pathLength, float phase, float offset){
        return new DashPathEffect(new float[]{pathLength, pathLength}, Math.max(phase*pathLength, offset));
    }

    public void removeListener() {
        mFirebaseRef.removeEventListener(mListener);
    }
    public void addListener(DatabaseReference ref){

        mListener = ref.addChildEventListener(new ChildEventListener() {
            /**
             * @param dataSnapshot The data we need to construct a new Segment
             * @param previousChildName Supplied for ordering, but we don't really care about ordering in this app
            **/
            @Override
            public void onChildAdded(@org.jetbrains.annotations.NotNull @NotNull DataSnapshot dataSnapshot, String previousChildName) {
                String name = dataSnapshot.getKey();
                // To prevent lag, we draw our own segments as they are created. As a result, we need to check to make
                // sure this event is a segment drawn by another user before we draw it
                if(!mOutstandingSegments.contains(name)){
                    // Deserialize the data into our Segment class
                    Segment segment = dataSnapshot.getValue(Segment.class);
                    // dBPath.reset();
                    drawSegment(segment, paintFromColor(segment.getColor()));
                    incomingSegments.add(segment);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {


                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(counter <= incomingSegments.size()-1) {

                                Segment sg = incomingSegments.get(counter);
                                dBPath.reset();
                                drawSegment(sg, paintFromColor(sg.getColor()));
                                addListener(ref);
                                counter += 1;
                            }

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }
                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mOutstandingSegments.add(name);
                    // Tell the view to redraw itself
                    // invalidate();
                }
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
                // No-op
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onCancelled(@NotNull DatabaseError firebaseError) {
                // No-op
            }
        });
    }
    public void setColor(int color) {
        mCurrentColor = color;
        mPaint.setColor(color);
    }

    public void setStrokeSize(int stokeSize){
        mCurrentStrokeSize = stokeSize;
        mPaint.setStrokeWidth(mCurrentStrokeSize);
    }

    public void clear() {
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
        mCurrentSegment = null;
        mOutstandingSegments.clear();
        dBPath.reset();
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mScale = Math.min(1.0f * w / mCanvasWidth, 1.0f * h / mCanvasHeight);
        mBitmap = Bitmap.createBitmap(Math.round(mCanvasWidth * mScale), Math.round(mCanvasHeight * mScale), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(dBPath, mPaint);
        mBuffer.drawPath(dBPath, mPaint);

        drawRecord(canvas);
        if (onDrawChangedListener != null)
            onDrawChangedListener.onDrawChanged();
    }

    public static Paint paintFromColor(int color) {
        return paintFromColor(color, Paint.Style.STROKE);
    }

    public static Paint paintFromColor(int color, Paint.Style style) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(color);
        p.setStyle(style);
        return p;
    }

    public static Path getPathForPoints(List<Point> points, double scale) {
        Path path = new Path();
        scale = scale * PIXEL_SIZE;
        Point current = points.get(0);
        path.moveTo(Math.round(scale * current.x), Math.round(scale * current.y));
        Point next = null;
        for (int i = 1; i < points.size(); ++i) {
            next = points.get(i);
            path.quadTo(
                    Math.round(scale * current.x), Math.round(scale * current.y),
                    Math.round(scale * (next.x + current.x) / 2), Math.round(scale * (next.y + current.y) / 2)
            );
            current = next;
        }
        if (next != null) {
            path.lineTo(Math.round(scale * next.x), Math.round(scale * next.y));
        }


        return path;
    }

    private void drawSegment(Segment segment, Paint paint) {
        if (mBuffer != null) {

            paint.setStrokeWidth(10);
            Segment testSeg = segment;
            List<Point> points = testSeg.getPoints();
            float scale = 1 * PIXEL_SIZE;
            Point current = points.get(0);
            dBPath.moveTo(Math.round(scale * current.x), Math.round(scale * current.y));
            Point next = null;
            for (int i = 1; i < points.size(); ++i) {
                next = points.get(i);
                dBPath.quadTo(
                        Math.round(scale * current.x), Math.round(scale * current.y),
                        Math.round(scale * (next.x + current.x) / 2), Math.round(scale * (next.y + current.y) / 2)
                );
                current = next;
            }
            if (next != null) {
                dBPath.lineTo(Math.round(scale * next.x), Math.round(scale * next.y));
            }
            PathMeasure measure = new PathMeasure(dBPath, false);
            length = measure.getLength();
            dBPath.reset();
            float[] intervals = new float[]{length, length};
            animator.setDuration(3000);
            animator.start();
        }



    }
    private void onTouchStart(float x, float y) {

        mPath.reset();
        mPath.moveTo(x, y);
        mCurrentSegment = new Segment(mCurrentColor);
        mLastX = (int) x / PIXEL_SIZE;
        mLastY = (int) y / PIXEL_SIZE;
        mCurrentSegment.addPoint(mLastX, mLastY);
    }

    private void onTouchMove(float x, float y) {

        int x1 = (int) x / PIXEL_SIZE;
        int y1 = (int) y / PIXEL_SIZE;
        float dx = Math.abs(x1 - mLastX);
        float dy = Math.abs(y1 - mLastY);
        if (dx >= 1 || dy >= 1) {
            mPath.quadTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE, ((x1 + mLastX) * PIXEL_SIZE) / 2, ((y1 + mLastY) * PIXEL_SIZE) / 2);
            mLastX = x1;
            mLastY = y1;
            mCurrentSegment.addPoint(mLastX, mLastY);

        }
    }

    private void onTouchEnd() {
        mPath.lineTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE);
        mBuffer.drawPath(mPath, mPaint);
       // mPath.reset();
        DatabaseReference segmentRef = mFirebaseRef.push();
        final String segmentName = segmentRef.getKey();
        mOutstandingSegments.add(segmentName);

        // create a scaled version of the segment, so that it matches the size of the board
        Segment segment = new Segment(mCurrentSegment.getColor());
        for (Point point: mCurrentSegment.getPoints()) {
            segment.addPoint((int)Math.round(point.x / mScale), (int)Math.round(point.y / mScale));
        }
        // Save our segment into Firebase. This will let other clients see the data and add it to their own canvases.
        // Also make a note of the outstanding segment name so we don't do a duplicate draw in our onChildAdded callback.
        // We can remove the name from mOutstandingSegments once the completion listener is triggered, since we will have
        // received the child added event by then.
        segmentRef.setValue(segment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NotNull DatabaseReference firebaseRef) {
                if (error != null) {
                    Log.e("Insight illustrator", error.toString());
                    throw error.toException();
                }
                mOutstandingSegments.remove(segmentName);
            }
        });
    }




//    public int curSketchData.strokeType = StrokeRecord.STROKE_TYPE_DRAW;


    public void setSketchData(SketchData sketchData) {
        this.curSketchData = sketchData;
        curPhotoRecord = null;
    }



    public void initParams(Context context) {

//        setFocusable(true);
//        setFocusableInTouchMode(true);
        setBackgroundColor(Color.WHITE);


        boardPaint = new Paint();
        boardPaint.setColor(Color.GRAY);
        boardPaint.setStrokeWidth(ScreenUtils.dip2px(mContext, 0.8f));
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setPathEffect(new DashPathEffect(new float[] {10f, 20f}, 0f));

        SCALE_MIN_LEN = ScreenUtils.dip2px(context, 20);
    }






    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    int[] location = new int[2];



    public float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }




    public void drawRecord(Canvas canvas) {
        drawRecord(canvas, true);
    }

    public void drawRecord(Canvas canvas, boolean isDrawBoard) {
        if (curSketchData != null) {
            for (PhotoRecord record : curSketchData.photoRecordList) {
                if (record != null) {
                    Log.d(getClass().getSimpleName(), "drawRecord" + record.bitmap.toString());
                    canvas.drawBitmap(record.bitmap, record.matrix, null);
                }
            }
            if (isDrawBoard && curSketchData.editMode == EDIT_PHOTO && curPhotoRecord != null) {
                SCALE_MAX = curPhotoRecord.scaleMax;
                float[] photoCorners = calculateCorners(curPhotoRecord);//计算图片四个角点和中心点
                drawBoard(canvas, photoCorners);//绘制图形边线
                drawMarks(canvas, photoCorners);//绘制边角图片
            }
            //新建一个临时画布，以便保存过多的画笔
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(getWidth() / drawDensity, getHeight() / drawDensity, Bitmap.Config.ARGB_4444);
                mBuffer = new Canvas(mBitmap);
            }
        }

    }

    /**
     * 清理画布canvas
     *
     * @param temptCanvas
     */
    public void clearCanvas(Canvas temptCanvas) {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        temptCanvas.drawPaint(p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    //绘制图像边线（由于图形旋转或不一定是矩形，所以用Path绘制边线）
    public void drawBoard(Canvas canvas, float[] photoCorners) {
        Path photoBorderPath = new Path();
        photoBorderPath.moveTo(photoCorners[0], photoCorners[1]);
        photoBorderPath.lineTo(photoCorners[2], photoCorners[3]);
        photoBorderPath.lineTo(photoCorners[4], photoCorners[5]);
        photoBorderPath.lineTo(photoCorners[6], photoCorners[7]);
        photoBorderPath.lineTo(photoCorners[0], photoCorners[1]);
        canvas.drawPath(photoBorderPath, boardPaint);
    }

    //绘制边角操作图标
    public void drawMarks(Canvas canvas, float[] photoCorners) {
        float x;
        float y;
        x = photoCorners[0] - markerCopyRect.width() / 2;
        y = photoCorners[1] - markerCopyRect.height() / 2;
        markerCopyRect.offsetTo(x, y);
        canvas.drawBitmap(mirrorMarkBM, x, y, null);

        x = photoCorners[2] - markerDeleteRect.width() / 2;
        y = photoCorners[3] - markerDeleteRect.height() / 2;
        markerDeleteRect.offsetTo(x, y);
        canvas.drawBitmap(deleteMarkBM, x, y, null);

        x = photoCorners[4] - markerRotateRect.width() / 2;
        y = photoCorners[5] - markerRotateRect.height() / 2;
        markerRotateRect.offsetTo(x, y);
        canvas.drawBitmap(rotateMarkBM, x, y, null);

        x = photoCorners[6] - markerResetRect.width() / 2;
        y = photoCorners[7] - markerResetRect.height() / 2;
        markerResetRect.offsetTo(x, y);
        canvas.drawBitmap(resetMarkBM, x, y, null);
    }

    public float[] calculateCorners(PhotoRecord record) {
        float[] photoCornersSrc = new float[10];//0,1代表左上角点XY，2,3代表右上角点XY，4,5代表右下角点XY，6,7代表左下角点XY，8,9代表中心点XY
        float[] photoCorners = new float[10];//0,1代表左上角点XY，2,3代表右上角点XY，4,5代表右下角点XY，6,7代表左下角点XY，8,9代表中心点XY
        RectF rectF = record.photoRectSrc;
        photoCornersSrc[0] = rectF.left;
        photoCornersSrc[1] = rectF.top;
        photoCornersSrc[2] = rectF.right;
        photoCornersSrc[3] = rectF.top;
        photoCornersSrc[4] = rectF.right;
        photoCornersSrc[5] = rectF.bottom;
        photoCornersSrc[6] = rectF.left;
        photoCornersSrc[7] = rectF.bottom;
        photoCornersSrc[8] = rectF.centerX();
        photoCornersSrc[9] = rectF.centerY();
        curPhotoRecord.matrix.mapPoints(photoCorners, photoCornersSrc);
        return photoCorners;
    }

    public float getMaxScale(RectF photoSrc) {
        return Math.max(getWidth(), getHeight()) / Math.max(photoSrc.width(), photoSrc.height());
//        SCALE_MIN = SCALE_MAX / 5;
    }


    public void touch_down() {
        downX = curX;
        downY = curY;
        if (curSketchData.editMode == EDIT_STROKE) {



        } else if (curSketchData.editMode == EDIT_PHOTO) {
            float[] downPoint = new float[]{downX * drawDensity, downY * drawDensity};//还原点倍数
            if (isInMarkRect(downPoint)) {// 先判操作标记区域
                return;
            }
            if (isInPhotoRect(curPhotoRecord, downPoint)) {//再判断是否点击了当前图片
                actionMode = ACTION_DRAG;
                return;
            }
            selectPhoto(downPoint);//最后判断是否点击了其他图片
        }
    }

    //judge click which photo，then can edit the photo
    public void selectPhoto(float[] downPoint) {
        PhotoRecord clickRecord = null;
        for (int i = curSketchData.photoRecordList.size() - 1; i >= 0; i--) {
            PhotoRecord record = curSketchData.photoRecordList.get(i);
            if (isInPhotoRect(record, downPoint)) {
                clickRecord = record;
                break;
            }
        }
        if (clickRecord != null) {
            setCurPhotoRecord(clickRecord);
            actionMode = ACTION_DRAG;
        } else {
            actionMode = ACTION_NONE;
        }
    }

    public boolean isInMarkRect(float[] downPoint) {
        if (markerRotateRect.contains(downPoint[0], (int) downPoint[1])) {//判断是否在区域内
            actionMode = ACTION_ROTATE;
            return true;
        }
        if (markerDeleteRect.contains(downPoint[0], (int) downPoint[1])) {//判断是否在区域内
            curSketchData.photoRecordList.remove(curPhotoRecord);
            setCurPhotoRecord(null);
            actionMode = ACTION_NONE;
            return true;
        }
        if (markerCopyRect.contains(downPoint[0], (int) downPoint[1])) {//判断是否在区域内
            PhotoRecord newRecord = initPhotoRecord(curPhotoRecord.bitmap);
            newRecord.matrix = new Matrix(curPhotoRecord.matrix);
            newRecord.matrix.postTranslate(ScreenUtils.dip2px(mContext, 20), ScreenUtils.dip2px(mContext, 20));//偏移小段距离以分辨新复制的图片
            setCurPhotoRecord(newRecord);
            actionMode = ACTION_NONE;
            return true;
        }
        if (markerResetRect.contains(downPoint[0], (int) downPoint[1])) {//判断是否在区域内
            curPhotoRecord.matrix.reset();
            curPhotoRecord.matrix.setTranslate(getWidth() / 2 - curPhotoRecord.photoRectSrc.width() / 2,
                    getHeight() / 2 - curPhotoRecord.photoRectSrc.height() / 2);
            actionMode = ACTION_NONE;
            return true;
        }
        return false;
    }

    public boolean isInPhotoRect(PhotoRecord record, float[] downPoint) {
        if (record != null) {
            float[] invertPoint = new float[2];
            Matrix invertMatrix = new Matrix();
            record.matrix.invert(invertMatrix);
            invertMatrix.mapPoints(invertPoint, downPoint);
            return record.photoRectSrc.contains(invertPoint[0], invertPoint[1]);
        }
        return false;
    }

    public void touch_move(MotionEvent event) {
        if (curSketchData.editMode == EDIT_PHOTO && curPhotoRecord != null) {
            if (actionMode == ACTION_DRAG) {
                onDragAction((curX - preX) * drawDensity, (curY - preY) * drawDensity);
            } else if (actionMode == ACTION_ROTATE) {
                onRotateAction(curPhotoRecord);
            } else if (actionMode == ACTION_SCALE) {
                mScaleGestureDetector.onTouchEvent(event);
            }
        }
        preX = curX;
        preY = curY;
    }

    public void onScaleAction(ScaleGestureDetector detector) {
        float[] photoCorners = calculateCorners(curPhotoRecord);
        //目前图片对角线长度
        float len = (float) Math.sqrt(Math.pow(photoCorners[0] - photoCorners[4], 2) + Math.pow(photoCorners[1] - photoCorners[5], 2));
        double photoLen = Math.sqrt(Math.pow(curPhotoRecord.photoRectSrc.width(), 2) + Math.pow(curPhotoRecord.photoRectSrc.height(), 2));
        float scaleFactor = detector.getScaleFactor();
        //设置Matrix缩放参数
        if ((scaleFactor < 1 && len >= photoLen * SCALE_MIN && len >= SCALE_MIN_LEN) || (scaleFactor > 1 && len <= photoLen * SCALE_MAX)) {
            Log.e(scaleFactor + "", scaleFactor + "");
            curPhotoRecord.matrix.postScale(scaleFactor, scaleFactor, photoCorners[8], photoCorners[9]);
        }
    }

    public void onRotateAction(PhotoRecord record) {
        float[] corners = calculateCorners(record);
        //放大
        //目前触摸点与图片显示中心距离,curX*drawDensity为还原缩小密度点数值
        float a = (float) Math.sqrt(Math.pow(curX * drawDensity - corners[8], 2) + Math.pow(curY * drawDensity - corners[9], 2));
        //目前上次旋转图标与图片显示中心距离
        float b = (float) Math.sqrt(Math.pow(corners[4] - corners[0], 2) + Math.pow(corners[5] - corners[1], 2)) / 2;
//        Log.e(TAG, "onRotateAction: a=" + a + ";b=" + b);
        //设置Matrix缩放参数
        double photoLen = Math.sqrt(Math.pow(record.photoRectSrc.width(), 2) + Math.pow(record.photoRectSrc.height(), 2));
        if (a >= photoLen / 2 * SCALE_MIN && a >= SCALE_MIN_LEN && a <= photoLen / 2 * SCALE_MAX) {
            //这种计算方法可以保持旋转图标坐标与触摸点同步缩放
            float scale = a / b;
            record.matrix.postScale(scale, scale, corners[8], corners[9]);
        }

        //旋转
        //根据移动坐标的变化构建两个向量，以便计算两个向量角度.
        PointF preVector = new PointF();
        PointF curVector = new PointF();
        preVector.set((preX * drawDensity - corners[8]), preY * drawDensity - corners[9]);//旋转后向量
        curVector.set(curX * drawDensity - corners[8], curY * drawDensity - corners[9]);//旋转前向量
        //计算向量长度
        double preVectorLen = getVectorLength(preVector);
        double curVectorLen = getVectorLength(curVector);
        //计算两个向量的夹角.
        double cosAlpha = (preVector.x * curVector.x + preVector.y * curVector.y)
                / (preVectorLen * curVectorLen);
        //由于计算误差，可能会带来略大于1的cos，例如
        if (cosAlpha > 1.0f) {
            cosAlpha = 1.0f;
        }
        //本次的角度已经计算出来。
        double dAngle = Math.acos(cosAlpha) * 180.0 / Math.PI;
        // 判断顺时针和逆时针.
        //判断方法其实很简单，这里的v1v2其实相差角度很小的。
        //先转换成单位向量
        preVector.x /= preVectorLen;
        preVector.y /= preVectorLen;
        curVector.x /= curVectorLen;
        curVector.y /= curVectorLen;
        //作curVector的逆时针垂直向量。
        PointF verticalVec = new PointF(curVector.y, -curVector.x);

        //判断这个垂直向量和v1的点积，点积>0表示俩向量夹角锐角。=0表示垂直，<0表示钝角
        float vDot = preVector.x * verticalVec.x + preVector.y * verticalVec.y;
        if (vDot > 0) {
            //v2的逆时针垂直向量和v1是锐角关系，说明v1在v2的逆时针方向。
        } else {
            dAngle = -dAngle;
        }
        record.matrix.postRotate((float) dAngle, corners[8], corners[9]);
    }

    /**
     * 获取p1到p2的线段的长度
     *
     * @return
     */
    public double getVectorLength(PointF vector) {
        return Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }

    public void onDragAction(float distanceX, float distanceY) {
        curPhotoRecord.matrix.postTranslate((int) distanceX, (int) distanceY);
    }

    public void touch_up() {
    }

    @NonNull
    public Bitmap getResultBitmap() {
        return getResultBitmap(null);
    }

    @NonNull
    public Bitmap getResultBitmap(Bitmap addBitmap) {
        Bitmap newBM = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
//        Bitmap newBM = Bitmap.createBitmap(1280, 800, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(newBM);
//        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));//抗锯齿
        //绘制背景
        drawRecord(canvas, false);

        if (addBitmap != null) {
            canvas.drawBitmap(addBitmap, 0, 0, null);
        }
        canvas.save();
        canvas.restore();
//        return newBM;
        Bitmap bitmap = createBitmapThumbnail(newBM, true, 800, 1280);
        return bitmap;
    }


    @NonNull
    public Bitmap getThumbnailResultBitmap() {
        return createBitmapThumbnail(getResultBitmap(), true, ScreenUtils.dip2px(mContext, 200), ScreenUtils.dip2px(mContext, 200));
    }




    public int getStrokeSize() {
        return Math.round(this.strokeSize);
    }


    public void erase() {
        // 先判断是否已经回收
        for (PhotoRecord record : curSketchData.photoRecordList) {
            if (record != null && record.bitmap != null && !record.bitmap.isRecycled()) {
                record.bitmap.recycle();
                record.bitmap = null;
            }
        }

        curSketchData.photoRecordList.clear();
        curPhotoRecord = null;
        mBuffer = null;
        mBitmap.recycle();
        mBitmap = null;
        System.gc();
        invalidate();
    }

    public void setOnDrawChangedListener(OnDrawChangedListener listener) {
        this.onDrawChangedListener = listener;
    }

    public void addPhotoByPath(String path) {
        Bitmap sampleBM = getSampleBitMap(path);
        addPhotoByBitmap(sampleBM);
    }

    public void addPhotoByBitmap(Bitmap sampleBM) {
        if (sampleBM != null) {
            PhotoRecord newRecord = initPhotoRecord(sampleBM);
            setCurPhotoRecord(newRecord);
        } else {
            Toast.makeText(mContext, "图片文件路径有误！", Toast.LENGTH_SHORT).show();
        }
    }

    public void addPhotoByBitmap(Bitmap sampleBM, int[] position) {
        if (sampleBM != null) {
            PhotoRecord newRecord = initPhotoRecord(sampleBM, position);
            setCurPhotoRecord(newRecord);
        } else {
            Toast.makeText(mContext, "图片文件路径有误！", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeCurrentPhotoRecord() {
        curSketchData.photoRecordList.remove(curPhotoRecord);
        setCurPhotoRecord(null);
        actionMode = ACTION_NONE;
    }


    public Bitmap getSampleBitMap(String path) {
        Bitmap sampleBM = null;
        if (path.contains(Environment.getExternalStorageDirectory().toString())) {
            sampleBM = getSDCardPhoto(path);
        }
        return sampleBM;
    }

    @NonNull
    public PhotoRecord initPhotoRecord(Bitmap bitmap) {
        PhotoRecord newRecord = new PhotoRecord();
        newRecord.bitmap = bitmap;
        newRecord.photoRectSrc = new RectF(0, 0, newRecord.bitmap.getWidth(), newRecord.bitmap.getHeight());
        newRecord.scaleMax = getMaxScale(newRecord.photoRectSrc);//放大倍数
        newRecord.matrix = new Matrix();
        newRecord.matrix.postTranslate(getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2);
        return newRecord;
    }

    @NonNull
    public PhotoRecord initPhotoRecord(Bitmap bitmap, int[] position) {
        PhotoRecord newRecord = new PhotoRecord();
        newRecord.bitmap = bitmap;
        newRecord.photoRectSrc = new RectF(0, 0, newRecord.bitmap.getWidth(), newRecord.bitmap.getHeight());
        newRecord.scaleMax = getMaxScale(newRecord.photoRectSrc);//放大倍数
        newRecord.matrix = new Matrix();
        newRecord.matrix.postTranslate(position[0], position[1]);
        return newRecord;
    }

    public void setCurPhotoRecord(PhotoRecord record) {
        curSketchData.photoRecordList.remove(record);
        curSketchData.photoRecordList.add(record);
        curPhotoRecord = record;
        invalidate();
    }

    public Bitmap getSDCardPhoto(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapUtils.decodeSampleBitMapFromFile(mContext, path, simpleScale);
        } else {
            return null;
        }
    }

    public int getEditMode() {
        return curSketchData.editMode;
    }

    public void setEditMode(int editMode) {
        this.curSketchData.editMode = editMode;
        invalidate();
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        curX = (event.getRawX() - location[0]) / drawDensity;
        curY = (event.getRawY() - location[1]) / drawDensity;
        int toolType = event.getToolType(0);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    float downDistance = spacing(event);
                    if (actionMode == ACTION_DRAG && downDistance > 10)//防止误触
                        actionMode = ACTION_SCALE;
                    break;
                case MotionEvent.ACTION_DOWN:
                    touch_down();
                    onTouchStart(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(event);
                    onTouchMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    onTouchEnd();
                    invalidate();
                    break;
            }

        preX = curX;
        preY = curY;
        return true;
    }

    public interface OnDrawChangedListener {

        public void onDrawChanged();
    }
}

