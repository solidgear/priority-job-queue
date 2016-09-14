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

package com.solidgeargroup.priorityjobqueueexample.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.solidgeargroup.priorityjobqueueexample.database.FakeDatabase;
import com.solidgeargroup.priorityjobqueueexample.events.LocationFailedEvent;
import com.solidgeargroup.priorityjobqueueexample.events.LocationFetchedEvent;
import com.solidgeargroup.priorityjobqueueexample.managers.AppRetrofitManager;
import com.solidgeargroup.priorityjobqueueexample.model.Geocode;
import com.solidgeargroup.priorityjobqueueexample.networking.exceptions.ErrorRequestException;
import com.solidgeargroup.priorityjobqueueexample.networking.requests.GeocodingInterface;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;

/**
 * Created by jcmontesmartos on 2016/09/12.
 */
public class FetchLocationByAddressJob extends Job {

    private String mAddress;

    public static final String TAG = FetchLocationByAddressJob.class.getCanonicalName();

    public FetchLocationByAddressJob(String address) {
        super(new Params(JobConstants.PRIORITY_NORMAL)
                .requireNetwork()
                .singleInstanceBy(TAG)
                .addTags(TAG)
        );

        mAddress = address;
    }

    @Override
    public void onAdded() {
        // Store address in database
        FakeDatabase.setLastAddress(getApplicationContext(), mAddress);
    }

    @Override
    public void onRun() throws Throwable {
        String address = FakeDatabase.getLastAddress(getApplicationContext());

        GeocodingInterface service = AppRetrofitManager.getGeocodingInterface();

        Call<Geocode> request = service.getInfoByAddress(address);
        Geocode geocode = AppRetrofitManager.performRequest(request);

        // Ensure we have information about the search address
        if (geocode == null || geocode.getResults().size() <= 0) {
            EventBus.getDefault().post(new LocationFailedEvent());
            return;
        }

        // Just retrieve the first result
        EventBus.getDefault().post(new LocationFetchedEvent(geocode.getResults().get(0)));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        EventBus.getDefault().post(new LocationFailedEvent());
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        if (throwable instanceof ErrorRequestException) {

            ErrorRequestException error = (ErrorRequestException) throwable;

            int statusCode = error.getResponse().raw().code();

            if (statusCode >= 400 && statusCode < 500) {
                return RetryConstraint.CANCEL;
            }
        }

        return RetryConstraint.RETRY;
    }
}
