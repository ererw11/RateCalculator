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

public class CostFragment extends Fragment {

    Calc calc;

    TextView baseCostTextView;
    TextView promoAmountOnCostTextView;
    TextView costAfterPromoTextView;

    TextView baseCostCalculationExplanationTextView;
    TextView promoAmountOnCostCalculationExplanationTextView;
    TextView costAfterPromoCalculationExplanationTextView;

    TextView baseCostCalculationTextView;
    TextView promoAmountOnCostCalculationTextView;
    TextView costAfterPromoCalculationTextView;

    public CostFragment() {
        // Required empty public constructor
    }

    public static CostFragment newInstance(Calc calc) {
        Bundle arg = new Bundle();
        arg.putParcelable(Utils.ARG_CALC_TAG, calc);

        CostFragment fragment = new CostFragment();
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
        View v = inflater.inflate(R.layout.fragment_cost, container, false);

        // The TextViews for the actual rates
        // ex: 49.49
        baseCostTextView = v.findViewById(R.id.cost_base_cost_text_view);
        promoAmountOnCostTextView = v.findViewById(R.id.cost_promo_amount_on_cost_text_view);
        costAfterPromoTextView = v.findViewById(R.id.cost_cost_after_promo_text_view);

        //The TextViews for the rate calculation explanations
        // Base cost - Promo amount on cost = Cost after promo
        baseCostCalculationExplanationTextView = v.findViewById(R.id.cost_base_cost_breakdown_title_text_view);
        promoAmountOnCostCalculationExplanationTextView = v.findViewById(R.id.cost_promo_amount_on_cost_breakdown_title_text_view);
        costAfterPromoCalculationExplanationTextView = v.findViewById(R.id.cost_cost_after_promo_breakdown_title_text_view);

        // The TextViews for the rate calculations
        // ex: 52.09 - 2.60 = 49.49
        baseCostCalculationTextView = v.findViewById(R.id.cost_base_cost_breakdown_text_view);
        promoAmountOnCostCalculationTextView = v.findViewById(R.id.cost_promo_amount_on_cost_breakdown_text_view);
        costAfterPromoCalculationTextView = v.findViewById(R.id.cost_cost_after_promo_breakdown_text_view);

        // Calculate and display rates and explanations
        double baseCost = Calculate.calculateBaseCostAndExplanation(calc,
                baseCostTextView,
                baseCostCalculationExplanationTextView,
                baseCostCalculationTextView);
        double promoAmount = Calculate.calculatePromoAmountOnCostAndExplanation(calc,
                baseCost,
                promoAmountOnCostTextView,
                promoAmountOnCostCalculationExplanationTextView,
                promoAmountOnCostCalculationTextView);
        Calculate.calculateCostAfterPromoAndExplanation(baseCost,
                promoAmount,
                costAfterPromoTextView,
                costAfterPromoCalculationExplanationTextView,
                costAfterPromoCalculationTextView);

        return v;
    }
}
