package dk.easj.anbo.locationmanagerexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView latitudeView, longitudeView, altitudeView;
    private static final int minimumTime = 0;
    private static final int minimumDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeView = findViewById(R.id.mainLatitudeTextView);
        longitudeView = findViewById(R.id.mainLongitudeTextView);
        altitudeView = findViewById(R.id.mainAltitudeTextView);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LinearLayout layout = findViewById(R.id.mainLayout);
            final Snackbar snackbar = Snackbar.make(layout, "Missing location permission", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        showLocation(lastKnownLocation);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                showLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minimumTime, minimumDistance, locationListener);
        } catch (SecurityException ex) {
            Log.e("SHIT", ex.toString());
        }
    }

    private void showLocation(Location location) {
        if (location == null) {
            latitudeView.setText("No location found!");
            return;
        }
        double latitude = location.getLatitude();
        latitudeView.setText("Latitude: " + latitude);
        longitudeView.setText("Longitude: " + location.getLongitude());
        altitudeView.setText("Altitude: " + location.getAltitude());
        final String message = String.format("Lat %s Lon %s Alt %s", latitude, location.getLongitude(), location.getAltitude());
        Log.d("MINE", message);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (locationManager != null && locationListener != null)
                locationManager.removeUpdates(locationListener);
        } catch (SecurityException ex) {
            Log.e("SHIT", ex.toString());
        }
    }

    //http://stackoverflow.com/questions/10459109/requestlocationupdates-recalls-onpause-and-onresume-infinitely
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (locationManager != null && locationListener!=null)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minimumTime, minimumDistance, locationListener);
        } catch (SecurityException ex) {
            Log.e("SHIT", ex.toString());
        }
    }
}
