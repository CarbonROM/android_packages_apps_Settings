/*
 * Copyright (C) 2019 CarbonROM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.settings.carbon;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.widget.TextView;

import com.android.settings.R;

public class SchedulePreference extends Preference {
    private SunriseSunsetView mSunriseSunsetView;
    private TextView mSunriseView;
    private TextView mSunsetView;

    private static final int DEFAULT_VALUE = 1;

    public SchedulePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        setLayoutResource(R.layout.schedule);
    }

    public SchedulePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SchedulePreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mSunriseSunsetView = (SunriseSunsetView) holder.findViewById(R.id.sunriseSunset);
        mSunriseView = (TextView) holder.findViewById(R.id.sunriseTextView);
        mSunsetView = (TextView) holder.findViewById(R.id.sunsetTextView);
        holder.setDividerAllowedAbove(false);
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);
        this.setShouldDisableView(true);
        if (mSunriseSunsetView != null)
            mSunriseSunsetView.setEnabled(!disableDependent);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index) {
        int defaultValue = ta.getInt(index, DEFAULT_VALUE);
        return defaultValue;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        //do nothing for now
		return;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mSunriseSunsetView != null)
            mSunriseSunsetView.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    /**
     * Sets the color for the segment of the day where the
     * sun is above the horizon.
     *
     * @param color                 The sunrise color.
     */
    public void setSunriseColor(int color) {
        mSunriseSunsetView.setSunriseColor(color);
    }

    /**
     * @return The color of the segment of the day where the
     *         sun is above the horizon.
     */
    public int getSunriseColor() {
        return mSunriseSunsetView.getSunriseColor();
    }

    /**
     * Sets the color for the segment of the day where the
     * sun is below the horizon.
     *
     * @param color                 The sunset color.
     */
    public void setSunsetColor(int color) {
        mSunriseSunsetView.setSunsetColor(color);
    }

    /**
     * @return The color of the segment of the day where the
     *         sun is below the horizon.
     */
    public int getSunsetColor() {
        return mSunriseSunsetView.getSunsetColor();
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
    public void setFutureColor(int color) {
        mSunriseSunsetView.setFutureColor(color);
    }

    /**
     * @return The color of the segment of the day which
     *         has yet to occur.
     */
    public int getFutureColor() {
        return mSunriseSunsetView.getFutureColor();
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
        mSunriseSunsetView.setMinMax(minMinutes, maxMinutes);
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
        mSunriseSunsetView.setSunrise(dayStartMillis, animate);
    }

    /**
     * Calculate the sunrise time, in milliseconds. Returned values
     * will not range beyond a 24 hour period.
     *
     * @return The sunrise time, in milliseconds.
     */
    public long getSunrise() {
        return mSunriseSunsetView.getSunrise();
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
        mSunriseSunsetView.setSunset(dayEndMillis, animate);
    }

    /**
     * Calculate the sunset time, in milliseconds. Returned values
     * will not range beyond a 24 hour period.
     *
     * @return The sunset time, in milliseconds.
     */
    public long getSunset() {
        return mSunriseSunsetView.getSunset();
    }

}
