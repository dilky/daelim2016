package example.expense.user.app.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import example.expense.user.app.ExpenseList;
import example.expense.user.app.R;
import example.expense.user.app.common.ErrorUtils;

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

            // TODO : ID, PWD 검증한다.
            Intent intent = new Intent(this, ExpenseList.class);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }
}
