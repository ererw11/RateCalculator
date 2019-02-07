package com.eemery.android.ratecalculator;

import android.os.Parcel;
import android.os.Parcelable;

public class Calc implements Parcelable {

    Boolean acquiredSell;
    Boolean taxesInSellRate;
    Boolean feesInSellRate;
    Boolean taxesWaivedRatePlan;
    Boolean taxableFeeIsPercentage;
    Boolean nonTaxableFeeIsPercentage;
    double acquiredRate;
    double taxes;
    double compensation;
    double promotion;
    double accelerator;
    double taxableServiceCharge;
    double nonTaxableServiceCharge;
    double directlyRemittedTax;

    public Calc(Boolean acquiredSell,
                Boolean taxesInSellRate,
                Boolean feesInSellRate,
                Boolean taxesWaivedRatePlan,
                double acquiredRate,
                double taxes,
                double compensation,
                double promotion) {

        if (acquiredSell == null) {
            this.acquiredSell = true;
        } else {
            this.acquiredSell = acquiredSell;
        }

        if (taxesInSellRate == null) {
            this.taxesInSellRate = false;
        } else {
            this.taxesInSellRate = taxesInSellRate;
        }

        if (feesInSellRate == null) {
            this.feesInSellRate = false;
        } else {
            this.feesInSellRate = feesInSellRate;
        }

        if (taxesWaivedRatePlan == null) {
            this.taxesWaivedRatePlan = false;
        } else {
            this.taxesWaivedRatePlan = taxesWaivedRatePlan;
        }

        this.taxesInSellRate = taxesInSellRate;
        this.feesInSellRate = feesInSellRate;
        this.taxesWaivedRatePlan = taxesWaivedRatePlan;
        this.acquiredRate = acquiredRate;
        this.taxes = taxes;
        this.compensation = compensation;
        this.promotion = promotion;
    }

    protected Calc(Parcel in) {
        byte acquiredSellVal = in.readByte();
        acquiredSell = acquiredSellVal == 0x02 ? null : acquiredSellVal != 0x00;
        byte taxesInSellRateVal = in.readByte();
        taxesInSellRate = taxesInSellRateVal == 0x02 ? null : taxesInSellRateVal != 0x00;
        byte feesInSellRateVal = in.readByte();
        feesInSellRate = feesInSellRateVal == 0x02 ? null : feesInSellRateVal != 0x00;
        byte taxesWaivedRatePlanVal = in.readByte();
        taxesWaivedRatePlan = taxesWaivedRatePlanVal == 0x02 ? null : taxesWaivedRatePlanVal != 0x00;
        byte taxableFeeIsPercentageVal = in.readByte();
        taxableFeeIsPercentage = taxableFeeIsPercentageVal == 0x02 ? null : taxableFeeIsPercentageVal != 0x00;
        byte nonTaxableFeeIsPercentageVal = in.readByte();
        nonTaxableFeeIsPercentage = nonTaxableFeeIsPercentageVal == 0x02 ? null : nonTaxableFeeIsPercentageVal != 0x00;
        acquiredRate = in.readDouble();
        taxes = in.readDouble();
        compensation = in.readDouble();
        promotion = in.readDouble();
        accelerator = in.readDouble();
        taxableServiceCharge = in.readDouble();
        nonTaxableServiceCharge = in.readDouble();
        directlyRemittedTax = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (acquiredSell == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (acquiredSell ? 0x01 : 0x00));
        }
        if (taxesInSellRate == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (taxesInSellRate ? 0x01 : 0x00));
        }
        if (feesInSellRate == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (feesInSellRate ? 0x01 : 0x00));
        }
        if (taxesWaivedRatePlan == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (taxesWaivedRatePlan ? 0x01 : 0x00));
        }
        if (taxableFeeIsPercentage == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (taxableFeeIsPercentage ? 0x01 : 0x00));
        }
        if (nonTaxableFeeIsPercentage == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (nonTaxableFeeIsPercentage ? 0x01 : 0x00));
        }
        dest.writeDouble(acquiredRate);
        dest.writeDouble(taxes);
        dest.writeDouble(compensation);
        dest.writeDouble(promotion);
        dest.writeDouble(accelerator);
        dest.writeDouble(taxableServiceCharge);
        dest.writeDouble(nonTaxableServiceCharge);
        dest.writeDouble(directlyRemittedTax);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Calc> CREATOR = new Parcelable.Creator<Calc>() {
        @Override
        public Calc createFromParcel(Parcel in) {
            return new Calc(in);
        }

        @Override
        public Calc[] newArray(int size) {
            return new Calc[size];
        }
    };
}