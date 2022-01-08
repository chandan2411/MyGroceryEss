package com.essentials.customerapp.utilities;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.essentials.customerapp.FetchAddressIntentService;

public class LocationUtils {

    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    Activity context;
    AddressListener addressListener;


    public LocationUtils(Activity context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.context = context;
        addressListener = (AddressListener) context;
        fetchAddressHandler();
    }

    public interface AddressListener{
        void onAddressReceive(String address);
    }

    private void fetchAddressHandler() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastKnownLocation = location;
                        // In some rare cases the location returned can be null
                        if (lastKnownLocation == null) {
                            AppUtils.dismissProgressDialog();
                            return;
                        }
                        if (!Geocoder.isPresent()) {
                            AppUtils.dismissProgressDialog();
                            return;
                        }
                        startIntentService();
                    }
                });
    }

    private void startIntentService() {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, new AddressResultReceiver(new Handler()));
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastKnownLocation);
        context.startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        private String addressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }
            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            AppUtils.dismissProgressDialog();
            if (addressOutput == null) {
                addressOutput = "";
            }
            addressListener.onAddressReceive(addressOutput);
            new MySharedPref(context).writeString(PrefConstant.LOCATION_SELECTED, addressOutput);
        }
    }

}
