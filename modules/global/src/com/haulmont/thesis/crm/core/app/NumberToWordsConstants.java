/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

/**
 * @author Created by k.khoroshilov on 21.03.2016.
 */
class NumberToWordsConstants {

    final static int DG_POWER = 6;

    final static String minus_ru = "минус ";
    final static String minus_en = "negative ";
    final static String zero_ru = "ноль ";
    final static String zero_en = "zero ";

    final static String[][] a_power_ru = new String[][]{
            {"0", ""            , ""             , ""              },  // 1
            {"1", "тысяча "     , "тысячи "      , "тысяч "        },  // 2
            {"0", "миллион "    , "миллиона "    , "миллионов "    },  // 3
            {"0", "миллиард "   , "миллиарда "   , "миллиардов "   },  // 4
            {"0", "триллион "   , "триллиона "   , "триллионов "   },  // 5
            {"0", "квадриллион ", "квадриллиона ", "квадриллионов "},  // 6
            {"0", "квинтиллион ", "квинтиллиона ", "квинтиллионов "},  // 7
            {"0", "секстиллион" , "секстиллиона ", "секстиллионов "},  // 8
            {"0", "септиллион"  , "септиллиона " , "септиллионов " },  // 9
            {"0", "октиллион"   , "октиллиона "  , "октиллионов "  },  // 10
            {"0", "нониллион"   , "нониллиона "  , "нониллионов "  },  // 11
            {"0", "дециллион"   , "дециллиона "  , "дециллионов "  }   // 12
    };

    final static String[][] digit_ru = new String[][] {
            {""   ,""       , "десять "      , ""            ,""      },
            {"один "  ,"одна "  , "одиннадцать " , "десять "     ,"сто "      },
            {"два "   ,"две "   , "двенадцать "  , "двадцать "   ,"двести "   },
            {"три "   ,"три "   , "тринадцать "  , "тридцать "   ,"триста "   },
            {"четыре ","четыре ", "четырнадцать ", "сорок "      ,"четыреста "},
            {"пять "  ,"пять "  , "пятнадцать "  , "пятьдесят "  ,"пятьсот "  },
            {"шесть " ,"шесть " , "шестнадцать " , "шестьдесят " ,"шестьсот " },
            {"семь "  ,"семь "  , "семнадцать "  , "семьдесят "  ,"семьсот "  },
            {"восемь ","восемь ", "восемнадцать ", "восемьдесят ","восемьсот "},
            {"девять ","девять ", "девятнадцать ", "девяносто "  ,"девятьсот "}
    };


    static final String[] majorNames = {
            "", " thousand", " million", " billion", " trillion", " quadrillion", " quintillion",
            " sextillion", " septillion", " octillion", " nonillion", " decillion"

    };

    static final String[] tensNames = {
            "", " ten", " twenty", " thirty", " fourty", " fifty", " sixty", " seventy", " eighty", " ninety"
    };

    static final String[] numNames = {
            "", " one", " two", " three", " four", " five", " six", " seven", " eight", " nine",
            " ten", " eleven", " twelve", " thirteen", " fourteen", " fifteen",
            " sixteen", " seventeen", " eighteen", " nineteen"
    };
}
