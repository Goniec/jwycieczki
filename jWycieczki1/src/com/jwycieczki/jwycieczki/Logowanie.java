package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class Logowanie {
	
	private String login;
	private String haslo;
	protected int id_uzytkownika;
	
	Logowanie(){
		login = "";
		haslo = "";
	}
	
	public void przypHaslo(){}
	
	public String Zaloguj(String m_login,String m_haslo)
	{
		login = m_login;
		haslo = m_haslo;
		if(login == "" || haslo == "")
		{
			// tutaj komunikat ï¿½e uï¿½ytkownik nie wpisal danych
			return "Nie wpisano Danych";
		}
		else
		{
			
			// DODAJE POLACZENIE Z BAZA WOJTEK
	        List<NameValuePair> nvp=new ArrayList<NameValuePair>();
	        nvp.add(new BasicNameValuePair("nick", login));
	        nvp.add(new BasicNameValuePair("password", haslo));
	        String r=BazaDanych.wykonajSkrypt("login.php", nvp);
	        Log.i("sprawdzam", "tutaj");
	        int i=Integer.parseInt(r);
	        if(-1<i){
	        	id_uzytkownika=i;
    			return "zalogowano";
    		} else if(-3==i){
    			return "Niepoprawny login lub haslo";
    		} else if(-2==i){
    			return "aktywuj konto";
    		} else{
    			return r+" jakis b³¹d";
    		}
		}
	}
	
}
