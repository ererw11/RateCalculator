package com.eemery.android.ratecalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class TaxFragment extends Fragment {

    TextView customerTaxTextView;
    TextView taxToHotelTextView;

    TextView customerTaxExplanationTextView;
    TextView taxToHotelExplanationTextView;

    TextView customerTaxCalculationTextView;
    TextView taxToHotelCalculationTextView;

    Calc calc;

    public TaxFragment() {
        // Required empty public constructor
    }

    public static TaxFragment newInstance(Calc calc) {
        Bundle arg = new Bundle();
        arg.putParcelable(Utils.ARG_CALC_TAG, calc);

        TaxFragment fragment = new TaxFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calc = Objects.requireNonNull(getArguments()).getParcelable(Utils.ARG_CALC_TAG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_taxes, container, false);

        // The TextViews for the actual rates
        // ex: 6.43
        customerTaxTextView = v.findViewById(R.id.tax_customer_tax_text_view);
        taxToHotelTextView = v.findViewById(R.id.tax_tax_to_hotel_text_view);

        //The TextViews for the rate calculation explanations
        // ex: Cost after promo * Tax = Tax to hotel
        customerTaxExplanationTextView = v.findViewById(R.id.tax_customer_tax_breakdown_title_text_view);
        taxToHotelExplanationTextView = v.findViewById(R.id.tax_tax_to_hotel_breakdown_title_text_view);

        // The TextViews for the rate calculations
        // ex: 49.49 * .13 = 6.43
        customerTaxCalculationTextView = v.findViewById(R.id.tax_customer_tax_breakdown_text_view);
        taxToHotelCalculationTextView = v.findViewById(R.id.tax_tax_to_hotel_breakdown_text_view);

        Calculate.calculateCustomerTaxAndExplanation(calc,
                customerTaxTextView,
                customerTaxExplanationTextView,
                customerTaxCalculationTextView);

        Calculate.calculateTaxToHotelAndExplanation(calc,
                taxToHotelTextView,
                taxToHotelExplanationTextView,
                taxToHotelCalculationTextView);

        return v;
    }
}
