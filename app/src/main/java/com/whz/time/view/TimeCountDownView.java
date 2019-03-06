package com.whz.time.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.whz.time.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kevin on 2018/7/3
 */
public class TimeCountDownView extends View {

    private final String aTag = TimeCountDownView.class.getSimpleName();

    //刻盘画笔
    private Paint mDailPaint;
    //时间画笔
    private Paint mTimePaint;
    //刻盘宽、高
    private int mWidth, mHeight;
    //刻盘半径
    private int mRadius;
    // 小时刻度高
    private float mHourScaleHeight;
    // 分钟刻度高
    private float mInuteScaleHeight;
    // 按下的角度
    private float mCurrentAngle;
    // 是否移动
    private boolean isMove;
    // 当前旋转的角度
    private float mRotateAngle;
    // 时间-分
    private int mTime = 0;
    // 定时进度条宽
    private float mArcWidth;
    //刻盘颜色
    private int mDailColor;
    //时间颜色
    private int mTimeColor;
    //时间文本大小
    private int mTextWidth;
    //间距
    private int mArgin;
    //默认颜色
    private int mDefaultColor = Color.parseColor("#3F51B5");

    private OnTimeCountDownListener mListener;

    public interface OnTimeCountDownListener {
        void countDown(int time);
    }

    public void setOnTimeCountDownListener(OnTimeCountDownListener listener) {
        this.mListener = listener;
    }

    public TimeCountDownView(Context context) {
        this(context, null, 0, 0);
    }

    public TimeCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimeCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
        initSetting();
    }

    /**
     * 初始化资源
     */
    private void init(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TimeCountDownView);
        //宽度、高度
        mArgin = array.getInt(R.styleable.TimeCountDownView_time_margin, dp2px(10));
        mArcWidth = array.getInt(R.styleable.TimeCountDownView_time_arc_width, dp2px(4));
        mTextWidth = array.getInt(R.styleable.TimeCountDownView_time_text_width, sp2px(33));
        mHourScaleHeight = array.getInt(R.styleable.TimeCountDownView_time_hour_height, dp2px(6));
        mInuteScaleHeight = array.getInt(R.styleable.TimeCountDownView_time_mius_height, dp2px(4));
        //颜色
        mDailColor = array.getColor(R.styleable.TimeCountDownView_time_dail_color, mDefaultColor);
        mTimeColor = array.getColor(R.styleable.TimeCountDownView_time_time_color, mDefaultColor);
    }

    /**
     * 初始化
     */
    private void initSetting() {
        mDailPaint = getPaint(mDailColor);
        mDailPaint.setStrokeCap(Paint.Cap.ROUND);

        mTimePaint = getPaint(mTimeColor);
        mTimePaint.setTextSize(mTextWidth);
    }

    /**
     * 设置画笔
     */
    private Paint getPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = mHeight = Math.min(w, h);
        mRadius = mWidth / 2 - mArgin;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //刻盘
        drawDail(canvas);
        //绘制定时进度条
        drawArc(canvas);
        //绘制时间
        drawTime(canvas);
    }

    /**
     * 绘制时间
     */
    private void drawTime(Canvas canvas) {
        canvas.restore();
        String timeText = String.format(Locale.CHINA, "%02d", mTime) + " : 00";
        // 获取时间的宽高
        float timeWidth = mTimePaint.measureText(timeText);
        float timeHeight = Math.abs(mTimePaint.ascent() + mTimePaint.descent());
        // 居中显示
        canvas.drawText(timeText, -timeWidth / 2, timeHeight / 2, mTimePaint);
    }

    /**
     * 绘制定时进度条
     */
    private void drawArc(Canvas canvas) {
        if (mTime > 0) {
            // 绘制起始标志
            mDailPaint.setStrokeWidth(dp2px(3));
            canvas.drawLine(0, -mRadius - mHourScaleHeight, 0, -mRadius + mHourScaleHeight, mDailPaint);
            // 取消直线圆角设置
            mDailPaint.setStrokeCap(Paint.Cap.BUTT);
            // 绘制进度
            for (int i = 0; i <= mTime * 6; i++) {
                canvas.drawLine(0, -mRadius - mArcWidth / 2, 0, -mRadius + mArcWidth / 2, mDailPaint);
                // 最后一次绘制后不旋转画布
                if (i != mTime * 6) {
                    canvas.rotate(1);
                }
            }
            // 绘制结束标志
            mDailPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(0, -mRadius - mHourScaleHeight, 0, -mRadius + mHourScaleHeight, mDailPaint);
        }
    }

    /**
     * 绘制刻盘
     */
    private void drawDail(Canvas canvas) {
        // 绘制外层圆盘
        mDailPaint.setStrokeWidth(dp2px(2));
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mDailPaint);
        // 将坐标原点移到控件中心
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.save();
        // 绘制小时刻度
        for (int i = 0; i < 12; i++) {
            // 定时时间为0时正常绘制小时刻度
            // 小时刻度没有被定时进度条覆盖时正常绘制小时刻度
            if (mTime == 0 || i > mTime / 5) {
                canvas.drawLine(0, -mRadius, 0, -mRadius + mHourScaleHeight, mDailPaint);
            }
            canvas.rotate(30);
        }
        // 绘制分钟刻度
        mDailPaint.setStrokeWidth(dp2px(1));
        for (int i = 0; i < 60; i++) {
            if (i % 5 != 0 && i > mTime) {
                canvas.drawLine(0, -mRadius, 0, -mRadius + mInuteScaleHeight, mDailPaint);
            }
            canvas.rotate(6);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentAngle = calcAngle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                // 移动的角度
                float moveAngle = calcAngle(event.getX(), event.getY());
                // 滑过的角度偏移量
                float offsetAngle = moveAngle - mCurrentAngle;
                // 防止越界
                if (offsetAngle < -270) {
                    offsetAngle = offsetAngle + 360;
                } else if (offsetAngle > 270) {
                    offsetAngle = offsetAngle - 360;
                }
                mCurrentAngle = moveAngle;
                // 计算时间
                calcTime(offsetAngle);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isMove && mListener != null) {
                    mListener.countDown(mTime);
                    isMove = false;
                }
                break;
        }
        return true;
    }

    /**
     * 计算需要到时的时间
     */
    private void calcTime(float offsetAngle) {
        mRotateAngle += offsetAngle;
        if (mRotateAngle < 0) {
            mRotateAngle = 0;
        } else if (mRotateAngle > 360) {
            mRotateAngle = 360;
        }
        mTime = (int) (mRotateAngle / 6);
        invalidate();
    }

    /**
     * 设置时间
     */
    public void setTimeCountDown(int time) {
        if (time < 0 || time > 60) {
            return;
        }
        mTime = time;
        invalidate();
    }

    /**
     * 开始倒计时
     */
    public void startCountDown(final Activity ctx) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTime--;
                        setTimeCountDown(mTime);
                        if (mTime == 0) {
                            timer.cancel();
                        }
                    }
                });

            }
        }, 1000, 1000);
    }

    /**
     * 以刻度盘圆心为坐标圆点，建立坐标系,求出(targetX, targetY)坐标与x轴的夹角
     */
    private float calcAngle(float targetX, float targetY) {
        // 以刻度盘圆心为坐标圆点
        float x = targetX - mWidth / 2;
        float y = targetY - mHeight / 2;
        // 滑过的弧度
        double radian;

        if (x != 0) {
            float tan = Math.abs(y / x);
            if (x > 0) {
                if (y >= 0) {
                    // 第四象限
                    radian = Math.atan(tan);
                } else {
                    // 第一象限
                    radian = 2 * Math.PI - Math.atan(tan);
                }
            } else {
                if (y >= 0) {
                    // 第三象限
                    radian = Math.PI - Math.atan(tan);
                } else {
                    // 第二象限
                    radian = Math.PI + Math.atan(tan);
                }
            }
        } else {
            if (y > 0) {
                // Y轴向下方向
                radian = Math.PI / 2;
            } else {
                // Y轴向上方向
                radian = Math.PI + Math.PI / 2;
            }
        }
        // 完整圆的弧度为2π，角度为360度，所以180度等于π弧度
        // 弧度 = 角度 / 180 * π
        // 角度 = 弧度 / π * 180
        return (float) (radian / Math.PI * 180);
    }

    /**
     * sp to px
     */
    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * dp to px
     */
    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
