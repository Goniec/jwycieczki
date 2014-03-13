package com.jwycieczki.jwycieczki;


public class Uzytkownik {
	String login;
	String imie;
	String nazwisko;
	String haslo;
	String email;
	int status;
	Wycieczka wycieczka=null;
	
	
	
	Uzytkownik(String login, String imie, String nazwisko, String haslo,
			String email, int status){
		this.login=login;
		this.imie =imie;
		this.nazwisko=nazwisko;
		this.email=email;
		this.status=status;
		this.haslo=haslo;
	}
	Uzytkownik(String login, String imie, String nazwisko){
		this.login=login;
		this.imie =imie;
		this.nazwisko=nazwisko;
	}
	public String grtImie(){
		return this.imie;
	}
	
	/*private void zmienHaslo(){
		
	}
	
	private void utworzWycieczke(){
		
	}*/
	
	

}
