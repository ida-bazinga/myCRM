/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.crm.entity.Currency;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import static com.haulmont.thesis.crm.core.app.NumberToWordsConstants.*;

/**
 * @author Created by k.khoroshilov on 21.03.2016.
 */
@ManagedBean(NumberToWord.NAME)
public class NumberToWordImpl implements NumberToWord {

    private static BigInteger integerPart;
    private static int fractionalPart;
    private static Currency currency;
    private static Locale locale;

    @Inject
    protected DataManager dataManager;

    @Override
    public String bigDecimalToWord(BigDecimal value, Locale locale, Currency currency) {

        if (locale == null ) throw new IllegalArgumentException();
        NumberToWordImpl.locale = locale;
        NumberToWordImpl.currency = currency;

        value = value.setScale(2,BigDecimal.ROUND_HALF_UP);

        DecomposeValue(value);

        return getAmountString();
    }

    @Override
    public String bigDecimalToWord(BigDecimal value, Locale locale, String currencyCode) {

        if (currencyCode == null || currencyCode.isEmpty() ) throw new IllegalArgumentException();

        currency = getCurrencyByCode(currencyCode);

        return bigDecimalToWord(value, locale, currency);
    }

    private static void DecomposeValue(BigDecimal value){
        integerPart = value.toBigInteger();

        BigInteger i = BigInteger.valueOf((long)Math.pow(10, value.scale()));

        fractionalPart = value.unscaledValue().subtract(integerPart.multiply(i)).intValue();
    }

    private Currency getCurrencyByCode(String currencyCode) {
        LoadContext loadContext = new LoadContext(Currency.class).setView("_local");

        loadContext.setQueryString("select e from crm$Currency e where e.code = :code")
                .setParameter("code", currencyCode);
        return dataManager.load(loadContext);
    }

    private static String toString_RU (long value){
        int    i,mny;

        StringBuilder result= new StringBuilder("");
        final int step = 1000;
        long divisor =(long)Math.pow(step,DG_POWER ); //делитель
        long psum = value;

        int one  = 1;
        int four = 2;
        int many = 3;
        int hun  = 4;
        int dec  = 3;
        int dec2 = 2;

        if(value == 0) return zero_ru;
        if(value < 0){
            result.append(minus_ru);
            psum = -psum;
        }

        for(i = DG_POWER-1; i >= 0; i--){
            divisor /= step;
            mny = (int)(psum / divisor);
            psum %= divisor;
            if(mny == 0){
                if(i > 0) continue;
                result.append( a_power_ru[i][one] );
            } else {
                if(mny >= 100){ result.append( digit_ru[mny/100][hun]); mny %= 100; }
                if(mny >= 20 ){ result.append( digit_ru[mny/10 ][dec]); mny %= 10; }
                if(mny >= 10 ){
                    result.append( digit_ru[mny-10][dec2]);
                } else {
                    if(mny >= 1 ) result.append( digit_ru[mny]["0".equals(a_power_ru[i][0]) ? 0 : 1 ] );
                }
                switch(mny){
                    case  1: result.append( a_power_ru[i][one] ); break;
                    case  2:
                    case  3:
                    case  4: result.append( a_power_ru[i][four]); break;
                    default: result.append( a_power_ru[i][many]); break;
                }
            }
        }
        return result.toString();
    }

    private static String toString_EN(long value){
        /* special case */
        if(value == 0) return zero_en;

        String prefix = "";

        if(value < 0) {
            value = -value;
            prefix = minus_en;
        }

        String soFar = "";
        int place = 0;

        do {
            long n = value % 1000;
            if(n != 0){
                String s = convertLessThanOneThousand(n, place);
                soFar = s + majorNames[place] + soFar;
            }
            place++;
            value /= 1000;
        }
        while(value > 0);

        return (prefix + soFar).trim();
    }

    private static String convertLessThanOneThousand (long value, int place){

        String soFar;
        if(value % 100 < 20) {
            soFar = numNames[(int)value % 100];
            value /= 100;
        } else {
            soFar = numNames[(int)value % 10];
            value /= 10;
            soFar = tensNames[(int)value % 10] + (place == 0 ? " -": "") + soFar;
            value /= 10;
        }
        if(value == 0) return soFar;
        return numNames[(int)value] + " hundred" + soFar;
    }

    private static String getAmountString() {

        StringBuilder sb = new StringBuilder();
        sb.append(("en".equalsIgnoreCase(locale.getLanguage())) ? toString_EN(integerPart.longValue()) : toString_RU(integerPart.longValue()));
        sb.append(" ");
        sb.append(getCurrencyName());
        sb.append( (fractionalPart < 10) ? " 0": " ");
        sb.append(fractionalPart);
        sb.append(" ");
        sb.append(getSubCurrencyName());
        toUpperCaseFirstLetter(sb);

        return sb.toString();
    }

    private static String getCurrencyName() {

        if ("en".equalsIgnoreCase(locale.getLanguage())) return currency.getFullName_en()+ "s and";

        BigInteger r = integerPart.remainder(BigInteger.valueOf(100));
        if (r.compareTo(BigInteger.TEN) > 0 && r.compareTo(BigInteger.valueOf(20)) < 0)
            return currency.getFullName_five();

        int last = r.remainder(BigInteger.TEN).intValue();
        switch (last) {
            case 1:
                return currency.getFullName_one();
            case 2:
            case 3:
            case 4:
                return currency.getFullName_two();
            default:
                break;
        }
        return currency.getFullName_five();
    }

    private static String getSubCurrencyName() {

        if ("en".equalsIgnoreCase(locale.getLanguage())) return currency.getSubCurrencyName_en()+ "s";

        if (fractionalPart > 10 && fractionalPart < 20) return currency.getSubCurrencyFullName_five();

        int last = fractionalPart % 10;
        switch (last) {
            case 1:
                return currency.getSubCurrencyFullName_one();
            case 2:
            case 3:
            case 4:
                return currency.getSubCurrencyFullName_two();
            default:
                break;
        }
        return currency.getSubCurrencyFullName_five();
    }
    private static void toUpperCaseFirstLetter(StringBuilder sb) {
        // to upper case
        char first = sb.charAt(0);
        first = Character.toUpperCase(first);
        sb.setCharAt(0, first);
    }

    /*
    public static String toString(double num ){
        return toString( (int)num) + "."+ toString( (int)(num*100 - ((int)num)*100) );
        }

    private static char lastNonWhitespace(CharSequence sb) {
        for (int i = sb.length() - 1; i >= 0; i--) {
            char ch = sb.charAt(i);
            if (!Character.isWhitespace(ch)) {
                return ch;
            }
        }
        return 0;
    }
    */
}