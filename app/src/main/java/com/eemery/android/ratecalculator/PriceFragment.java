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

public class PriceFragment extends Fragment {

    TextView basePriceTextView;
    TextView promoAmountOnPriceTextView;
    TextView priceAfterPromoTextView;
    TextView compensationOnRateTextView;

    TextView basePriceExplanationTextView;
    TextView promoAmountOnPriceExplanationTextView;
    TextView priceAfterPromoExplanationTextView;
    TextView compensationOnRateExplanationTextView;

    TextView basePriceCalculationTextView;
    TextView promoAmountOnPriceCalculationTextView;
    TextView priceAfterPromoCalculationTextView;
    TextView compensationOnRateCalculationTextView;

    Calc calc;

    public PriceFragment() {
        // Required empty public constructor
    }

    public static PriceFragment newInstance(Calc calc) {
        Bundle args = new Bundle();
        args.putParcelable(Utils.ARG_CALC_TAG, calc);

        PriceFragment fragment = new PriceFragment();
        fragment.setArguments(args);
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
        View v = inflater.inflate(R.layout.fragment_price, container, false);

        // The TextViews for the actual rates
        // ex: 58.22
        basePriceTextView = v.findViewById(R.id.price_base_price_text_view);
        promoAmountOnPriceTextView = v.findViewById(R.id.price_promo_amount_on_price_text_view);
        priceAfterPromoTextView = v.findViewById(R.id.price_price_after_promo_text_view);
        compensationOnRateTextView = v.findViewById(R.id.price_compensation_on_rate_text_view);

        //The TextViews for the rate calculation explanations
        // ex: Base price - Promo amount = Price after promo
        basePriceExplanationTextView = v.findViewById(R.id.price_base_price_breakdown_title_text_view);
        promoAmountOnPriceExplanationTextView = v.findViewById(R.id.price_promo_amount_on_price_breakdown_title_text_view);
        priceAfterPromoExplanationTextView = v.findViewById(R.id.price_price_after_promo_breakdown_title_text_view);
        compensationOnRateExplanationTextView = v.findViewById(R.id.price_compensation_on_rate_breakdown_title_text_view);

        // The TextViews for the rate calculations
        // ex: 61.28 - 3.06 = 58.22
        basePriceCalculationTextView = v.findViewById(R.id.price_base_price_breakdown_text_view);
        promoAmountOnPriceCalculationTextView = v.findViewById(R.id.price_promo_amount_on_price_breakdown_text_view);
        priceAfterPromoCalculationTextView = v.findViewById(R.id.price_price_after_promo_breakdown_text_view);
        compensationOnRateCalculationTextView = v.findViewById(R.id.price_compensation_on_rate_breakdown_text_view);

        double basePrice = Calculate.calculateBasePriceAndExplanation(calc,
                basePriceTextView,
                basePriceExplanationTextView,
                basePriceCalculationTextView);

        double promoAmountOnPrice = Calculate.calculatePromoAmountOnPriceAndExplanation(calc,
                basePrice,
                promoAmountOnPriceTextView,
                promoAmountOnPriceExplanationTextView,
                promoAmountOnPriceCalculationTextView);

        Calculate.calculatePriceAfterPromoAndExplanation(basePrice,
                promoAmountOnPrice,
                priceAfterPromoTextView,
                priceAfterPromoExplanationTextView,
                priceAfterPromoCalculationTextView);

        Calculate.calculateCompensationOnRateAndExplanation(calc,
                compensationOnRateTextView,
                compensationOnRateExplanationTextView,
                compensationOnRateCalculationTextView);

        return v;
    }

}
