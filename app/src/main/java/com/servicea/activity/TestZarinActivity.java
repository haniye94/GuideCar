package com.servicea.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;

import com.zarinpal.ZarinPalBillingClient;
import com.zarinpal.billing.purchase.Purchase;
import com.zarinpal.client.BillingClientStateListener;
import com.zarinpal.client.ClientState;
import com.zarinpal.provider.core.future.FutureCompletionListener;
import com.zarinpal.provider.core.future.TaskResult;
import com.zarinpal.provider.model.response.Receipt;

import ir.servicea.R;

public class TestZarinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_zarin);

        BillingClientStateListener stateListener = new BillingClientStateListener() {
            @Override
            public void onClientSetupFinished(ClientState state) {
                // Observing client states
            }

            @Override
            public void onClientServiceDisconnected() {
                Log.v("TAG_INAPP", "Billing client Disconnected");
                // When Service disconnect
            }
        };

        ZarinPalBillingClient client = ZarinPalBillingClient.newBuilder(this)
                .enableShowInvoice()
                .setListener(stateListener)
                .setNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                .build();


        Purchase purchase = Purchase.newBuilder()
                .asPaymentRequest(
                        "bbdb616f-3868-4399-94f6-0dc026c98ee4",
                        1000L,
                        "https://api.autoservicea.ir/",
                        "1000IRR Purchase"
                ).build();

        client.launchBillingFlow(purchase, new FutureCompletionListener<Receipt>() {
            @Override
            public void onComplete(TaskResult<Receipt> task) {
                if (task.isSuccess()) {
                    Receipt receipt = task.getSuccess();
                    Log.v("ZP_RECEIPT", receipt.getTransactionID());

                    // here you can send receipt data to your server
                    // sentToServer(receipt);

                } else {
                    if (task.getFailure() != null) {
                        task.getFailure().printStackTrace();
                    }
                }
            }

        });
    }
}