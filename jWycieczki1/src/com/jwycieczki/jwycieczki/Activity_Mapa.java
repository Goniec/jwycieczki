package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Activity_Mapa extends Activity implements LocationListener {
	
	private Context context;
	private int id_uzytkownika;
	private Resources resources;
	private GoogleMap mMap;
	private LocationManager location_manager;
	private LatLng polozenie_ja;
	private Marker marker_ja;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        context=this.getApplicationContext();
        id_uzytkownika=this.getIntent().getIntExtra("IdUzytkownika", -1);
        if(-1<id_uzytkownika){
        	resources=this.getResources();
        	mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        	location_manager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        	if((location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
        		Location location=location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        	    if(null!=location){
        	    	marker_ja=mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), 
            	    		location.getLongitude())).title(resources.getString(R.string.Jestes_tutaj)));
        	        onLocationChanged(location);
        	        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie_ja, 15));
        	      }
        	    Log.i("sprawdzam", "oke");
        	List<NameValuePair> p=new ArrayList<NameValuePair>();
        	p.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));
        	String r=BazaDanych.wykonajSkrypt("mapa.php", p);
        	if(0<Integer.parseInt(r)){
            	List<NameValuePair> x=new ArrayList<NameValuePair>();
            	x.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
            	x.add(new BasicNameValuePair("id_wycieczki", r));
            	x.add(new BasicNameValuePair("szerokosc", String.valueOf(polozenie_ja.latitude)));
            	x.add(new BasicNameValuePair("dlugosc", String.valueOf(polozenie_ja.longitude)));
            	String w=BazaDanych.wykonajSkrypt("polozenie.php", x);
            	try {
    				JSONArray json=new JSONArray(w);
    				for(int i=0; i<json.length(); ++i){
    					JSONArray jar=json.getJSONArray(i);
    					mMap.addMarker(new MarkerOptions()
    	                .position(new LatLng(Double.valueOf(jar.getString(1)), Double.valueOf(jar.getString(2))))
    	                .title(jar.getString(0)));
    				}
    			} catch(JSONException e){}
        	}
        	} else{
        		AlertDialog.Builder wlacz_gps=new AlertDialog.Builder(this);
        		wlacz_gps.setTitle(resources.getString(R.string.Wlacz_GPS));
        		wlacz_gps.setPositiveButton(resources.getString(R.string.Wlacz), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
		          	  	startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		          	  	dialog.dismiss();
		          	  	finish();
					}
				});
        		wlacz_gps.setNegativeButton(resources.getString(R.string.Wyjdz), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						finish();
					}
				});
        		wlacz_gps.show();
        	}
        }
    }
    
    
    public void onClickPowrot(View v)
    {    	
    	Intent intent = new Intent(context,Activity_Obszar_Roboczy.class );
		startActivity(intent);
    }

	public void onLocationChanged(Location location)
	{
	    polozenie_ja=new LatLng(location.getLatitude(), location.getLongitude());
	    marker_ja.setPosition(polozenie_ja);
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
	}

	@Override
	public void onProviderEnabled(String arg0)
	{
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
	}
	
	@Override
	public void onResume(){
		super.onResume();
		location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		location_manager.removeUpdates(this);
	}
	
}
