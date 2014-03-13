package com.jwycieczki.jwycieczki;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Main extends Activity {
	
	private Context context;
	private Intent intent;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        context=this.getApplicationContext();
        // $$$ zeby nie logowac sie za kazdym razem dane przykladowe
        intent=new Intent(context, Activity_Obszar_Roboczy.class);
		intent.putExtra("IdUzytkownika", 2);
		startActivity(intent);
		this.finish();
		// $$
        onClickZakoncz();
    }
    
    public void onClick_Logowanie(View v)
    {
    	Logowanie log=new Logowanie();
    	String login=((EditText)findViewById(R.id.etLoginL)).getText().toString();
    	String haslo=((EditText)findViewById(R.id.etHasloL)).getText().toString();
    	String info=log.Zaloguj(login, haslo);
    	if(info=="zalogowano"){
    		intent=new Intent(context, Activity_Obszar_Roboczy.class);
    		intent.putExtra("IdUzytkownika", log.id_uzytkownika);
    		startActivity(intent);
    		this.finish();
    	} else{
    		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
    	}
    }
    
    public void onClick_Activity_Rejestracja(View v)
    {
    	intent=new Intent(context, Activity_Rejestracja.class);
		startActivity(intent);
    }
    
    public void onClick_Activity_Nowe_Haslo(View v)
    {
		intent=new Intent(context, Activity_Nowe_Haslo.class);
		startActivity(intent);
	}
    
    private void onClickZakoncz(){
    	TextView zakoncz= (TextView) findViewById(R.id.tvZamknijL);
    	zakoncz.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				System.exit(0);
				
			}
		});
    }
    
    
}
