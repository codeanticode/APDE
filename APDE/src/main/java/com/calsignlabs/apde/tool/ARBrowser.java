package com.calsignlabs.apde.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.calsignlabs.apde.APDE;
import com.calsignlabs.apde.KeyBinding;
import com.calsignlabs.apde.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// https://github.com/firebase/geofire-java
// https://stackoverflow.com/questions/40959215/geofire-android-to-store-and-retrive-locations-and-display-it-on-map
public class ARBrowser implements Tool {
  public static final String PACKAGE_NAME = "com.calsignlabs.apde.tool.ARBrowser";

  private APDE context;

  private AlertDialog uploadDialog;
  private AlertDialog downloadDialog;

  private FirebaseAuth mAuth;
  private GeoFire geoFire;
  private FirebaseUser currentUser;

  private FusedLocationProviderClient locationClient;
  private LocationCallback locationCallback;

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

    initGeoFire();

    AlertDialog dialog;
    if (context.isSketchbook()) {
      if (uploadDialog == null) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getEditor());
        builder.setTitle(R.string.tool_ar_browser);
        ConstraintLayout layout = (ConstraintLayout) View.inflate(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog), R.layout.ar_browser_upload,null);
        builder.setView(layout);

        Button uploadButton = layout.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
          @SuppressWarnings("deprecation")
          @SuppressLint("NewApi")
          @Override
          public void onClick(View view) {
            requestLastLocation();
          }
        });

        uploadDialog = builder.create();
      }
      dialog = uploadDialog;
    } else {
      if (downloadDialog == null) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getEditor());
        builder.setTitle(R.string.tool_ar_browser);
        ConstraintLayout layout = (ConstraintLayout) View.inflate(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog), R.layout.ar_browser_download,null);
        builder.setView(layout);
        downloadDialog = builder.create();


        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(42.3824987, -71.1092777), 0.1);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
          @Override
          public void onKeyEntered(String key, GeoLocation location) {
            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
          }

          @Override
          public void onKeyExited(String key) {
            System.out.println(String.format("Key %s is no longer in the search area", key));
          }

          @Override
          public void onKeyMoved(String key, GeoLocation location) {
            System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
          }

          @Override
          public void onGeoQueryReady() {
            System.out.println("All initial data has been loaded and events have been fired!");
          }

          @Override
          public void onGeoQueryError(DatabaseError error) {
            System.err.println("There was an error with this query: " + error);
          }
        });


      }
      dialog = downloadDialog;
    }

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

  protected final int LOCATION_REQUEST_CODE = 1001;

  @Override
  public void handlePermissionsResult(int requestCode, String permissions[], int[] grantResults) {

    switch (requestCode) {
      case LOCATION_REQUEST_CODE:
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          // TODO Explain that we NEED this permission!


//          AlertDialog alertDialog = new AlertDialog.Builder(context.getEditor()).create();
//          alertDialog.setTitle("Location");
//          alertDialog.setMessage("APDE needs location to find nearby AR sketches and to upload your AR sketch to your current location");
//          alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//              new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                  dialog.dismiss();
//                }
//              });
//          alertDialog.show();

//          Toast.makeText(context.getEditor(), R.string.tool_color_selector_copy_hex_to_clipboard_success, Toast.LENGTH_SHORT).show();

        }
        break;
    }

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
            LOCATION_REQUEST_CODE);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    } else {
      // Permission has already been granted

    }

    initLocation();
  }


  private void initLocation() {
    locationClient = LocationServices.getFusedLocationProviderClient(context.getEditor());

    /*
    LocationRequest request = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(1000 * 60)
        .setMaxWaitTime(1000 * 60 * 10);
    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
          return;
        }
        for (Location location : locationResult.getLocations()) {
          // Do something with location
        }
      };
    };
    locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    */
  }

  private void initGeoFire() {
    if (geoFire == null) {

      // Anonymous login
      // https://firebase.google.com/docs/auth/android/anonymous-auth
      // Initialize Firebase Auth
      mAuth = FirebaseAuth.getInstance();


      mAuth.signInAnonymously().addOnCompleteListener(
          new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                // Check if user is signed in (non-null) and update UI accordingly.
                currentUser = mAuth.getCurrentUser();
                System.out.println("signInAnonymously:success " + currentUser.getUid());
              } else {
                System.err.println("signInAnonymously:failure" + task.getException());
              }
            }
      });

      DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
      geoFire = new GeoFire(ref);
      System.out.println(geoFire);
    }
  }

  private void requestLastLocation() {
    locationClient.getLastLocation()
      .addOnSuccessListener(context.getEditor(), new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
          // Got last known location. In some rare situations this can be null.
          if (location != null) {
            // Logic to handle location object
            uploadSketch(location);
          }
        }
    });
  }

  private void uploadSketch(Location location) {
    String name = context.getSketchName();



    System.out.println("Uploading sketch " + name + " to location " + location.getLatitude() + ", " + location.getLongitude());
    geoFire.setLocation(name, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
      @Override
      public void onComplete(String key, DatabaseError error) {
        if (error != null) {
          System.err.println("There was an error saving the location to GeoFire: " + error);
        } else {
          System.out.println("Location saved on server successfully! " + key);
        }
      }
    });
  }
}
