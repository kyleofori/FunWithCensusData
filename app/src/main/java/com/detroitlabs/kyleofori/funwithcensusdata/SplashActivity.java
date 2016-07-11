package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

public class SplashActivity extends AppCompatActivity
    implements ProviderInstaller.ProviderInstallListener, View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {

  public static final String VAR_NAME = "Variable name";
  public static final String VAR_DESC = "Variable description";

  private static final int ERROR_DIALOG_REQUEST_CODE = 1;
  private static final String NO_PROVIDER_TAG = "No provider available";

  private Button continueToTheMapButton;
  private TextView loadingText;
  private String variableName;
  private String variableDescription;

  private boolean retryProviderInstall;


  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    ProviderInstaller.installIfNeededAsync(this, this);
    loadingText = (TextView) findViewById(R.id.loading_text);
    continueToTheMapButton = (Button) findViewById(R.id.continue_button);
    continueToTheMapButton.setOnClickListener(this);
    RadioGroup dataRadioGroup = (RadioGroup) findViewById(R.id.data_radiogroup);
    dataRadioGroup.setOnCheckedChangeListener(this);
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    if (retryProviderInstall) {
      ProviderInstaller.installIfNeededAsync(this, this);
    }
    retryProviderInstall = false;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
      retryProviderInstall = true;
    }
  }

  @Override public void onProviderInstalled() {
    loadingText.setVisibility(View.GONE);
    continueToTheMapButton.setVisibility(View.VISIBLE);
  }

  @Override public void onProviderInstallFailed(int errorCode, Intent intent) {
    GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
    if (availability.isUserResolvableError(errorCode)) {
      availability.showErrorDialogFragment(this, errorCode, ERROR_DIALOG_REQUEST_CODE, new DialogInterface.OnCancelListener() {
        @Override public void onCancel(DialogInterface dialog) {
          onProviderInstallerNotAvailable();
        }
      });
    } else {
      onProviderInstallerNotAvailable();
    }
  }

  private void onProviderInstallerNotAvailable() {
    //We will have to consider all HTTP communication as vulnerable.
    Log.i(NO_PROVIDER_TAG, "All HTTP communication is vulnerable because the security provider could"
        + "not be installed.");
  }

  @Override public void onClick(View v) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(VAR_NAME, variableName);
    intent.putExtra(VAR_DESC, variableDescription);
    startActivity(intent);
  }

  @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
    continueToTheMapButton.setEnabled(true);
    continueToTheMapButton.setText(R.string.continue_to_the_map);
    if(checkedId == R.id.eighteen_radiobutton) {
      variableName = "NAME,B01001B_007E";
      variableDescription = getResources().getString(R.string.number_of_18_and_19_year_old_black_men);
    } else if (checkedId == R.id.median_age_radiobutton) {
      variableName = "NAME,B01002_001E";
      variableDescription = getResources().getString(R.string.median_age);
    }
  }
}
