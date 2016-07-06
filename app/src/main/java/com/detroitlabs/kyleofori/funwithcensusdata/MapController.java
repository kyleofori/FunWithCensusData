package com.detroitlabs.kyleofori.funwithcensusdata;

import android.graphics.Color;
import com.detroitlabs.kyleofori.funwithcensusdata.api_interfaces.StatesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.MapClearer;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapController implements GoogleMap.OnMapClickListener, MapClearer {
  private static final LatLng USA_COORDINATES = new LatLng(39, -98);
  private static final int USA_ZOOM_LEVEL = 3;

  private GoogleMap map;
  private MainActivity mainActivity;
  private StatesModelCallback statesModelCallback;
  private List<List<LatLng>> polygonCollection = new ArrayList<>();
  private int indexOfMostRecentPolygon = 0;

  public MapController(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
  }

  public StatesModelCallback getStatesModelCallback() {
    return statesModelCallback;
  }

  @Override public void clearMap() {
    map.clear();
  }

  @Override public void onMapClick(LatLng latLng) {
    makeHttpCallForStateNames(latLng);
  }

  public void init() {
    setUpMapIfNeeded();
    map.setOnMapClickListener(this);
    statesModelCallback = new StatesModelCallback(this, mainActivity);
  }

  public void setUpMapIfNeeded() {
    if (map == null) {
      map = ((SupportMapFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
      if (map != null) {
        setUpMap();
      }
    }
  }

  private void setUpMap() {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(USA_COORDINATES, USA_ZOOM_LEVEL));
  }

  protected void makeHttpCallForStateNames(LatLng latLng) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.GOOGLE_MAPS_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    StatesApi statesApi = retrofit.create(StatesApi.class);

    String latLngString = latLng.latitude + "," + latLng.longitude;
    Call<StatesModel> statesModelCall = statesApi.getStatesModel(latLngString);
    statesModelCall.enqueue(statesModelCallback);
  }

  public void highlightState(OutlinesModel.Feature state) {
    OutlinesModel.Feature.Geometry geometry = state.getGeometry();
    Object coordinates = geometry.getCoordinates();

    if (geometry.getType().equals(Constants.POLYGON)) {
      List<List<List<Double>>> polygonOutline = (ArrayList<List<List<Double>>>) coordinates;
      addEachPolygonToMap(polygonOutline);
    } else if (geometry.getType().equals(Constants.MULTIPOLYGON)) {
      List<List<List<List<Double>>>> multiPolygonOutline =
          (ArrayList<List<List<List<Double>>>>) coordinates;
      for (List<List<List<Double>>> polygonOutline : multiPolygonOutline) {
        addEachPolygonToMap(polygonOutline);
      }
    }
  }

  private void addEachPolygonToMap(List<List<List<Double>>> polygonOutline) {
    List<LatLng> polygon = makePolygonOfCoordinatePairs(polygonOutline);
    polygonCollection.add(polygon);
    addMostRecentPolygonToMap();
  }

  private List<LatLng> makePolygonOfCoordinatePairs(List<List<List<Double>>> polygonOutline) {
    List<LatLng> polygon = new ArrayList<>();
    for (List<Double> coordinatePair : polygonOutline.get(0)) {
      double lng = coordinatePair.get(0);
      double lat = coordinatePair.get(1);
      polygon.add(new LatLng(lat, lng));
    }
    return polygon;
  }

  private void addMostRecentPolygonToMap() {
    map.addPolygon(new PolygonOptions().addAll(polygonCollection.get(indexOfMostRecentPolygon))
        .strokeColor(Color.BLACK)
        .strokeWidth(2)
        .fillColor(Color.WHITE));

    indexOfMostRecentPolygon++;
  }
}
