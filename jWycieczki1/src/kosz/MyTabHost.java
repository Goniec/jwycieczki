package kosz;
import java.util.ArrayList;

import com.jwycieczki.jwycieczki.R;
import com.jwycieczki.jwycieczki.R.id;

import android.app.TabActivity;
import android.content.Intent;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;



@SuppressWarnings("deprecation")
public class MyTabHost extends TabActivity {
	TabHost tabHost;
	ArrayList<TabSpec> cards ;
	int n;
	
	MyTabHost(){
		tabHost=(TabHost) findViewById(R.id.thZakladkiO);
		n=0;
		cards= new ArrayList<TabHost.TabSpec>();
	}
	
	void addCard(String name){
		TabSpec tmp=tabHost.newTabSpec(name);
		tmp.setIndicator(name);
		tmp.setContent(new Intent(this, TabHostElement.class));
		cards.add(tmp);
		n++;
		
		
	}
	void drawTabHost(){
		for(TabSpec ts : cards){
			tabHost.addTab(ts);
		}
	}

}
