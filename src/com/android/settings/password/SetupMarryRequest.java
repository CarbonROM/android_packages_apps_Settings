/*
 * Copyright (C) 2014 The Android Open Source Project
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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.settings.R;
import com.android.settings.SetupWizardUtils;
import com.android.settings.utils.SettingsDividerItemDecoration;
import com.android.setupwizardlib.GlifPreferenceLayout;

/**
 * Setup Wizard's version of ChooseLockGeneric screen. It inherits the logic and basic structure
 * from ChooseLockGeneric class, and should remain similar to that behaviorally. This class should
 * only overload base methods for minor theme and behavior differences specific to Setup Wizard.
 * Other changes should be done to ChooseLockGeneric class instead and let this class inherit
 * those changes.
 */
public class SetupMarryRequest extends ChooseLockGeneric {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SetupMarryRequestFragment.class.getName().equals(fragmentName);
    }

    @Override
    /* package */ Class<? extends PreferenceFragment> getFragmentClass() {
        return SetupMarryRequestFragment.class;
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

    public static class SetupMarryRequestFragment extends ChooseLockGenericFragment {

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            GlifPreferenceLayout layout = (GlifPreferenceLayout) view;
            layout.setDividerItemDecoration(new SettingsDividerItemDecoration(getContext()));
            layout.setDividerInset(getContext().getResources().getDimensionPixelSize(
                    R.dimen.suw_items_glif_text_divider_inset));

            layout.setIcon(getContext().getDrawable(R.drawable.empty_icon));

            int titleResource = R.string.setup_marry_title;
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
            setHeaderView(R.layout.setup_marry_me_generic_header);
        }

        @Override
        protected void setuponCreate() {
            return;
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                Bundle savedInstanceState) {
            GlifPreferenceLayout layout = (GlifPreferenceLayout) parent;
            return layout.onCreateRecyclerView(inflater, parent, savedInstanceState);
        }

        @Override
        protected void addPreferences() {
            addPreferencesFromResource(R.xml.setup_marry_me_picker);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            final String key = preference.getKey();
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.marry_me_try_again_title);
            switch (key) {
                case "marry_me_yes":
                    try{
                        PackageManager localPackageManager = getPackageManager();
                        localPackageManager.setComponentEnabledSetting(new ComponentName("com.android.settings", "com.android.settings.password.SetupMarryRequest"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 1);

                    }
            } catch (Exception e) {}
                    break;
                case "marry_me_no":
                    builder.setMessage(R.string.marry_me_no_dialog);
                    break;
                default:
                    builder.setMessage(R.strings.marry_me_try_again_dialog);
                    break;
            }

            // add a button
            builder.setPositiveButton(R.string.lockpassword_ok_label, null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }
}
