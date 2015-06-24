/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.android.settings.cyanogenmod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settings.ButtonSettings;
import com.android.settings.DisplaySettings;
import com.android.settings.hardware.VibratorIntensity;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.livedisplay.DisplayGamma;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final String CHARGE_SETTINGS_PROP = "sys.charge.restored";

    @Override
    public void onReceive(Context ctx, Intent intent) {

        /* Performance Misc crDroid */
        if (PerformanceSettings.FAST_CHARGE_PATH != null) {
            if (SystemProperties.getBoolean(CHARGE_SETTINGS_PROP, false) == false
                    && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                SystemProperties.set(CHARGE_SETTINGS_PROP, "true");
                configureCharge(ctx);
            } else {
                SystemProperties.set(CHARGE_SETTINGS_PROP, "false");
            }
        }

        /* Restore the hardware tunable values */
        DisplaySettings.restore(ctx);
        ButtonSettings.restoreKeyDisabler(ctx);
        DisplayGamma.restore(ctx);
        VibratorIntensity.restore(ctx);
        InputMethodAndLanguageSettings.restore(ctx);
    }
}
