package com.example.gisulee.lossdog.common;

import android.content.res.Resources;
import android.util.Log;

import com.example.gisulee.lossdog.data.entity.DepAddress;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.remote.AsyncTaskGetAreaCategory;
import com.example.gisulee.lossdog.data.remote.AsyncTaskGetDepAddress;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AreaUtil {

    private static final String TAG = "AreaUtil";
    public static ArrayList<DepAddress> depAddressList = new ArrayList();
    public static ArrayList<NameCode> areaList = new ArrayList<>();
    public static Resources res;

    private AreaUtil() {
    }

    private static class ClassHolder {
        public static final AreaUtil Instance = new AreaUtil();
    }

    public static AreaUtil getInstance() {
        return AreaUtil.ClassHolder.Instance;
    }

    public void setRes(Resources res) {
        this.res = res;
    }

    public boolean isPlaceContainArea(String place, String code) {
        boolean ret;

        ArrayList<String> depList = getPlaceContainAddr(code);

        for (int i = 0; i < depList.size(); i++) {
            if (depList.get(i).equals(place)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getPlaceContainAddr(String code) {
        ArrayList<String> ret = new ArrayList();

        boolean isFlag = false;
        DepAddress depAddress = new DepAddress();

        String findMainName = getNameFromCode(getMainAreaCode(code));
        String findAddress = findMainName + " " + getNameFromCode(code);
        int beginIndex = getBeginIndex(findMainName);
        for (int i = beginIndex; i < depAddressList.size(); i++) {
            depAddress = depAddressList.get(i);
            if (depAddress.name.equals(findMainName) == false) {
                break;
            }
            if (depAddress.addr.startsWith(findAddress)) {
                if (!isFlag) {
                    ret.add(depAddress.police);
                    isFlag = true;
                }
                ret.add(depAddress.subPolice);

        /*        Log.d(TAG, "getPlaceContainAddr: subpolice = " + depAddress.police);
                Log.d(TAG, "getPlaceContainAddr: subpolice = " + depAddress.subPolice);
                Log.d(TAG, "getPlaceContainAddr: addr = " + depAddress.addr);*/
            }
        }

        return ret;
    }

    public void loadAreaList() {
        try {
            new AsyncTaskGetAreaCategory(res, areaList).execute().get();
            new AsyncTaskGetDepAddress(res, depAddressList).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
   /*     Log.d(TAG, "loadAreaList: arealist size = " + areaList.size());
        Log.d(TAG, "loadAreaList: depAddressList size = " + depAddressList.size());*/
    }

    public String getMainAreaCode(String code) {
        String ret = code.substring(0, 3) + "000";
        return ret;
    }

    private String getNameFromCode(String code) {
        for (int i = 0; i < areaList.size(); i++) {
            if (areaList.get(i).code.equals(code)) {
                return areaList.get(i).name;
            }
        }

        Log.d(TAG, "getAddressFromCode: null");
        return null;
    }

    private int getBeginIndex(String name) {
        int beginIndex;
        switch (name) {
            case "서울특별시":
                beginIndex = 0;
                break;
            case "부산광역시":
                beginIndex = 240;
                break;
            case "대구광역시":
                beginIndex = 333;
                break;
            case "인천광역시":
                beginIndex = 395;
                break;
            case "광주광역시":
                beginIndex = 470;
                break;
            case "대전광역시":
                beginIndex = 510;
                break;
            case "울산광역시":
                beginIndex = 538;
                break;
            case "경기도":
                beginIndex = 568;
                break;
            case "강원도":
                beginIndex = 903;
                break;
            case "충청북도":
                beginIndex = 1008;
                break;
            case "충청남도":
                beginIndex = 1087;
                break;
            case "전라북도":
                beginIndex = 1209;
                break;
            case "전라남도":
                beginIndex = 1373;
                break;
            case "경상북도":
                beginIndex = 1579;
                break;
            case "경상남도":
                beginIndex = 1812;
                break;
            case "제주특별자치도":
                beginIndex = 1984;
                break;
            default:
                beginIndex = -1;
        }
        return beginIndex;
    }
}
