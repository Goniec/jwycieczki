package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Profil extends Activity {
	
	private Context context;
	private int id_uzytkownika;
	
	ImageButton oRoboczy;
	
	Uzytkownik wlasciciel;
	ArrayList<String> expProfil=null;
	ExpandableListView elvWycieczki;
	HashMap<String, List<String>> listItem = new HashMap<String, List<String>>();
	
	TextView login, imie, nazwisko;
	Wycieczka mojaWycieczka=null;
	private boolean blok; //blokowanie usuwania uzytkownikow z wycieczki
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profil);
		id_uzytkownika=this.getIntent().getIntExtra("IdUzytkownika", -1);
		
		if(-1<id_uzytkownika){
			pobierzUzytkownika();
	    	
	    	pobierzWycieczke();	
		}
        
		((ImageButton)findViewById(R.id.ibBlokP)).setVisibility(View.GONE);
        
		blok=true;
        Button usunWycieczke=(Button)findViewById(R.id.usunWycieczkeP);
		if(blok){
			usunWycieczke.setVisibility(View.GONE);
		}else{
			usunWycieczke.setVisibility(View.VISIBLE);
		}
        
    	if(mojaWycieczka!=null){
    		((ImageButton)findViewById(R.id.ibBlokP)).setVisibility(View.VISIBLE);
    		((TextView)findViewById(R.id.tvNazwaWycieczkiP)).setText(mojaWycieczka.nazwa);
    		((Button)findViewById(R.id.bDodajWycieczkeP)).setVisibility(View.GONE);
    		mojaWycieczka.dodajUczestnikow();
    		wyswietlMojaWycieczke();
    	}
        
        wyswietlDane();
        
        //odswierzanie
        
        final Handler chwytakP = new Handler();
    	final Runnable odswierzanieP = new Runnable (){
    		public void run(){
				if(mojaWycieczka != null && mojaWycieczka.odswierzUczestnikow()){
					wyswietlMojaWycieczke();
				}
    		}
    	};
    	Timer timer= new Timer();
    	timer.scheduleAtFixedRate(new TimerTask(){
    		public void run(){
    			chwytakP.post(odswierzanieP);
    		}
    	}	, 1000, 40000);
	}
	
	private void pobierzUzytkownika(){
		context=this.getApplicationContext();
		List<NameValuePair> parametry=new ArrayList<NameValuePair>();
    	parametry.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));
    	String dane=BazaDanych.wykonajSkrypt("profil_pobierz.php", parametry);
    	try {
			JSONArray json=new JSONArray(dane);
			wlasciciel=new Uzytkownik(json.getString(0), json.getString(2), 
					json.getString(3), "maslo", json.getString(1), 1);
		} catch(JSONException e){}
	}
	
	
	private void pobierzWycieczke(){
		List<NameValuePair> parametry=new ArrayList<NameValuePair>();
		String  mWycieczka=BazaDanych.wykonajSkrypt("pobierz_id_mojejWycieczki.php", parametry);
    	try{
    		JSONArray json= new JSONArray(mWycieczka);
    		mojaWycieczka=new Wycieczka(json.getJSONArray(0).getString(0),
					json.getJSONArray(0).getString(1));
    	}catch (JSONException e){}
	}
	
	
	private void wyswietlDane(){
		login=(TextView) findViewById(R.id.tvLoginP);
        imie=(TextView) findViewById(R.id.tvImieP);
        nazwisko=(TextView) findViewById(R.id.tvNazwiskoP);
        login.setText(wlasciciel.login);
        imie.setText(wlasciciel.imie);
        nazwisko.setText(wlasciciel.nazwisko);
	}
	

	
	public void onClickdblokoj(View V){
		final Dialog dialog= new Dialog(Activity_Profil.this);
		dialog.setContentView(R.layout.dialog_zmien_dane);
		//dialog.setTitle("Znieñ dane");
		
		
		((EditText) dialog.findViewById(R.id.etImieDialogP)).setText(wlasciciel.imie);
		((EditText) dialog.findViewById(R.id.etNazwiskoDialogP)).setText(wlasciciel.nazwisko);
		
		Button ok=(Button) dialog.findViewById(R.id.bZatwierdzDaneDialogP);
		Button anuluj= (Button) dialog.findViewById(R.id.bAnulujDaneDialogP);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String haslo=((EditText) dialog.findViewById(R.id.etHasloDialogP)).getText().toString();
				String imie=((EditText) dialog.findViewById(R.id.etImieDialogP)).getText().toString();
				String nazwisko=((EditText) dialog.findViewById(R.id.etNazwiskoDialogP)).getText().toString();
				ArrayList<NameValuePair> par= new ArrayList<NameValuePair>();
				par.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));
				par.add(new BasicNameValuePair("imie", imie));
				par.add(new BasicNameValuePair("nazwisko", nazwisko));
				par.add(new BasicNameValuePair("haslo", haslo));
				String dane=BazaDanych.wykonajSkrypt("profil_edytuj.php", par);
				//
				Toast.makeText(context, dane, Toast.LENGTH_LONG).show();
				if (dane.equals("1")){
			        wlasciciel.imie=imie;
			        wlasciciel.nazwisko=nazwisko;
				}
				else{
					Toast.makeText(context, "blad podczas zmiany danych," +
							" sprawdz czy haslo jest poprawne", Toast.LENGTH_LONG).show();
				}

			     wyswietlDane();
			     dialog.dismiss();
				}

		});
		anuluj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	
	
	public void onClickZmienHaslo(View V){
		final Dialog dialog= new Dialog(Activity_Profil.this);
		dialog.setContentView(R.layout.dialog_zmien_haslo);

		Button ok=(Button) dialog.findViewById(R.id.bOkHasloP);
		Button anuluj= (Button) dialog.findViewById(R.id.bAnulujHasloP);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String haslo = ((EditText) dialog
						.findViewById(R.id.etStareHasloP)).getText().toString();
				String noweHaslo = ((EditText) dialog
						.findViewById(R.id.etNoweHasloP)).getText().toString();
				String powNoweHaslo = ((EditText) dialog
						.findViewById(R.id.etPowNoweHasloP)).getText()
						.toString();
				if (noweHaslo.equals(powNoweHaslo)) {
					ArrayList<NameValuePair> par = new ArrayList<NameValuePair>();
					par.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));
					par.add(new BasicNameValuePair("haslo_poprzednie", haslo));
					par.add(new BasicNameValuePair("haslo_nowe", noweHaslo));

					String dane = BazaDanych.wykonajSkrypt("profil_haslo.php",
							par);
					if (dane.equals("1")) {
						Toast.makeText(context, "Haslo zostalo zmienione",
								Toast.LENGTH_LONG).show();

					} else {
						Toast.makeText(
								context,
								"blad podczas zmiany danych,"
										+ " sprawdz czy haslo jest poprawne",
								Toast.LENGTH_LONG).show();
					}
					dialog.dismiss();
				} else {

				}

			}

		});
		anuluj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	
	public void onClickDodajWycieczke(View v)
	{
		final Dialog dialog = new Dialog(Activity_Profil.this);
		dialog.setContentView(R.layout.dialog_nowa_wycieczka);
		dialog.setTitle("Stworz wlasna wycieczke");
		Button ok= (Button) dialog.findViewById(R.id.bOkDialogP);
		
		ok.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				EditText nazwa= (EditText) dialog.findViewById(R.id.etNazwaWycieczkiDialogP);
				final DatePicker dataR= (DatePicker)dialog.findViewById(R.id.dpDataRozpoczeciaDialogP);
				final DatePicker dataZ= (DatePicker)dialog.findViewById(R.id.dpDataZakonczeniaDialogP);
				Calendar rozpoczecie=Calendar.getInstance();;
				rozpoczecie.set(Calendar.YEAR, dataR.getYear());
				rozpoczecie.set(Calendar.MONTH, dataR.getMonth());
				rozpoczecie.set(Calendar.DAY_OF_MONTH, dataR.getDayOfMonth());
				Calendar zakonczenie=Calendar.getInstance();
				zakonczenie.set(dataZ.getYear(), dataZ.getMonth(), dataZ.getDayOfMonth());
				if(rozpoczecie.getTimeInMillis()< zakonczenie.getTimeInMillis()){
					if ( !nazwa.getText().toString().equals("")){
						mojaWycieczka= new Wycieczka(nazwa.getText().toString(), wlasciciel, rozpoczecie, zakonczenie);
												
						List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
						php_parametry.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
						php_parametry.add(new BasicNameValuePair("nazwa", nazwa.getText().toString()));
						php_parametry.add(new BasicNameValuePair("data_rozpoczecia", DateFormat.format("yyyy-MM-dd hh:mm:ss", rozpoczecie).toString()));
						php_parametry.add(new BasicNameValuePair("data_zakonczenia", DateFormat.format("yyyy-MM-dd hh:mm:ss", zakonczenie).toString()));
						//php_parametry.add(new BasicNameValuePair("opis", String.valueOf(id_uzytkownika)));
			        	String r=BazaDanych.wykonajSkrypt("wycieczka_dodaj.php", php_parametry);
			        	if(r.equals("1")){
			        		Toast.makeText(context, "Stworzono wycieczke: " + mojaWycieczka.nazwa, Toast.LENGTH_LONG).show();
			        	}
			        	dialog.dismiss();
						
				
						((ListView)findViewById(R.id.lvMojaWycieczkaP)).setVisibility(View.VISIBLE);
						wyswietlMojaWycieczke();
					}
					else{
						Toast.makeText(context,
							      "Prosze podac nazw wycieczki", Toast.LENGTH_LONG)
							      .show();
					}
						
				}
				else
					Toast.makeText(context,
						      "data zakonczenia musi byc pozniejsza niz data rozpoczecia", Toast.LENGTH_LONG)
						      .show();
				
			}
		});
		Button anuluj=(Button)dialog.findViewById(R.id.bAnulujDialogP);
		anuluj.setOnClickListener(new OnClickListener(){			
			
			@Override
			public void onClick(View view){
				dialog.dismiss();
			}
			
		});
		dialog.show();
	}
	
	
	public void onClickUsunWycieczke(View v)
	{
		List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
		php_parametry.add(new BasicNameValuePair("id", String.valueOf(mojaWycieczka.getId())));
		String r=BazaDanych.wykonajSkrypt("wycieczka_usun.php", php_parametry);
		if(r.equals("1")){
			Toast.makeText(context, "usunoles wycieczke", Toast.LENGTH_LONG).show();
		}
		
		mojaWycieczka=null;
		((ListView)findViewById(R.id.lvMojaWycieczkaP)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.tvNazwaWycieczkiP)).setVisibility(View.GONE);
	}
	
	public void onClickWyslijWiadomosc (View v){
		final Dialog dialog = new Dialog(Activity_Profil.this);
		dialog.setContentView(R.layout.dialog_wia_wyc);
		dialog.setTitle("Wyslij wiadomosc");
		/*pobranie wycieczek gdzie jest organizatorem lub opiekunem*/
		List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
		php_parametry.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
		String r=BazaDanych.wykonajSkrypt("szukaj_id_wyc.php", php_parametry);
		if (r.isEmpty()){
			dialog.dismiss();
			Toast.makeText(context, "nie jestes organizatorem ani opiekunem", Toast.LENGTH_LONG).show();
		}
		JSONArray json;
		final RadioGroup rbWycieczki= (RadioGroup) dialog.findViewById(R.id.rgWycieczkiWiaWycP);
		final ArrayList<String> id_wycieczek= new ArrayList<String>();
		try{
			json = new JSONArray(r);
			for (int i = 0; i < json.length(); ++i) {
				RadioButton rb= new RadioButton(Activity_Profil.this);
				rb.setId(i);
				rb.setText(json.getJSONArray(i).getString(0));
				id_wycieczek.add(i,json.getJSONArray(i).getString(1));
				rbWycieczki.addView(rb);
			}
		}catch (JSONException e){}
		rbWycieczki.check(0);
		Button ok= (Button) dialog.findViewById(R.id.bOKWiaWycP);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String id_wyc=id_wycieczek.get(rbWycieczki.getCheckedRadioButtonId());
				String tresc=((TextView) dialog.findViewById(R.id.etTrescWiaWycP)).getText().toString();
				List<NameValuePair> php_par=new ArrayList<NameValuePair>();
				php_par.add(new BasicNameValuePair("id_uzytkownika", String.valueOf(id_uzytkownika)));
				php_par.add(new BasicNameValuePair("id_wycieczki", id_wyc));
				php_par.add(new BasicNameValuePair("tresc",tresc ));
				String dane= BazaDanych.wykonajSkrypt("wiadomosc_wyc_wys.php", php_par);
				dialog.dismiss();
			}
		});
		Button anuluj= (Button) dialog.findViewById(R.id.bAnulujWiaWycP);
		anuluj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		dialog.show();
	}
	
	
	public void onClickBlok (View v){
		blok=!blok;
		
		Button usunWycieczke=(Button)findViewById(R.id.usunWycieczkeP);
		ImageButton im=(ImageButton)findViewById(R.id.ibBlokP);
		if(blok){
			usunWycieczke.setVisibility(View.GONE);
			im.setImageResource(R.drawable.ic_action_new_account);
		}else{
			usunWycieczke.setVisibility(View.VISIBLE);
			im.setImageResource(R.drawable.ic_action_lock_account);
		}
		if(mojaWycieczka!=null){	
		wyswietlMojaWycieczke();
		}
	}
	
	void wyswietlMojaWycieczke()
	{
		ListView mWycieczka=(ListView)findViewById(R.id.lvMojaWycieczkaP);
		ArrayList<Uzytkownik> wycieczkowicze= new ArrayList<Uzytkownik>();
		
		//if(mojaWycieczka.organizator!=null)
		//	wycieczkowicze.add(mojaWycieczka.organizator);
		if(!mojaWycieczka.opiekunowie.isEmpty())
			wycieczkowicze.addAll(mojaWycieczka.opiekunowie);		
		if(!mojaWycieczka.uczestnicy.isEmpty())
		wycieczkowicze.addAll(mojaWycieczka.uczestnicy);
		if(!mojaWycieczka.aplikanci.isEmpty())
			wycieczkowicze.addAll(mojaWycieczka.aplikanci);
		
		ProfilListAdapter adapter= new ProfilListAdapter(Activity_Profil.this, R.layout.list_item_profil,
				wycieczkowicze,mojaWycieczka, blok);
		
		mWycieczka.setOnItemClickListener(new OnItemClickListener() {
			@Override
			   public void onItemClick(AdapterView<?> parent, View v,
			     final int position, long id){
			}

		});
		mWycieczka.setAdapter(adapter);
		
	}
	

	
}