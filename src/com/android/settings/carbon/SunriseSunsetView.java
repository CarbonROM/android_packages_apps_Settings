package com.android.settings.carbon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

import android.annotation.ColorInt;
import android.annotation.Nullable;

import com.android.settings.R;

public class SunriseSunsetView extends View implements View.OnTouchListener {

    private static final float DAY_START = 0f;
    private static final float DAY_END = 0.99998842592f;
    private static final float DAY_HOUR = 0.04167f;

    private static final long DAY_LENGTH = 86400000L;

    private Paint sunrisePaint;
    private Paint sunsetPaint;
    private Paint linePaint;

    private AnimatedFloat dayStart;
    private AnimatedFloat dayEnd;

    private Float moveBeginStart;
    private Float moveBeginEnd;
    private float min = DAY_HOUR, max = DAY_END;

    private SunriseListener listener;

    public SunriseSunsetView(Context context) {
        super(context);
        init();
    }

    public SunriseSunsetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SunriseSunsetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dayStart = new AnimatedFloat(0.25f);
        dayEnd = new AnimatedFloat(0.75f);
        int accent = getResources().getColor(R.color.switch_accent_color);

        sunrisePaint = new Paint();
        sunrisePaint.setAntiAlias(true);
        sunrisePaint.setStyle(Paint.Style.FILL);
        sunrisePaint.setColor(accent);

        sunsetPaint = new Paint();
        sunsetPaint.setAntiAlias(true);
        sunsetPaint.setStyle(Paint.Style.FILL);
        sunsetPaint.setColor(accent);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(accent);
        linePaint.setAlpha(20);

        //setOnTouchListener(this);
        //setClickable(true);
        //setFocusable(true);
    }

    /**
     * Sets the color for the segment of the day where the
     * sun is above the horizon.
     *
     * @param color                 The sunrise color.
     */
    public void setSunriseColor(@ColorInt int color) {
        sunrisePaint.setColor(color);
        postInvalidate();
    }

    /**
     * @return The color of the segment of the day where the
     *         sun is above the horizon.
     */
    @ColorInt
    public int getSunriseColor() {
        return sunrisePaint.getColor();
    }

    /**
     * Sets the color for the segment of the day where the
     * sun is below the horizon.
     *
     * @param color                 The sunset color.
     */
    public void setSunsetColor(@ColorInt int color) {
        sunsetPaint.setColor(color);
        postInvalidate();
    }

    /**
     * @return The color of the segment of the day where the
     *         sun is below the horizon.
     */
    @ColorInt
    public int getSunsetColor() {
        return sunsetPaint.getColor();
    }

    /**
     * Sets the color for the segment of the day that has
     * not passed yet; some may refer to it as the future,
     * but what truly is the future but a moment in time
     * which has yet to occur? Since nobody truly knows the
     * exact outcome of a future event, the future cannot
     * possibly exist until it actually happens. With that
     * said, we refer to an event which we believe might
     * happen as the future, but there is no fail-safe method
     * of proving that said event will actually occur, short
     * of it occurring. Can one really say it is possible to
     * determine what is essentially an abstraction of a
     * future event with no uncertainty that it might not
     * occur?
     *
     * @param color                 The future color.
     */
    public void setFutureColor(@ColorInt int color) {
        linePaint.setColor(color);
        postInvalidate();
    }

    /**
     * @return The color of the segment of the day which
     *         has yet to occur.
     */
    @ColorInt
    public int getFutureColor() {
        return linePaint.getColor();
    }

    /**
     * Set the sunrise time, in milliseconds. Values can range
     * beyond the period of a day; they are modulated by a 24 hour
     * period. Change in values will not be animated.
     *
     * @param dayStartMillis            The sunrise time, in milliseconds.
     */
    public void setSunrise(long dayStartMillis) {
        setSunrise(dayStartMillis, false);
    }

    /**
     * Set the minimum and maximum distance allowed between sunrise
     * and sunset.
     *
     * @param minMinutes                The minimum distance, in minutes.
     * @param maxMinutes                The maximum distance, in minutes.
     */
    public void setMinMax(int minMinutes, int maxMinutes) {
        min = (float) minMinutes / 1440;
        max = (float) maxMinutes / 1440;
    }

    /**
     * Set the sunrise time, in milliseconds. Values can range
     * beyond the period of a day; they are modulated by a 24 hour
     * period.
     *
     * @param dayStartMillis            The sunrise time, in milliseconds.
     * @param animate                   Whether to animate the change in
     *                                  values.
     */
    public void setSunrise(long dayStartMillis, boolean animate) {
        dayStartMillis %= DAY_LENGTH;
        if (animate)
            dayStart.to((float) dayStartMillis / DAY_LENGTH);
        else dayStart.setCurrent((float) dayStartMillis / DAY_LENGTH);
        postInvalidate();
    }

    /**
     * Calculate the sunrise time, in milliseconds. Returned values
     * will not range beyond a 24 hour period.
     *
     * @return The sunrise time, in milliseconds.
     */
    public long getSunrise() {
        return (long) (dayStart.getTarget() * DAY_LENGTH);
    }

    /**
     * Set the sunset time, in milliseconds. Values can range
     * beyond the period of a day; they are modulated by a 24 hour
     * period. Change in values will not be animated.
     *
     * @param dayEndMillis              The sunset time, in milliseconds.
     */
    public void setSunset(long dayEndMillis) {
        setSunset(dayEndMillis, false);
    }

    /**
     * Set the sunset time, in milliseconds. Values can range
     * beyond the period of a day; they are modulated by a 24 hour
     * period.
     *
     * @param dayEndMillis              The sunset time, in milliseconds.
     * @param animate                   Whether to animate the change in
     *                                  values.
     */
    public void setSunset(long dayEndMillis, boolean animate) {
        dayEndMillis %= DAY_LENGTH;
        if (animate)
            dayEnd.to((float) dayEndMillis / DAY_LENGTH);
        else dayEnd.setCurrent((float) dayEndMillis / DAY_LENGTH);
        postInvalidate();
    }

    /**
     * Calculate the sunset time, in milliseconds. Returned values
     * will not range beyond a 24 hour period.
     *
     * @return The sunset time, in milliseconds.
     */
    public long getSunset() {
        return (long) (dayEnd.getTarget() * DAY_LENGTH);
    }

    /**
     * Specify an interface to receive updates when the sunrise/sunset
     * times are modified by the user. Methods in this interface are only
     * called when the view is interacted with; calling setSunset or
     * setSunrise will not result in this interface being notified.
     *
     * @param listener                  An interface to receive updates
     *                                  when the sunrise/sunset times
     *                                  are modified.
     */
    public void setListener(SunriseListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dayStart.next(true);
        dayEnd.next(true);

        float scaleX = getWidth() / 23f;
        float scaleY = getHeight() / 2f;
        float interval = (dayEnd.val() - dayStart.val()) / 2;
        float interval2 = (1 - dayEnd.val() + dayStart.val()) / 2;
        float start = dayStart.val() - (1 - dayEnd.val() + dayStart.val());
        interval *= 24 * scaleX;
        interval2 *= 24 * scaleX;
        start *= 24 * scaleX;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Path path = new Path();
        path.moveTo(start, scaleY);
        path.rQuadTo(interval2, scaleY * ((interval2 / interval + 1) / 2), interval2 * 2, 0);
        path.rQuadTo(interval, -scaleY * ((interval / interval2 + 1) / 2), interval * 2, 0);
        path.rQuadTo(interval2, scaleY * ((interval2 / interval + 1) / 2), interval2 * 2, 0);
        path.rQuadTo(interval, -scaleY * ((interval / interval2 + 1) / 2), interval * 2, 0);

        canvas.clipPath(path);
        canvas.drawRect(0, 0, (int) scaleX * hour, (int) scaleY, sunrisePaint);
        canvas.drawRect(0, (int) scaleY, (int) scaleX * hour, canvas.getHeight(), sunsetPaint);
        canvas.drawRect((int) scaleX * hour, 0, canvas.getWidth(), canvas.getHeight(), linePaint);

        if (!dayStart.isTarget() || !dayEnd.isTarget())
            postInvalidate();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float horizontalDistance = event.getX() / getWidth();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveBeginStart = null;
                moveBeginEnd = null;
                if (!dayStart.isTarget() || !dayEnd.isTarget())
                    break;

                if (Math.abs(horizontalDistance - dayStart.val()) < Math.abs(horizontalDistance - dayEnd.val()))
                    moveBeginStart = dayStart.val() - horizontalDistance;
                else moveBeginEnd = dayEnd.val() - horizontalDistance;

                break;
            case MotionEvent.ACTION_MOVE:
                if (moveBeginStart != null)
                    dayStart.to(Math.min(Math.min(dayEnd.getTarget() - DAY_HOUR, dayEnd.getTarget() - min),
                            Math.max(Math.max(DAY_START, dayEnd.getTarget() - max), moveBeginStart + horizontalDistance)));
                else if (moveBeginEnd != null)
                    dayEnd.to(Math.min(Math.min(DAY_END, dayStart.getTarget() + max), Math.max(dayStart.getTarget() + DAY_HOUR,
                            Math.max(moveBeginEnd + horizontalDistance, dayStart.getTarget() + min))));

                if (getParent() != null)
                    getParent().requestDisallowInterceptTouchEvent(true);

                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (listener != null) {
                    if (moveBeginStart != null)
                        listener.onSunriseChanged(this, getSunrise());
                    else if (moveBeginEnd != null)
                        listener.onSunsetChanged(this, getSunset());
                }

                if (getParent() != null)
                    getParent().requestDisallowInterceptTouchEvent(false);

                moveBeginStart = null;
                moveBeginEnd = null;
                break;
        }
        return false;
    }

    public interface SunriseListener {
        void onSunriseChanged(SunriseSunsetView view, long sunriseMillis);
        void onSunsetChanged(SunriseSunsetView view, long sunsetMillis);
    }
}

