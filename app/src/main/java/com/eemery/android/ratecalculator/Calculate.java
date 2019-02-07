package com.eemery.android.ratecalculator;

import android.util.Log;
import android.widget.TextView;

import static com.eemery.android.ratecalculator.Utils.formatDoubleAsCurrency;
import static com.eemery.android.ratecalculator.Utils.formatDoubleAsCurrencyString;
import static com.eemery.android.ratecalculator.Utils.formatDoublePercentage;

public class Calculate {

    private final static String TAG = Calculate.class.getSimpleName();

    public static double calculateBaseCostAndExplanation(Calc calc,
                                                         TextView baseCostTextView,
                                                         TextView baseCostCalculationExplanationTextView,
                                                         TextView baseCostCalculationTextView) {
        double baseCost;
        String baseCostExplanation;
        String baseCostCalculation;
        // Confirm if taxes are in rate
        if (!calc.taxesInSellRate || calc.taxesWaivedRatePlan) {
            // In this case we do not remove taxes
            // Check if there are fees to remove
            if (!calc.feesInSellRate ||
                    calc.taxableServiceCharge + calc.nonTaxableServiceCharge == 0.0) {
                // Either fees are not included in rate or there are no fees, solve the base cost
                baseCost = calc.acquiredRate * (1 - calc.compensation);
                baseCostExplanation = "Sell x (1 - Compensation) = Base Cost";
                baseCostCalculation = String.format("%s x (1 - %s) = %s",
                        formatDoubleAsCurrencyString(calc.acquiredRate),
                        formatDoubleAsCurrencyString(calc.compensation),
                        formatDoubleAsCurrencyString(baseCost));
                Log.i(TAG, "Base Cost: " + baseCost);
            } else {
                // Fees are included in sell rate
                // Remove the fees first
                double fees = formatDoublePercentage(calc.taxableServiceCharge + calc.nonTaxableServiceCharge);
                double rateMinusFees = calc.acquiredRate / (1 + fees);
                // Calculate the base cost from here
                baseCost = rateMinusFees * (1 - calc.compensation);
                baseCost = formatDoubleAsCurrency(baseCost);
                baseCostExplanation = "Sell / (1 + Fee %) = Price without fees \n" +
                        "Sell without fees x (1 - compensation) = Base Cost";
                baseCostCalculation = String.format("%s / (1 + %s) = %s\n%s x (1 - %s) = %s",
                        formatDoubleAsCurrencyString(calc.acquiredRate),
                        fees,
                        rateMinusFees,
                        rateMinusFees,
                        calc.compensation,
                        baseCost);
                Log.i(TAG, "Base Cost: " + baseCost);
            }
        } else {
            // In this case we do remove taxes
            // Check if there are fees to remove also
            if (!calc.feesInSellRate ||
                    calc.taxableServiceCharge + calc.nonTaxableServiceCharge == 0.0) {
                // There are no fees to remove, remove taxes and solve base
                double allTaxes = calc.taxes + calc.directlyRemittedTax;
                double rateMinusTaxes = calc.acquiredRate / (1 + allTaxes);
                // Calculate base cost
                baseCost = rateMinusTaxes * (1 - calc.compensation);
                baseCost = formatDoubleAsCurrency(baseCost);
                baseCostExplanation = "Sell / (1 + Tax) = Rate without taxes \n" +
                        "Sell without taxes x (1 - compensation + = Base Cost";
                baseCostCalculation = String.format("%s / (1 + %s) = %s\n%s x (1 - %s) = %s",
                        calc.acquiredRate,
                        allTaxes,
                        rateMinusTaxes,
                        rateMinusTaxes,
                        calc.compensation,
                        baseCost);
                Log.i(TAG, "Base Cost: " + baseCost);
            } else {
                String removeTaxesExplanationString;
                String removeFeesExplanationString;
                String removeCompensationExplanationString;
                String removeTaxesString;
                String removeFeesString;
                double allTaxes;
                double rateMinusTaxes;
                double rateMinusFees;
                // Both taxes and fees are included in sell rate and both must be removed
                // Taxes first
                if (calc.nonTaxableFeeIsPercentage && calc.nonTaxableServiceCharge != 0.0) {
                    // There is a nonTaxableFee that we must remove, its a %
                    allTaxes = calc.taxes + calc.directlyRemittedTax + calc.nonTaxableServiceCharge;
                    rateMinusTaxes = calc.acquiredRate / (1 + allTaxes);
                    removeTaxesExplanationString = "Sell / (1 + Tax + DRTax) = Sell w/o taxes";
                    removeTaxesString = String.format("%s / (1 + %s) = %s",
                            calc.acquiredRate,
                            allTaxes,
                            rateMinusTaxes);
                } else if (!calc.nonTaxableFeeIsPercentage && calc.nonTaxableServiceCharge != 0.0) {
                    // There is a nonTaxableFee that we must remove also, its a whole amount
                    allTaxes = calc.taxes + calc.directlyRemittedTax;
                    rateMinusTaxes = (calc.acquiredRate - calc.nonTaxableServiceCharge) / (1 + allTaxes);
                    removeTaxesExplanationString = "(Sell - DRTax) / (1 + Tax) = Sell w/o taxes";
                    removeTaxesString = String.format("(%s - %s) / (1 + %s) = %s",
                            calc.acquiredRate,
                            calc.directlyRemittedTax,
                            allTaxes,
                            rateMinusTaxes);
                } else {
                    // There are no nonTaxableFee to remove
                    allTaxes = calc.taxes + calc.directlyRemittedTax;
                    rateMinusTaxes = calc.acquiredRate / (1 + allTaxes);
                    removeTaxesExplanationString = "Sell / (1 + Tax) = Sell w/o taxes";
                    removeTaxesString = String.format("%s / (1 + %s) = %s",
                            calc.acquiredRate,
                            allTaxes,
                            rateMinusTaxes);
                }
                // Remove the Fees
                if (calc.taxableServiceCharge != 0.0) {
                    if (calc.taxableFeeIsPercentage) {
                        // There is a taxable fee and it is a percentage
                        rateMinusFees = rateMinusTaxes / (1 + calc.taxableServiceCharge);
                        removeFeesExplanationString = "Sell w/o taxes / (1 + Taxable Fee) = Sell w/o fees";
                        // Remove compensation
                        baseCost = rateMinusFees * (1 - calc.compensation);
                        removeCompensationExplanationString = "Sell w/o fees x (1 - compensation) = Base Cost";
                        baseCostExplanation = removeTaxesExplanationString + "\n"
                                + removeFeesExplanationString + "\n"
                                + removeCompensationExplanationString;
                        removeFeesString = String.format("%s / (1 - %s)\n%s x (1 - %s) = %s",
                                calc.acquiredRate,
                                calc.taxableServiceCharge,
                                rateMinusFees,
                                calc.compensation,
                                baseCost);
                        baseCostCalculation = removeTaxesString + "\n" + removeFeesString;
                    } else {
                        // The taxable fee is a whole amount
                        rateMinusFees = rateMinusTaxes - calc.taxableServiceCharge;
                        removeFeesExplanationString = "Sell w/o taxes - Taxable fee = Sell w/o fees";
                        // Remove compensation
                        baseCost = rateMinusFees * (1 - calc.compensation);
                        removeCompensationExplanationString = "Sell w/o fees x (1 - compensation) = Base Cost";
                        baseCostExplanation = removeTaxesExplanationString + "\n"
                                + removeFeesExplanationString + "\n"
                                + removeCompensationExplanationString;
                        removeFeesString = String.format("%s - %s = %s\n%s x (1 - %s) = %s",
                                rateMinusTaxes,
                                calc.taxableServiceCharge,
                                rateMinusFees,
                                rateMinusFees,
                                calc.compensation,
                                baseCost);
                        baseCostCalculation = removeTaxesString + "\n" + removeFeesString;
                    }
                } else {
                    // There are no taxable service fees
                    baseCost = rateMinusTaxes * (1 - calc.compensation);
                    baseCostExplanation = removeTaxesExplanationString;
                    baseCostCalculation = String.format("%s x (1 - %s) = %s",
                            rateMinusTaxes,
                            calc.compensation,
                            baseCost);
                }
            }
        }

        baseCostTextView.setText(formatDoubleAsCurrencyString(baseCost));
        baseCostCalculationExplanationTextView.setText(baseCostExplanation);
        baseCostCalculationTextView.setText(baseCostCalculation);
        return baseCost;
    }

    public static double calculateBaseCost(Calc calc) {
        double baseCost;
        // Confirm if taxes are in rate
        if (!calc.taxesInSellRate || calc.taxesWaivedRatePlan) {
            // In this case we do not remove taxes
            // Check if there are fees to remove
            if (!calc.feesInSellRate ||
                    calc.taxableServiceCharge + calc.nonTaxableServiceCharge == 0.0) {
                // Either fees are not included in rate or there are no fees, solve the base cost
                baseCost = calc.acquiredRate * (1 - calc.compensation);
                Log.i(TAG, "Base Cost: " + baseCost);
            } else {
                // Fees are included in sell rate
                // Remove the fees first
                double fees = formatDoublePercentage(calc.taxableServiceCharge + calc.nonTaxableServiceCharge);
                double rateMinusFees = calc.acquiredRate / (1 + fees);
                // Calculate the base cost from here
                baseCost = rateMinusFees * (1 - calc.compensation);
                baseCost = formatDoubleAsCurrency(baseCost);
                Log.i(TAG, "Base Cost: " + baseCost);
            }
        } else {
            // In this case we do remove taxes
            // Check if there are fees to remove also
            if (!calc.feesInSellRate ||
                    calc.taxableServiceCharge + calc.nonTaxableServiceCharge == 0.0) {
                // There are no fees to remove, remove taxes and solve base
                double allTaxes = calc.taxes + calc.directlyRemittedTax;
                double rateMinusTaxes = calc.acquiredRate / (1 + allTaxes);
                // Calculate base cost
                baseCost = rateMinusTaxes * (1 - calc.compensation);
                baseCost = formatDoubleAsCurrency(baseCost);
                Log.i(TAG, "Base Cost: " + baseCost);
            } else {
                double allTaxes;
                double rateMinusTaxes;
                double rateMinusFees;
                // Both taxes and fees are included in sell rate and both must be removed
                // Taxes first
                if (calc.nonTaxableFeeIsPercentage && calc.nonTaxableServiceCharge != 0.0) {
                    // There is a nonTaxableFee that we must remove, its a %
                    allTaxes = calc.taxes + calc.directlyRemittedTax + calc.nonTaxableServiceCharge;
                    rateMinusTaxes = calc.acquiredRate / (1 + allTaxes);
                } else if (!calc.nonTaxableFeeIsPercentage && calc.nonTaxableServiceCharge != 0.0) {
                    // There is a nonTaxableFee that we must remove also, its a whole amount
                    allTaxes = calc.taxes + calc.directlyRemittedTax;
                    rateMinusTaxes = (calc.acquiredRate - calc.nonTaxableServiceCharge) / (1 + allTaxes);
                } else {
                    // There are no nonTaxableFee to remove
                    allTaxes = calc.taxes + calc.directlyRemittedTax;
                    rateMinusTaxes = calc.acquiredRate / (1 + allTaxes);
                }
                // Remove the Fees
                if (calc.taxableServiceCharge != 0.0) {
                    if (calc.taxableFeeIsPercentage) {
                        // There is a taxable fee and it is a percentage
                        rateMinusFees = rateMinusTaxes / (1 + calc.taxableServiceCharge);
                        baseCost = rateMinusFees * (1 - calc.compensation);
                    } else {
                        // The taxable fee is a whole amount
                        rateMinusFees = rateMinusTaxes - calc.taxableServiceCharge;
                        // Remove compensation
                        baseCost = rateMinusFees * (1 - calc.compensation);
                    }
                } else {
                    // There are no taxable service fees
                    baseCost = rateMinusTaxes * (1 - calc.compensation);
                }
            }
        }
        return baseCost;
    }

    public static double calculatePromoAmountOnCostAndExplanation(Calc calc,
                                                                  double baseCost,
                                                                  TextView promoAmountOnCostTextView,
                                                                  TextView promoAmountOnCostCalculationExplanationTextView,
                                                                  TextView promoAmountOnCostCalculationTextView) {
        double promoAmount = baseCost * calc.promotion;
        String promoAmountExplanation = "Base cost x Promotion = Promo amount";
        String promoAmountCalculation = String.format("%s x %s = %s",
                baseCost,
                calc.promotion,
                promoAmount);

        promoAmountOnCostTextView.setText(formatDoubleAsCurrencyString(promoAmount));
        promoAmountOnCostCalculationExplanationTextView.setText(promoAmountExplanation);
        promoAmountOnCostCalculationTextView.setText(promoAmountCalculation);

        return promoAmount;
    }

    public static double calculatePromoAmountOnCost(Calc calc) {
        double baseCost = calculateBaseCost(calc);
        double promoAmount = baseCost * calc.promotion;
        return promoAmount;
    }

    public static void calculateCostAfterPromoAndExplanation(double baseCost,
                                                             double promoAmount,
                                                             TextView costAfterPromoTextView,
                                                             TextView costAfterPromoCalculationExplanationTextView,
                                                             TextView costAfterPromoCalculationTextView) {
        double costAfterPromo = baseCost - promoAmount;
        String costAfterPromoExplanation = "Base cost - Promo amount = Cost after promo";
        String costAfterPromoCalculation = String.format("%s - %s = %s",
                baseCost,
                promoAmount,
                costAfterPromo);

        costAfterPromoTextView.setText(formatDoubleAsCurrencyString(costAfterPromo));
        costAfterPromoCalculationExplanationTextView.setText(costAfterPromoExplanation);
        costAfterPromoCalculationTextView.setText(costAfterPromoCalculation);
    }

    public static double calculateCostAfterPromo(Calc calc) {
        double baseCost = calculateBaseCost(calc);
        double promoAmount = calculatePromoAmountOnCost(calc);
        double costAfterPromo = baseCost - promoAmount;
        return costAfterPromo;
    }


    public static double calculateBasePriceAndExplanation(Calc calc,
                                                          TextView basePriceTextView,
                                                          TextView basePriceCalculationExplanationTextView,
                                                          TextView basePriceCalculationTextView) {
        double baseCost = calculateBaseCost(calc);
        double basePrice = formatDoubleAsCurrency(baseCost / (1 - calc.compensation));
        basePriceTextView.setText(formatDoubleAsCurrencyString(basePrice));
        Log.i(TAG, "Base price: " + basePrice);
        basePriceCalculationExplanationTextView.setText("Base cost 1 (1 - Comp) = Base price");
        basePriceCalculationTextView.setText(String.format("%s / (1 - %s) = %s",
                baseCost,
                calc.compensation,
                basePrice));
        return basePrice;
    }

    public static double calculateBasePrice(Calc calc) {
        double baseCost = calculateBaseCost(calc);
        double basePrice = formatDoubleAsCurrency(baseCost / (1 - calc.compensation));
        return basePrice;
    }


    public static double calculatePromoAmountOnPriceAndExplanation(Calc calc,
                                                                   double basePrice,
                                                                   TextView promoAmountOnPriceTextView,
                                                                   TextView promoAmountOnPriceExplanationTextView,
                                                                   TextView promoAmountOnPriceCalculationTextView) {
        double promoAmountOnPrice = basePrice * calc.promotion;
        promoAmountOnPriceTextView.setText(formatDoubleAsCurrencyString(promoAmountOnPrice));
        promoAmountOnPriceExplanationTextView.setText("Base price x Promo = Promo amount");
        promoAmountOnPriceCalculationTextView.setText(String.format("%s x %s = %s",
                basePrice,
                calc.promotion,
                promoAmountOnPrice));
        return promoAmountOnPrice;
    }

    public static double calculatePromoAmountOnPrice(Calc calc) {
        double basePrice = calculateBasePrice(calc);
        double promoAmountOnPrice = basePrice * calc.promotion;
        return promoAmountOnPrice;
    }


    public static void calculatePriceAfterPromoAndExplanation(double basePrice,
                                                              double promoAmountOnPrice,
                                                              TextView priceAfterPromoTextView,
                                                              TextView priceAfterPromoExplanationTextView,
                                                              TextView priceAfterPromoCalculateTextView) {
        double priceAfterPromo = basePrice - promoAmountOnPrice;
        priceAfterPromoTextView.setText(formatDoubleAsCurrencyString(priceAfterPromo));
        priceAfterPromoExplanationTextView.setText(" Base price - Promo amount = Price after promo");
        priceAfterPromoCalculateTextView.setText(String.format("%s - %s = %s",
                basePrice,
                promoAmountOnPrice,
                priceAfterPromo));
    }

    public static double calculatePriceAfterPromo(Calc calc) {
        double basePrice = calculateBasePrice(calc);
        double priceAfterPromo = basePrice - calculatePromoAmountOnPrice(calc);
        return priceAfterPromo;
    }


    public static void calculateCompensationOnRateAndExplanation(Calc calc,
                                                                 TextView compensationOnRateTextView,
                                                                 TextView compensationOnRateExplanationTextView,
                                                                 TextView compensationOnRateCalculateTextView) {
        double basePrice = calculateBasePrice(calc);
        double baseCost = calculateBaseCost(calc);
        double priceAfterPromo = basePrice - baseCost;
        compensationOnRateTextView.setText(formatDoubleAsCurrencyString(priceAfterPromo));
        compensationOnRateExplanationTextView.setText("Price after promo - Final cost = Comp on rate");
        compensationOnRateCalculateTextView.setText(String.format("%s - %s = %s",
                basePrice,
                baseCost,
                priceAfterPromo));
    }


    public static void calculateCustomerTaxAndExplanation(Calc calc,
                                                          TextView customerTaxTextView,
                                                          TextView customerTaxExplanationTextView,
                                                          TextView customerTaxCalculationTextView) {
        if (calc.taxesWaivedRatePlan) {
            // Taxes are waived and there are no Customer taxes
            customerTaxTextView.setText("0.00");
            customerTaxExplanationTextView.setText("Taxes are waived");
            customerTaxCalculationTextView.setText(""); // Blank for now
        } else {
            // Taxes are not waived
            if (calc.promotion != 0.0) {
                // There are no promos
                double basePrice = calculateBasePrice(calc);
                double customerTax = basePrice * calc.taxes;
                customerTaxTextView.setText(formatDoubleAsCurrencyString(customerTax));
                customerTaxExplanationTextView.setText("Base price x Taxes = Cust tax");
                customerTaxCalculationTextView.setText(String.format("%s x %s = %s",
                        basePrice,
                        calc.taxes,
                        customerTax));
            } else {
                // There is a promo
                double priceAfterPromo = calculatePriceAfterPromo(calc);
                double customerTax = priceAfterPromo * calc.taxes;
                customerTaxTextView.setText(formatDoubleAsCurrencyString(customerTax));
                customerTaxExplanationTextView.setText("Price after promo x Taxes = Cust tax");
                customerTaxCalculationTextView.setText(String.format("%s x %s = %s",
                        priceAfterPromo,
                        calc.taxes,
                        customerTax));
            }
        }

    }

    public static void calculateTaxToHotelAndExplanation(Calc calc,
                                                         TextView taxToHotelTextView,
                                                         TextView taxToHotelExplanationTextView,
                                                         TextView taxToHotelCalculationTextView) {
        if (calc.taxesWaivedRatePlan) {
            // Taxes are waived so there is no tax to hotel
            taxToHotelTextView.setText("0.00");
            taxToHotelExplanationTextView.setText("Taxes are waived");
            taxToHotelCalculationTextView.setText(""); // Blank for now
        } else {
            if (calc.promotion != 0.0) {
                // There are no promos
                double baseCost = calculateBaseCost(calc);
                double taxToHotel = baseCost * calc.taxes;
                taxToHotelTextView.setText(formatDoubleAsCurrencyString(taxToHotel));
                taxToHotelExplanationTextView.setText("Base cost x Taxes = Tax to hotel");
                taxToHotelCalculationTextView.setText(String.format("%s x %s = %s",
                        baseCost,
                        calc.taxes,
                        taxToHotel));
            } else {
                // There is a promo
                double costAfterPromo = calculateCostAfterPromo(calc);
                double taxToHotel = costAfterPromo * calc.taxes;
                taxToHotelTextView.setText(formatDoubleAsCurrencyString(taxToHotel));
                taxToHotelExplanationTextView.setText("Cost after promo x Taxes = Tax to hotel");
                taxToHotelCalculationTextView.setText(String.format("%s x %s = %s",
                        costAfterPromo,
                        calc.taxes,
                        taxToHotel));
            }
        }
    }
}
