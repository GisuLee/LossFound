package com.example.gisulee.lossdog.view.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.data.remote.AsyncTaskGetPlaceDetail;
import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.data.entity.PlaceDetail;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.adapter.NearPlaceListRecyclerAdapter;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static android.content.Context.LOCATION_SERVICE;

public class Backup_NearFinderFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        PlacesListener {

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = Integer.MAX_VALUE;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private static final String KEY = "AIzaSyBBM474qKIdfcGX7KKAeY5opePjWUmRt_o";
    HashMap<String, MarkerHolder> markerHolderMap = new HashMap<String, MarkerHolder>();
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

    private static HashMap<String,String> mHashPlaceTel = new HashMap();
    private DataManager mDataManager = DataManager.getInstance();
    private MapView mapView = null;
    private FloatingActionButton btnMarker;
    private AppCompatButton btnMyLocation;
    private Button btnPick;
    private ImageView btnClose;
    private LinearLayout linearTopPanel;
    private ImageView imageCenterPick;
    private ProgressDialog progressDialog;
    private List<Marker> previous_marker = null;
    private NearPlaceListRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;

    public Backup_NearFinderFragment() {
        // required
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesFinished() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: ");
        if(isVisibleToUser){
            if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){
                parsingPlaceTel(mHashPlaceTel);
                Log.d(TAG, "onStart: mGoogleApiClient connect");
                mGoogleApiClient.connect();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        previous_marker = new ArrayList<Marker>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.activity_nearfind, container, false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        
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
                startLocationUpdates();
                if(mCurrentLocatiion != null)
                    setCurrentLocation(mCurrentLocatiion);
            }
        });

        btnMarker = view.findViewById(R.id.btn_marker);
        btnMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearTopPanel.setVisibility(View.VISIBLE);
                imageCenterPick.setVisibility(View.VISIBLE);
                //showPlaceInformation(mCurrentPosition);
            }
        });

        btnPick = view.findViewById(R.id.btn_pick);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.clear();
                LatLng center = mGoogleMap.getCameraPosition().target;
                mPickPosition = mGoogleMap.getCameraPosition().target;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(center);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                currentMarker = mGoogleMap.addMarker(markerOptions);
                linearTopPanel.setVisibility(View.INVISIBLE);
                imageCenterPick.setVisibility(View.INVISIBLE);
                showPlaceInformation(mPickPosition);
                progressDialog.setMessage("로딩 중...");
                progressDialog.show();
            }
        });

        btnClose = view.findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearTopPanel.setVisibility(View.INVISIBLE);
                imageCenterPick.setVisibility(View.INVISIBLE);
            }
        });

        progressDialog = new ProgressDialog(getContext(),R.style.ProgressDailogStyle);
        mRecyclerView = view.findViewById(R.id.listView);
        return view;
    }


    @Override
    public void onPlacesSuccess(final List<Place> places) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onPlacesSuccess Run");
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());

                    markerOptions.snippet(markerSnippet);
                    Marker item = mGoogleMap.addMarker(markerOptions);

            /*        String imgUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + placeDetail[0].getPhotoRef() +
                            "&sensor=false&maxheight=150&maxwidth=150&key=" + KEY;*/

                    MarkerHolder mHolder = new MarkerHolder(place.getPlaceId());
                    markerHolderMap.put(item.getId(), mHolder); //Add info to HashMap
                    previous_marker.add(item);

                    //중복 마커 제거
                    HashSet<Marker> hashSet = new HashSet<Marker>();
                    hashSet.addAll(previous_marker);
                    previous_marker.clear();
                    previous_marker.addAll(hashSet);
                }
                mRecyclerAdapter = new NearPlaceListRecyclerAdapter(getContext(), previous_marker);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())) ;
                mRecyclerView.setAdapter(mRecyclerAdapter);

                Log.d(TAG, "run: " + mRecyclerAdapter.getItemCount());
                progressDialog.dismiss();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        LatLng sydney = new LatLng(37.56, 126.97);

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                View v = getLayoutInflater().inflate(R.layout.map_infowindow, null);

                TextView txtLocation = (TextView) v.findViewById(R.id.txtTitle);
                TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
                TextView txtTel = (TextView) v.findViewById(R.id.txtTel);
                TextView btnSearch = (TextView) v.findViewById(R.id.btn_search);
                txtLocation.setText(arg0.getTitle());
                txtAddress.setText(arg0.getSnippet());

                MarkerHolder mHolder = markerHolderMap.get(arg0.getId()); //use the ID to get the info
                String tel = getPlaceTel(arg0.getTitle(), mHolder.placeId);
                mHolder.tel = tel;
                txtTel.setText(mHolder.tel);

                return v;

            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String placeTitle = getLossPlaceTitle(marker.getTitle());
                MarkerHolder mHolder = markerHolderMap.get(marker.getId());
                String tel = mHolder.tel;
                String[] menus = {"전화걸기", "습득물 보기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(placeTitle);
                builder.setItems(menus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
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
        });

    }

    public static String getPlaceTel(String title, String placeId) {
        String tel = mHashPlaceTel.get(title.replaceAll(" ", ""));
        if(tel==null) {
            PlaceDetail[] placeDetail = {null};
            try {
                placeDetail[0] = new AsyncTaskGetPlaceDetail().execute(placeId, KEY).get();
                Log.d(TAG, "getPlaceTel: ??" + placeDetail[0].getPhoneNumber());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            tel = placeDetail[0].getPhoneNumber();
        }
        return tel;
    }

    private String getLossPlaceTitle(String orgStr){
        String convertStr=null;
        if(!orgStr.contains(" ")) {
            return orgStr;
        }else{
            StringTokenizer st = new StringTokenizer(orgStr, " ");
            st.nextToken();
            convertStr = st.nextToken();
            return convertStr;
        }
    }

    public void showPlaceInformation(LatLng location) {

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(this)
                .key(KEY)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(5000) //500 미터 내에서 검색
                .language("ko","KR")
                .type(PlaceType.POLICE) //음식점
                .build()
                .execute();

    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG, "onConnected: ");
        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

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
        mapView.onResume();

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

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
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
                = new LatLng( location.getLatitude(), location.getLongitude());
        Log.d(TAG, "onLocationChanged : " + getCurrentAddress(mCurrentPosition));
        mCurrentLocatiion = location;
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: ");
        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

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

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
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
/*        if (currentMarker != null) currentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        currentMarker = mGoogleMap.addMarker(markerOptions);*/

        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng,15);
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


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
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

            if ( mGoogleApiClient.isConnected() == false) {

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


                if ( mGoogleApiClient.isConnected() == false) {

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
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    private void parsingPlaceTel(HashMap<String,String> hashMap){
        StringBuffer name = new StringBuffer("");
        StringBuffer tel = new StringBuffer("");
        try{
            XmlPullParser parser = getResources().getXml(R.xml.police_tel);
            int eventType= parser.getEventType();
            String tag;
            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_TAG:
                        tag= parser.getName();
                        if(tag.equals("item")) {
                            name.setLength(0);
                            tel.setLength(0);
                        }
                        else if(tag.equals("name")){
                            name.append(parser.nextText());
                        }
                        else if(tag.equals("tel")){
                            tel.append(parser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tag= parser.getName(); //테그 이름 얻어오기
                        if(tag.equals("item")) {
                            hashMap.put(name.toString(), tel.toString());
                        }
                        break;
                }

                eventType= parser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
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


