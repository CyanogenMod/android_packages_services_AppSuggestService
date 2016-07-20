/*
 * Copyright (c) 2011-2015 CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.appsuggestservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Slog;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.suggest.ApplicationSuggestion;
import cyanogenmod.app.suggest.IAppSuggestManager;
import org.cyanogenmod.platform.internal.AppSuggestProviderInterface;
import org.cyanogenmod.platform.internal.AppSuggestProviderProxy;

import java.util.ArrayList;
import java.util.List;

public class AppSuggestService extends Service {

    private static final String TAG = AppSuggestService.class.getSimpleName();

    public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    public static final String NAME = "appsuggest";

    public static final String ACTION = "org.cyanogenmod.app.suggest";

    private AppSuggestProviderInterface mImpl;
    private Context mContext;

    private final IBinder mService = new IAppSuggestManager.Stub() {
        public boolean handles(Intent intent) {
            if (mImpl == null) return false;

            return mImpl.handles(intent);
        }

        public List<ApplicationSuggestion> getSuggestions(Intent intent) {
            if (mImpl == null) return new ArrayList<>(0);

            return mImpl.getSuggestions(intent);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mImpl = AppSuggestProviderProxy.createAndBind(mContext, TAG, ACTION,
                org.cyanogenmod.platform.internal.R.bool.config_enableAppSuggestOverlay,
                org.cyanogenmod.platform.internal.R.string.config_appSuggestProviderPackageName,
                org.cyanogenmod.platform.internal.R.array.config_appSuggestProviderPackageNames);
        if (mImpl == null) {
            Slog.e(TAG, "no app suggest provider found");
        } else {
            Slog.i(TAG, "Bound to to suggest provider");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }
}

