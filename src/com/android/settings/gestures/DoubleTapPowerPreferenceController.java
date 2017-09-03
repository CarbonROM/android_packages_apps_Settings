/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.settings.gestures;

import static android.provider.Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;

import com.android.settings.R;

public class DoubleTapPowerPreferenceController extends GesturePreferenceController {

    @VisibleForTesting
    static final int ON = 0;
    @VisibleForTesting
    static final int OFF = 1;

    private static final String PREF_KEY_VIDEO = "gesture_double_tap_power_video";

    private final String SECURE_KEY = CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED;

    public DoubleTapPowerPreferenceController(Context context, String key) {
        super(context, key);
    }

    public static boolean isSuggestionComplete(Context context, SharedPreferences prefs) {
        return !isGestureAvailable(context)
                || prefs.getBoolean(DoubleTapPowerSettings.PREF_KEY_SUGGESTION_COMPLETE, false);
    }

    private static boolean isGestureAvailable(Context context) {
        return context.getResources()
                .getBoolean(com.android.internal.R.bool.config_cameraDoubleTapPowerGestureEnabled);
    }

    @Override
    public int getAvailabilityStatus() {
        return isGestureAvailable(mContext) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "gesture_double_tap_power");
    }

    @Override
    protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override
    public boolean isChecked() {
        final int cameraDisabled = Settings.Secure.getInt(mContext.getContentResolver(),
                SECURE_KEY, ON);
        return cameraDisabled == ON;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        boolean returnValue = Settings.Secure.putInt(mContext.getContentResolver(), SECURE_KEY,
                isChecked ? ON : OFF);
        int previousTorchPref = Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.TORCH_POWER_BUTTON_GESTURE, 0);
        if (isChecked && (previousTorchPref == 1)) {
            // if double-tap for torch was active but double tap for camera is enabled here,
            // disable torch action and send a toast
            Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.TORCH_POWER_BUTTON_GESTURE, 0);
            Toast.makeText((Activity) mContext,
                    (R.string.gesture_double_tap_power_torch_toast),
                    Toast.LENGTH_SHORT).show();
        }
        return returnValue;
    }

}
