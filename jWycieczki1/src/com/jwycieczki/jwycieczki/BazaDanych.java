package com.jwycieczki.jwycieczki;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.text.format.DateFormat;

public abstract class BazaDanych {

	protected static String wykonajSkrypt(String nazwa, List<NameValuePair> parametry)
	{
		/*
		 * List<NameValuePair>=new ArrayList<NameValuePair>();
		 * nvp.add(new BasicNameValuePair("nick", login))
		 */
		HttpPost hp=new HttpPost("http://jwycieczki.ugu.pl/"+nazwa);
        try{
        	hp.setEntity(new UrlEncodedFormEntity(parametry));
        	return (new Pobierz().execute(hp)).get();
        } catch(Exception e){
        	return e.toString();
        }
	}
	
	private static class Pobierz extends AsyncTask<HttpPost, Integer, String> {
		
	    protected String doInBackground(HttpPost... hp)
	    {
	    	HttpClient hc=new DefaultHttpClient();
	    	try{
	    		HttpResponse hr=hc.execute(hp[0]);
	    		HttpEntity he=hr.getEntity();
	    		return filtrujOdpowiedz(he.getContent());
	    	} catch(Exception e){
	    		return e.toString();
	    	}
	    }
	    
	}
	
	private static String filtrujOdpowiedz(InputStream is)
	{
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(is, "iso-8859-2"), 8);
			StringBuilder sb=new StringBuilder();
			String l=br.readLine();
			int c=0; // tylko gdy na hostingu
			while(null!=l){
				/*
				 * gdy nie na hostingu zamienic kod zawarty pomiedzy
				 * "// <###" i "// ###>" z ponizszym
				 * sb.append(l);
				 */
				// <###
				if(1==c){
					sb.append(l);
					if(l.contains("</jwycieczki>")){
						c=0;
					}
				} else{
					if(l.contains("<jwycieczki>")){
						c=1;
					}
				}
				// ###>
				l=br.readLine();
			}
			br.close();
			String s=sb.toString();
			s=s.substring(s.lastIndexOf("<jwycieczki>")+1, s.indexOf("</jwycieczki>")); // filtrowanie danych z hostingu
			return s;
		} catch(Exception e){
			return e.toString();
		}
	}
	
}
