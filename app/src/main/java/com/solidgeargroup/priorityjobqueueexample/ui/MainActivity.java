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

package com.solidgeargroup.priorityjobqueueexample.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.solidgeargroup.priorityjobqueueexample.R;
import com.solidgeargroup.priorityjobqueueexample.databinding.ActivityMainBinding;
import com.solidgeargroup.priorityjobqueueexample.events.LocationFailedEvent;
import com.solidgeargroup.priorityjobqueueexample.events.LocationFetchedEvent;
import com.solidgeargroup.priorityjobqueueexample.jobs.FetchLocationByAddressJob;
import com.solidgeargroup.priorityjobqueueexample.jobs.FetchLocationByLatLngJob;
import com.solidgeargroup.priorityjobqueueexample.jobs.FetchRandomLatLngJob;
import com.solidgeargroup.priorityjobqueueexample.managers.AppJobManager;
import com.solidgeargroup.priorityjobqueueexample.model.AddressComponent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

/**
 * Created by jcmontesmartos on 2016/09/12.
 */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbar);

        mBinding.options.setOnCheckedChangeListener(this);
        mBinding.searchFab.setOnClickListener(this);

        mBinding.setSelectedOption(mBinding.options.getCheckedRadioButtonId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        if (mBinding.getSelectedOption() == R.id.radio_random_location) {

            String serialId = UUID.randomUUID().toString();
            AppJobManager.getJobManager().addJobInBackground(new FetchRandomLatLngJob(serialId));
            AppJobManager.getJobManager().addJobInBackground(new FetchLocationByLatLngJob(serialId));

            mBinding.addressLayout.setErrorEnabled(false);
        } else {

            String address = mBinding.address.getEditableText().toString().trim();

            if (address == null || address.length() == 0) {
                mBinding.addressLayout.setErrorEnabled(true);
                mBinding.addressLayout.setError(getString(R.string.error_empty_address));
                return;
            }

            mBinding.addressLayout.setErrorEnabled(false);

            AppJobManager.getJobManager().addJobInBackground(new FetchLocationByAddressJob(address));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        mBinding.setSelectedOption(checkedId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationFetchedEvent(LocationFetchedEvent event) {
        AddressComponent addressComponent = event.getAddressComponent();

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.ADDRESS_KEY, new Gson().toJson(addressComponent));
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationFailedEvent(LocationFailedEvent event) {
        Snackbar.make(mBinding.content, R.string.error_not_found, Snackbar.LENGTH_LONG).show();
    }

}