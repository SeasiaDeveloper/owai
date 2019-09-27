package com.oway.ui.trip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.oway.R;
import com.oway.adapters.MapPopularLocationsRecyclerAdapter;
import com.oway.base.BaseActivity;
import com.oway.callbacks.CancelButtonClick;
import com.oway.callbacks.DriverProfileDialog;
import com.oway.callbacks.PopularLocationsCallBack;
import com.oway.datasource.pref.PreferenceHandler;
import com.oway.model.PopularLocationsModal;
import com.oway.model.request.GetNearestDriverRequest;
import com.oway.model.request.GetRecommendedPlacesRequest;
import com.oway.model.response.GetNearestDriverResponse;
import com.oway.model.response.GetRecommendedPlacesResponse;
import com.oway.ui.home.MainActivity;
import com.oway.utillis.AppConstants;
import com.oway.utillis.CommonUtils;
import com.oway.utillis.Location;
import com.oway.utillis.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MotorTripActivity extends BaseActivity implements Location.OnLocationChangeListener, Location.OnLocationSatiListener, CancelButtonClick, DriverProfileDialog, TripActivityView {
    private static final String LOG_TAG = MotorTripActivity.class.getSimpleName();
    private boolean isClicked = true;
    private Double[] lat = {23.52437, 12.5444, 67.564656, 78.456456, 54.547646};
    private Double[] longitude = {45.76767, 78.65, 77.56656, 76.567, 56.756567};
    private String[] addresses = {"ZBXhb", "ndcjsv", "dsvbvffbfv", "dvbhdfvb", "sddsvc"};
    private ArrayList<PopularLocationsModal> modalArrayList = new ArrayList<PopularLocationsModal>();

    private Map map = null;
    private SupportMapFragment mapFragment = null;
    private BottomSheetBehavior sheetBehavior;
    private Location location;
    private CancelButtonClick cancelButtonClick;
    private DriverProfileDialog profileDialog;
    @BindView(R.id.popular_location)
    RecyclerView recyclerView;
    @BindView(R.id.etxPickUp)
    EditText etxPickUp;
    @BindView(R.id.etxDropDown)
    EditText etxDropDown;
    @BindView(R.id.back_motor)
    CircularImageView backButton;
    @BindView(R.id.civ_search)
    CircularImageView civxSearch;
    @BindView(R.id.ll_recycler_location)
    LinearLayout layoutPopularLocations;
    @BindView(R.id.ll_locations)
    LinearLayout layoutSourceDestination;
    @BindView(R.id.ll_please_wait)
    LinearLayout layoutPleaseWaitForRide;
    @BindView(R.id.ll_below_float_btn)
    RelativeLayout layoutBelowFloatButton;
    @BindView(R.id.ll_bottom_sheet_view)
    RelativeLayout layoutBottomSheet;
    @BindView(R.id.ll_driver_riding_to_you)
    RelativeLayout layoutDriverRidingToYou;

    @Inject
    TripActivityPresenter<TripActivityView> tripActivityPresenter;


    @OnClick(R.id.btn_cancel_ride)
    public void onCancelRideClick() {
        CommonUtils.showRideDialog(this);
    }

    private LatLng mlocation;

    @OnClick(R.id.btn_float)
    public void onFloatButtonClick() {
        if ((sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)) {
            layoutBelowFloatButton.setVisibility(View.GONE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            layoutBelowFloatButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.cencel_ride)
    public void onCancelRide() {
        layoutPleaseWaitForRide.setVisibility(View.GONE);
        layoutPopularLocations.setVisibility(View.VISIBLE);
        layoutSourceDestination.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        CommonUtils.showCancelDialog(this, cancelButtonClick);
    }

    @Override
    public void onCancelClick() {
        layoutPopularLocations.setVisibility(View.GONE);
        layoutSourceDestination.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutBottomSheet.setVisibility(View.VISIBLE);
        layoutDriverRidingToYou.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.btn_map_next)
    public void onClickNextOnMap() {
        CommonUtils.showCancelRideDialog(this, profileDialog);
    }

    @Override
    public void onCancelOrderClick() {

    }

    @Override
    public void onOrderClick() {
        layoutPopularLocations.setVisibility(View.GONE);
        layoutSourceDestination.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutPleaseWaitForRide.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.back_motor)
    public void onClick() {
        Intent intent = new Intent(MotorTripActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.civ_search)
    public void onSearchClick() {
        Intent intent = new Intent(this, SearchPlaces.class);
        intent.putExtra(AppConstants.LATITUDE, mlocation.latitude);
        intent.putExtra(AppConstants.LONGITUDE, mlocation.longitude);
        startActivity(intent);

    }


    @OnTouch(R.id.etxPickUp)
    public void onPicUpTouch() {
        etxPickUp.requestFocus();  //keep focus on the EditText(redTime)
        isClicked = true;
        Intent intent =new Intent(MotorTripActivity.this,SearchPlaces.class);
        intent.putExtra(AppConstants.LATITUDE, mlocation.latitude);
        intent.putExtra(AppConstants.LONGITUDE, mlocation.longitude);
        startActivityForResult(intent,AppConstants.REQUEST_CODE_PICK);
    }

    @OnTouch(R.id.etxDropDown)
    public void onDropDown() {
        etxDropDown.requestFocus();  //keep focus on the EditText(redTime)
        isClicked = false;
        Intent intent =new Intent(MotorTripActivity.this,SearchPlaces.class);
        intent.putExtra(AppConstants.LATITUDE, mlocation.latitude);
        intent.putExtra(AppConstants.LONGITUDE, mlocation.longitude);
        startActivityForResult(intent,AppConstants.REQUEST_CODE_DROP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        View view = findViewById(R.id.include_sheets);
        sheetBehavior = BottomSheetBehavior.from(view);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setPeekHeight(0);
        cancelButtonClick = this;
        profileDialog = this;


    }

    @Override
    protected void setUp() {
    }

    private SupportMapFragment getSupportMapFragment() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initialize() {
        setContentView(R.layout.activity_motor_trip);
        ButterKnife.bind(this);
        startLocationUpdates();
        getActivityComponent().inject(this);
        tripActivityPresenter.onAttach(MotorTripActivity.this);

    }





    private void getNearByDriver() {
        GetNearestDriverRequest nearRequest = new GetNearestDriverRequest();
        nearRequest.setLatitude(String.valueOf(mlocation.latitude));
        nearRequest.setLongitude(String.valueOf(mlocation.longitude));
        nearRequest.setOrder_feature("1");
        nearRequest.setEkl_customer(PreferenceHandler.readString(MotorTripActivity.this, AppConstants.USER_ID, ""));
        nearRequest.setAccess_token(PreferenceHandler.readString(MotorTripActivity.this, AppConstants.MBR_TOKEN, ""));
        tripActivityPresenter.getNearestDriver(nearRequest);
    }

    @Override
    public void onLocationChanged(LatLng location) {
        // setCurrentLocation(location);
        mlocation = location;

        mapFragment = getSupportMapFragment();
        CommonUtils.setCurrentLocation(mapFragment, location);
        getNearByDriver();
    }

    void startLocationUpdates() {
        location = new Location(this);
        location.setup();
        location.setOnLocationChangeListener(this, this);
    }

    @Override
    public void onLocationSatisfied() {

    }

    void setCurrentLocation(LatLng location) {
        try {
            Image image = new Image();
            image.setImageResource(R.drawable.currentlocation);
            MapMarker customMarker = new MapMarker(new GeoCoordinate(location.latitude, location.longitude, 0.0), image);
            map.addMapObject(customMarker);
        } catch (Exception e) {
            Log.e("HERE", e.getMessage());
        }
    }

    @Override
    public void onGetNearestDriverResponseSuccess(GetNearestDriverResponse status) {
        ToastUtils.shortToast("Driver found");
        CommonUtils.setDriversOnMap(status);
        GetRecommendedPlacesRequest mRequest = new GetRecommendedPlacesRequest();
        mRequest.setLatitude(String.valueOf(mlocation.latitude));
        mRequest.setLongitude(String.valueOf(mlocation.longitude));
        mRequest.setEkl_customer(PreferenceHandler.readString(MotorTripActivity.this, AppConstants.USER_ID, ""));
        mRequest.setAccess_token(PreferenceHandler.readString(MotorTripActivity.this, AppConstants.MBR_TOKEN, ""));
        tripActivityPresenter.getRecommendedPlaces(mRequest);
    }

    @Override
    public void onGetNearestDriverResponseFailure(String response) {
        ToastUtils.shortToast(response);
    }

    @Override
    public void onGetRecommendedPlacesSuccess(GetRecommendedPlacesResponse response) {
        for (int i = 0; i < response.getResults().size(); i++) {
            PopularLocationsModal locationsModal = new PopularLocationsModal();
            locationsModal.setLatitude(response.getResults().get(i).getGeometry().getLocation().getLat());
            locationsModal.setLongitude(response.getResults().get(i).getGeometry().getLocation().getLng());
            locationsModal.setAddress(response.getResults().get(i).getName());
            modalArrayList.add(locationsModal);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MapPopularLocationsRecyclerAdapter adapter = new MapPopularLocationsRecyclerAdapter(modalArrayList, this, new PopularLocationsCallBack() {
            @Override
            public void onItemClick(View v, String address) {
                if (isClicked) {
                    etxPickUp.setText(address);
                } else {
                    etxDropDown.setText(address);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onGetRecommendedPlacesFailure(String response) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
