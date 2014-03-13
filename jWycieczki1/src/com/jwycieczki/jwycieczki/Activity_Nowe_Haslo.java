package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Nowe_Haslo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nowe_haslo);
	}
	
	public void wygeneruj_haslo(View v)
	{
		String nick=((EditText)findViewById(R.id.nowehaslonick)).getText().toString();
		String email=((EditText)findViewById(R.id.nowehasloemail)).getText().toString();
        List<NameValuePair> nvp=new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("nick", nick));
        nvp.add(new BasicNameValuePair("email", email));
        String r=BazaDanych.wykonajSkrypt("password.php", nvp);
        String komunikat;
        if(r.equals("1")){
			komunikat="zmieni³eœ has³o";
		} else if(r.equals("0")){
			komunikat="niepoprawny login lub adres e-mail";
		} else if(r.equals("2")){
			komunikat="aktywuj konto";
		} else{
			komunikat="jakis b³¹d";
		}
        Toast.makeText(getApplicationContext(), komunikat, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.nowe_haslo, menu);
		return true;
	}
	
}
