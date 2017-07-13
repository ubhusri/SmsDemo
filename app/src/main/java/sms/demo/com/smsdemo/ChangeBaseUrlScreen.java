package sms.demo.com.smsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ChangeBaseUrlScreen extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_base_url_screen);
        findViewById(R.id.btn_base_url).setOnClickListener(this);
        ((EditText)findViewById(R.id.et_base_url)).setText(new SharedPrefHelper(this).getStringKey("base_url"));
    }

    @Override
    public void onClick(View v) {
        new SharedPrefHelper(this).saveStringKey("base_url",((EditText)findViewById(R.id.et_base_url)).getText().toString().trim());
    }
}
