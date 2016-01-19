/*
 * Copyright (C) 2016 CarbonROM
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

package com.android.settings.carbon;

import android.content.Context;
import android.support.v7.preference.PreferenceScreen;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.carbon.logging.CarbonMetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.List;


public class CRPrivacySettings extends SettingsPreferenceFragment implements Indexable {

    @Override
    protected int getMetricsCategory() {
        return CarbonMetricsLogger.PRIVACY_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();

        createPreferenceHierarchy();
    }

     /**
     * Important!
     *
     * Dont forget to update the SecuritySearchIndexProvider if you are doing any change in the
     * logic or adding/removing preferences here.
     */

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceScreen();
        if (root != null) {
            root.removeAll();
        }

        addPreferencesFromResource(R.xml.cr_privacy_settings);

        root = getPreferenceScreen();

        return root;
    }

    /**
     * For Search. Please keep it in sync when updating "createPreferenceHierarchy()"
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            (SearchIndexProvider) new SecuritySearchIndexProvider();

    static private class SecuritySearchIndexProvider extends BaseSearchIndexProvider {

        public SecuritySearchIndexProvider() {
            super();

        }

        @Override
        public List<SearchIndexableResource> getXmlResourcesToIndex(
                Context context, boolean enabled) {

            List<SearchIndexableResource> result = new ArrayList<SearchIndexableResource>();

            SearchIndexableResource sir = new SearchIndexableResource(context);
            sir.xmlResId = R.xml.cr_privacy_settings;
            result.add(sir);

            return result;
        }

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> keys = new ArrayList<String>();

            return keys;
        }
    }
}
