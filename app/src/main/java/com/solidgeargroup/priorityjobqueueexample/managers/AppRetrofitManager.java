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

import com.google.gson.Gson;
import com.solidgeargroup.priorityjobqueueexample.BuildConfig;
import com.solidgeargroup.priorityjobqueueexample.networking.exceptions.ErrorRequestException;
import com.solidgeargroup.priorityjobqueueexample.networking.requests.GeocodingInterface;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jcmontesmartos on 2016/09/12.
 */
public class AppRetrofitManager {

    private static Retrofit mRetrofit;

    private static GeocodingInterface mGeocodingInterface;

    public static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.GOOGLE_API_MAPS)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .client(okHttpClient)
                    .build();

            mGeocodingInterface = mRetrofit.create(GeocodingInterface.class);
        }
        return mRetrofit;
    }

    public static <T> T performRequest(Call<T> request) throws IOException, ErrorRequestException {
        retrofit2.Response<T> response = request.execute();

        if (response == null || !response.isSuccessful() || response.errorBody() != null) {
            throw new ErrorRequestException(response);
        }

        return response.body();
    }

    public static GeocodingInterface getGeocodingInterface() {
        if (mGeocodingInterface == null) {
            getRetrofit();
        }
        return mGeocodingInterface;
    }
}
