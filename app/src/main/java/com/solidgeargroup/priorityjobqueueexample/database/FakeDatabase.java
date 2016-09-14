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

package com.solidgeargroup.priorityjobqueueexample.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.solidgeargroup.priorityjobqueueexample.model.LatLng;

/**
 * Created by jcmontesmartos on 2016/09/12.
 */
public class FakeDatabase {

    private static final String FAKE_DB_NAME = "FAKE_DB_NAME";

    private static final String KEY_LAST_ADDRESS = "KEY_LAST_ADDRESS";
    private static final String KEY_LAST_LATLNG = "KEY_LAST_LATLNG";

    public static String getLastAddress(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAKE_DB_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(KEY_LAST_ADDRESS, "");
    }

    public static void setLastAddress(Context context, String address) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAKE_DB_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_LAST_ADDRESS, address);

        editor.commit();
    }

    public static LatLng getLastLatLng(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAKE_DB_NAME, Context.MODE_PRIVATE);

        String latLngJson = sharedPreferences.getString(KEY_LAST_LATLNG, "");

        return new Gson().fromJson(latLngJson, LatLng.class);
    }

    public static void setLastLatLng(Context context, LatLng latLng) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAKE_DB_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_LAST_LATLNG, new Gson().toJson(latLng));

        editor.commit();
    }
}
