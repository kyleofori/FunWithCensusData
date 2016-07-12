package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Context;
import android.os.AsyncTask;
import com.detroitlabs.kyleofori.funwithcensusdata.model.Feature;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.google.gson.Gson;
import flexjson.JSONDeserializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GetStateOutlinesTask extends AsyncTask<Context, Void, ArrayList<Feature>> {
  private Gson gson;
  private JSONDeserializer deserializer;
  private OutlinesModel model;
  private SplashActivity activity;

  @Override protected ArrayList<Feature> doInBackground(Context... params) {
    activity = (SplashActivity) params[0];
    if (gson == null && model == null) {
      gson = new Gson();
      model = gson.fromJson(loadJsonStringFromAsset(params[0]), OutlinesModel.class);
    }
    //if (model == null) {
    //  model = new JSONDeserializer<OutlinesModel>().deserialize(loadJsonStringFromAsset(params[0]));
    //}
    return model.getFeatures();
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

  @Override protected void onPostExecute(ArrayList<Feature> allFeatures) {
    super.onPostExecute(allFeatures);
    activity.onStateOutlinesReceived(allFeatures);
  }
}
