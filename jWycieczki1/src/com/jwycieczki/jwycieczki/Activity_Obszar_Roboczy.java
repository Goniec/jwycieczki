package com.jwycieczki.jwycieczki;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Obszar_Roboczy extends Activity {
	
	//zmienne do aplikacji
	private Context context;
	private int id_uzytkownika; // id_uzytkownika pobierane przy logowaniu
	private Intent intent;
	
	//listylokalne i wycieczek	
	HashMap<String, List<String>> listItemLokalna = new HashMap<String, List<String>>();
	ArrayList<String> expWiadomosci=null; //expandable list
	
	ArrayList<Wiadomosc> wiadomosci; // wiadomosci wysylane w tab lokalnej
	int startWiadomosci;// zakres ktore wiadomosci maja zostac pobrane
	int stopWiadomosci;
	ArrayList<Wycieczka> wycieczki;
	int startWycieczki;
	int stopWycieczki;
	
	int aktywnyObszarWiadomosci;
	
	int serverResponseCode = 0;
		
	//zmienne do wysylania wiadomosci
	Wiadomosc wiadomosc;
	Uzytkownik wlasciciel;
	
	// stale do zdjec
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private Uri fileUri; //przechowuje adres zjecia w telefonie
	
	//menu
	ImageButton popUpMenu;
	
	// odswierzanie
	
	
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obszar_roboczy);
        context=this.getApplicationContext();
        id_uzytkownika=this.getIntent().getIntExtra("IdUzytkownika", -1); 
       
        wycieczki=new ArrayList<Wycieczka>();
        wiadomosci=new ArrayList<Wiadomosc>();
        wiadomosc=new Wiadomosc();
        
        if(-1<id_uzytkownika){
        	
        	List<NameValuePair> p=new ArrayList<NameValuePair>();
	    	p.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));
	    	String dane=BazaDanych.wykonajSkrypt("profil_pobierz.php", p);
	    	JSONArray json;
	    	//pobieranie wlasciciela
	    	try {
				json=new JSONArray(dane);
				wlasciciel=new Uzytkownik(json.getString(0), json.getString(2), 
						json.getString(3), "maslo", json.getString(1), 1);
			} catch(JSONException e){}
	    	
        	// pobieranie listy wycieczek
	    	startWycieczki=1;
        	pobieranieListyWycieczek();
			
			//pobieranie wiadomosci lokalnych
        	startWiadomosci=1;
			pobieranieWiadomosciLokalnych();
        }
      
        ((ExpandableListView)findViewById(R.id.elvWiadomosciO)).setVisibility(View.GONE);
        aktywnyObszarWiadomosci=0;    	
    	uzupelnijLokalna();
    	
    	onClickWyloguj();
    	
    	// menu
    	popUpMenu=(ImageButton)findViewById(R.id.bPopupMenuO);
    	popUpMenu.setOnCreateContextMenuListener(this);
    	
    	// odswierzanie
    	
    	final Handler chwytak = new Handler();
    	final Runnable odswierzanie = new Runnable (){
    		public void run(){
				if (aktywnyObszarWiadomosci == 0) {
					if (pobieranieWiadomosciLokalnych()) {
						uzupelnijLokalna();
					}
				} else {
					if (wycieczki.get(aktywnyObszarWiadomosci).dodajWiadomosci()) {
						uzupelnijWycieczki(aktywnyObszarWiadomosci);
					}
				}

    		}
    	};
    	Timer timer= new Timer();
    	timer.scheduleAtFixedRate(new TimerTask(){
    		public void run(){
    			chwytak.post(odswierzanie);
    		}
    	}	, 1000, 40000);

	}
	
	void pobieranieListyWycieczek() {
		List<NameValuePair> php_parametry = new ArrayList<NameValuePair>();

		php_parametry.add(new BasicNameValuePair("id", String
				.valueOf(id_uzytkownika)));

		String dane = BazaDanych.wykonajSkrypt(
				"lista_wycieczek_uzytkownik.php", php_parametry);
		JSONArray json;
		try {
			json = new JSONArray(dane);
			for (int i = 0; i < json.length(); ++i) {
				Wycieczka wycieczka=new Wycieczka(json.getJSONArray(i).getString(0),
						json.getJSONArray(i).getString(1));
				wycieczka.dodajWiadomosci();
				wycieczki.add(wycieczka);
			}
		} catch (JSONException e) {
		}
	}

	private boolean pobieranieWiadomosciLokalnych() {
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("id", String.valueOf(id_uzytkownika)));

		String stop = BazaDanych.wykonajSkrypt(	"liczba_wiadomosci_uz.php", p);
		stop=stop.substring(2,stop.length()-2);
		stopWiadomosci = Integer.parseInt(stop);
		if (stopWiadomosci > startWiadomosci) { // pobieranie tylko tych co nie sa w bazie
			p.add(new BasicNameValuePair("start", String.valueOf(startWiadomosci)));
			p.add(new BasicNameValuePair("stop", String.valueOf(stopWiadomosci)));
			String dane = BazaDanych.wykonajSkrypt("wiadomosci_uzytkownik.php",	p);
			JSONArray json;
			try {
				json = new JSONArray(dane);
				for (int i = 0; i < json.length(); ++i) {
					wiadomosci.add(new Wiadomosc(json.getJSONArray(i)
							.getString(3),// tresc
							json.getJSONArray(i).getString(1),// odbiorca
							json.getJSONArray(i).getString(2), // treesc
							json.getJSONArray(i).getString(0))); // adresat
				}
			} catch (JSONException e) {	}
			startWiadomosci=stopWiadomosci;
			return true;
		}
		else{
			return false;
		}

	}
    
	/*------------------GUZIKI------------------*/

    public void onClick_Activity_Profil(View v)
    {
    	intent=new Intent(context, Activity_Profil.class);
    	intent.putExtra("IdUzytkownika", id_uzytkownika);
		startActivity(intent);
    }
	
	public void onClick_Activity_Galeria(View v)
    {
    	intent=new Intent(context, Activity_Galeria.class);
    	intent.putExtra("IdUzytkownika", id_uzytkownika);
		startActivity(intent);
    }
	
	public void onClick_Activity_Ustawienia(View v)
    {
    	intent=new Intent(context, Activity_Ustawienia.class);
    	intent.putExtra("IdUzytkownika", id_uzytkownika);
		startActivity(intent);
    }
	
	public void onClick_Activity_Mapa(View v)
    {
    	intent=new Intent(context, Activity_Mapa.class);
    	intent.putExtra("IdUzytkownika", id_uzytkownika);
		startActivity(intent);
    }
    
	public void onClickZdjecie(View v){
		Toast.makeText(context, "Cykam zdjecie",
                Toast.LENGTH_LONG).show();
		cyknijFoto();
	}
	
	public void onClickWyslij(View v)
	{
		//Toast.makeText(context, "Wysylam wiadomosc",
        //        Toast.LENGTH_LONG).show();
		String tresc=((TextView)findViewById(R.id.etWiadomoscO)).getText().toString();
		Uzytkownik adresat= wlasciciel;
		ArrayList<Uzytkownik> odbiorcy= new ArrayList<Uzytkownik>();
		
		String sOdbiorcy=((TextView)findViewById(R.id.etOdbiorcyO)).getText().toString();

		String [] sTab=(sOdbiorcy.split(","));
		ArrayList<String> odbiorcyS=new ArrayList<String>();
		for (String ss : sTab ){
			ss=ss.trim();
			odbiorcyS.add(ss); // odbiorcy zostali podzieleni na stringi
		}
		//data
		SimpleDateFormat dataWyslaniaDateFormat= new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");			
		String data_wyslania=dataWyslaniaDateFormat.format((Calendar.getInstance()).getTime());

		
		for(String odbiorca : odbiorcyS){
			List<NameValuePair> p=new ArrayList<NameValuePair>();
			p.add(new BasicNameValuePair("login", odbiorca));
			String dane=BazaDanych.wykonajSkrypt("wiadomosci_uz_pob.php", p);
			String id_odbiorcy;
    	//pobieranie odbiorcy

			id_odbiorcy=dane.substring(2, dane.length()-2);
			if (Integer.parseInt(id_odbiorcy)>0){
				p=new ArrayList<NameValuePair>();
				p.add(new BasicNameValuePair("id_nadawcy", String.valueOf(id_uzytkownika)));
				p.add(new BasicNameValuePair("id_odbiorcy", id_odbiorcy));				
				p.add(new BasicNameValuePair("data_wyslania", data_wyslania));
				p.add(new BasicNameValuePair("tresc", tresc));
    	
				dane=BazaDanych.wykonajSkrypt("wiadomosci_uz_wys.php", p);
				pobieranieWiadomosciLokalnych();
				
				uzupelnijLokalna();
			}
		}
    	
	}
	
	private void onClickWyloguj(){
		TextView wyloguj= (TextView) findViewById(R.id.tvWylogujO);
		wyloguj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent=new Intent(context, Activity_Main.class);
				startActivity(intent);
				
			}
		});
	}
	
	/************ SEKCJA MENU**************/
	
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, view, menuInfo);
		createMenu(menu);
	}
	
	public boolean onContextItemSelected(MenuItem item){
		return menuChoise(item);
	}
	
	private void createMenu(Menu menu){
		int i=0;
		menu.add(0,i,i,"Wiadomosci");
		for(Wycieczka w : wycieczki){
			i++;
			menu.add(0,i,i,w.nazwa);
		}
	}
	
	private boolean menuChoise(MenuItem item){
		aktywnyObszarWiadomosci=item.getItemId();
		if(item.getItemId() !=0){
			uzupelnijWycieczki(item.getItemId());

			return true;
		}
		else if(item.getItemId()==0){
			uzupelnijLokalna();
			
			return true;
		}
		return false;
	}
	
	
	
	/************ SEKCJA WYSWIETLANIA ********/

	void uzupelnijWycieczki(int position) {
		ListView lvWycieczki = (ListView) findViewById(R.id.lvWycieczkiO);
		ArrayList<String> tabWycieczki = new ArrayList<String>();
		for (Wiadomosc w : wycieczki.get(position - 1).tabWycieczki) {
			tabWycieczki.add(0, w.tresc);
		}
		ArrayAdapter<String> aaWycieczki = new ArrayAdapter<String>(context,
				R.layout.text_view_zakladki_profil, tabWycieczki);
		lvWycieczki.setAdapter(aaWycieczki);
		
		((ExpandableListView) findViewById(R.id.elvWiadomosciO)).setVisibility(View.GONE);
		((ListView) findViewById(R.id.lvWycieczkiO)).setVisibility(View.VISIBLE);
	}
	
	void uzupelnijLokalna() {
		ExpandableListView elvWiadomosci = (ExpandableListView) findViewById(R.id.elvWiadomosciO);
		ObRoboczyExpandableListAdapter expListA;
		expListA = new ObRoboczyExpandableListAdapter(
				context, wlasciciel, wiadomosci);
		elvWiadomosci.setAdapter(expListA);

		((ListView) findViewById(R.id.lvWycieczkiO)).setVisibility(View.GONE);
		((ExpandableListView) findViewById(R.id.elvWiadomosciO)).setVisibility(View.VISIBLE);
	}


    
    
    /***********SEKCJA DO ZDJÊÆ **************/
    private void cyknijFoto(){
    	Intent intent_= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri= getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
    	intent_.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
    	
    	startActivityForResult(intent_, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	if (resultCode==RESULT_OK){
    		podgladFoty();
    	}
    	else if (resultCode==RESULT_CANCELED){
    		Toast.makeText(getApplicationContext(),
                    "Anulowano", Toast.LENGTH_SHORT)
                    .show();
    	}
    	else{
    		Toast.makeText(getApplicationContext(),
                    "Wystapil nieznany blad", Toast.LENGTH_SHORT)
                    .show();
    	}
    }
    
    private void podgladFoty(){
    	final Dialog dialog= new Dialog(Activity_Obszar_Roboczy.this);
    	dialog.setContentView(R.layout.dialog_pokaz_zdjecie);
    	ImageView foto= (ImageView) dialog.findViewById(R.id.ivFotoO);
    	Button wyslij= (Button) dialog.findViewById(R.id.bZapiszZdjO);
    	Button powrot =(Button) dialog.findViewById(R.id.bPowrotZdjO);
    	EditText nazwa =(EditText) dialog.findViewById(R.id.etNazwaZdjO);
    	String dataZdj = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
    	final String nazwaZdj=wlasciciel.login+"_"+dataZdj;
    	nazwa.setText(nazwaZdj);
    	try{
    		BitmapFactory.Options opcje= new BitmapFactory.Options();
    		opcje.inSampleSize=8;
    		final Bitmap bitmapa= BitmapFactory.decodeFile(fileUri.getPath(),opcje);
    		foto.setImageBitmap(bitmapa);
    	}catch (NullPointerException e){
    		e.printStackTrace();
    	}
    	wyslij.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO wyslanie zdjecia
				/*******************
				 *  Miejsce na kod wysylajacy zdjecie do bazy
				 *   sciezka w fileUri
				 *   nazwa w nazwaZdj
				 */
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						 dodajZdjecie(fileUri + "" + nazwaZdj);
					}

					private int dodajZdjecie(final String source) {
						// TODO Auto-generated method stub
						
						String fileName = source;
						
						 HttpURLConnection conn = null;
				         DataOutputStream dos = null; 
				         String lineEnd = "\r\n";
				         String twoHyphens = "--";
				         String boundary = "*****";
				         int bytesRead, bytesAvailable, bufferSize;
				         byte[] buffer;
				         int maxBufferSize = 1 * 1024 * 1024;
				         File sourceFile = new File(source); 
				         
				         if (!sourceFile.isFile()) {
				               
				               dialog.dismiss();
				                
				               Log.e("uploadFile", "Plik nie istnieje : "
				                                   +source);
				                
				               runOnUiThread(new Runnable() {
				                   public void run() {
				                	   Toast.makeText(context, source, Toast.LENGTH_LONG).show();
				                   }
				               });
				                
				               return 0;
				           
				          }
				         else
				         {
				        	 try {
				        		 FileInputStream fileInputStream = new FileInputStream(sourceFile);
				        		 URL url = new URL("http://jwycieczki.ugu.pl/dodaj_zdjecie.php");
				        		 
				        		 
				        		 // otwieranie polaczenia
				        		 conn = (HttpURLConnection) url.openConnection();
				        		 conn.setDoInput(true); 
				                 conn.setDoOutput(true); 
				                 conn.setUseCaches(false);
				                 conn.setRequestMethod("POST");
				                 conn.setRequestProperty("Connection", "Keep-Alive");
				                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				                 conn.setRequestProperty("uploaded_file", fileName); 
				                 
				                 dos = new DataOutputStream(conn.getOutputStream());
				                 
				                 dos.writeBytes(twoHyphens + boundary + lineEnd);
				                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="   + fileName + "" + lineEnd);
				                    
				                 dos.writeBytes(lineEnd);
				          
	
				                 bytesAvailable = fileInputStream.available();
				          
				                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
				                 buffer = new byte[bufferSize];
				          

				                 bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
				                 
				                 while (bytesRead > 0) {
				                        
				                     dos.write(buffer, 0, bufferSize);
				                     bytesAvailable = fileInputStream.available();
				                     bufferSize = Math.min(bytesAvailable, maxBufferSize);
				                     bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
				                      
				                    }
		
				                   dos.writeBytes(lineEnd);
				                   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				          

				                   serverResponseCode = conn.getResponseCode();
				                   String serverResponseMessage = conn.getResponseMessage();
				                   
				                   if(serverResponseCode == 200){
				                        
				                       runOnUiThread(new Runnable() {
				                            public void run() {    
				                                Toast.makeText(Activity_Obszar_Roboczy.this, "Udalo sie wgrac zdjecie",
				                                             Toast.LENGTH_SHORT).show();
				                            }
				                        });               
				                   } 
				                   
				                   fileInputStream.close();
				                   dos.flush();
				                   dos.close();
				                 
				        	 } catch(Exception ex)
				        	 {
				        		  dialog.dismiss(); 
				                 // e.printStackTrace();
				                   
				                  runOnUiThread(new Runnable() {
				                      public void run() {
				                          Toast.makeText(Activity_Obszar_Roboczy.this, "Blad",
				                                  Toast.LENGTH_SHORT).show();
				                      }
				                  });
				        	 }
				        
				         }
				         dialog.dismiss();      
			             return serverResponseCode; 
					}
		
					
				}).start();
				
				dialog.dismiss();
				
			}
		});
	
    	powrot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
    	
    	dialog.show();
    }
    
    public Uri getOutputMediaFileUri(int type){
    	String katWycieczki="";
    	if ( !wycieczki.isEmpty()){
    		katWycieczki=wycieczki.get(0).nazwa;
    	}
    	return Uri.fromFile(getOutputMediaFile(type, katWycieczki));
    }
    
    private static File getOutputMediaFile(int type, String katWycieczki){
    	File katalogGlowny = new File(Environment.
    			getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
    			"jWycieczki");
    	if (!katalogGlowny.exists()){
    		if(!katalogGlowny.mkdirs()){
    			Log.d("jWycieczki", "Oops! Blad podczas tworzenia katalogu jWycieczki");
                return null;
    		}
    	}
    	File katalogWycieczki=katalogGlowny;
		if (!katWycieczki.equals("")) {
			katalogWycieczki = new File(katalogGlowny, katWycieczki);
			if (!katalogWycieczki.exists()) {
				if (!katalogWycieczki.mkdir()) {
					Log.d("jWycieczki", "Blad podczas tworzenia katalogu "
							+ katWycieczki);
					return null;
				}
			}
		}
    	
    	String nazwaZdj = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
    	
    	File plikZdj= new File(katalogWycieczki.getPath()+File.separator+"IMG_"+nazwaZdj+".jpg");
    	return plikZdj;
    }
	
}
