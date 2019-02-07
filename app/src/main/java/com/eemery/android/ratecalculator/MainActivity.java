package com.eemery.android.ratecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import static com.eemery.android.ratecalculator.Utils.formatDoublePercentage;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    Toolbar toolbar;
    RadioGroup acquiredRateRadioGroup;
    RadioButton sellLarRadioButton;
    CheckBox taxesIncludedCheckBox;
    CheckBox feesIncludedCheckBox;
    CheckBox taxesWaivedCheckBx;
    EditText acquiredRateEditText;
    EditText taxesEditText;
    EditText promotionsEditText;
    EditText compensationEditText;
    MaterialButton calculateButton;

    Boolean acquiredSell;
    Boolean taxesInSellRate;
    Boolean feesInSellRate;
    Boolean taxesWaivedRatePlan;

    double acquiredRate;
    double taxes;
    double compensation;
    double promotion;

    Calc calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_activity_main);
        acquiredRateRadioGroup = findViewById(R.id.acquired_rate_radio_group);
        sellLarRadioButton = findViewById(R.id.sell_lar_radio_button);
        taxesIncludedCheckBox = findViewById(R.id.taxes_included_sell_rate_radio_button);
        feesIncludedCheckBox = findViewById(R.id.fees_included_sell_rate_radio_button);
        taxesWaivedCheckBx = findViewById(R.id.taxes_waived_rate_plan_check_box);
        acquiredRateEditText = findViewById(R.id.acquired_rate_edit_text);
        taxesEditText = findViewById(R.id.taxes_edit_text);
        promotionsEditText = findViewById(R.id.promotion_edit_text);
        compensationEditText = findViewById(R.id.compensation_edit_text);
        calculateButton = findViewById(R.id.calculate_button);

        toolbar.setTitle("Rate Calculator");
        setSupportActionBar(toolbar);

        // Set up the Radio Group for the acquired rate type
        acquiredRateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton selectedRadioButton = radioGroup.findViewById(checkedId);

                disableInvalidOptions(selectedRadioButton.getText().equals("Sell / LAR"));

            }
        });

        // The Calculate button has been pressed
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveInputVariables();
                createIntent(calc);
            }
        });
    }

    // Disable the checkboxes that do not apply when Net is selected
    private void disableInvalidOptions(boolean b) {
        if (!b) {
            taxesIncludedCheckBox.setChecked(false);
            feesIncludedCheckBox.setChecked(false);
        }
        acquiredSell = b;
        taxesIncludedCheckBox.setEnabled(b);
        feesIncludedCheckBox.setEnabled(b);
    }

    // Collect and format the rate variables
    private void retrieveInputVariables() {
        acquiredSell = retrieveAcquiredRateType();

        // Acquired Rate - Required
        if (!TextUtils.isEmpty(acquiredRateEditText.getText().toString())) {
            acquiredRate = Double.parseDouble(acquiredRateEditText.getText().toString());
            Log.i(TAG, "Rate: " + acquiredRate);
        } else {
            acquiredRateEditText.setError("Please enter a rate");
            Log.i(TAG, "Invalid rate");
        }

        // Taxes - Required - Divide by 100 to get %
        if (!TextUtils.isEmpty(taxesEditText.getText().toString())) {
            taxes = formatDoublePercentage(Double.parseDouble(taxesEditText.getText().toString()) / 100); //Expect the user to enter whole number
            Log.i(TAG, "Taxes: " + taxes);
        } else {
            taxesEditText.setError("Please enter tax amount");
            Log.i(TAG, "Invalid taxes");
        }

        // Compensation - Required - Divide by 100 to get %
        if (!TextUtils.isEmpty(compensationEditText.getText().toString())) {
            compensation = formatDoublePercentage(Double.parseDouble(compensationEditText.getText().toString()) / 100); //Expect the user to enter whole number
            Log.i(TAG, "Compensation: " + compensation);
        } else {
            compensationEditText.setError("Please enter compensation amount");
            Log.i(TAG, "Invalid compensation");
        }

        // Promotion Amount - Not Required - Divide by 100 to get %
        if (!TextUtils.isEmpty(promotionsEditText.getText().toString())) {
            promotion = formatDoublePercentage(Double.parseDouble(promotionsEditText.getText().toString()) / 100); //Expect the user to enter whole number;
            Log.i(TAG, "Promotion: " + promotion);
        }

        // Taxes included in sell rate
        if (acquiredSell) {
            taxesInSellRate = taxesIncludedCheckBox.isChecked();
            Log.i(TAG, "Taxes included in sell rate: " + taxesInSellRate.toString());
        }

        // Fees included in sell rate
        if (acquiredSell) {
            feesInSellRate = feesIncludedCheckBox.isChecked();
            Log.i(TAG, "Fees included in sell rate: " + feesInSellRate.toString());
        }

        // Taxes waived at rate plan level
        taxesWaivedRatePlan = taxesWaivedCheckBx.isChecked();
        Log.i(TAG, "Taxes waived at rate plan level: " + taxesWaivedRatePlan.toString());

        // Create the Calc
        calc = new Calc(acquiredSell,
                taxesInSellRate,
                feesInSellRate,
                taxesWaivedRatePlan,
                acquiredRate,
                taxes,
                compensation,
                promotion);
    }

    private boolean retrieveAcquiredRateType() {
        return sellLarRadioButton.isChecked();
    }

    private void createIntent(Calc calc) {
        Intent newIntent = new Intent(MainActivity.this, AnswerActivity.class);
        newIntent.putExtra("calculation", calc);
        startActivity(newIntent);
    }

}