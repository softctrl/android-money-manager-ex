/*
 * Copyright (C) 2012-2015 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.money.manager.ex.core;

import android.content.Context;
import android.text.TextUtils;

import com.money.manager.ex.Constants;
import com.money.manager.ex.currency.CurrencyService;
import com.money.manager.ex.database.TableCurrencyFormats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import info.javaperformance.money.Money;

/**
 * Various methods that help out working with numbers.
 */
public class NumericHelper {
    private CurrencyService mCurrencyService;

    public static boolean isNumeric(String str) {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public NumericHelper(Context context) {
        mContext = context;
    }

    private Context mContext;

    public int tryParse(String value) {
        int result;
        try {
            result = Integer.parseInt(value);
        } catch (Exception ex) {
            // nothing
            result = Constants.NOT_SET;
        }
        return result;
    }

    public String getNumberFormatted(Money value, int decimals, String decimalSeparator, String groupSeparator) {
//        value = roundNumber(value, decimals);
        value = value.truncate(decimals);

        // set format
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        // getDecimalPoint()
        if (!(TextUtils.isEmpty(decimalSeparator))) {
            formatSymbols.setDecimalSeparator(decimalSeparator.charAt(0));
        }
        // getGroupSeparator()
        if (!(TextUtils.isEmpty(groupSeparator))) {
            formatSymbols.setGroupingSeparator(groupSeparator.charAt(0));
        }

        // All these use locale-dependent formatting.
//        DecimalFormat formatter = new DecimalFormat();
//        Locale appLocale = MoneyManagerApplication.getInstanceApp().getLocale();
//        DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(appLocale);
        String pattern = NumericPatternGenerator.getPattern(decimals);
        DecimalFormat formatter = new DecimalFormat(pattern);

        formatter.setGroupingSize(3);
        formatter.setDecimalFormatSymbols(formatSymbols);

        formatter.setMaximumFractionDigits(decimals);
        formatter.setMinimumFractionDigits(decimals);

//        Double number = value.toDouble();
        String result = formatter.format(value.toBigDecimal());
        return result;
    }

//    public String getNumberFormatted(Money value, TableCurrencyFormats currency) {
//        if (currency == null) {
//            currency = this.getCurrencyService().getBaseCurrency();
//        }
//
//        return getNumberFormatted(value, currency.getScale(), currency.getDecimalPoint(),
//                    currency.getGroupSeparator());
//    }

    public String getNumberFormatted(Money value, double scale, String decimalPoint, String groupSeparator) {
        // Round the number first.
        int decimals = getNumberOfDecimals(scale);

        return getNumberFormatted(value, decimals, decimalPoint, groupSeparator);
    }

    /**
     * Extracts the number of decimal places from scale/precision value.
     * @param scale
     * @return
     */
    public int getNumberOfDecimals(double scale) {
        double decimals = Math.log(scale) / Math.log(10.0);
        int result = (int) Math.round(decimals);
        return result;
    }

    public String removeBlanks(String input) {
        return input.replace(" ", "");
    }

    /**
     *
     * @param value value to format
     * @param showSymbols Whether to include the currency symbol in the output.
     * @return formatted value
     */
    public String getValueFormatted(Money value, boolean showSymbols, TableCurrencyFormats currency) {
        String result = this.getNumberFormatted(value, currency.getScale(),
                currency.getDecimalPoint(), currency.getGroupSeparator());

        // check suffix
        if ((showSymbols) && (!TextUtils.isEmpty(currency.getSfxSymbol()))) {
            result = result + " " + currency.getSfxSymbol();
        }
        // check prefix
        if (((showSymbols) && !TextUtils.isEmpty(currency.getPfxSymbol()))) {
            result = currency.getPfxSymbol() + " " + result;
        }

        return result;
    }

    /**
     *
     * @param value value to format
     * @return value formatted
     */
    public String getValueFormatted(Money value, TableCurrencyFormats currency) {
        return getValueFormatted(value, true, currency);
    }

    /**
     * Clean up the number based on the locale settings for grouping and decimal separators.
     * @param numberString Formatted string
     * @return (English) number string that can be used for expression.
     */
    public String cleanUpNumberString(String numberString) {
        // replace any blanks
        numberString = removeBlanks(numberString);

        FormatUtilities format = new FormatUtilities(mContext);

        // Remove grouping separator(s)
        String groupingSeparator = format.getGroupingSeparatorForAppLocale();
        numberString = numberString.replace(groupingSeparator, "");

        // Replace the decimal separator with a dot.
        String decimalSeparator = format.getDecimalSeparatorForAppLocale();
        if (!decimalSeparator.equals(".")) {
            numberString = numberString.replace(decimalSeparator, ".");
        }

        return numberString;
    }

    public CurrencyService getCurrencyService() {
        if (mCurrencyService == null) {
            mCurrencyService = new CurrencyService(mContext);
        }
        return mCurrencyService;
    }
}
