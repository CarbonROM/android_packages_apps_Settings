/*
 * Copyright (C) 2020 The LineageOS Project
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
 * limitations under the License.
 */

package com.android.settings.display;

import static android.provider.Settings.System.MIN_REFRESH_RATE;
import static android.provider.Settings.System.PEAK_REFRESH_RATE;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MinRefreshRatePreferenceController extends BasePreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "RefreshRateControl";
    private static final String KEY_MIN_REFRESH_RATE = "min_refresh_rate";

    private ListPreference mListPreference;

    public MinRefreshRatePreferenceController(Context context) {
        super(context, KEY_MIN_REFRESH_RATE);
    }

    @Override
    public int getAvailabilityStatus() {
        return mContext.getResources().getBoolean(R.bool.config_show_min_refresh_rate_switch) &&
                mListPreference != null && mListPreference.getEntries().length > 1
                        ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_MIN_REFRESH_RATE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mListPreference = screen.findPreference(getPreferenceKey());
	Log.d(TAG, "inside displaypreference()");

        List<String> entries = new ArrayList<>(), values = new ArrayList<>();
        Display.Mode mode = mContext.getDisplay().getMode();
        Display.Mode[] modes = mContext.getDisplay().getSupportedModes();
        for (Display.Mode m : modes) {
            if (m.getPhysicalWidth() == mode.getPhysicalWidth() &&
                    m.getPhysicalHeight() == mode.getPhysicalHeight()) {
		entries.add(String.format("%d", Math.round(m.getRefreshRate())));
                values.add(String.format("%d", Math.round(m.getRefreshRate())));
            }
        }
	Log.d(TAG, "inside displaypreference() setting entries and values");
        mListPreference.setEntries(entries.toArray(new String[entries.size()]));
        mListPreference.setEntryValues(values.toArray(new String[values.size()]));

        super.displayPreference(screen);
    }

    public void updateState(int newRefreshRate) {
	Log.d(TAG, "entering updateState() with value, newRefreshRate: " + newRefreshRate);
        int index = mListPreference.findIndexOfValue(Integer.toString(newRefreshRate));
	if (index < 0) index = 0;
	Log.d(TAG, "finding index value, newRefreshRate: " + newRefreshRate + " index value is " + index);
        mListPreference.setValueIndex(index);
        mListPreference.setSummary(mListPreference.getEntries()[index]);
	updateRefreshRate(newRefreshRate);
	Log.d(TAG, "calling updateRefreshRate() with value, newRefreshRate: " + newRefreshRate);
	Log.d(TAG, "exiting updateState() after updating index value " + index);
    }

    public void updateRefreshRate(int newRefreshRate) {
	Log.d(TAG, "entering updateRefreshRate() with value, newRefreshRate: " + newRefreshRate);
	switch(newRefreshRate) {
            default :
            case 60 :  Settings.System.putInt(mContext.getContentResolver(), MIN_REFRESH_RATE,60);
		       Settings.System.putInt(mContext.getContentResolver(), PEAK_REFRESH_RATE,60);
		       Log.d(TAG, "case 60");
                       break;
            case 90 :  Settings.System.putInt(mContext.getContentResolver(), MIN_REFRESH_RATE,90);
		       Settings.System.putInt(mContext.getContentResolver(), PEAK_REFRESH_RATE,90);
		       Log.d(TAG, "case 90");
                       break;
            case 120:  Settings.System.putInt(mContext.getContentResolver(), MIN_REFRESH_RATE,120);
		       Settings.System.putInt(mContext.getContentResolver(), PEAK_REFRESH_RATE,120);
		       Log.d(TAG, "case 120");
                       break;
            case 144:  Settings.System.putInt(mContext.getContentResolver(), MIN_REFRESH_RATE,144);
		       Settings.System.putInt(mContext.getContentResolver(), PEAK_REFRESH_RATE,144);
		       Log.d(TAG, "case 144");
                       break;
            case 160:  Settings.System.putInt(mContext.getContentResolver(), MIN_REFRESH_RATE,160);
		       Settings.System.putInt(mContext.getContentResolver(), PEAK_REFRESH_RATE,160);
		       Log.d(TAG, "case 160");
                       break;
	}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	Log.d(TAG, "entering onpreferencechange()");
        int newRefreshRate = Integer.valueOf((String) newValue);
	Log.d(TAG, "new value updating, newRefreshRate: " + newRefreshRate);
        updateState(newRefreshRate);
        return true;
    }
}
