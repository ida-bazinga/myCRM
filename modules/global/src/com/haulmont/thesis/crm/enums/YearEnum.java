package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author d.ivanov
 */
public enum YearEnum implements EnumClass<Integer> {
    Y_2016(2016),
    Y_2017(2017),
    Y_2018(2018),
    Y_2019(2019),
    Y_2020(2020),
    Y_2021(2021),
    Y_2022(2022),
    Y_2023(2023),
    Y_2024(2024),
    Y_2025(2025);

    private Integer id;

    YearEnum(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static YearEnum fromId(Integer id) {
        for (YearEnum at : YearEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public static List<YearEnum> getOptions(){
        return getOptions(new ArrayList<YearEnum>());
    }

    public static List<YearEnum> getOptions(List<YearEnum> exclude){
        List<YearEnum> result = new LinkedList<>();

        for (YearEnum val : YearEnum.values()) {
            if (!exclude.contains(val)) result.add(val);
        }
        return result;
    }
}