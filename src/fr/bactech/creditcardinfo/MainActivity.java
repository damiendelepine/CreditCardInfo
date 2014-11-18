package fr.bactech.creditcardinfo;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private NfcAdapter mAdapter;
	private CardHelper cardhelper;
	private String PAN;
	private String expDate;
	private String bank;
	private Tag tag; 				// objet NFC carte bleue
	private TextView tv1, banktv, numerotv, datetv;
	private Button button;
	private Dialog dialog;
	private EditText edit;
	private NfcAdapter mNfcAdapter;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		cardhelper = CardHelper.getCardHelper();
		
		// Create custom dialog object
	    dialog = new Dialog(MainActivity.this);
	    // Include dialog.xml file
	    dialog.setContentView(R.layout.dialog);
	    final View dialogview = findViewById(R.layout.dialog);
		
		tv1 = (TextView)findViewById(R.id.tv1);
		banktv = (TextView)dialog.findViewById(R.id.bank);
		numerotv = (TextView)dialog.findViewById(R.id.numero);
		datetv = (TextView)dialog.findViewById(R.id.date);
		edit = (EditText)dialog.findViewById(R.id.edittext);
		button = (Button)dialog.findViewById(R.id.button);
		
		button.setOnClickListener(new OnClickListener() {
		     @Override
		     public void onClick(View v) {
		      
		    	 edit.setText("");	// clear le champs
		    	 dialog.dismiss(); // ferme la fentre
		      
		    	 // fermer le clavier
		    	 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    	 imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
		     }
		});
		
		nfc_init();	// initialisation du NFC
	}

	@Override
	public void onResume() {
		super.onResume();

		setupForegroundDispatch(this, mAdapter);
	}

	@Override
	public void onPause() {
		super.onPause();

		stopForegroundDispatch(this, mAdapter);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		try {
			tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); 	// on récupère l'objet tag
			CardHelper.setCard(tag); 								// on donne l'objet tag au helper
			CardHelper.connect(); 									// connection to card
			CardHelper.selectCardApplication();						// select card java applet
			CardHelper.searchForPANTag();							// recherche les informations sur les pistes lisibles
			if(CardHelper.RESPONSE_AVAILABLE){
				
				// récupération des infos
				PAN = CardHelper.getPAN(); 							// récupération et affichage du numéro de carte
				expDate = CardHelper.getExpDate(); 					// récupération et affichage de la date d'expiration de la carte
				bank = CardHelper.getBank();						// récupération et affichage de la banque émettrice

			    // affichage des infos
			    banktv.setText(" " + bank);
			    numerotv.setText(" " + PAN);
			    datetv.setText(" " + expDate);
			    dialog.setTitle("Validez votre achat");
			    dialog.show();

			    // met le focus sur l'edittext
			    edit.requestFocus();
			    
			    // ouvre le clavier
			    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
			}
			else
			{
				// affichage
				tv1.setText("Une erreur est survenue lors de la lecture de la carte.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void nfc_init()
	{
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // on vérifie si le hardware NFC est présent sur le device
        if (mNfcAdapter == null) {
        	// si le device n'a pas de module NFC, on quitte
            Toast.makeText(this, "This device doesn't support NFC !", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // on vérifié si le NFC est activé
		if (!mNfcAdapter.isEnabled()) {
			// si le NFC n'est pas activé on indique qu'il faut le faire
			Toast.makeText(this, "NFC has to be enabled !", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
		} else {
			// tout est OK, on attend la suite
			//Toast.makeText(this, "NFC enabled and waiting for a tag to read !", Toast.LENGTH_LONG).show();
		}
	}
	
	public static void setupForegroundDispatch(final Activity activity,NfcAdapter adapter) 
	{
		final Intent intent = new Intent(activity.getApplicationContext(),activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
		IntentFilter[] filters = new IntentFilter[1];
		filters[0] = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		String[][] techList = new String[][] { new String[] { IsoDep.class.getName() },
											   new String[] { NdefFormatable.class.getName() }
		};
		adapter.enableForegroundDispatch(activity, pendingIntent, filters,techList);
	}

	public static void stopForegroundDispatch(final Activity activity,
			NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}