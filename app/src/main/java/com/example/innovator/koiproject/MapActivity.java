package com.example.innovator.koiproject;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback , ActivityCompat.OnRequestPermissionsResultCallback , PlacesListener {


    List<Marker> previous_marker = null;

    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private String nearName;
    private String nearLoc;
    private static final String GOOLGE_PLACE_KEY="AIzaSyCOulLgxDstqwdbV5iNxCbu4qFvJ-o_dbQ";
    private static final String TAG = "testaaa";//
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;//
    private static final int UPDATE_INTERVAL_MS = 1000;  // 업데이트기간 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;
    // 앱을 실행하기 위해 필요한 퍼미션 정의
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;//마지막위치
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar View
    private final long FINISH_INTERVAL_TIME = 2000;//뒤로가기 버튼 종료시간
    public long lastTouchedTime=0;

    SharedPreferences lastLocation;
    SharedPreferences.Editor lastLocationEditor;//마지막위치

    LatLng nearPos=null;
    int radius;
    double lastLat=0;
    double lastLng=0;
    TextView locationText;
    TextView neartext;
    TextView nearloctext;
    Bitmap policeMarker;
    Bitmap hospitalMarker;
    Bitmap fireMarker;
    Bitmap conMarker;
    Bitmap nearPoliMarker;
    boolean load;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lastLocation=getSharedPreferences("LASTLOCATION",Activity.MODE_PRIVATE);
        lastLocationEditor=lastLocation.edit();
        previous_marker = new ArrayList<Marker>();

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.hospmar);
        Bitmap b=bitmapdraw.getBitmap();
        hospitalMarker = Bitmap.createScaledBitmap(b, 90, 90, false);

        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.polimarkkno);
        b=bitmapdraw.getBitmap();
        policeMarker = Bitmap.createScaledBitmap(b, 90, 90, false);

        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.firf2);
        b=bitmapdraw.getBitmap();
        fireMarker = Bitmap.createScaledBitmap(b, 90, 90, false);

        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.conv2);
        b=bitmapdraw.getBitmap();
        conMarker = Bitmap.createScaledBitmap(b, 90, 90, false);

        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.policenear);
        b=bitmapdraw.getBitmap();
        nearPoliMarker = Bitmap.createScaledBitmap(b, 90, 90, false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map);
        ImageButton menuBtn = (ImageButton) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLastLocation();
                Intent settingsIntent = new Intent(MapActivity.this, MainActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        /*
        ImageButton mapBtn=(ImageButton) findViewById(R.id.map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(MapActivity.this,MapActivity.class);
                startActivity(settingsIntent);
            }
        });
        */
        ImageButton triggerBtn = (ImageButton) findViewById(R.id.trigger);
        triggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLastLocation();
                Intent settingsIntent = new Intent(MapActivity.this, TriggerActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton alarmBtn = (ImageButton) findViewById(R.id.alarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLastLocation();
                Intent settingsIntent = new Intent(MapActivity.this, AlarmActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        LinearLayout linearLayoutGo=(LinearLayout) findViewById(R.id.go);
        linearLayoutGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nearPos!=null) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(nearPos));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));//카메라 이동
                }
            }
        });

        locationText=(TextView) findViewById(R.id.location);
        neartext=(TextView) findViewById(R.id.safeZone);
        nearloctext=(TextView) findViewById(R.id.loctext);
        //위치 가져오기
        mLayout=findViewById(R.id.layout);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        /*
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
                */
        /*
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            Log.d("aaaaaaa","fadfsfads");
                            if (location != null) {
                                // Logic to handle location object
                                Log.d("aaaaaaa","succed");
                            }
                        }
                    });
        } catch (SecurityException e)
        {
            Log.d("aaaaaaa","error");
            e.printStackTrace();
        }//구글맵 소스
        */

        /*
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.googleMap);
        */
    }
    LocationCallback locationCallback=new LocationCallback(){
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);

        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Log.d(TAG,"ffffffffff");
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());
                locationText.setText("현재위치:"+getCurrentAddress(currentPosition));
                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());
                Log.d(TAG,"marker");
                //현재 위치에 마커 생성하고 이동
                showPlaceInformation(currentPosition,PlaceType.FIRE_STATION);
                showPoliceInformation(currentPosition);
                showPlaceInformation(currentPosition,PlaceType.DOCTOR);
                showPlaceInformation(currentPosition,PlaceType.CONVENIENCE_STORE);
                showPlaceInformation(currentPosition,PlaceType.HOSPITAL);

                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
                //마지막위치 저장
                if(location!=null)
                {
                    lastLat=location.getLatitude();
                    lastLng=location.getLongitude();
                }else
                {
                    Log.d(TAG,"set defaultlocation null");
                }

            }
        }

    };
    public void showPlaceInformation(LatLng location,String placeType)
    {
        //mGoogleMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key(GOOLGE_PLACE_KEY)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(1000) //500 미터 내에서 검색
                .type(placeType)
                .opennow(true)
                .language("ko", "KR")
                .build()
                .execute();
    }
    public void showPoliceInformation(LatLng location)
    {
        //mGoogleMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key(GOOLGE_PLACE_KEY)
                .latlng(location.latitude, location.longitude)//현재 위치
                .rankby("distance")
                .opennow(true)
                .type(PlaceType.POLICE)
                .language("ko", "KR")
                .build()
                .execute();
    }
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mGoogleMap.setMyLocationEnabled(true);

        }

    }
    @Override
    public void onBackPressed() {//뒤로가기 버튼 눌럿을 시

        if(System.currentTimeMillis() < lastTouchedTime + FINISH_INTERVAL_TIME)
        {
            //super.onBackPressed();//꺼짐
            saveLastLocation();
            finishAffinity();//종료코드
        }else {
            Toast.makeText(getApplicationContext(), " '뒤로'두번클릭시 종료됩니다", Toast.LENGTH_SHORT).show();//메세지 띄우기
        }
        lastTouchedTime=System.currentTimeMillis();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {// 맵 준비시

        //LatLng location = new LatLng(37.56, 126.97);//위치

        mGoogleMap = googleMap;
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            startLocationUpdates();
        }else{  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        //mFusedLocationClient.getLastLocation();
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d( TAG, "onMapClick :");
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mGoogleMap!=null)
                mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mGoogleMap.moveCamera(cameraUpdate);

    }
    public void setDefaultLocation() {


        //디폴트 위치, Seoul

        double lat=Double.parseDouble(lastLocation.getString("lastLat","37.56"));
        double lng=Double.parseDouble(lastLocation.getString("lastLng","126.97"));
        //Log.d(TAG,lastLocation.getString("lastLat","37.56"));
        Log.d(TAG,"getdefaultlocation");
        LatLng DEFAULT_LOCATION = new LatLng(lat, lng);
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

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }
        }
    }
    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 변경하시겠습니까?");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");

                        needRequest = true;

                        return;
                    }
                }
                break;
        }
    }
    public void saveLastLocation()
    {
        if(lastLat!=0&&lastLng!=0) {
            lastLocationEditor.putString("lastLat", Double.toString(lastLat));
            lastLocationEditor.putString("lastLng", Double.toString(lastLng));
            lastLocationEditor.commit();
            Log.d(TAG,"lastlocation saved");
        }
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
        //e.printStackTrace();
    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {

            @Override

            public void run() {

                boolean is=true;
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng

                            = new LatLng(place.getLatitude()

                            , place.getLongitude());
                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);

                    String a[]=place.getTypes();
                    if(a[0].equals("fire_station"))
                    {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(fireMarker));
                        Marker item = mGoogleMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }else if(a[0].equals("police"))
                    {
                        Marker item;
                        if (is)
                        {
                            nearName=place.getName();
                            nearLoc=place.getVicinity();
                            nearPos=new LatLng(place.getLatitude(),place.getLongitude());
                            neartext.setText("현재 "+nearName+"가 제일 가깝습니다!");
                            nearloctext.setText(nearLoc);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(nearPoliMarker));
                            item = mGoogleMap.addMarker(markerOptions);
                            is=false;
                        }else{
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(policeMarker));
                            item = mGoogleMap.addMarker(markerOptions);
                        }
                        previous_marker.add(item);
                    }else if(a[0].equals("doctor"))
                    {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(hospitalMarker));
                        Marker item = mGoogleMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }else  if(a[0].equals("convenience_store"))
                    {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(conMarker));
                        Marker item = mGoogleMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }else {

                    }
                }

                //중복 마커 제거

                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }

        });
    }

    @Override
    public void onPlacesFinished() {

    }

    //////////////////

    /*

        MarkerOptions markerOptions = new MarkerOptions();
        markerOpt
        ions.position(location);
        markerOptions.title("내 위치");//이름
        markerOptions.snippet("00시00구00동");//부가설명
        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));//카메라 이동
        */
}
