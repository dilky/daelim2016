package example.expense.user.app.expense;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import example.expense.user.app.R;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by dilky on 2016-07-20.
 * 상세보기 화면
 */

/**
 * Created by dilky on 2016-07-20.
 * 상세보기 화면
 */
public class ViewExpense extends AppCompatActivity implements onNetworkResponseListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.content_view_expense);

            addToolBar();

            if ( !getIntent().hasExtra("EXPENSE_SEQ") ) {
                Toast.makeText(this, getString(R.string.error_msg_required_values_are_missing), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            String expenseSeq = getIntent().getStringExtra("EXPENSE_SEQ");
            JSONObject req_data = new JSONObject();
            req_data.put("USER_ID", "test_user1");
            req_data.put("EXPENSE_SEQ", expenseSeq);

            CommNetwork network = new CommNetwork(this, this);
            network.requestToServer("EXPENSE_R001", req_data);

        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_detail_view);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        try {
            int id = item.getItemId();
            switch (id) {
                case android.R.id.home:
                    finish();
                    break;
                case R.id.menu_modify:
                    Toast.makeText(getApplicationContext(), "수정 메뉴 클릭",
                            Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_delete:

                    String expenseSeq = getIntent().getStringExtra("EXPENSE_SEQ");
                    JSONObject req_data = new JSONObject();
                    req_data.put("USER_ID", "test_user1");
                    req_data.put("EXPENSE_SEQ", expenseSeq);

                    CommNetwork network = new CommNetwork(this, this);
                    network.requestToServer("EXPENSE_D001", req_data);
                    return true;
            }
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {
        try {
            if ("EXPENSE_R001".equals(api_key)) {

                TextView tvStatus = (TextView) findViewById(R.id.tv_Status);
                TextView tvPaymentStoreName = (TextView) findViewById(R.id.tv_PaymentStoreName);
                TextView tvPaymentAmount = (TextView) findViewById(R.id.tv_PaymentAmount);
                TextView tvPaymentDate = (TextView) findViewById(R.id.tv_PaymentDate);
                TextView tvSummary = (TextView) findViewById(R.id.tv_Summary);
                TextView tvAccountTitle = (TextView) findViewById(R.id.tv_AccountTitle);

                tvStatus.setText(response.getString("ADMISSION_STATUS_NM"));
                tvPaymentStoreName.setText(response.getString("PAYMENT_STORE_NM"));
                tvPaymentAmount.setText(response.getString("PAYMENT_AMT"));
                tvPaymentDate.setText(response.getString("PAYMENT_DTTM"));
                tvSummary.setText(response.getString("SUMMARY"));
                tvAccountTitle.setText(response.getString("ACCOUNT_TTL_NM"));
            } else if ("EXPENSE_D001".equals(api_key)) {
                if( "1".equals(response.getString("DELETE_RSLT"))) {
                    finish();
                } else {
                    ErrorUtils.Alert(this, response.getString("DELETE_RSLT_MSG"));
                }
            }
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {
        ErrorUtils.Alert(this, String.format("[%s:%s]\n%s", api_key, error_cd, error_msg));
    }

}
