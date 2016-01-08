package com.android.settings.carbon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

// fragments import for entry
import com.android.settings.*;
import com.android.settings.carbon.*;
import com.android.settings.carbon.gestureanywhere.*;
import com.android.settings.carbon.hideappfromrecents.*;
import com.android.settings.cyanogenmod.*;
import com.android.settings.cyanogenmod.qs.*;
import com.android.settings.slim.*;
import com.android.settings.slim.dslv.*;
import com.android.settings.slim.fragments.*;
import com.android.settings.slim.util.*;
import com.android.settings.carbon.*;
import com.android.settings.carbon.widgets.*;

public class CarbonSettingsActivity extends PreferenceActivity {

    private static final String TAG = "CR_Settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String[] ENTRY_FRAGMENTS = {
        ActionListViewSettings.class.getName(),
        AnimationControls.class.getName(),
        AppCircleBar.class.getName(),
        AppSidebar.class.getName(),
        BacklightTimeoutSeekBar.class.getName(),
        ButtonBacklightBrightness.class.getName(),
        ButtonSettings.class.getName(),
        CarbonAbout.class.getName(),
        CarbonButtons.class.getName(),
        CarbonInterface.class.getName(),
        CarbonMoreDeviceSettings.class.getName(),
        CarbonNotificationDrawerSettings.class.getName(),
        CarbonRecentsSettings.class.getName(),
        CarbonStatusBar.class.getName(),
        DeveloperPreference.class.getName(),
        DeviceUtils.class.getName(),
        DisplayAnimationsSettings.class.getName(),
        DragSortController.class.getName(),
        DragSortItemView.class.getName(),
        DragSortListView.class.getName(),
        GestureAnywhereSettings.class.getName(),
        HAFRAppListActivity.class.getName(),
        NavBar.class.getName(),
        NavBarStyleDimen.class.getName(),
        NavRing.class.getName(),
        NetworkTraffic.class.getName(),
        NetworkTrafficFragment.class.getName(),
        NotificationDrawerSettings.class.getName(),
        OverscrollEffects.class.getName(),
        PowerMenuActions.class.getName(),
        QuickTileFragment.class.getName(),
        QSColors.class.getName(),
        QSTiles.class.getName(),
        RecentPanel.class.getName(),
        ScrollAnimationInterfaceSettings.class.getName(),
        ShortcutPickerHelper.class.getName(),
        SimpleFloatViewManager.class.getName(),
        StatusBarBatteryStatusSettings.class.getName(),
        StatusBarClockStyle.class.getName(),
        StatusBarExpandedHeaderSettings.class.getName(),
        StatusBarNotifSystemIconsSettings.class.getName(),
        StatusBarSettings.class.getName(),
        StatusBarSignalSettings.class.getName(),
        SystemappRemover.class.getName(),
        WakeLockBlocker.class.getName()
    };

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Almost all fragments are wrapped in this,
        // except for a few that have their own activities.
        for (int i = 0; i < ENTRY_FRAGMENTS.length; i++) {
            if (ENTRY_FRAGMENTS[i].equals(fragmentName)) return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
