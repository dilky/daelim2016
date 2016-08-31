package example.expense.user.app.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import example.expense.user.app.R;

/**
 * Created by dilky on 2016-08-31.
 */
public class DatePickerFragment  extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText etPaymentDate;


    public void setEditText (EditText paymentDate) {
        etPaymentDate = paymentDate;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        if (etPaymentDate.getTag(R.id.tag_year) == null) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            year = (Integer) etPaymentDate.getTag(R.id.tag_year);
            month = (Integer) etPaymentDate.getTag(R.id.tag_month);
            day = (Integer) etPaymentDate.getTag(R.id.tag_day);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        String date = String.format("%04d년 %2d월 %2d일", year, month + 1, day);
        etPaymentDate.setText(date);
        etPaymentDate.setTag(R.id.tag_year, year);
        etPaymentDate.setTag(R.id.tag_month, month);
        etPaymentDate.setTag(R.id.tag_day, day);
    }

}
