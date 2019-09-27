package com.oway.ui.trip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oway.R;
import com.oway.base.BaseActivity;
import com.oway.datasource.pref.PreferenceHandler;
import com.oway.model.request.GetSearchPlacesRequest;
import com.oway.model.response.GetRecommendedPlacesResponse;
import com.oway.utillis.AppConstants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchPlaces extends BaseActivity implements SearchPlacesView {

    @BindView(R.id.et_place_name)
    AutoCompleteTextView etxPlaces;
    @BindView(R.id.ib_search_back)
    ImageButton ibxSearchBack;
    @BindView(R.id.rv_places_list)
    RecyclerView rvxPlaceList;
    double latitude;
    double longitude;
    Intent intent;

    @OnClick(R.id.ib_search_back)
    public void onSearchBackClick(){
        Intent intent=new Intent(SearchPlaces.this,MotorTripActivity.class);
        startActivity(intent);
        finish();
    }

    private ArrayList<GetRecommendedPlacesResponse.ResultsBean> placesResponses = new ArrayList<GetRecommendedPlacesResponse.ResultsBean>();
    @Inject
    SearchPlacesPresenter<SearchPlacesView> searchPlacesPresenter;
    PlacesAdapter adapter = new PlacesAdapter(placesResponses, SearchPlaces.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        searchPlacesPresenter.onAttach(SearchPlaces.this);
        intent = getIntent();
        latitude = intent.getDoubleExtra(AppConstants.LATITUDE, 0);
        longitude = intent.getDoubleExtra(AppConstants.LONGITUDE, 0);
        etxPlaces.setThreshold(3);

      /*  Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.ADDRESS, "resultValue");
        resultIntent.putExtra(AppConstants.SELECT_LATITUDE, "878");
        resultIntent.putExtra(AppConstants.SELECT_LONGITUDE, "76");
        setResult(RESULT_OK, resultIntent);
        finish();*/

        etxPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    placesResponses.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    GetSearchPlacesRequest request = new GetSearchPlacesRequest();
                    request.setLatitude(String.valueOf(latitude));
                    request.setLongitude(String.valueOf(longitude));
                    request.setQuery(Objects.requireNonNull(etxPlaces.getText()).toString());
                    request.setAccess_token(PreferenceHandler.readString(SearchPlaces.this, AppConstants.MBR_TOKEN, ""));
                    searchPlacesPresenter.getSearchPlaces(request);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void placesList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvxPlaceList.setLayoutManager(layoutManager);
        rvxPlaceList.setAdapter(adapter);
    }

    @Override
    protected void setUp() {

    }


    @Override
    public void onGetSearchPlacesResponseSuccess(GetRecommendedPlacesResponse status) {
        placesResponses.clear();
        for (int i = 0; i < status.getResults().size(); i++) {
            placesResponses.addAll(status.getResults());
        }
        placesList();
    }

    @Override
    public void onGetsearchPlacesResponseFailure(String response) {

    }

}
