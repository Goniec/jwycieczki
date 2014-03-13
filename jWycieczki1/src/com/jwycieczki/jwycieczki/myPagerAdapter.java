package com.jwycieczki.jwycieczki;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class myPagerAdapter extends PagerAdapter {
	
	public int getCount(){
		return 5;
	}
	
	public Object instantiateItem(View collection, int position){
		
		LayoutInflater inf = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		int layoutId = 0;
		switch (position){
		case 0:
			layoutId= R.layout.activity_galeria;
			break;
		case 1:
			layoutId = R.layout.activity_profil;
			break;
		case 2:
			layoutId = R.layout.activity_obszar_roboczy;
			break;
		case 3:
			layoutId = R.layout.activity_ustawienia;
			break;
		case 4:
			layoutId = R.layout.activity_mapa;
			break;
		}
		
		View view = inf.inflate(layoutId, null);
		return view;
	}
	
	public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }
	
	public boolean isViewFromObject(View arg0, Object arg1){
		return arg0 == ((View)arg1);
	}
	
	public Parcelable saveState(){
		return null;
	}

}
