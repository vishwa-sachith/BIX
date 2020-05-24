package com.example.bix;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class GetCurrentLocation extends AppCompatActivity {

    GoogleMap map;

    Marker marker;
    Marker marker2;
    boolean done = true;
    TextView locationtxt;

    List<Address> addresses;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_current_location);

        SupportMapFragment fragment = new SupportMapFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map1, fragment, "map");
        transaction.commit();
        locationtxt = findViewById(R.id.locationtxtview);
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

           /*     LatLng location1 = new LatLng(6.896963,79.860336);
                CameraPosition.Builder camBuilder = new CameraPosition.Builder();
                camBuilder.target(location1);
                camBuilder.zoom(18);
                CameraPosition cameraPosition = camBuilder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);

            */

//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(location1);
//                markerOptions.title("GG MDFKR");
//
//                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ggggg);
//                markerOptions.icon(icon);
//
//                markerOptions.draggable(false);
//                markerOptions.flat(false);
//                marker = map.addMarker(markerOptions);
//                marker.showInfoWindow();

                MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle);
                map.setMapStyle(mapStyleOptions);

                UiSettings uiSettings = map.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setCompassEnabled(true);
                uiSettings.setMapToolbarEnabled(true);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(500);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        double longcurrunt =locationResult.getLastLocation().getLatitude();
                        double alticurrunt =locationResult.getLastLocation().getLongitude();

                        LatLng location2 = new LatLng(longcurrunt, alticurrunt);



                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(location2);
                        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.ggggg);
                        markerOptions2.icon(icon2);
                        markerOptions2.title("You");

                        if (done) {
                            done = false;


                            EditProfileActivity.currentLocation = longcurrunt + ", " + alticurrunt;
                            locationtxt.setText(EditProfileActivity.currentLocation);
                            Toast.makeText(GetCurrentLocation.this, EditProfileActivity.currentLocation, Toast.LENGTH_SHORT).show();

                            System.out.println(EditProfileActivity.currentLocation);

                            CameraPosition.Builder camBuilder = new CameraPosition.Builder();
                            camBuilder.target(location2);
                            camBuilder.zoom(18);
                            CameraPosition cameraPosition = camBuilder.build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            map.animateCamera(cameraUpdate);
                        }

                        if (marker != null) {
                            marker.remove();
                        }

                        marker = map.addMarker(markerOptions2);
                        marker.showInfoWindow();


                        System.out.println("location changed");
                    }
                };


                FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    providerClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }

                map.setMyLocationEnabled(true);


                GoogleMap.OnMapLongClickListener listener = new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng location) {

                        if (marker2 != null) {
                            marker2.remove();
                        }

                        locationtxt = findViewById(R.id.locationtxtview);
                        EditProfileActivity.currentLocation = location.longitude + ", " + location.latitude;
                        locationtxt.setText(EditProfileActivity.currentLocation);

                        MarkerOptions markerOptions3 = new MarkerOptions();
                        markerOptions3.position(location);
                        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);
                        markerOptions3.icon(icon2);
                        markerOptions3.title("You");

                        markerOptions3.draggable(false);
                        markerOptions3.flat(false);

                        marker2 = map.addMarker(markerOptions3);
                        marker2.showInfoWindow();

                    /*    PolylineOptions polylineOptions1 = new PolylineOptions();
                        polylineOptions1.width(10);
                        polylineOptions1.color(R.color.color1);

                        polylineOptions1.add(marker.getPosition());
                        polylineOptions1.add(location);
                        map.addPolyline(polylineOptions1);

                     */

                        final String items[] = new String[5];

                        try {
                            Geocoder geocoder = new Geocoder(GetCurrentLocation.this);

                            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 5);

                            int i =0;
                            for(Address address : addresses) {
                                items[i] = address.getAddressLine(0);
                                //items[i] = address.getThoroughfare();
                                System.out.println(address.getAddressLine(0));
                                i++;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(GetCurrentLocation.this);
                        aleBuilder.setTitle("Nearby Locations");
                        aleBuilder.setIcon(R.drawable.ggggg);

                        DialogInterface.OnClickListener listener1 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Toast.makeText(getApplicationContext(),items[i],Toast.LENGTH_SHORT).show();


                                EditProfileActivity.currentLocation = addresses.get(i).getLongitude() + ", " + addresses.get(i).getLatitude();
                                locationtxt.setText(EditProfileActivity.currentLocation);

                            }
                        };


                        aleBuilder.setSingleChoiceItems(items, 0, listener1);
                        aleBuilder.show();



                        final String start = marker.getPosition().latitude+","+marker.getPosition().longitude;
                        final String end = marker2.getPosition().latitude+","+marker2.getPosition().longitude;

                       /* Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {


                                HttpsURLConnection con = null;
                                try {
                                    //URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="+start+"&destination="+end+"&mode=driving&key=AIzaSyD1SWFFDe_wPh_-A2Gs5kVLK7KV814_2tA");
                                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="+start+"&destination="+end+"&mode=driving&key=AIzaSyCZiRmkC6EwQXgSqTE0VBi7-8jewvXd7Vg");
                                    //URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=6.896946,79.860333&destination=6.889634,79.859135&mode=driving&key=AIzaSyDmWoljxr8CB_2nHYKIgmC-1Ks-Cu-NHn4");
                                    con = (HttpsURLConnection) url.openConnection();
                                    con.setRequestMethod("GET");
                                    //con.setDoOutput(true);
                                    //con.setChunkedStreamingMode(0);


                                    if (con.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                                        System.out.println("response is here");

                                        InputStream inputStream = con.getInputStream();

                                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                                        BufferedReader br = new BufferedReader(inputStreamReader);
                                        String resptext = br.readLine();

                                        System.out.println(resptext);

                                        String split1 = resptext.split("overview_polyline")[1];
                                        String split2 = split1.split("points\" : \"")[1];
                                        String split3 = split2.split("\"")[0];


                                        final List<LatLng> polylinelist = PolyUtil.decode(split3);


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                PolylineOptions polylineOptions2 = new PolylineOptions();
                                                polylineOptions2.color(getColor(R.color.color1));
                                                polylineOptions2.width(10);
                                                polylineOptions2.addAll(polylinelist);
                                                map.addPolyline(polylineOptions2);

                                            }
                                        });




                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (con != null) {
                                        con.disconnect();
                                    }
                                }

                            }
                        });
                        t.start();


                        */

                    }
                };

                map.setOnMapLongClickListener(listener);

            }
        });

    }

    public void onclickbtn (View view) {
        startActivity(new Intent(GetCurrentLocation.this, EditProfileActivity.class));
    }

}
