package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.Date;

public class Wiadomosc {
	
	protected String adresat;
	protected String tresc;
	protected String data;
	protected String odbiorca;
	
	protected Wiadomosc()
	{
		//
	}
	
	protected Wiadomosc(String tresc, Uzytkownik u, int id)
	{
		this.tresc=tresc;
		//odbiorcy= new ArrayList<Uzytkownik>();
	}
	
	protected Wiadomosc(String tresc, String odbiorca, String data, String adresat)
	{
		this.tresc=tresc;
		this.odbiorca=odbiorca;
		this.data=data;
		this.adresat=adresat;
	}
	
	protected void addWiadomosc(String tresc)
	{
		this.tresc=tresc;
		//this.adresat=adresat;
		//this.odbiorcy=odbiorcy;
	}
	
	protected void addWiadomosc(String tresc, String adresat, String odbiorca)
	{
		this.tresc=tresc;
		this.adresat=adresat;
		this.odbiorca=odbiorca;
	}
	
	protected String wyswietl()
	{
		return this.tresc;
	}
	

	
}
