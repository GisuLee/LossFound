package com.example.gisulee.lossdog.data.entity;

import android.os.Bundle;

import com.example.gisulee.lossdog.common.DateCalculator;

public class NameCodePoliceAPI extends NameCode  {

    public static final String instance = "POLICE_API";
    public NameCodePoliceAPI(){}
    public NameCodePoliceAPI(String name, String code) {
        this.instanceCode = instance;
        this.name = name;
        this.code = code;
    }

    @Override
    public String convertProCategory(NameCode nameCode) {
        String mainCode;

        /* 물품 카테고리가 상위코드이면*/
        if (nameCode.code.contains("000")) {
            mainCode = nameCode.code;
            //subCode = "";

            /* 물품 카테고리를 전체("")로 선택했다면*/
        } else if (nameCode.code.equals("")) {
            mainCode = "";
            //subCode = "";

        } else {  /* 물품 카테고리가 하위코드이면*/
            mainCode = nameCode.code.substring(0, 3) + "000";
            //subCode = settings.getSelectedPrdList().get(j).code;
        }

        return mainCode;
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
