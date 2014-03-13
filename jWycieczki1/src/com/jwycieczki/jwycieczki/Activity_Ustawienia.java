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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Activity_Ustawienia extends Activity {
	
	private int id_uzytkownika; // id_uzytkownika pobierane przy logowaniu
	private Context context;
	
	private ListView l;
	private List<TrzyWartosci> lista_wycieczek;
	List<String> pl;
	ArrayAdapter<String> adapter;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ustawienia);
		id_uzytkownika=this.getIntent().getIntExtra("IdUzytkownika", -1);
        if(-1<id_uzytkownika){
        	context=this.getApplicationContext();
        	
        	pl=new ArrayList<String>();
        	List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
        	php_parametry.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));
        	lista_wycieczek=new ArrayList<TrzyWartosci>();
        	String dane=BazaDanych.wykonajSkrypt("nowe_wycieczki.php", php_parametry);
        	try {
        		JSONArray json=new JSONArray(dane);
    			for(int i=0; i<json.length(); ++i){
    				if(json.getJSONArray(i).getString(2).toLowerCase().contains("czlonek")){
    				pl.add(json.getJSONArray(i).getString(0)+" (zapisany)");
    				lista_wycieczek.add(new TrzyWartosci(json.getJSONArray(i).getString(0)+" (zapisany)", json.getJSONArray(i).getString(1), json.getJSONArray(i).getString(2)));
    				} else{
    					pl.add(json.getJSONArray(i).getString(0));
    				lista_wycieczek.add(new TrzyWartosci(json.getJSONArray(i).getString(0), json.getJSONArray(i).getString(1), json.getJSONArray(i).getString(2)));
    				}
    			}
    		} catch (JSONException e) {}
        	l=(ListView)findViewById(R.id.lvListaWycieczekU);
        	adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pl);
        	l.setAdapter(adapter);
            l.setOnItemClickListener(mMessageClickedHandler);
        }
        
        
	}
	
	public void onClickPowrot(View v){    	
    	Intent intent = new Intent(context,Activity_Obszar_Roboczy.class );
    	intent.putExtra("IdUzytkownika", id_uzytkownika);
		startActivity(intent);	
    }
	
	public void onClickSzukaj(View v){
		String nazwaWycieczki=((EditText) findViewById(R.id.etSzukajWycieczkiU)).getText().toString();
		pl.clear();
		for(int i=0; i<lista_wycieczek.size(); ++i){
			if(lista_wycieczek.get(i).pierwsza.toLowerCase().contains(nazwaWycieczki.toLowerCase())){
				pl.add(lista_wycieczek.get(i).pierwsza);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private OnItemClickListener mMessageClickedHandler=new OnItemClickListener(){
		int pozycja=-1;
        public void onItemClick(AdapterView parent, View v, int position, long id){
        	for(int i=0; i<lista_wycieczek.size(); ++i){
        		if(lista_wycieczek.get(i).pierwsza.equals(pl.get(position))){
        			pozycja=i;
        			break;
        		}
        	}
        	if(lista_wycieczek.get(pozycja).trzecia.toLowerCase().equals("null")){
        		AlertDialog.Builder dialog_zapisz_sie=new AlertDialog.Builder(Activity_Ustawienia.this);
        		dialog_zapisz_sie.setTitle("zapisz sie na wycieczke "+lista_wycieczek.get(position).pierwsza);
        		dialog_zapisz_sie.setPositiveButton("zapisz sie", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
						php_parametry.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
						php_parametry.add(new BasicNameValuePair("id_wycieczki", lista_wycieczek.get(pozycja).druga));
						String dane=BazaDanych.wykonajSkrypt("wycieczka_zapisz_sie.php", php_parametry);
						if(dane.equals("1")){
						Toast.makeText(context, "zapisany", Toast.LENGTH_LONG).show();
						}
		          	  	dialog.dismiss();
					}
				});
        		dialog_zapisz_sie.setNegativeButton(getResources().getString(R.string.Wyjdz), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
        		dialog_zapisz_sie.show();
        	}
        }
	};
	
	private class TrzyWartosci {
		
		public String pierwsza, druga, trzecia;
		
		public TrzyWartosci()
		{	
			pierwsza=new String();
			druga=new String();
			trzecia=new String();
		}
		
		public TrzyWartosci(final String jeden, final String dwa, final String trzy)
		{
			pierwsza=jeden;
			druga=dwa;
			trzecia=trzy;
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String ktoryElement = ""; 
		switch (item.getItemId()){
			case R.id.Zakoncz:
				ktoryElement = "konczy";
				break;
			default:
				ktoryElement = "¿aden";
		}
		Toast.makeText(context, "Element: " + ktoryElement, Toast.LENGTH_LONG).show(); 
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.ustawienia, menu);
		return true;
	}
	
}
