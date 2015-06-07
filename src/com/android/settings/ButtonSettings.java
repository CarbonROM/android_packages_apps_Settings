/*
 * Copyright (C) 2013 The CyanogenMod project
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

package com.android.settings;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.CmHardwareManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;

import com.android.internal.util.cm.ScreenType;

import com.android.settings.cyanogenmod.ButtonBacklightBrightness;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "SystemSettings";

    private static final String KEY_BUTTON_BACKLIGHT = "button_backlight";

    private static final String KEY_VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
    private static final String KEY_SWAP_VOLUME_BUTTONS = "swap_volume_buttons";
    private static final String KEY_ENABLE_HW_KEYS = "enable_hw_keys";
    private static final String KEY_NAVIGATION_BAR_LEFT = "navigation_bar_left";
    private static final String KEY_POWER_END_CALL = "power_end_call";
    private static final String KEY_HOME_ANSWER_CALL = "home_answer_call";
    private static final String KEY_VOLUME_MUSIC_CONTROLS = "volbtn_music_controls";
    private static final String KEY_VOLUME_CONTROL_RING_STREAM = "volume_keys_control_ring_stream";

    private static final String CATEGORY_POWER = "power_key";
    private static final String CATEGORY_CAMERA = "camera_key";
    private static final String CATEGORY_VOLUME = "volume_keys";
    private static final String CATEGORY_BACKLIGHT = "key_backlight";
    private static final String CATEGORY_HW_KEYS = "hw_keys";

    // Masks for checking presence of hardware keys.
    // Must match values in frameworks/base/core/res/res/values/config.xml
    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;
    public static final int KEY_MASK_ASSIST = 0x08;
    public static final int KEY_MASK_APP_SWITCH = 0x10;
    public static final int KEY_MASK_CAMERA = 0x20;
    public static final int KEY_MASK_VOLUME = 0x40;

    private SwitchPreference mCameraWakeScreen;
    private SwitchPreference mCameraSleepOnRelease;
    private SwitchPreference mCameraLaunch;
    private ListPreference mVolumeKeyCursorControl;
    private SwitchPreference mVolumeWakeScreen;
    private SwitchPreference mVolumeMusicControls;
    private SwitchPreference mVolumeControlRingStream;
    private SwitchPreference mSwapVolumeButtons;
    private SwitchPreference mEnableNavigationBar;
    private SwitchPreference mEnableHwKeys;
    private SwitchPreference mNavigationBarLeftPref;
    private ListPreference mNavigationRecentsLongPressAction;
    private SwitchPreference mPowerEndCall;
    private SwitchPreference mHomeAnswerCall;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.button_settings);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        // Power button ends calls.
        mPowerEndCall = (SwitchPreference) findPreference(KEY_POWER_END_CALL);

        // Home button answers calls.
        mHomeAnswerCall = (SwitchPreference) findPreference(KEY_HOME_ANSWER_CALL);

        mHandler = new Handler();

        // Navigation bar left
        mNavigationBarLeftPref = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_LEFT);

        // Enable/disable hw keys
        boolean enableHwKeys = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.ENABLE_HW_KEYS, 1) == 1;
        mEnableHwKeys = (SwitchPreference) findPreference(KEY_ENABLE_HW_KEYS);
        mEnableHwKeys.setChecked(enableHwKeys);
        mEnableHwKeys.setOnPreferenceChangeListener(this);
        // Check if this feature is enable through device config
        if(!getResources().getBoolean(com.android.internal.R.bool.config_hwKeysPref)) {
            PreferenceCategory hwKeysPref = (PreferenceCategory)
                    getPreferenceScreen().findPreference(CATEGORY_HW_KEYS);
            getPreferenceScreen().removePreference(hwKeysPref);
        }

        if (Utils.hasVolumeRocker(getActivity())) {
            int cursorControlAction = Settings.System.getInt(getContentResolver(),
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0);
            mVolumeKeyCursorControl = initActionList(KEY_VOLUME_KEY_CURSOR_CONTROL,
                    cursorControlAction);

            int swapVolumeKeys = Settings.System.getInt(getContentResolver(),
                    Settings.System.SWAP_VOLUME_KEYS_ON_ROTATION, 0);
            mSwapVolumeButtons = (SwitchPreference)
                    prefScreen.findPreference(KEY_SWAP_VOLUME_BUTTONS);
            if (mSwapVolumeButtons != null) {
                mSwapVolumeButtons.setChecked(swapVolumeKeys > 0);
            }
        }

        mCameraWakeScreen = (SwitchPreference) findPreference(Settings.System.CAMERA_WAKE_SCREEN);
        mCameraSleepOnRelease =
                (SwitchPreference) findPreference(Settings.System.CAMERA_SLEEP_ON_RELEASE);
        mCameraLaunch = (SwitchPreference) findPreference(Settings.System.CAMERA_LAUNCH);

        if (mCameraWakeScreen != null) {
            if (mCameraSleepOnRelease != null && !getResources().getBoolean(
                    com.android.internal.R.bool.config_singleStageCameraKey)) {
                mCameraSleepOnRelease.setDependency(Settings.System.CAMERA_WAKE_SCREEN);
            }
        }

        mVolumeWakeScreen = (SwitchPreference) findPreference(Settings.System.VOLUME_WAKE_SCREEN);
        mVolumeMusicControls = (SwitchPreference) findPreference(KEY_VOLUME_MUSIC_CONTROLS);

        mVolumeControlRingStream = (SwitchPreference)
                findPreference(KEY_VOLUME_CONTROL_RING_STREAM);
        int volumeControlRingtone = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_KEYS_CONTROL_RING_STREAM, 1);
        if (mVolumeControlRingStream != null) {
            mVolumeControlRingStream.setChecked(volumeControlRingtone > 0);
        }

        if (mVolumeWakeScreen != null) {
            if (mVolumeMusicControls != null) {
                mVolumeMusicControls.setDependency(Settings.System.VOLUME_WAKE_SCREEN);
                mVolumeWakeScreen.setDisableDependentsState(true);
            }
        }

        updateDisableHwKeysOption();
    }

    private static Map<String, String> getPreferencesToRemove(ButtonSettings settings,
                   Context context) {
        HashMap<String, String> result = new HashMap<String, String>();

        final ContentResolver resolver = context.getContentResolver();
        final Resources res = context.getResources();

        final int deviceKeys = res.getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);
        final int deviceWakeKeys = res.getInteger(
                com.android.internal.R.integer.config_deviceHardwareWakeKeys);

        final boolean hasPowerKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_POWER);
        final boolean hasCameraKey = (deviceKeys & KEY_MASK_CAMERA) != 0;

        final boolean showHomeWake = (deviceWakeKeys & KEY_MASK_HOME) != 0;
        final boolean showBackWake = (deviceWakeKeys & KEY_MASK_BACK) != 0;
        final boolean showMenuWake = (deviceWakeKeys & KEY_MASK_MENU) != 0;
        final boolean showAssistWake = (deviceWakeKeys & KEY_MASK_ASSIST) != 0;
        final boolean showAppSwitchWake = (deviceWakeKeys & KEY_MASK_APP_SWITCH) != 0;
        final boolean showCameraWake = (deviceWakeKeys & KEY_MASK_CAMERA) != 0;
        final boolean showVolumeWake = (deviceWakeKeys & KEY_MASK_VOLUME) != 0;

        final CmHardwareManager cmHardwareManager =
                (CmHardwareManager) context.getSystemService(Context.CMHW_SERVICE);

        if (hasPowerKey) {
            if (!Utils.isVoiceCapable(context)) {
                result.put(KEY_POWER_END_CALL, CATEGORY_POWER);
                if (settings != null) {
                    settings.mPowerEndCall = null;
                }
            }
        } else {
            result.put(CATEGORY_POWER, null);
        }

        if (hasCameraKey) {
          if (!showCameraWake) {
            result.put(Settings.System.CAMERA_WAKE_SCREEN, CATEGORY_CAMERA);
          }
          // Only show 'Camera sleep on release' if the device has a focus key
          if (res.getBoolean(com.android.internal.R.bool.config_singleStageCameraKey)) {
            result.put(Settings.System.CAMERA_SLEEP_ON_RELEASE, CATEGORY_CAMERA);
          }
        } else {
          result.put(CATEGORY_CAMERA, null);
        }

        if (Utils.hasVolumeRocker(context)) {
            if (!showVolumeWake) {
                result.put(Settings.System.VOLUME_WAKE_SCREEN, CATEGORY_VOLUME);
            }
        } else {
            result.put(CATEGORY_VOLUME, null);
        }

        if (!ButtonBacklightBrightness.isButtonSupported(context) &&
                !ButtonBacklightBrightness.isKeyboardSupported(context)) {
            result.put(KEY_BUTTON_BACKLIGHT, null);
        }

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Power button ends calls.
        if (mPowerEndCall != null) {
            final int incallPowerBehavior = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR,
                    Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_DEFAULT);
            final boolean powerButtonEndsCall =
                    (incallPowerBehavior == Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_HANGUP);
            mPowerEndCall.setChecked(powerButtonEndsCall);
        }

        // Home button answers calls.
        if (mHomeAnswerCall != null) {
            final int incallHomeBehavior = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.RING_HOME_BUTTON_BEHAVIOR,
                    Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_DEFAULT);
            final boolean homeButtonAnswersCall =
                (incallHomeBehavior == Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_ANSWER);
            mHomeAnswerCall.setChecked(homeButtonAnswersCall);
        }

    }

    private ListPreference initActionList(String key, int value) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference(key);
        if (list == null) return null;
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleActionListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        Settings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableHwKeys) {
            boolean hWkeysValue = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLE_HW_KEYS, hWkeysValue ? 1 : 0);
            writeDisableHwKeysOption(getActivity(), hWkeysValue);
            updateDisableHwKeysOption();
            return true;
        } else if (preference == mVolumeKeyCursorControl) {
            handleActionListChange(mVolumeKeyCursorControl, newValue,
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSwapVolumeButtons) {
            int value = mSwapVolumeButtons.isChecked()
                    ? (ScreenType.isTablet(getActivity()) ? 2 : 1) : 0;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SWAP_VOLUME_KEYS_ON_ROTATION, value);
        } else if (preference == mVolumeControlRingStream) {
            int value = mVolumeControlRingStream.isChecked() ? 1 : 0;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLUME_KEYS_CONTROL_RING_STREAM, value);
        } else if (preference == mPowerEndCall) {
            handleTogglePowerButtonEndsCallPreferenceClick();
            return true;
        } else if (preference == mHomeAnswerCall) {
            handleToggleHomeButtonAnswersCallPreferenceClick();
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private static void writeDisableHwKeysOption(Context context, boolean enabled) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final int defaultBrightness = context.getResources().getInteger(
                com.android.internal.R.integer.config_buttonBrightnessSettingDefault);

        Settings.Secure.putInt(context.getContentResolver(),
                Settings.Secure.ENABLE_HW_KEYS, enabled ? 1 : 0);

        CmHardwareManager cmHardwareManager =
                (CmHardwareManager) context.getSystemService(Context.CMHW_SERVICE);
        cmHardwareManager.set(CmHardwareManager.FEATURE_KEY_DISABLE, !enabled);

        /* Save/restore button timeouts to disable them in softkey mode */
        Editor editor = prefs.edit();

        if (!enabled) {
            int currentBrightness = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, defaultBrightness);
            if (!prefs.contains("pre_navbar_button_backlight")) {
                editor.putInt("pre_navbar_button_backlight", currentBrightness);
            }
            Settings.Secure.putInt(context.getContentResolver(),
                    Settings.Secure.BUTTON_BRIGHTNESS, 0);
        } else {
            int oldBright = prefs.getInt("pre_navbar_button_backlight", defaultBrightness);
            Settings.Secure.putInt(context.getContentResolver(),
                    Settings.Secure.BUTTON_BRIGHTNESS, oldBright);
            editor.remove("pre_navbar_button_backlight");
        }
        editor.commit();
    }

    private void updateDisableHwKeysOption() {
        boolean enabled = Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.ENABLE_HW_KEYS, 1) == 1;

        mEnableHwKeys.setChecked(enabled);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        final ButtonBacklightBrightness backlight =
                (ButtonBacklightBrightness) prefScreen.findPreference(KEY_BUTTON_BACKLIGHT);

        /* Toggle backlight control depending on hw keys state, force it to
           off if enabling */
        if (backlight != null) {
            backlight.setEnabled(enabled);
            backlight.updateSummary();
        }
    }

    public static void restoreKeyDisabler(Context context) {
        CmHardwareManager cmHardwareManager =
                (CmHardwareManager) context.getSystemService(Context.CMHW_SERVICE);
        if (!cmHardwareManager.isSupported(CmHardwareManager.FEATURE_KEY_DISABLE)) {
            return;
        }
        writeDisableHwKeysOption(context, Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.ENABLE_HW_KEYS, 1) == 1);
    }

    private void handleTogglePowerButtonEndsCallPreferenceClick() {
        Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR, (mPowerEndCall.isChecked()
                        ? Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_HANGUP
                        : Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF));
    }

    private void handleToggleHomeButtonAnswersCallPreferenceClick() {
        Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.RING_HOME_BUTTON_BEHAVIOR, (mHomeAnswerCall.isChecked()
                        ? Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_ANSWER
                        : Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_DO_NOTHING));
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.button_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();

                    Map<String, String> items = getPreferencesToRemove(null, context);
                    for (String key : items.keySet()) {
                        result.add(key);
                    }
                    return result;
                }
            };

}
