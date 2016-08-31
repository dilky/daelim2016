package example.expense.user.app.expense;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import example.expense.user.app.R;
import example.expense.user.app.common.AccountTitleSpinnerList;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.DatePickerFragment;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by dilky on 2016-08-31.
 */
public class UpdateExpense extends AppCompatActivity implements onNetworkResponseListener {

    private EditText etPaymentStoreName;    // 지급처
    private EditText etPaymentAmount;       // 지급액
    private EditText etPaymentDate;         // 지급일자
    private EditText etSummary;             // 적요(메모)
    private Spinner spnAccountTitle;
    private AccountTitleSpinnerList spinnerList;
    private JSONObject viewJsonObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.content_new_expense);

            addToolBar();

            initializeView();

        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void initializeView() throws Exception {

        etPaymentStoreName = (EditText) findViewById(R.id.et_PaymentStoreName);
        etPaymentAmount = (EditText) findViewById(R.id.et_PaymentAmount);
        etPaymentDate = (EditText) findViewById(R.id.et_PaymentDate);
        etSummary = (EditText) findViewById(R.id.et_Summary);

        if ( !getIntent().hasExtra("json_data") || TextUtils.isEmpty(getIntent().getStringExtra("json_data"))) {
            Toast.makeText(this, "수정할 수 없습니다. 데이터를 확인하세요", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewJsonObject = new JSONObject(getIntent().getStringExtra("json_data"));

        etPaymentStoreName.setText(viewJsonObject.getString("PAYMENT_STORE_NM"));
        etPaymentAmount.setText(viewJsonObject.getString("PAYMENT_AMT"));
        etSummary.setText(viewJsonObject.getString("SUMMARY"));
        setPaymentDate(viewJsonObject.getString("PAYMENT_DTTM"), etPaymentDate);

        // 클릭시 데이트피커 보여주기
        etPaymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        // 계정코드 조회
        spnAccountTitle = (Spinner)findViewById(R.id.spn_AccountTitle);
        getAccounttitleCodes();
    }

    private void setPaymentDate(String date, EditText editText) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt( date.substring(4, 6)) - 1;
        int day =  Integer.parseInt(date.substring(6, 8));

        editText.setTag(R.id.tag_year, year);
        editText.setTag(R.id.tag_month,  month);
        editText.setTag(R.id.tag_day, day);

        editText.setText(String.format("%04d년 %2d월 %2d일", year, month + 1, day));
    }

    private void getAccounttitleCodes() throws Exception {

        CommNetwork network = new CommNetwork(this, this);

        JSONObject requestObject = new JSONObject();
        network.requestToServer("ACCOUNT_L001", requestObject);
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_update_expense);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // 등록 버튼 클릭
    public void toolbarRightButtonClick(View v) {
        try {
            EditText paymentStoreNm = (EditText) findViewById(R.id.et_PaymentStoreName);
            EditText paymentAmount = (EditText) findViewById(R.id.et_PaymentAmount);
            EditText summary = (EditText) findViewById(R.id.et_Summary);

            if ( TextUtils.isEmpty(paymentStoreNm.getText().toString())
                    || TextUtils.isEmpty(paymentAmount.getText().toString())
                    || TextUtils.isEmpty(summary.getText().toString())
                    || TextUtils.isEmpty(etPaymentDate.getText().toString()) ) {
                Toast.makeText(this, "필수입력 값이 누락되었습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            // 서버에 저장하기
            JSONObject requestObject = new JSONObject();
            requestObject.put("PAYMENT_STORE_NM", paymentStoreNm.getText().toString());
            requestObject.put("PAYMENT_AMT", paymentAmount.getText().toString());
            requestObject.put("PAYMENT_DTTM", String.format("%04d%02d%02d", etPaymentDate.getTag(R.id.tag_year), ((Integer)etPaymentDate.getTag(R.id.tag_month) + 1), etPaymentDate.getTag(R.id.tag_day)));
            requestObject.put("SUMMARY", summary.getText().toString());
            requestObject.put("ACCOUNT_TTL_CD", spinnerList.getAccountTitleCd(spnAccountTitle.getSelectedItemPosition()));
            requestObject.put("USER_ID", "test_user1");
            requestObject.put("EXPENSE_SEQ", viewJsonObject.getString("EXPENSE_SEQ"));

            Log.d("dilky", requestObject.toString());

            CommNetwork network = new CommNetwork(this, this);
            network.requestToServer("EXPENSE_U001", requestObject);
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(etPaymentDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {
        try {
            if ("ACCOUNT_L001".equals(api_key)) {
                //
                // 계정코드 조회
                //
                JSONArray array = response.getJSONArray("REC");
                spinnerList = new AccountTitleSpinnerList(array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateExpense.this, android.R.layout.simple_spinner_item, spinnerList.getArrayList());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnAccountTitle.setAdapter(adapter);

                int position = 0;
                for(String name : spinnerList.getArrayList()) {
                    if (name.equals( viewJsonObject.getString("ACCOUNT_TTL_NM"))) {
                        spnAccountTitle.setSelection(position);
                        break;
                    }
                    position++;
                }

            } else if ("EXPENSE_U001".equals(api_key)) {
                //
                setResult(RESULT_OK);
                finish();
            }


        } catch (Exception e) {
            ErrorUtils.AlertException(UpdateExpense.this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {

    }
}
