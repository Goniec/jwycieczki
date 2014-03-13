package com.jwycieczki.jwycieczki;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
/***************************************************
 * 
 * @author Lukasz
 * Wykorzystywana w Activity_Obszar_Roboczy do pobierania listy przyyciskow wycieczek w onCreate
 ****************************************************/
public class ButtonAdapter extends ArrayAdapter<Button>{
	Context context;
	int layoutResourceId;
	ArrayList<Button> przyciski;
	ArrayList<Wycieczka> wycieczki;
	
	public ButtonAdapter(Context context, int layoutResourceId,
			ArrayList<Button> przyciski,ArrayList<Wycieczka> wycieczki){
		super(context,layoutResourceId,przyciski);
		this.layoutResourceId=layoutResourceId;
		this.context=context;
		this.przyciski=przyciski;
		this.wycieczki= wycieczki;
	}
	public View getView(int position, View convertView,ViewGroup parent){
		View row= convertView;
		Button przycisk= null;
		CharSequence nazwa;
		if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
 
            przycisk = new Button(context);
            przycisk = (Button) row.findViewById(R.id.bPrzykladowy);
            
 
            row.setTag(przycisk);
        }
        else
        {
            przycisk = (Button)row.getTag();
        }
		Button object= przyciski.get(position);
		//nazwa=object.getText().toString().substring(0, 2);
        przycisk.setText(object.getText());
 
        return row;
    }

}
