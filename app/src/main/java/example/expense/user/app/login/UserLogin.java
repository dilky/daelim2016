package example.expense.user.app.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import example.expense.user.app.ExpenseList;
import example.expense.user.app.R;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.SharedPref;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by dilky on 2016-07-20.
 * 로그인 화면
 */
public class UserLogin extends AppCompatActivity {
    private EditText etUserId;
    private EditText etPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.content_login);

            addToolBar();

            if (!TextUtils.isEmpty(SharedPref.getUserId(this)) && !TextUtils.isEmpty(SharedPref.getPwd(this))) {
                // TODO : 로그인 API 호출
                Intent intent = new Intent(this, ExpenseList.class);
                startActivity(intent);
                finish();
                return;
            }

            etUserId = (EditText) findViewById(R.id.et_UserId);
            etPwd = (EditText) findViewById(R.id.et_Pwd);
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_login);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    // 로그인 버튼 클릭
    public void loginClick(View view) {
        try {
            // Check Empty Value
            if (TextUtils.isEmpty(etUserId.getText())
                    || TextUtils.isEmpty(etPwd.getText())) {
                return;
            }

            CommNetwork commNetwork = new CommNetwork(this, new onNetworkResponseListener() {
                @Override
                public void onSuccess(String api_key, JSONObject response) {

                    SharedPref.putUserId(UserLogin.this, etUserId.getText().toString());
                    SharedPref.putPwd(UserLogin.this, etPwd.getText().toString());

                    Intent intent = new Intent(UserLogin.this, ExpenseList.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String api_key, String error_cd, String error_msg) {
                    ErrorUtils.Alert( UserLogin.this, String.format("[%s] %s", error_cd,  error_msg) );
                }
            });
            JSONObject req_data = new JSONObject();
            req_data.put("USER_ID", etUserId.getText().toString());
            req_data.put("PWD", etPwd.getText().toString());
            commNetwork.requestToServer("LOGIN_R001", req_data);

        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }
}
