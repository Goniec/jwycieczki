package com.jwycieczki.jwycieczki;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Rejestracja extends Activity {
	
	private String login;
	private String haslo;
	private String pHaslo;
	private String email;
	private boolean regulamin;
	
	private Context context;
	private Resources resources;
	private Intent intent;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);
        context=getApplicationContext();
        resources=getResources();
    }
    
	public void onClickZarejestruj(View v)
	{
		boolean potwierdzenie=true;  
		login = ((EditText)findViewById(R.id.etLoginR)).getText().toString();
		haslo = ((EditText)findViewById(R.id.etHasloR)).getText().toString();
		pHaslo = ((EditText)findViewById(R.id.etPHasloR)).getText().toString();
		email = ((EditText)findViewById(R.id.etEmailR)).getText().toString();
		regulamin = ((CheckBox)findViewById(R.id.cbAkceptujeR)).isChecked();
		
		if(login.length()==0 || haslo.length()==0 || pHaslo.length()==0 || email.length()==0)
		{
			Toast.makeText(context, resources.getString(R.string.Uzupelnij_pola), Toast.LENGTH_LONG).show();
			// "Prosze uzupelnic wszystkie pola"
			potwierdzenie=false;
			
		}
		if(regulamin==false && potwierdzenie){
			Toast.makeText(context, resources.getString(R.string.Akceptacja_regulaminu), Toast.LENGTH_LONG).show();
			potwierdzenie=false;
			//"Akceptacja regulaminu jest konieczna do zarejestrowania konta"
		}
		if(haslo.length()<5 && potwierdzenie){
			Toast.makeText(context, resources.getString(R.string.Haslo_krotkie), Toast.LENGTH_LONG).show();
			potwierdzenie=false;
			// "haso za krótkie"
		}
		
		if(!haslo.equals(pHaslo)&& potwierdzenie ){
			Toast.makeText(context, resources.getString(R.string.Hasla_rozne), Toast.LENGTH_LONG).show();
			potwierdzenie=false;
			// "wprowadzono ró¿ne hasla"
		}
		
		if(potwierdzenie){// wszystko jest OK mo¿na przesylac
		
	        List<NameValuePair> nvp=new ArrayList<NameValuePair>();
	        nvp.add(new BasicNameValuePair("nick", login));
	        nvp.add(new BasicNameValuePair("email", email));
	        nvp.add(new BasicNameValuePair("password", haslo));
	        String wynik=BazaDanych.wykonajSkrypt("signup.php", nvp);
	        if(wynik.equals("1")){
        		Intent intent = new Intent(context,Activity_Main.class );
        		startActivity(intent);
        		this.finish();
        	}
	        else if(wynik.equals("-4")){
        		Toast.makeText(context, resources.getString(R.string.Nazwa_niedozwolona), Toast.LENGTH_LONG).show();
        		// "nazwa u¿ytkownika niedozwolona"
        	}
	        else if(wynik.equals("-3")){
        		Toast.makeText(context, resources.getString(R.string.Email_niedozwolony), Toast.LENGTH_LONG).show();
        		// "adres e-mail niedozwolony"
        	}
	        else{
	        	Toast.makeText(context, wynik, Toast.LENGTH_LONG).show();
	        }
		}
	}
	
	public void onClickAnuluj(View v)
	{
		Toast.makeText(context, resources.getString(R.string.Anulowano), Toast.LENGTH_LONG).show();
		// "Anulowano"
		intent=new Intent(context, Activity_Main.class);
		startActivity(intent);
		this.finish();
	}
	
	public void onClick_Activity_Regulamin(View v)
	{
		startActivity(new Intent(context, Activity_Regulamin.class));
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String ktoryElement = ""; 
        switch (item.getItemId()) {
        case R.id.Zakoncz:
            ktoryElement = "pierwszy";
            break;
        default:
            ktoryElement = "¿aden";
        }
 
        Toast.makeText(context, "Element: " + ktoryElement,
                Toast.LENGTH_LONG).show(); 
        return true;
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        
    }
	
}
