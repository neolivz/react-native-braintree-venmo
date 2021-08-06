package com.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

public class OpenActivityModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext = null;

    public OpenActivityModule(ReactApplicationContext reactContext){
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    private static final String AUTH_STRING  = "";

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_ACTIVITY_ERROR = "E_ACTIVITY_ERROR";

    private Promise venmoPromise;



    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public String getName() {
        return "OpenActivity";
    }


    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
		public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
			if (venmoPromise != null) {
					String error = intent.getStringExtra("ERROR");
					String dataCollectorError = intent.getStringExtra("DATA_COLLECTOR_ERROR");
					

					if(error != null) {
						venmoPromise.reject(E_ACTIVITY_ERROR, error);
					} else if(dataCollectorError != null) {
						venmoPromise.reject(E_ACTIVITY_ERROR, dataCollectorError);
					}
					String deviceData = intent.getStringExtra("DEVICE_DATA");
					String nonce = intent.getStringExtra("VENMO_ACCOUNT_NONCE");
					String username = intent.getStringExtra("VENMO_ACCOUNT_USERNAME");
					WritableMap map = Arguments.createMap();
                    map.putString("deviceData", deviceData);
                    map.putString("nonce", nonce);
                    map.putString("username", username);

					venmoPromise.resolve(map);

					venmoPromise = null;
			}
		}
	};


    @ReactMethod
    public void open() {
        Intent intent = new Intent(getCurrentActivity(), AndroidActivity.class);
        getCurrentActivity().startActivity(intent);
    }

    @ReactMethod
    public void authorizeVenmo(Promise promise) {
        try{
            Activity currentActivity = getCurrentActivity();
            if (currentActivity == null) {
                promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
                return;
            }

            Intent intent = new Intent(reactContext, AndroidActivity.class);
            intent.putExtra("CLIENT_AUTHORIZATION", AUTH_STRING);
            currentActivity.startActivityForResult(intent, 1);

            // Store the promise to resolve/reject when picker returns data
            venmoPromise = promise;
        }catch(Exception e){
            throw new JSApplicationIllegalArgumentException(
                    "Can not open Activity:"+e.getMessage());
        }
    }
}

