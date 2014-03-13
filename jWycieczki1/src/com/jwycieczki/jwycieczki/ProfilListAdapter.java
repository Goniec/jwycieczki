package com.jwycieczki.jwycieczki;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilListAdapter extends ArrayAdapter<Uzytkownik> {
	Context context;
	int layoutResourceId;

	Uzytkownik organizator;
	Wycieczka wycieczka;
	ArrayList<Uzytkownik> wycieczkowicze;
	boolean blok;


	public ProfilListAdapter(Context context,
			int layoutResourceId,ArrayList<Uzytkownik>wycieczkowicze, Wycieczka wycieczka, boolean blok) {
		super(context, layoutResourceId,wycieczkowicze);

		this.organizator=wycieczka.organizator;
		this.wycieczka=wycieczka;
		this.wycieczkowicze=wycieczkowicze;
				
		// TODO Auto-generated constructor stub
		this.context=context;
		this.layoutResourceId=layoutResourceId;
		this.blok=blok;

	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		  View row = convertView;
		  Wiersz holder = null;
		  Uzytkownik u = wycieczkowicze.get(position);
		  if (row == null) {
			  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			  row = inflater.inflate(layoutResourceId, parent, false);
			  holder = new Wiersz();
			  holder.uzytkownik=u;
			  holder.etykieta = (TextView) row.findViewById(R.id.tvDanePlv);	  
			  holder.awansuj = (Button) row.findViewById(R.id.bAwansujPlv);
			  holder.awansuj.setTag(u);
			  holder.degraduj = (Button) row.findViewById(R.id.bDegradujPlv);
			  holder.degraduj.setTag(u);
			  holder.dodaj=(Button) row.findViewById(R.id.bDodajPlv);
			  holder.dodaj.setTag(u);
			  holder.usun= (Button) row.findViewById(R.id.bUsunPlv);
			  holder.usun.setTag(u);
			  holder.uzytkownik= u;
			  row.setTag(holder);
		  } else {
		   holder = (Wiersz) row.getTag();
		   holder.awansuj.setTag(u);
		   holder.degraduj.setTag(u);
		   holder.usun.setTag(u);
		   holder.dodaj.setTag(u);
		  }
		  
		  holder.etykieta.setText(u.login+ " "+ u.imie+" "+u.nazwisko);
		  if(u.equals(wycieczka.organizator)){
			  jestOrganizatorem(holder);
		  }
		  else if(wycieczka.opiekunowie.contains(u)){
			 jestOpiekunem(holder);
		  }
		  else if(wycieczka.uczestnicy.contains(u)){
			  jestCzlonkiem(holder);
		  }
		  else if(wycieczka.aplikanci.contains(u)){
			  jestAplikantem(holder);
		  }
		  
		  holder.awansuj.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				//Uzytkownik uzytkownik= (Uzytkownik) v.getTag();
				Uzytkownik u= (Uzytkownik)v.getTag();
				wycieczka.uczestnicy.remove(u);
				wycieczka.opiekunowie.add(u);
				ArrayList <NameValuePair> p= new ArrayList<NameValuePair>();
			    p.add(new BasicNameValuePair("login", u.login));
			    p.add(new BasicNameValuePair("id_wycieczki", String.valueOf(wycieczka.getId())));
			    p.add(new BasicNameValuePair("status", "opiekun"));
			    BazaDanych.wykonajSkrypt("wycieczki_modyfikuj_uzytkownika.php", p); 
				updateList();
				Toast.makeText(context, "Awansowalem "+ u.login,
					      Toast.LENGTH_LONG).show();
			}
		});
		  holder.degraduj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uzytkownik u= (Uzytkownik)v.getTag();
				wycieczka.uczestnicy.add(u);
				wycieczka.opiekunowie.remove(u);
				ArrayList <NameValuePair> p= new ArrayList<NameValuePair>();
			    p.add(new BasicNameValuePair("login", u.login));
			    p.add(new BasicNameValuePair("id_wycieczki", String.valueOf(wycieczka.getId())));
			    p.add(new BasicNameValuePair("status", "czlonek"));
			    BazaDanych.wykonajSkrypt("wycieczki_modyfikuj_uzytkownika.php", p); 
				updateList();
				Toast.makeText(context, "Zdegradowalem "+ u.login,
					      Toast.LENGTH_LONG).show();
			}
		});
		  holder.usun.setOnClickListener(new OnClickListener() {
		   @Override
		   public void onClick(View v) {
					// TODO Auto-generated method stub
					Uzytkownik u= (Uzytkownik)v.getTag();
				    if(wycieczka.opiekunowie.contains(u)){
				    	wycieczka.opiekunowie.remove(u);
				    }
				    else if(wycieczka.uczestnicy.contains(u)){
				    	wycieczka.uczestnicy.remove(u);
				    }
				    else if(wycieczka.aplikanci.contains(u)){
				    	wycieczka.aplikanci.remove(u);
				    }
				    
				    ArrayList <NameValuePair> p= new ArrayList<NameValuePair>();
				    p.add(new BasicNameValuePair("login", u.login));
				    p.add(new BasicNameValuePair("id_wycieczki", String.valueOf(wycieczka.getId())));
				    BazaDanych.wykonajSkrypt("wycieczki_usuwanie_uzytkownika.php", p); 
				    updateList();
		   }
		   });
		  holder.dodaj.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uzytkownik u= (Uzytkownik)v.getTag();
					wycieczka.uczestnicy.add(u);
					wycieczka.aplikanci.remove(u);
					ArrayList <NameValuePair> p= new ArrayList<NameValuePair>();
				    p.add(new BasicNameValuePair("login", u.login));
				    p.add(new BasicNameValuePair("id_wycieczki", String.valueOf(wycieczka.getId())));
				    p.add(new BasicNameValuePair("status", "czlonek"));
				    BazaDanych.wykonajSkrypt("wycieczki_modyfikuj_uzytkownika.php", p); 
					updateList();
					Toast.makeText(context, "Dodalem "+ u.login,
						      Toast.LENGTH_LONG).show();
				}
			});
				
		  return row;

	}
	void jestOrganizatorem(Wiersz holder){
		holder.awansuj.setVisibility(View.GONE);
		holder.degraduj.setVisibility(View.GONE);
		holder.dodaj.setVisibility(View.GONE);
		holder.usun.setVisibility(View.GONE);
	}
	void jestOpiekunem (Wiersz holder){
		 holder.awansuj.setVisibility(View.GONE);
		  holder.degraduj.setVisibility(View.VISIBLE);
		  holder.dodaj.setVisibility(View.GONE);
		  if(blok){
			  holder.usun.setVisibility(View.GONE);
		  }
		  else{
			  holder.usun.setVisibility(View.VISIBLE);
		  }
	}
	void jestCzlonkiem (Wiersz holder){
		holder.awansuj.setVisibility(View.VISIBLE);
		  holder.degraduj.setVisibility(View.GONE);
		  holder.dodaj.setVisibility(View.GONE);
		  if(blok){
			  holder.usun.setVisibility(View.GONE);
		  }
		  else{
			  holder.usun.setVisibility(View.VISIBLE);
		  }
	}
	void jestAplikantem (Wiersz holder){
		holder.awansuj.setVisibility(View.GONE);
		  holder.degraduj.setVisibility(View.GONE);
		  holder.dodaj.setVisibility(View.VISIBLE);
		  if(blok){
			  holder.usun.setVisibility(View.GONE);
		  }
		  else{
			  holder.usun.setVisibility(View.VISIBLE);
		  }
	}
	
	public void updateList(){
		this.clear();
		organizator=wycieczka.organizator;
		wycieczkowicze= new ArrayList<Uzytkownik>();
		//if(wycieczka.organizator!=null)
		//	wycieczkowicze.add(wycieczka.organizator);
		if(!wycieczka.opiekunowie.isEmpty())
			wycieczkowicze.addAll(wycieczka.opiekunowie);		
		if(!wycieczka.uczestnicy.isEmpty())
			wycieczkowicze.addAll(wycieczka.uczestnicy);
		if(!wycieczka.aplikanci.isEmpty())
			wycieczkowicze.addAll(wycieczka.aplikanci);
		this.addAll(wycieczkowicze);
		this.notifyDataSetChanged();
	}
	
	private class Wiersz{
		Uzytkownik uzytkownik;
		TextView etykieta;
		Button awansuj;
		Button degraduj;
		Button usun;
		Button dodaj;
	}

}

