package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Context;
import android.os.AsyncTask;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GetStateOutlinesTask extends AsyncTask<Context, Void, Void> {
  private Gson gson;
  private OutlinesModel model;

  @Override protected Void doInBackground(Context... params) {
    if (gson == null && model == null) {
      gson = new Gson();
      model = gson.fromJson(loadJsonStringFromAsset(params[0]), OutlinesModel.class);
    }
    ArrayList<OutlinesModel.Feature> allFeatures =  model.getFeatures();
    ((MainActivity) params[0]).onStateOutlinesReceived(allFeatures);
    return null;
  }

  private String loadJsonStringFromAsset(Context context) {
    String json;
    try {
      InputStream inputStream = context.getAssets().open("ERIC-CLST-Outline.json");
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return json;
  }

  @Override protected void onPostExecute(Void v) {
    super.onPostExecute(v);
  }
}
