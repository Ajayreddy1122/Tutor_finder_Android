package com.saveetha.tutorfinder.student;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.saveetha.tutorfinder.AppConstants;
import com.saveetha.tutorfinder.LoginActivity;
import com.saveetha.tutorfinder.MainActivity;
import com.saveetha.tutorfinder.R;
import com.saveetha.tutorfinder.RestClient;
import com.saveetha.tutorfinder.model.paymentapi.PaymentResponse;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity  extends AppCompatActivity implements PaymentResultListener {
    AppCompatImageView back_image;
    AppCompatTextView topbar_text;
    TextView pacNameTextView,creditsTextView,costTextView;
    String packageName,credits,cost,packageId,is_user_logged,userName,userEmail;
    ProgressBar progress;

    @SuppressLint("WrongViewCast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        back_image  = findViewById(R.id.back_icon);
        topbar_text = findViewById(R.id.bar_text);
        progress    = findViewById(R.id.progress);

        topbar_text.setText("Check Out");
        Intent intent=getIntent();
         packageName=intent.getStringExtra("package_name");
         credits= intent.getStringExtra("credits");
         cost=intent.getStringExtra("package_cost");
         packageId=intent.getStringExtra("package_id");
         SharedPreferences sf = getSharedPreferences(AppConstants.SHARED_PREF_NAME, MODE_PRIVATE);
         is_user_logged = sf.getString(AppConstants.KEY_ID, null);
         userName=sf.getString(AppConstants.KEY_NAME,null);
         userEmail=sf.getString(AppConstants.KEY_EMAIL,null);
        pacNameTextView=findViewById(R.id.showPackageName);
        this.creditsTextView= findViewById(R.id.showCredits);
         this.costTextView=findViewById(R.id.showCost);
        pacNameTextView.setText(packageName);
        this.creditsTextView.setText(credits);
        this.costTextView.setText(cost);
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });
        findViewById(R.id.payNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                payment(packageName,Long.parseLong(cost),userName,userEmail);
            }
        });
    }
    @SuppressLint("RestrictedApi")
    private void payment(String packageName, long amount,String username,String userEmail)
    {
        // initialize Razorpay account.
        Checkout checkout = new Checkout();

        // set your id as below
        checkout.setKeyID("rzp_test_U2XWpODmhRkL0l" +
                "");

        // set image
        checkout.setImage(R.drawable.profile);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", username);

            // put description
            object.put("description", "Test payment");

            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", (amount*100));

            // put mobile number
            object.put("prefill.contact", "");

            // put package name
            object.put("package name",packageName);

            // put email
            object.put("prefill.email", userEmail);

            // open razorpay to checkout activity
            checkout.open(this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        retrofit2.Call<PaymentResponse> responseCall= RestClient.makeAPI().payment(is_user_logged,packageId,s,cost);
        responseCall.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if(response.isSuccessful())
                {
                    if(response.body().getStatus()==200)
                    {
                        Toast.makeText(PaymentActivity.this,"Payment Success",Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);

                    }
                    else {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(PaymentActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(PaymentActivity.this,"server busy", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(PaymentActivity.this,"check your internet connection",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        progress.setVisibility(View.GONE);
        Toast.makeText(this,"Payment Failure",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

}
