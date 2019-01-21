package com.example.walkmyandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location,Void,String> {

    private static final String TAG=FetchAddressTask.class.getSimpleName();
    private Context context;
    private OnTaskCompleted listener;

    FetchAddressTask(Context context, OnTaskCompleted listener){
        this.context=context;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(Location... locations) {
        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
        Location location=locations[0];
        List<Address> addresses=null;
        String resultMessage="";

        try{
            addresses=geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        }catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage = context.getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage = context.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }
        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = context.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        } else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join(
                    "\n",
                    addressParts);

        }

        return resultMessage;
    }

    @Override
    protected void onPostExecute(String address) {
        listener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}
