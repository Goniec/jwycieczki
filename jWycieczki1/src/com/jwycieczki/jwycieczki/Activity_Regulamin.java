package com.jwycieczki.jwycieczki;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Activity_Regulamin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regulamin);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity__regulamin, menu);
		return true;
	}

}
