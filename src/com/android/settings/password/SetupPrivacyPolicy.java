/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright (C) 2018 CarbonROM
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

package com.android.settings.password;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.settings.R;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.GlifPreferenceLayout;
import com.android.setupwizardlib.DividerItemDecoration;

import org.carbonrom.carbonfibers.crstats.Utilities;

public class SetupPrivacyPolicy extends ChooseLockGeneric {
    private static final String UNIQUE_ID = "preview_id";
    private static final String DEVICE = "preview_device";
    private static final String VERSION = "preview_version";
    private static final String COUNTRY = "preview_country";
    private static final String CARRIER = "preview_carrier";

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SetupPrivacyPolicyFragment.class.getName().equals(fragmentName);
    }

    @Override
        /* package */ Class<? extends PreferenceFragment> getFragmentClass() {
        return SetupPrivacyPolicyFragment.class;
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        resid = SetupWizardUtils.getTheme(getIntent());
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_parent);
        layout.setFitsSystemWindows(false);
    }

    public static class SetupPrivacyPolicyFragment extends ChooseLockGenericFragment {
        private static final String ACCEPT_POLICY = "suw_accept";
        private static final String STATS_COLLECTION = "stats_collection";
        private static final String VIEW_PRIVACY_POLICY = "view_privacy_policy";

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            GlifPreferenceLayout layout = (GlifPreferenceLayout) view;
            DividerItemDecoration divider = new DividerItemDecoration(getContext());
            divider.setDivider(new ColorDrawable(Color.TRANSPARENT));
            layout.setDividerItemDecoration(divider);
            layout.setDividerInset(getContext().getResources().getDimensionPixelSize(
                    R.dimen.suw_items_glif_text_divider_inset));


            layout.setIcon(getContext().getDrawable(R.drawable.ic_suw_carbon));

            int titleResource = R.string.suw_privacy_policy_title;
            if (getActivity() != null) {
                getActivity().setTitle(titleResource);
            }

            layout.setHeaderText(titleResource);
            // Use the dividers in SetupWizardRecyclerLayout. Suppress the dividers in
            // PreferenceFragment.
            setDivider(null);
        }

        @Override
        protected void addHeaderView() {
            setHeaderView(R.layout.setup_privacy_policy_header);
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                                                 Bundle savedInstanceState) {
            GlifPreferenceLayout layout = (GlifPreferenceLayout) parent;
            return layout.onCreateRecyclerView(inflater, parent, savedInstanceState);
        }

        @Override
        protected void addPreferences() {
                addPreferencesFromResource(R.xml.setup_privacy_policy);

                // Generate collected data preview
                final PreferenceScreen prefSet = getPreferenceScreen();
                final Context context = getActivity();
                prefSet.findPreference(UNIQUE_ID).setSummary(Utilities.getUniqueID(context));
                prefSet.findPreference(DEVICE).setSummary(Utilities.getDevice());
                prefSet.findPreference(VERSION).setSummary(Utilities.getModVersion());
                prefSet.findPreference(COUNTRY).setSummary(Utilities.getCountryCode(context));
                prefSet.findPreference(CARRIER).setSummary(Utilities.getCarrier(context));
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            final String key = preference.getKey();
            switch (key) {
                case ACCEPT_POLICY:
                    try{
                        PackageManager localPackageManager = getPackageManager();
                        localPackageManager.setComponentEnabledSetting(
                                new ComponentName("com.android.settings", "com.android.settings.password.SetupPrivacyPolicy"),
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 1);
                        finish();
                    } catch (Exception e) {}
                    break;
                case STATS_COLLECTION:
                    TwoStatePreference stats = (TwoStatePreference) preference;
                    Settings.System.putInt(getActivity().getContentResolver(), key,
                            (stats.isChecked() ? 1 : 0));
                    break;
                case VIEW_PRIVACY_POLICY:
                    openPrivacyPolicy();
                    break;
                default:
                    break;
            }

            return true;
        }

        public void openPrivacyPolicy() {
            Uri webpage = Uri.parse("https://legal.carbonrom.org");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            getActivity().startActivity(intent);
        }
    }
}
