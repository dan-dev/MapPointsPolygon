package com.example.danny.mappointspolygons;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private JSONArray array;
    private JSONArray arrayPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String json = "";

        try {
            InputStream inputStream = getAssets().open("Points.json");
            int size = inputStream.available();

            byte[] buffer = new byte[size];

            inputStream.read(buffer);

            inputStream.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(json);

            arrayPoints = jsonObject.getJSONArray("Ponto");
            array = jsonObject.getJSONArray("Poligono");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ISMAI = new LatLng(41.2677387, -8.6189434);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ISMAI, 15.0f));

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_invisible);

        final HashMap<Integer, Marker> hashMap = new HashMap<>();


        try {

            for(int i = 0; i < array.length(); i++){

                PolygonOptions poly = new PolygonOptions();

                LatLng center = null;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                JSONArray list = array.getJSONObject(i).getJSONArray("pontos");
                for (int j = 0; j < list.length(); j++){
                    JSONObject jsonObject = list.getJSONObject(j);
                    LatLng pol = new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"));
                    poly.add(pol);
                    builder.include(pol);
                }

                LatLngBounds bounds = builder.build();
                center = bounds.getCenter();

                poly.strokeColor(Color.BLUE).fillColor(Color.argb(60, 13, 114, 181)).clickable(true);

                Marker m = mMap.addMarker(new MarkerOptions()
                        .icon(icon)
                        .position(center).title(array.getJSONObject(i).getString("nome")));

                hashMap.put(i, m);

                Log.e("------------hhh:  ", "  " + i);

                mMap.addPolygon(poly).setTag(i);
            }


            for (int i = 0; i < arrayPoints.length(); i++){
                JSONObject jsonObject = arrayPoints.getJSONObject(i);
                LatLng pos = new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"));
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(pos).title(jsonObject.getString("nome")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Log.e("-------GG-------  ", "  "+polygon.getTag());
                hashMap.get(polygon.getTag()).showInfoWindow();

                //m.showInfoWindow();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

    }
}
