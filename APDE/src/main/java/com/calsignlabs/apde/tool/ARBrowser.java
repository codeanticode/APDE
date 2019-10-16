package com.calsignlabs.apde.tool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.calsignlabs.apde.APDE;
import com.calsignlabs.apde.KeyBinding;
import com.calsignlabs.apde.R;

// https://github.com/firebase/geofire-android
// https://stackoverflow.com/questions/40959215/geofire-android-to-store-and-retrive-locations-and-display-it-on-map
public class ARBrowser implements Tool {
  public static final String PACKAGE_NAME = "com.calsignlabs.apde.tool.ARBrowser";

  private APDE context;

  private AlertDialog dialog;

  private ConstraintLayout layout;

  @Override
  public void init(APDE context) {
    this.context = context;
  }

  @Override
  public String getMenuTitle() {
    return context.getResources().getString(R.string.tool_ar_browser);
  }

  @Override
  public void run() {

    checkLocationPermission();

//    if(dialog == null) {

      AlertDialog.Builder builder = new AlertDialog.Builder(context.getEditor());
      builder.setTitle(R.string.tool_ar_browser);

      if (context.isSketchbook()) {
        layout = (ConstraintLayout) View.inflate(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog), R.layout.ar_browser_upload, null);
      } else {
        layout = (ConstraintLayout) View.inflate(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog), R.layout.ar_browser_download, null);
      }

      builder.setView(layout);
      dialog = builder.create();
//    }

    dialog.show();
  }

  @Override
  public KeyBinding getKeyBinding() {
    return null;
  }

  @Override
  public boolean showInToolsMenu(APDE.SketchLocation sketchLocation) {
    return !sketchLocation.isExample();
  }

  @Override
  public boolean createSelectionActionModeMenuItem(MenuItem convert) {
    return false;
  }

  private void checkLocationPermission() {
// Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(context.getEditor(),
        Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {

      // Permission is not granted
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(context.getEditor(),
          Manifest.permission.ACCESS_FINE_LOCATION)) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(context.getEditor(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            42);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    } else {
      // Permission has already been granted
    }

  }
}
