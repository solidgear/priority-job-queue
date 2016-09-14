/**
 The MIT License (MIT)
 Copyright (c) 2016 Solid Gear SL

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.solidgeargroup.priorityjobqueueexample.managers;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.solidgeargroup.priorityjobqueueexample.services.AppGcmJobService;
import com.solidgeargroup.priorityjobqueueexample.services.AppJobService;

/**
 * Created by jcmontesmartos on 2016/09/12.
 */
public class AppJobManager {

    private static JobManager mJobManager;

    public static synchronized JobManager getJobManager() {
        return mJobManager;
    }

    public static synchronized JobManager getJobManager(Context context) {
        if (mJobManager == null) {
            configureJobManager(context);
        }
        return mJobManager;
    }

    private static synchronized void configureJobManager(Context context) {
        if (mJobManager == null) {
            Configuration.Builder builder = new Configuration.Builder(context)
                    .minConsumerCount(1) // always keep at least one consumer alive
                    .maxConsumerCount(3) // up to 3 consumers at a time
                    .loadFactor(3) // 3 jobs per consumer
                    .consumerKeepAlive(120) // wait 2 minute
                    .customLogger(new CustomLogger() {
                        private static final String TAG = "JOBS";
                        @Override
                        public boolean isDebugEnabled() {
                            return true;
                        }

                        @Override
                        public void d(String text, Object... args) {
                            Log.d(TAG, String.format(text, args));
                        }

                        @Override
                        public void e(Throwable t, String text, Object... args) {
                            Log.e(TAG, String.format(text, args), t);
                        }

                        @Override
                        public void e(String text, Object... args) {
                            Log.e(TAG, String.format(text, args));
                        }

                        @Override
                        public void v(String text, Object... args) {

                        }
                    });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(context,
                        AppJobService.class), true);
            } else {
                int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
                if (enableGcm == ConnectionResult.SUCCESS) {
                    builder.scheduler(GcmJobSchedulerService.createSchedulerFor(context,
                            AppGcmJobService.class), true);
                }
            }
            mJobManager = new JobManager(builder.build());
        }
    }
}
