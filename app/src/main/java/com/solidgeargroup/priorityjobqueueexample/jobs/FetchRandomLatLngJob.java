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
import com.solidgeargroup.priorityjobqueueexample.model.LatLng;

import java.util.Random;

/**
 * Created by jcmontesmartos on 2016/09/12.
 */
public class FetchRandomLatLngJob extends Job {

    private static final double POINT_X = 37.474715;
    private static final double POINT_Y = -4.429496;
    private static final double POINT_RADIUS = 100000;

    public FetchRandomLatLngJob(String groupId) {
        super(new Params(JobConstants.PRIORITY_HIGH)
                .groupBy(groupId)
        );
    }

    @Override
    public void onAdded() {
        // Nothing to do
    }

    @Override
    public void onRun() throws Throwable {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = POINT_RADIUS / 111000f;

        double u = random.nextFloat();
        double v = random.nextFloat();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(POINT_Y);

        double foundLatitude = new_x + POINT_X;
        double foundLongitude = y + POINT_Y;
        LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);

        // Insert the location in our fake database
        FakeDatabase.setLastLatLng(getApplicationContext(), randomLatLng);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        // Impossible to fail
        // Nothing to do here
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.RETRY;
    }
}
