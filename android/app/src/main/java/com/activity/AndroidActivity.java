package com.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.braintreepayments.api.VenmoClient;
import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.VenmoPaymentMethodUsage;
import com.braintreepayments.api.VenmoRequest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import javax.annotation.Nullable;

public class AndroidActivity extends AppCompatActivity {

    BraintreeClient braintreeClient;
    VenmoClient venmoClient;
    DataCollector dataCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ReactNative","AndroidActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);
//        Intent intent = getIntent();
        String authorization = "______REPLCAE_WITH_ACTUAL_TOKEN_____";
        braintreeClient = new BraintreeClient(this, authorization);
        venmoClient = new VenmoClient(braintreeClient);
        dataCollector = new DataCollector(braintreeClient);
        this.tokenizeVenmoAccount();
    }

    private void tokenizeVenmoAccount() {
        VenmoRequest request = new VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE);
        request.setShouldVault(false);

        venmoClient.tokenizeVenmoAccount( this, request, (error) -> {
            if (error != null) {
                Intent intent = getIntent();

                intent.putExtra("ERROR", error.getMessage());
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ReactNative","AndroidActivity onActivityResult");
        venmoClient.onActivityResult(this, resultCode, data, (venmoAccountNonce, venmoError) -> {
            dataCollector.collectDeviceData(AndroidActivity.this, (deviceData, dataCollectorError) -> {
                // send venmoAccountNonce.getString() and deviceData to server
                Intent resultIntent = new Intent();
                if(dataCollectorError != null) {
                    resultIntent.putExtra("DATA_COLLECTOR_ERROR", dataCollectorError.getMessage());
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                } else if(venmoError != null) {
                    resultIntent.putExtra("ERROR", venmoError.getMessage());
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }else {
                    resultIntent.putExtra("DEVICE_DATA", deviceData);
                    resultIntent.putExtra("VENMO_ACCOUNT_NONCE", venmoAccountNonce.getString());
                    resultIntent.putExtra("VENMO_ACCOUNT_USERNAME", venmoAccountNonce.getUsername());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

            });
        });
        Log.d("ReactNative","AndroidActivity onActivityResult 2");
    }
}