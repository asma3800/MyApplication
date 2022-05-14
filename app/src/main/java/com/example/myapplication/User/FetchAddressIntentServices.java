package com.example.myapplication.User;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;


public class FetchAddressIntentServices extends IntentService {
    ResultReceiver resultReceiver;

    public FetchAddressIntentServices() {
        super("FetchAddressIntentServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String errormessgae = "";
            resultReceiver = intent.getParcelableExtra(Constants.RECEVIER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if (location == null) {
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
            } catch (Exception ioException) {
                Log.e("", "Error in getting address for the location");
            }

            if (addresses == null || addresses.size() == 0) {
                errormessgae = "No address found for the location";
                Toast.makeText(this, "" + errormessgae, Toast.LENGTH_SHORT).show();
            } else {
                Address address = addresses.get(0);
                String str_postcode = address.getPostalCode();
                String str_Country = address.getCountryName();
                String str_state = address.getAdminArea();
                String str_district = address.getSubAdminArea();
                String str_locality = address.getLocality();
                String str_address = address.getFeatureName();
                devliverResultToRecevier(Constants.SUCCESS_RESULT, str_address, str_locality, str_district, str_state, str_Country, str_postcode);
            }
        }

    }

    private void devliverResultToRecevier(int resultcode, String address, String locality, String district, String state, String country, String postcode) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ADDRESS, address);
        bundle.putString(Constants.LOCAITY, locality);
        bundle.putString(Constants.DISTRICT, district);
        bundle.putString(Constants.STATE, state);
        bundle.putString(Constants.COUNTRY, country);
        bundle.putString(Constants.POST_CODE, postcode);
        resultReceiver.send(resultcode, bundle);
    }

}


class Constants {
    public static final String PACKAGE_NAME = "com.example.currentaddress";
    static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    static final String RECEVIER = PACKAGE_NAME + ".RECEVIER";
    static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    static final String ADDRESS = PACKAGE_NAME + ".ADDRESS";
    static final String LOCAITY = PACKAGE_NAME + ".LOCAITY";
    static final String COUNTRY = PACKAGE_NAME + ".COUNTRY";
    static final String DISTRICT = PACKAGE_NAME + ".DISTRICT";
    static final String POST_CODE = PACKAGE_NAME + ".POST_CODE";
    static final String STATE = PACKAGE_NAME + ".STATE";

    static final int SUCCESS_RESULT = 1;
    static final int FAILURE_RESULT = 0;
}

