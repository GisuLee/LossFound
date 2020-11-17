package com.example.gisulee.lossdog.view.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.view.listenerinterface.OnBackPressedCustom;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.adapter.NearPlaceTdataListRecyclerAdapter;
import com.example.gisulee.lossdog.view.activity.MainActivity;
import com.example.gisulee.lossdog.view.activity.NearPlaceLossListActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class NearFinderTDataFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnBackPressedCustom {


    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private final int POLICE = 1;
    private final int SUB_POLICE = 6;
    private final int SUBWAY = 2;
    private final int KORAIL = 3;
    private final int AIRPORT = 4;
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = Integer.MAX_VALUE;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = Integer.MAX_VALUE; // 0.5초
    private static final String KEY = "AIzaSyBBM474qKIdfcGX7KKAeY5opePjWUmRt_o";
    HashMap<String, MarkerHolder> markerHolderMap = new HashMap<String, MarkerHolder>();
    HashMap<String, TMapMarkerHolder> TMapMarkerHolderMap = new HashMap<String, TMapMarkerHolder>();
    private AppCompatActivity mActivity = (MainActivity) getActivity();
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng mCurrentPosition;
    LatLng mPickPosition;
    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private DataManager mDataManager = DataManager.getInstance();
    private MapView mMapView = null;
    private FloatingActionButton btnMarker;
    private FloatingActionButton btnMyLocation;
    private Button btnPick;
    private ImageView btnClose;
    private LinearLayout linearTopPanel;
    private ImageView imageCenterPick;
    private ProgressDialog progressDialog;

    private TMapData mTmapData;              //T맵 데이터
    private TMapView mTMapView;             //T맵 뷰
    private ArrayList<TMapPOIItem> mTMapPOIItemList = new ArrayList();
    private static HashMap<String, Marker> previous_marker = new HashMap();
    private SlidingDrawer mSlide;
    private LinearLayout mContainer;
    private ImageView mSlideHandle;
    private boolean isRequestMyLocation = false;
    private TextView btnSetRound;
    private LinearLayout linearInfoLayout;
    private CheckBox checkPolice;
    private CheckBox checkKorail;
    private CheckBox checkSubway;
    private CheckBox checkSubPolice;
    private CheckBox checkAirport;
    private boolean isCheckPolice = true;
    private boolean isCheckKorail = true;
    private boolean isCheckSubway = true;
    private boolean isCheckSubPolice = true;
    private boolean isCheckAirport = true;
    private int mRoundIndex = 1;                                        //반경 설정 변수
    private final int mRoundRadius[] = new int[]{1, 2, 3, 5, 10};       //TMap 반경인덱스
    private final String mRoundItems[] = {"1KM", "2KM", "3KM", "5KM", "10KM"};
    private Circle mCircle;

    public NearFinderTDataFragment() {
        // required
    }

    @Override
    public boolean onBackPressed() {
        if (mSlide != null) {
            if (mSlide.isOpened()) {
                mSlide.animateClose();
                return true;
            }
        }
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (btnMarker != null) {
                ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                anim.setFillEnabled(true);
                anim.setDuration(500);
                anim.setInterpolator(new OvershootInterpolator(1.5f));
                btnMarker.startAnimation(anim);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mTMapView = new TMapView(getContext());
        mTMapView.setSKTMapApiKey("5ef92620-07a1-4561-992e-55eac991c742");
        mTmapData = new TMapData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.activity_nearfind, container, false);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        linearTopPanel = view.findViewById(R.id.topPanel);
        imageCenterPick = view.findViewById(R.id.location_pick_icon);

        btnMyLocation = view.findViewById(R.id.btn_mylocation);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
                    Log.d(TAG, "onStart: mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                    isRequestMyLocation = true;
                }

                if (mCurrentLocatiion != null)
                    setCurrentLocation(mCurrentLocatiion);

                if (mSlide.isOpened())
                    mSlide.animateClose();
            }
        });

        btnMarker = view.findViewById(R.id.btn_marker);
        btnMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlide.isOpened())
                    mSlide.animateClose();
                linearTopPanel.setVisibility(View.VISIBLE);
                imageCenterPick.setVisibility(View.VISIBLE);

                LatLng center = mGoogleMap.getCameraPosition().target;

                if (mCircle == null) {
                    mCircle = mGoogleMap.addCircle(new CircleOptions()
                            .center(center)
                            .radius(mRoundRadius[mRoundIndex] * 1000)
                            .strokeWidth(5)
                            .strokeColor(getContext().getResources().getColor(R.color.colorAccent))
                            .fillColor(0x220385ff));
                } else {
                    mCircle.setVisible(true);
                }
            }
        });

        btnPick = view.findViewById(R.id.btn_pick);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.clear();
                mCircle = null;

                LatLng center = mGoogleMap.getCameraPosition().target;
                mPickPosition = mGoogleMap.getCameraPosition().target;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(center);
                markerOptions.title("내 위치");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                currentMarker = mGoogleMap.addMarker(markerOptions);

                linearTopPanel.setVisibility(View.INVISIBLE);
                imageCenterPick.setVisibility(View.INVISIBLE);
                previous_marker.clear();
                mTMapPOIItemList.clear();
                mContainer.removeAllViews();

                progressDialog.setMessage("로딩 중...");
                progressDialog.show();

                mGoogleMap.addCircle(new CircleOptions()
                        .center(center)
                        .radius(mRoundRadius[mRoundIndex] * 1000)
                        .strokeWidth(5)
                        .strokeColor(getContext().getResources().getColor(R.color.colorAccent))
                        .fillColor(0x150385ff));

                int radius = mRoundRadius[mRoundIndex];
                Log.d(TAG, "onClick: radis" + radius);

                View added;
                ArrayList<String> lowBizs;

                lowBizs = getLowBizs(POLICE);
                added = addRecycelerView(mContainer, "경찰서", 0);
                showTmapFindAroundKeywordPOI(added, mPickPosition, "경찰서", radius, false, lowBizs);

                lowBizs = getLowBizs(SUBWAY);
                added = addRecycelerView(mContainer, "지하철", 0);
                showTmapFindAroundKeywordPOI(added, mPickPosition, "지하철", radius, false, lowBizs);

                lowBizs = getLowBizs(AIRPORT);
                added = addRecycelerView(mContainer, "기타", 0);
                showTmapFindAroundKeywordPOI(added, mPickPosition, "공항", radius, false, lowBizs);
                lowBizs = getLowBizs(KORAIL);
                showTmapFindAroundKeywordPOI(added, mPickPosition, "역", radius, true, lowBizs);
                //교통시설,

                linearInfoLayout.setVisibility(View.GONE);
            }
        });

        btnClose = view.findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearTopPanel.setVisibility(View.INVISIBLE);
                imageCenterPick.setVisibility(View.INVISIBLE);
                if (mCircle != null)
                    mCircle.setVisible(false);
            }
        });

        linearInfoLayout = view.findViewById(R.id.linear_info);
        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDailogStyle);
        mContainer = view.findViewById(R.id.container);
        mSlide = view.findViewById(R.id.slide);
        mSlideHandle = view.findViewById(R.id.handle);
        btnSetRound = view.findViewById(R.id.btn_set_round);
        checkPolice = view.findViewById(R.id.check_police);
        checkSubPolice = view.findViewById(R.id.check_sub_police);
        checkSubway = view.findViewById(R.id.check_subway);
        checkKorail = view.findViewById(R.id.check_korail);
        checkAirport = view.findViewById(R.id.check_airport);

        checkPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheckPolice = b;
            }
        });

        checkSubPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheckSubPolice = b;
            }
        });

        checkSubway.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheckSubway = b;
            }
        });

        checkKorail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheckKorail = b;
            }
        });

        checkAirport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheckAirport = b;
            }
        });

        checkPolice.setChecked(isCheckPolice);
        checkSubPolice.setChecked(isCheckSubPolice);
        checkSubway.setChecked(isCheckSubway);
        checkKorail.setChecked(isCheckKorail);
        checkAirport.setChecked(isCheckAirport);

        btnSetRound.setText(mRoundItems[mRoundIndex]);
        btnSetRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnSetRound(mRoundIndex, btnSetRound);

            }
        });
        return view;
    }

    private ArrayList<String> getLowBizs(int index) {
        ArrayList<String> ret = new ArrayList<>();
        switch (index) {
            case POLICE:
                if (isCheckPolice)
                    ret.add(new String("경찰서"));
                if (isCheckSubPolice)
                    ret.add(new String("파출소"));
                break;

            case SUBWAY:

                if (isCheckSubway)
                    ret.add(new String("지하철역"));
                break;

            case KORAIL:
                if (isCheckKorail)
                    ret.add(new String("기차역"));
                break;
            case AIRPORT:
                if (isCheckAirport)
                    ret.add(new String("공항"));
                break;
            case KORAIL | AIRPORT:
                if (isCheckKorail)
                    ret.add(new String("기차역"));
                if (isCheckAirport)
                    ret.add(new String("공항"));
                break;
        }
        return ret;
    }

    private void onBtnSetRound(int roundIndex, TextView btnSetRound) {

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                getActivity(), R.layout.dialog_checkbox, mRoundItems);


        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("탐색 범위 설정");
        dialog.setSingleChoiceItems(adapter, roundIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRoundIndex = i;
                btnSetRound.setText(mRoundItems[mRoundIndex]);
                if (mCircle != null)
                    mCircle.setRadius(mRoundRadius[mRoundIndex] * 1000);
                dialogInterface.dismiss();
            }
        }).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        LatLng sydney = new LatLng(37.56, 126.97);

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng center = mGoogleMap.getCameraPosition().target;
                if (mCircle != null) {
                    mCircle.setCenter(center);
                }
            }
        });


        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker mark) {

                View v = getLayoutInflater().inflate(R.layout.map_infowindow, null);

                TextView txtLocation = (TextView) v.findViewById(R.id.txtTitle);
                TextView txtArrow = (TextView) v.findViewById(R.id.txt_arrow_next);
                //TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
                //TextView txtTel = (TextView) v.findViewById(R.id.txtTel);

                TMapMarkerHolder mHolder = TMapMarkerHolderMap.get(mark.getSnippet());
                if (isMarkerMyLocation(mHolder)) {
                    txtLocation.setText(mark.getTitle());
                    txtArrow.setVisibility(View.GONE);
                } else {
                    TMapPOIItem place = mHolder.tMapPOIItem;
                    txtLocation.setText(place.getPOIName());
                    //txtAddress.setText(replaceSpaceFromNULL(place.getPOIAddress()));
                    //txtTel.setText(replaceTelNo(place.telNo));
                }

                return v;

            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                TMapMarkerHolder markerHolder = TMapMarkerHolderMap.get(marker.getSnippet());
                if (isMarkerMyLocation(null)) {
                    String placeTitle = markerHolder.tMapPOIItem.getPOIName();
                    String tel = markerHolder.tMapPOIItem.telNo;
                    showActionMoreDialog(placeTitle, tel);
                }
            }
        });
    }

    private boolean isMarkerMyLocation(TMapMarkerHolder mHolder) {
        if (mHolder == null) return true;
        return false;
    }

    private void showActionMoreDialog(String placeTitle, String tel) {
        String[] menus = {"전화걸기", "습득물 보기"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(placeTitle);
        builder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                        startActivity(tt);
                        break;
                    case 1:
                        Intent intent = new Intent(getActivity(), NearPlaceLossListActivity.class);
                        intent.putExtra("placeTitle", placeTitle);
                        startActivityForResult(intent, 1);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    public static String replaceTelNo(String telNo) {
        if (telNo == null) {
            return "없음";
        }
        return telNo;
    }

    public static String replaceSpaceFromNULL(String str) {
        return str.replace(" null", "");
    }


    public void showTmapFindAroundKeywordPOI(View view, LatLng latLng, String keyword, int nRadius, boolean isDismiss, ArrayList<String> bizNames) {
        TMapPoint tMapPoint = new TMapPoint(latLng.latitude, latLng.longitude);
        mTmapData.findAroundKeywordPOI(tMapPoint, keyword, nRadius, 200, new TMapData.FindAroundKeywordPOIListenerCallback() {
            @Override
            public void onFindAroundKeywordPOI(ArrayList<TMapPOIItem> arrayList) {
                Log.d(TAG, "onFindAroundKeywordPOI: size = " + arrayList.size());
                processTmapData(view, arrayList, isDismiss, bizNames);
            }
        });
    }

    private View addRecycelerView(ViewGroup container, String name, int count) {
        View view = getLayoutInflater().inflate(R.layout.near_place_list, container, false);
        TextView txtCategory = view.findViewById(R.id.txtCategoryName);
        RecyclerView recyclerView = view.findViewById(R.id.listView);
        txtCategory.setText(name + " (" + count + ")");
        container.addView(view);
        return view;
    }


    private void processTmapData(View view, ArrayList<TMapPOIItem> arrayList, boolean isDismiss, ArrayList<String> bizNames) {

        ArrayList<TMapPOIItem> tMapPOIItems = new ArrayList();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arrayList.size(); i++) {
                    TMapPOIItem tmapPlace = arrayList.get(i);
                    if (!previous_marker.containsKey(tmapPlace.getPOIID())) {

                        if (tmapPlace.telNo == null)
                            continue;

                        boolean isContain = (bizNames.size() > 0 ? false : false);
                        for (String str : bizNames) {
                            Log.d(TAG, "run biz :" + str);
                            if (tmapPlace.lowerBizName.equals(str)) {
                                isContain = true;
                                break;
                            }
                        }

                        if (!isContain) continue;

                        LatLng latLng
                                = new LatLng(tmapPlace.getPOIPoint().getLatitude()
                                , tmapPlace.getPOIPoint().getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Tmap");
                        markerOptions.snippet(tmapPlace.getPOIID());
                        double dis = tmapPlace.getDistance(new TMapPoint(mPickPosition.latitude, mPickPosition.longitude));
                        dis = dis * 0.001;
                        dis = Math.round(dis * 10) / 10.0;
                        tmapPlace.distance = Double.toString(dis);
                        tmapPlace.telNo = replaceTelNo(tmapPlace.telNo);
                        if (tmapPlace.getPOIAddress() != null)
                            tmapPlace.address = replaceSpaceFromNULL(tmapPlace.getPOIAddress());
                        Marker item = mGoogleMap.addMarker(markerOptions);

                        TMapMarkerHolder mHolder = new TMapMarkerHolder(tmapPlace);
                        TMapMarkerHolderMap.put(tmapPlace.getPOIID(), mHolder); //Add info to HashMap
                        tMapPOIItems.add(tmapPlace);
                        previous_marker.put(tmapPlace.getPOIID(), item);

                        Log.d(TAG, "run: " + tmapPlace.getPOIAddress());

                    }

                }

                String strCount = new String("");
                TextView txtCategoryName = view.findViewById(R.id.txtCategoryName);
                for (int k = 0; k < txtCategoryName.getText().length(); k++) {
                    if (48 <= txtCategoryName.getText().charAt(k) && txtCategoryName.getText().charAt(k) <= 57) {
                        strCount += txtCategoryName.getText().charAt(k);
                    }
                }

                txtCategoryName.setText(txtCategoryName.getText().toString().replace(strCount, Integer.toString(Integer.parseInt(strCount) + tMapPOIItems.size())));
                RecyclerView recyclerView = view.findViewById(R.id.listView);
                NearPlaceTdataListRecyclerAdapter mRecyclerAdapter;
                mRecyclerAdapter = new NearPlaceTdataListRecyclerAdapter(getContext(), tMapPOIItems);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(mRecyclerAdapter);

                mRecyclerAdapter.setOnItemClickListener(new NearPlaceTdataListRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        TMapPoint tMapPoint = tMapPOIItems.get(position).getPOIPoint();
                        LatLng selectedLatLng = new LatLng(tMapPoint.getLatitude(), tMapPoint.getLongitude());
                        previous_marker.get(tMapPOIItems.get(position).getPOIID()).showInfoWindow();
                        changeOffsetCenter(selectedLatLng.latitude, selectedLatLng.longitude);
                    }

                    @Override
                    public void onButtonClick(View v, int position) {

                        String placeTitle = tMapPOIItems.get(position).getPOIName();
                        String tel = tMapPOIItems.get(position).telNo;

                        showActionMoreDialog(placeTitle, tel);
                    }
                });

                txtCategoryName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (recyclerView.getVisibility() == View.VISIBLE) {
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

                if (isDismiss) {
                    if (!mSlide.isOpened())
                        mSlide.animateOpen();
                    progressDialog.dismiss();

                    /*  *//* 마커를 모두 포함하는 카메ㅏㄹ 좌표무하기*//*
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Map.Entry<String, Marker> entry : previous_marker.entrySet()) {

                            String key = entry.getKey();
                            Marker marker = entry.getValue();
                            builder.include(marker.getPosition());

                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 0; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mGoogleMap.animateCamera(cu);*/
                }

            }
        });
    }

    public void changeOffsetCenter(double latitude, double longitude) {
        VisibleRegion visibleRegion = mGoogleMap.getProjection()
                .getVisibleRegion();

        Point x = mGoogleMap.getProjection().toScreenLocation(
                visibleRegion.farRight);
        Point y = mGoogleMap.getProjection().toScreenLocation(
                visibleRegion.nearLeft);

        Point centerPoint = new Point(x.x / 2, y.y / 2);

        int[] handlePoint = new int[2];
        int[] mapViewPoint = new int[2];
        mSlideHandle.getLocationOnScreen(handlePoint);
        mMapView.getLocationOnScreen(mapViewPoint);
        Log.d(TAG, "changeOffsetCenter: mapviewpoint" + Arrays.toString(mapViewPoint));
        Log.d(TAG, "changeOffsetCenter:handlepoint" + Arrays.toString(handlePoint));
        int handleToDis = ((handlePoint[1] - mapViewPoint[1]) / 2) - (int) convertDpToPixel(30, getContext());
        Point mappoint = mGoogleMap.getProjection().toScreenLocation(new LatLng(latitude, longitude));
        int dy = centerPoint.y - handlePoint[1] + handleToDis;
        if (dy < 0) dy = 0;
        mappoint.set(mappoint.x, mappoint.y + dy); // change these values as you need , just hard coded a value if you want you can give it based on a ratio like using DisplayMetrics  as well
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mGoogleMap.getProjection().fromScreenLocation(mappoint)));
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;

    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG, "onConnected: ");
        if (mRequestingLocationUpdates == false) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } else {

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        mMapView.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }

    @Override
    public void onStop() {
        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
//        setDefaultLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentPosition
                = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "onLocationChanged : " + getCurrentAddress(mCurrentPosition));
        mCurrentLocatiion = location;

        if (isRequestMyLocation) {
            setCurrentLocation(mCurrentLocatiion);
            isRequestMyLocation = false;
        }
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: ");
        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }


    private void stopLocationUpdates() {

        Log.d(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    public String getCurrentAddress(LatLng latlng) {
        Log.d(TAG, "getCurrentAddress: ");
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Log.d(TAG, "getCurrentAddress: 지오코더 서비스 사용불가");
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        //showDialogForLocationServiceSetting();
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location) {
        Log.d(TAG, "setCurrentLocation: ");
        mMoveMapByUser = false;

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        float zoom = mGoogleMap.getCameraPosition().zoom;       //defalut 15;

        if (mMoveMapByAPI) {

            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }


    public void setDefaultLocation() {
        Log.d(TAG, "setDefaultLocation: ");
        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //showDialogForLocationServiceSetting();
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if (mGoogleApiClient.isConnected() == false) {

                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if (mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if (mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }


            } else {

                checkPermissions();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mActivity.finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mActivity.finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들

    private void showDialogForLocationServiceSetting() {
        Log.d(TAG, "showDialogForLocationServiceSetting: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public class MarkerHolder {

        public String placeId;
        public String tel;

        public MarkerHolder(String placeId) {
            this.placeId = placeId;
        }
    }

    public class TMapMarkerHolder {
        public TMapPOIItem tMapPOIItem;

        public TMapMarkerHolder(TMapPOIItem tMapPOIItem) {
            this.tMapPOIItem = tMapPOIItem;
        }
    }

    private void parsingPlaceTel(HashMap<String, String> hashMap) {
        StringBuffer name = new StringBuffer("");
        StringBuffer tel = new StringBuffer("");
        try {
            XmlPullParser parser = getResources().getXml(R.xml.police_tel);
            int eventType = parser.getEventType();
            String tag;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            name.setLength(0);
                            tel.setLength(0);
                        } else if (tag.equals("name")) {
                            name.append(parser.nextText());
                        } else if (tag.equals("tel")) {
                            tel.append(parser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tag = parser.getName(); //테그 이름 얻어오기
                        if (tag.equals("item")) {
                            hashMap.put(name.toString(), tel.toString());
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public void mapViewFix(View view){
        AppBarLayout appBar = (AppBarLayout) view.findViewById(R.id.app_bar);
        if (appBar.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }
    }*/
}


