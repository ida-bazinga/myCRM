/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.mathematic;

import com.haulmont.thesis.crm.entity.DiscountTypeEnum;
import com.haulmont.thesis.crm.entity.OrderDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ProductCalculate {

    protected OrderDetail item;

    public ProductCalculate(OrderDetail orderDetail) {
        this.item = orderDetail;
    }

    public OrderDetail costCalculation(){
        BigDecimal marginS,discountS,cost,taxRate,amount,sum,taxSum,totalSum;
        marginS = item.getMarginSum() != null ? item.getMarginSum().abs() : BigDecimal.valueOf(0);
        discountS = item.getDiscountSum() != null ? item.getDiscountSum().abs() : BigDecimal.valueOf(0);
        BigDecimal secondaryAmountTmp = BigDecimal.valueOf(1);
        taxRate = item.getProduct().getNomenclature().getTax().getRate();//Налог по заказу, переделать на услугу
        cost = item.getCostPerPiece() != null ? item.getCostPerPiece() : BigDecimal.valueOf(0);
        amount = item.getAmount();

        if (item.getProduct().getSecondaryUnit() != null || item.getSecondaryAmount() != null) {
            secondaryAmountTmp = item.getSecondaryAmount() != null ? item.getSecondaryAmount().signum() == 1 ? item.getSecondaryAmount() : BigDecimal.valueOf(1) : BigDecimal.valueOf(1);
        }
        cost = cost.multiply(secondaryAmountTmp);
        totalSum = cost.multiply(amount);
        if (cost.signum() == 1) {

            if (item.getDiscountType() != DiscountTypeEnum.percent) {
                //Если скидка/наценка указана суммой
                if (item.getDiscountType() == DiscountTypeEnum.sumPrice) {
                    marginS = marginS.multiply(amount).multiply(secondaryAmountTmp);
                    discountS = discountS.multiply(amount).multiply(secondaryAmountTmp);
                }
            }
            //Если скидка/наценка указана в %
            if (item.getDiscountType() == DiscountTypeEnum.percent) {
                marginS = totalSum.multiply(marginS).divide(BigDecimal.valueOf(100),4,BigDecimal.ROUND_HALF_UP).abs();
                discountS = totalSum.multiply(discountS).divide(BigDecimal.valueOf(100),4,BigDecimal.ROUND_HALF_UP).abs();
            }
            totalSum = (totalSum.subtract(discountS).add(marginS)).setScale(2, BigDecimal.ROUND_HALF_UP);
            taxSum = totalSum.multiply(taxRate);
            taxSum = taxSum.divide(taxRate.add(BigDecimal.valueOf(1)), 2, BigDecimal.ROUND_HALF_UP);
            item.setCost(totalSum.divide(amount,2, BigDecimal.ROUND_HALF_UP));
            sum = totalSum.subtract(taxSum);
            item.setSumWithoutNds(sum);
            item.setTaxSum(taxSum);
            item.setTotalSum(totalSum);
        }
        return item;
    }

    public Map<String, Object> validateCalculate() {
        Map<String, Object> params = new HashMap<>();
        if (item.getProduct().getNomenclature().getTax() == null) {
            params.put("message", "Внимание! Отсутсвует значение в поле Налог!");
            return params;
        }

        if (item.getProduct().getSecondaryUnit() != null && item.getSecondaryAmount() == null) {
            params.put("message", "Внимание! Отсутсвует Количество для второй единицы измерения!");
            return params;
        }

        if (item.getProduct().getSecondaryUnit() != null && item.getSecondaryAmount().signum() != 1) {
            params.put("message", "Внимание! Количество для второй единицы измерения, должно быть больше \"0\"!");
            return params;
        }

        if (item.getAmount() == null) {
            params.put("message", "Внимание! Отсутсвует значение в поле Количество!");
            return params;
        } else {
            if (item.getProduct().getMinQuantity() != null) {
                if (item.getAmount().compareTo(item.getProduct().getMinQuantity()) == -1) { //amount<min
                    params.put("message", String.format("Внимание! Указаное количество меньше, чем минимально возможное (%s)!", item.getProduct().getMinQuantity()));
                    return params;
                }
            }
            if (item.getProduct().getMaxQuantity() != null) {
                if (item.getAmount().compareTo(item.getProduct().getMaxQuantity()) == 1) {   //amount>max
                    params.put("message", String.format("Внимание! Указаное количество больше, чем максимально возможное (%s)!", item.getProduct().getMaxQuantity()));
                    return params;
                }
            }
            if (item.getProduct().getNomenclature().getIsFractional() == null || !item.getProduct().getNomenclature().getIsFractional()) {
                if (!isWhole(item.getAmount())) {
                    params.put("message", "Внимание! Количество не может быть дробным!\nЗначение было округлено до целого числа.");
                    item.setAmount(item.getAmount().setScale(0, RoundingMode.HALF_UP));
                    return params;
                }
            }

        }
        return params;
    }

    public static boolean isWhole(BigDecimal bigDecimal) {
        return bigDecimal.setScale(0, RoundingMode.HALF_UP).compareTo(bigDecimal) == 0;
    }
}
