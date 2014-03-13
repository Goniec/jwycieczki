package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;



public class Wycieczka {
	
	private int id;
	
	String nazwa;
	//Date data;
	int status; //aktywna, zakoñczona
	ArrayList<Wiadomosc> tabWycieczki;
	Uzytkownik organizator;
	ArrayList<Uzytkownik> uczestnicy;
	ArrayList<Uzytkownik> opiekunowie;
	ArrayList<Uzytkownik> aplikanci;// ci co chca dolaczyc do wycieczki;
	//ArrayList<Grupa> grupy;
	Calendar dataRozpoczecia;
	Calendar dataZakoñczenia;

	
	Wycieczka(){
		//this.nazwa=nazwa;// tworzenie przykladowej wycieczki
		tabWycieczki= new ArrayList<Wiadomosc>();
		opiekunowie= new ArrayList<Uzytkownik>();
		uczestnicy=new ArrayList<Uzytkownik>();
		aplikanci= new ArrayList<Uzytkownik>();
	}
	Wycieczka(String nazwa, String id_wycieczki){
		id=Integer.parseInt(id_wycieczki);
		this.nazwa=nazwa;// tworzenie przykladowej wycieczki
		tabWycieczki= new ArrayList<Wiadomosc>();
		opiekunowie= new ArrayList<Uzytkownik>();
		uczestnicy=new ArrayList<Uzytkownik>();
		aplikanci= new ArrayList<Uzytkownik>();
	}
	Wycieczka(String nazwa,Uzytkownik u, Calendar rozpoczecnie,Calendar zakonczenie){
		this.nazwa=nazwa;// tworzenie przykladowej wycieczki
		this.organizator=u;
		this.dataRozpoczecia=rozpoczecnie;
		this.dataZakoñczenia=zakonczenie;
		tabWycieczki= new ArrayList<Wiadomosc>();
		opiekunowie= new ArrayList<Uzytkownik>();
		uczestnicy=new ArrayList<Uzytkownik>();
		aplikanci= new ArrayList<Uzytkownik>();
	}
	
	public boolean dodajWiadomosci(){
		List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
    	php_parametry.add(new BasicNameValuePair("id", String.valueOf(id)));
    	String r=BazaDanych.wykonajSkrypt("wiadomosci_wycieczki.php", php_parametry);
    	JSONArray json;
		try{
			json=new JSONArray(r);
			for(int i=0; i<json.length(); ++i){
				tabWycieczki.add(new Wiadomosc(json.getJSONArray(i).getString(2)+" "+json.getJSONArray(i).getString(0)+" "+
						json.getJSONArray(i).getString(1)+"\n"+json.getJSONArray(i).getString(3), null, 0));	
        	}
			
		}
		catch(JSONException e){
			return false;
		}
		if(json.length()>0){
			return true;
		}else{
			return false;
		}
	}
	public boolean dodajUczestnikow() {
		List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
    	php_parametry.add(new BasicNameValuePair("id", String.valueOf(id)));
    	String r=BazaDanych.wykonajSkrypt("uczestnicy_wycieczki.php", php_parametry);
    	JSONArray json;
		try{
			json=new JSONArray(r);
			for(int i=0; i<json.length(); ++i){
				Uzytkownik dodawany=new Uzytkownik(json.getJSONArray(i).getString(0),
						json.getJSONArray(i).getString(1),json.getJSONArray(i).getString(2));
				if(json.getJSONArray(i).getString(3).equals("organizator")){
					this.organizator=dodawany;
				}else if(json.getJSONArray(i).getString(3).equals("opiekun")){
					this.opiekunowie.add(dodawany);
				}else if(json.getJSONArray(i).getString(3).equals("czlonek")){
					this.uczestnicy.add(dodawany);
				}else if(json.getJSONArray(i).getString(3).equals("aplikant")){
					this.aplikanci.add(dodawany);
				}
					
        	}
		} catch(JSONException e){return false;}
		return (json.length()>0 ? true : false);
	}
	
	public boolean odswierzUczestnikow(){
		List<NameValuePair> php_parametry=new ArrayList<NameValuePair>();
    	php_parametry.add(new BasicNameValuePair("id", String.valueOf(id)));
    	String r=BazaDanych.wykonajSkrypt("uczestnicy_wycieczki.php", php_parametry);
    	JSONArray json;
    	boolean spr=false;
		try{
			json=new JSONArray(r);
			for(int i=0; i<json.length(); ++i){
				Uzytkownik dodawany=new Uzytkownik(json.getJSONArray(i).getString(0),
						json.getJSONArray(i).getString(1),json.getJSONArray(i).getString(2));
				if(json.getJSONArray(i).getString(3).equals("opiekun")){ //sprawdzanie czy jest opiekunem
					spr=sprNaLiscie(opiekunowie, dodawany);
					if(!spr){//nie jest na licie
						this.opiekunowie.add(dodawany);
					}
				}else if(json.getJSONArray(i).getString(3).equals("czlonek")){
					spr=sprNaLiscie(uczestnicy, dodawany);
					if(!spr){
						this.uczestnicy.add(dodawany);
					}
				}else if(json.getJSONArray(i).getString(3).equals("aplikant")){
					spr=sprNaLiscie(aplikanci, dodawany);
					if(!spr){
						this.aplikanci.add(dodawany);
					}
				}	
        	}
		} catch(JSONException e){return false;}
		return spr;
	}
	
	private boolean sprNaLiscie(List<Uzytkownik> lista, Uzytkownik uzytkownik){
		for(Uzytkownik u : lista)
			if(u.login.equals(uzytkownik.login))
				return true; //jest na licie
		return false;
	}
	
	
	public int getId(){
		return this.id;
	}
	
	
	
	/*private boolean dodajUczestnika(Uzytkownik uczestnik){
		return true;
	}
	
	private boolean dodajOpiekuna(Uzytkownik opiekun){
		return true;
	}*/

}
