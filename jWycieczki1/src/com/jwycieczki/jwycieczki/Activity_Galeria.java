package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Galeria extends Activity {
	
	private Context context;
	private Intent intent;
	private int id_uzytkownika;
	private List<NameValuePair> php_parametry;
	private String php_wynik;
	
	private List<String> albumy_nazwy;
	private List<String> zdjecia_album;
	private List<String> albumy_id;
	
	private GridView gvAlbumy;
	private GridView gvZdjecia;
	private List<ImageItem> tablicaZdjec;
	private List<ImageItem> albumy;
	private GaleriaAdapter adapter;
	
	ImageView dz;
	
	// zmienne tymczasowe
	ImageView imageView;
    TextView tx;
    Bitmap zdjecie; // do tej zmiennej zostanie pobrana bitmapa
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);
        context=this.getApplicationContext();
        id_uzytkownika=this.getIntent().getIntExtra("IdUzytkownika", -1);
        tablicaZdjec=new ArrayList<ImageItem>();
        albumy=new ArrayList();
        
        //albumy_nazwy=new ArrayList<String>();
        php_parametry=new ArrayList<NameValuePair>();
        php_parametry.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
        php_wynik=BazaDanych.wykonajSkrypt("albumy_nazwy.php", php_parametry);
        albumy_nazwy=new ArrayList<String>();
        albumy_id = new ArrayList<String>();
        try {
			JSONArray json=new JSONArray(php_wynik);
			for(int i=0; i<json.length(); ++i){
				JSONArray jar=json.getJSONArray(i);
				albumy_nazwy.add(jar.getString(1));
				albumy_id.add(jar.getString(0));
			}
		} catch (JSONException e){}
        
        php_parametry.clear();
        //Toast.makeText(context, albumy_nazwy.get(0), Toast.LENGTH_LONG).show();
        
        //ladowanie albumow
        TextView tmp = (TextView) findViewById(R.id.tvdataOelv);
		
        for(int i = 0; i < albumy_nazwy.size(); ++i)
        {
        	php_parametry.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
            php_parametry.add(new BasicNameValuePair("id_wycieczki", albumy_nazwy.get(i)));
            php_wynik=BazaDanych.wykonajSkrypt("album_zdjecia.php", php_parametry);
        	//tmp.setText(tmp.getText() + " " + albumy_nazwy.get(0));
            zdjecia_album=new ArrayList<String>();
            try {
    			JSONArray json=new JSONArray(php_wynik);
    			for(int j=0; j<json.length(); ++j){
    				JSONArray jar=json.getJSONArray(j);
    				zdjecia_album.add(jar.getString(0));
    			}
    		} catch (JSONException e){}
            albumy.add(new ImageItem(ObrazPobierz.pobierz(zdjecia_album.get(0))));    
       }
		
		gvAlbumy = (GridView) findViewById(R.id.gvAlbumy);
		adapter = new GaleriaAdapter(this,R.layout.row_grid,albumy);
		gvAlbumy.setAdapter(adapter);
		Log.i("tag", "dziala");
		
	//	gvAlbumy.setOn
		
		gvAlbumy.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				Log.i("tag", "dziala");
				TextView tmp = (TextView) findViewById(R.id.tvdataOelv);
				tmp.setText(albumy_id.get(position));
				ZaladujGalerie(albumy_nazwy.get(position));
			}
			
		});
		
		
	
        
    }
    
    public void onClickPowrot(View v)
    {
    	intent=new Intent(context, Activity_Obszar_Roboczy.class);
		startActivity(intent);
    }
    
    public void ZaladujGalerie(String id)
    {
    	// tutaj z bazy danych pobrac zdjecia
    	tablicaZdjec.clear();
    	 php_parametry.clear();
        // php_parametry.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(3)));
         php_parametry.add(new BasicNameValuePair("id_wycieczki", id));
         php_wynik=BazaDanych.wykonajSkrypt("album_zdjecia.php", php_parametry);
    	
         zdjecia_album=new ArrayList<String>();
         try {
 			JSONArray json=new JSONArray(php_wynik);
 			for(int i=0; i<json.length(); ++i){
 				JSONArray jar=json.getJSONArray(i);
 				zdjecia_album.add(jar.getString(0));
 			}
 		} catch (JSONException e){}
         
         for(int i = 0; i < zdjecia_album.size(); ++i)
         {
         	tablicaZdjec.add(new ImageItem(ObrazPobierz.pobierz(zdjecia_album.get(i))));
         }

    	gvZdjecia = (GridView) findViewById(R.id.gvZdjecia);
		adapter = new GaleriaAdapter(this,R.layout.row_grid,tablicaZdjec);
		gvZdjecia.setAdapter(adapter);
		
		gvZdjecia.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i("tag", "podglad zdjecia dziala");
				dz = (ImageView) findViewById(R.id.ivDuzeZdjecie);
				dz.setImageBitmap(ObrazPobierz.pobierz(zdjecia_album.get(arg2)));
				dz.setVisibility(View.VISIBLE);
				dz.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dz.setVisibility(View.GONE);
					}
				});
			}
			
		});
    	
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String ktoryElement = "";
        switch (item.getItemId()){
        	case R.id.DodajAlbum:
        		ktoryElement = "pierwszy";
        		break;
        	case R.id.DodajZdjecie:
        		ktoryElement = "drugi";
        		break;
        	default:
        		ktoryElement = "¿aden";
        }
        Toast.makeText(context, "Element: " + ktoryElement, Toast.LENGTH_LONG).show();
        return true;
    }
    
}
