package com.example.gisulee.lossdog.data.entity;

import android.os.Bundle;

import com.example.gisulee.lossdog.common.DateCalculator;

import java.util.StringTokenizer;

public class NameCodeSeoul extends NameCode {
    public static final String instance = "SEOUL";

    public static final String SELECT_BUS = "SELECT_BUS";
    public static final String SELECT_SUBWAY = "SELECT_SUBWAY";
    public static final String SELECT_TAXI = "SELECT_TAXI";
    public static final String SELECT_KORAIL = "SELECT_KORAIL";

    public NameCodeSeoul() {
    }

    public NameCodeSeoul(String name, String code) {
        this.instanceCode = instance;
        this.name = name;
        this.code = code;
    }


    @Override
    public String convertProCategory(NameCode categoryNameCode) {
        StringTokenizer st = new StringTokenizer(categoryNameCode.name);
        String name = st.nextToken();
        StringBuilder strCate = new StringBuilder("");

        switch (name) {
            case "물품":

                break;
            case "지갑":
                strCate.append("&cate1=%C1%F6%B0%A9");
                break;
            case "가방":
                strCate.append("&cate2=%BC%EE%C7%CE%B9%E9&cate4=%B0%A1%B9%E6&cate5=%BA%A3%B3%B6");
                break;
            case "서류":
                strCate.append("&cate3=%BC%AD%B7%F9%BA%C0%C5%F5");
                break;
            case "휴대폰":
                strCate.append("&cate6=%C7%DA%B5%E5%C6%F9");
                break;
            case "의류":
                strCate.append("&cate7=%BF%CA");
                break;
            case "도서용품":
                strCate.append("&cate8=%C3%A5&cate9=%C6%C4%C0%CF");
                break;
            case "기타":
                strCate.append("&cate10=9");
                break;
            default:
                strCate.append("-1");
        }
        return strCate.toString();
    }

    @Override
    public Bundle createRequestBundle(AppSettings settings, NameCode areaCategoryCode, NameCode prdCategoryNameCode) {
        String areaCode, prdMainCode, prdSubCode = "";
        areaCode = convertAreaCode(areaCategoryCode);
        prdMainCode = areaCategoryCode.convertProCategory(prdCategoryNameCode);

        Bundle createBundle =
                new Request.BundleBuilder()
                        .setInstance(areaCategoryCode.instanceCode)
                        .setAreaCode(areaCode)
                        .setPrdMainCategory(prdMainCode)
                        .setPrdSubCategory(prdSubCode)
                        .setBeginDate(DateCalculator.getConvertFormat(settings.getBeginDate()))
                        .setEndDate(DateCalculator.getConvertFormat(settings.getEndDate()))
                        .setPage(String.valueOf(1))
                        .setRows(Integer.toString(Integer.MAX_VALUE))
                        .build();

        return createBundle;
    }

    public String convertAreaCode(NameCode area) {
        return area.code;
        /*switch (area.code) {
            case NameCode.BUS:
                return NameCodeSeoul.SELECT_BUS;
            case NameCode.SUBWAY:
                return NameCodeSeoul.SELECT_SUBWAY;
            case NameCode.TAXI:
                return NameCodeSeoul.SELECT_TAXI;
            case NameCode.KORAIL:
                return NameCodeSeoul.SELECT_KORAIL;
            default:
                Log.d(TAG, "convertAreaCode: convert error " + area.code);
                return null;
        }
*/
    }
}
