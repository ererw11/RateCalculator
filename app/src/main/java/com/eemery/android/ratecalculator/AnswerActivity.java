package com.eemery.android.ratecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import static com.eemery.android.ratecalculator.Utils.formatDoubleAsCurrencyString;

public class AnswerActivity extends AppCompatActivity {

    Calc calc;

    Toolbar toolbar;
    TextView sellLarTextView;
    TextView priceRateTextView;
    TextView netRateTextView;
    TextView tripTotalTextView;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_cost:
                    selectedFragment = CostFragment.newInstance(calc);
                    toolbar.setTitle("Cost Breakdown");
                    break;
                case R.id.navigation_price:
                    selectedFragment = PriceFragment.newInstance(calc);
                    toolbar.setTitle("Price Breakdown");
                    break;
                case R.id.navigation_tax:
                    selectedFragment = TaxFragment.newInstance(calc);
                    toolbar.setTitle("Taxes and Fees");
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, Objects.requireNonNull(selectedFragment));
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        toolbar = findViewById(R.id.toolbar_activity_answer);
        sellLarTextView = findViewById(R.id.sell_lar_text_view);
        priceRateTextView = findViewById(R.id.price_rate_text_view);
        netRateTextView = findViewById(R.id.net_cost_text_view);
        tripTotalTextView = findViewById(R.id.trip_total_text_view);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Cost Breakdown"); // This is the starting fragment

        retrieveCalcFromIntent();
        calculateSellPriceNet(calc);
        calculateTotalRate(calc);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, CostFragment.newInstance(calc))
                    .commit();
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_view);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private void calculateTotalRate(Calc calc) {
        double priceAfterPromo  = Calculate.calculatePriceAfterPromo(calc);
        double totalRate = priceAfterPromo * (1 + calc.taxes);
        tripTotalTextView.setText(formatDoubleAsCurrencyString(totalRate));
    }

    /**
     * Retrieve the Calc object from the Intent that started this Activity
     */
    private void retrieveCalcFromIntent() {
        Intent openingIntent = getIntent();
        calc = openingIntent.getParcelableExtra("calculation");
    }

    private void calculateSellPriceNet(Calc calc) {
        double sellRate;
        double priceRate;
        double netRate;

        if (calc.acquiredSell) {
            // The acquired rate is sell
            sellRate = calc.acquiredRate;
            if (calc.taxesWaivedRatePlan) {
                // Since taxes are waived, price and sell are the same
                priceRate = sellRate;
            } else {
                // Calculate price from sell and tax
                priceRate = sellRate / (1 + calc.taxes);
            }
            // Calculate net from price and commission
            netRate = priceRate * (1 - calc.compensation);
        } else {
            // The acquired rate is net
            netRate = calc.acquiredRate;
            // Calculate price from net and commission
            priceRate = netRate / (1 - calc.compensation);
            // Calculate sell from price and tax
            sellRate = priceRate * (1 + calc.taxes);
        }

        sellLarTextView.setText(formatDoubleAsCurrencyString(sellRate));
        priceRateTextView.setText(formatDoubleAsCurrencyString(priceRate));
        netRateTextView.setText(formatDoubleAsCurrencyString(netRate));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}