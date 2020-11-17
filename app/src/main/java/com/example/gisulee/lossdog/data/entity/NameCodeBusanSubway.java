package com.example.gisulee.lossdog.data.entity;

import android.os.Bundle;

import com.example.gisulee.lossdog.common.DateCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class NameCodeBusanSubway extends NameCode {
    public static final String instance = "BUSAN_API";
    private final String[] mCategory = new String[]{
            "현금",
            "휴대폰",
            "전자기기",
            "가방",
            "지갑",
            "NULL",
            "의류",
            "NULL",
            "도서용품",
            "기타"
    };

    public NameCodeBusanSubway() {
    }

    public NameCodeBusanSubway(String name, String code) {
        this.instanceCode = instance;
        this.name = name;
        this.code = code;
    }


    @Override
    public String convertProCategory(NameCode nameCode) {

        StringTokenizer st= new StringTokenizer(nameCode.name);
        String name = st.nextToken();
        String code;
        if (name.equals("") || name.equals("물품"))
            code = "";
        else {
            ArrayList<String> arrayList = new ArrayList(Arrays.asList(mCategory));
            code = Integer.toString(arrayList.indexOf(name) + 1);
        }
        return code;
    }

    @Override
    public Bundle createRequestBundle(AppSettings settings, NameCode areaCategoryCode, NameCode prdCategoryNameCode) {
        String prdMainCode, prdSubCode = "";
        prdMainCode = areaCategoryCode.convertProCategory(prdCategoryNameCode);

        Bundle createBundle =
                new Request.BundleBuilder()
                        .setInstance(areaCategoryCode.instanceCode)
                        .setAreaCode(areaCategoryCode.code)
                        .setPrdMainCategory(prdMainCode)
                        .setPrdSubCategory(prdSubCode)
                        .setBeginDate(DateCalculator.getConvertFormat(settings.getBeginDate()))
                        .setEndDate(DateCalculator.getConvertFormat(settings.getEndDate()))
                        .setPage(String.valueOf(1))
                        .setRows(Integer.toString(Integer.MAX_VALUE))
                        .build();

        return createBundle;
    }
}
