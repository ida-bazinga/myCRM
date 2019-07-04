/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import javax.annotation.ManagedBean;
import java.util.regex.Pattern;

/**
 * Created by d.ivanov on 29.03.2017.
 */
@ManagedBean(IValidINN.NAME)
public class ValidINN implements IValidINN {

    private static final Pattern innPatter = Pattern.compile("\\d{10}|\\d{12}");
    private static final int[] checkArr = new int[] {3,7,2,4,10,3,5,9,4,6,8};

    public Boolean isValidINN (String inn)
    {
        inn = inn.trim();
        if (!innPatter.matcher(inn).matches()) {
            return false;
        }
        int length = inn.length();
        if (length == 12) {
            return INNStep(inn, 2, 1) && INNStep(inn, 1, 0);
        } else {
            return INNStep(inn, 1, 2);
        }
    }

    private static boolean INNStep(String inn, int offset, int arrOffset) {
        int sum = 0;
        int length = inn.length();
        for (int i = 0; i < length - offset; i++) {
            sum += (inn.charAt(i) - '0') * checkArr[i + arrOffset];
        }
        return (sum % 11) % 10 == inn.charAt(length - offset) - '0';
    }
}
