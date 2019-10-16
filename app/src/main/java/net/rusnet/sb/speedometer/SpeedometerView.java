package net.rusnet.sb.speedometer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpeedometerView extends View {

    public static final int SPEEDOMETER_SIZE = 700;
    public static final float CIRCLE_PAINT_STROKE_WIDTH = 128f;
    public static final float START_ANGLE = 135;
    public static final float TOTAL_ANGLE = 270;
    public static final int ZERO = 0;
    public static final int ARROW_LENGTH = 350;
    public static final float ARROW_STROKE_WIDTH = 10f;
    public static final float SPEED_TEXT_Y_POSITION_RATIO = 9/10f;

    //default values
    public static final int LOW_SPEED = 60;
    public static final int MID_SPEED = 120;
    public static final int MAX_SPEED = 180;
    public static final int LOW_SPEED_COLOR = Color.GREEN;
    public static final int MID_SPEED_COLOR = Color.YELLOW;
    public static final int MAX_SPEED_COLOR = Color.RED;
    public static final int TEXT_COLOR = Color.BLACK;
    public static final int ARROW_COLOR = Color.BLACK;

    private int mLowSpeed;
    private int mMidSpeed;
    private int mMaxSpeed;
    private int mLowSpeedColor;
    private int mMidSpeedColor;
    private int mMaxSpeedColor;
    private int mTextSize;
    private int mTextColor;
    private int mArrowColor;
    private int mCurrentSpeed = ZERO;

    private Paint mSpeedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF mSpeedometerRect = new RectF(0, 0, SPEEDOMETER_SIZE, SPEEDOMETER_SIZE);
    private Rect mTextBounds = new Rect();

    public SpeedometerView(Context context) {
        super(context);
        init(context, null);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setMaxSpeed(int maxSpeed) {
        mMaxSpeed = maxSpeed;
        invalidate();
    }

    public void setLowSpeedColor(int lowSpeedColor) {
        mLowSpeedColor = lowSpeedColor;
        invalidate();
    }

    public void setMidSpeedColor(int midSpeedColor) {
        mMidSpeedColor = midSpeedColor;
        invalidate();
    }

    public void setMaxSpeedColor(int maxSpeedColor) {
        mMaxSpeedColor = maxSpeedColor;
        invalidate();
    }

    public void setArrowColor(int arrowColor) {
        mArrowColor = arrowColor;
        invalidate();
    }

    public void setCurrentSpeed(int currentSpeed) {
        mCurrentSpeed = currentSpeed;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSpeedometerScale(canvas);
        drawSpeedometerText(canvas);
        drawSpeedometerArrow(canvas);
    }

    private void init(Context context, AttributeSet attrs) {
        extractAttributes(context, attrs);
        configureTextPaint();
        configureSpeedometerPaint();
        configureArrowPaint();
    }

    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        final Resources.Theme theme = context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(
                attrs,
                R.styleable.SpeedometerView,
                R.attr.speedometerStyle,
                0);
        try {
            mLowSpeed = typedArray.getInteger(R.styleable.SpeedometerView_low_speed, LOW_SPEED);
            mMidSpeed = typedArray.getInteger(R.styleable.SpeedometerView_mid_speed, MID_SPEED);
            mMaxSpeed = typedArray.getInteger(R.styleable.SpeedometerView_max_speed, MAX_SPEED);
            mLowSpeedColor = typedArray.getInteger(R.styleable.SpeedometerView_low_speed_color, LOW_SPEED_COLOR);
            mMidSpeedColor = typedArray.getInteger(R.styleable.SpeedometerView_mid_speed_color, MID_SPEED_COLOR);
            mMaxSpeedColor = typedArray.getInteger(R.styleable.SpeedometerView_max_speed_color, MAX_SPEED_COLOR);
            mTextSize = typedArray.getDimensionPixelSize(
                    R.styleable.SpeedometerView_text_size,
                    getResources().getDimensionPixelSize(R.dimen.defaultTextSize));
            mTextColor = typedArray.getInteger(R.styleable.SpeedometerView_text_color, TEXT_COLOR);
            mArrowColor = typedArray.getInteger(R.styleable.SpeedometerView_arrow_color, ARROW_COLOR);
        } finally {
            typedArray.recycle();
        }
    }

    private void configureTextPaint() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    private void configureSpeedometerPaint() {
        mSpeedometerPaint.setStrokeWidth(CIRCLE_PAINT_STROKE_WIDTH);
        mSpeedometerPaint.setStyle(Paint.Style.STROKE);
    }

    private void configureArrowPaint() {
        mArrowPaint.setColor(mArrowColor);
        mArrowPaint.setStyle(Paint.Style.STROKE);
        mArrowPaint.setStrokeWidth(ARROW_STROKE_WIDTH);
    }

    private void drawSpeedometerArrow(Canvas canvas) {
        float centerX = mSpeedometerRect.width() / 2f;
        float centerY = mSpeedometerRect.height() / 2f;
        float speedArrowAngle = START_ANGLE + TOTAL_ANGLE * mCurrentSpeed/mMaxSpeed;
        float endX = centerX + (float) (ARROW_LENGTH * Math.cos(Math.toRadians(speedArrowAngle)));
        float endY = centerY + (float) (ARROW_LENGTH * Math.sin(Math.toRadians(speedArrowAngle)));
        canvas.drawLine(centerX, centerY, endX, endY,mArrowPaint);
    }

    private void drawSpeedometerText(Canvas canvas) {
        final String speedString = String.format(
                getResources().getString(R.string.speed_template), mCurrentSpeed, mMaxSpeed);
        mTextPaint.getTextBounds(speedString, 0, speedString.length(), mTextBounds);
        float x = mSpeedometerRect.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left;
        float y = mSpeedometerRect.height() * SPEED_TEXT_Y_POSITION_RATIO - mTextBounds.bottom;
        canvas.drawText(speedString, x, y, mTextPaint);
    }

    private void drawSpeedometerScale(Canvas canvas) {
        canvas.translate(CIRCLE_PAINT_STROKE_WIDTH/2,CIRCLE_PAINT_STROKE_WIDTH/2);
        drawSpeedometerSegment(canvas, mLowSpeedColor, ZERO, mLowSpeed);
        drawSpeedometerSegment(canvas, mMidSpeedColor, mLowSpeed, mMidSpeed);
        drawSpeedometerSegment(canvas, mMaxSpeedColor, mMidSpeed, mMaxSpeed);
    }

    private void drawSpeedometerSegment(Canvas canvas, int color, int previousSpeed, int nextSpeed) {
        mSpeedometerPaint.setColor(color);
        canvas.drawArc(mSpeedometerRect,
        START_ANGLE + (TOTAL_ANGLE/mMaxSpeed*previousSpeed), (TOTAL_ANGLE/mMaxSpeed*(nextSpeed - previousSpeed)), false, mSpeedometerPaint);
    }
}
