package com.oway.ui.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.oway.R;
import com.oway.base.BaseActivity;
import com.oway.ui.home.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registration extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
    }

    @Override
    protected void setUp() {

    }

    public static void start(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, Registration.class);
        baseActivity.startActivity(intent);
    }
    @OnClick(R.id.bt_reg)
    public void onVC(){
        Intent intent=new Intent(Registration.this, RegisterPayment.class);
        startActivity(intent);
    }
}
