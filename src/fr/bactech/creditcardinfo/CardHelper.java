package fr.bactech.creditcardinfo;

import java.io.IOException;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;

public class CardHelper {

	public static CardHelper cardhelper = null;
	private static IsoDep card; 			// object SmartCard CB
	private static String response;			// answer to APDUs
	public static boolean RESPONSE_AVAILABLE = false; 	// booleen de gestion de la response
	private static String[] responseTab = new String[12];
	private static String pdolResponse = "";
	
	// APDUs
	public static byte selectCommand[] = { 0x00, (byte) 0xA4, 0x04, 0x00, 0x07, (byte) 0xA0, 0x00, 0x00, 0x00, 0x42, 0x10, 0x10 };
	public static byte pdol[] = { (byte) 0x80, (byte) 0xA8, (byte) 0x00, (byte) 0x00, 
			(byte) 0x23,
			(byte) 0x83, 
			(byte) 0x21,
			(byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
			(byte) 0x00, (byte) 0x02, (byte) 0x50, (byte) 0x00, (byte) 0x00, 
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09,
			(byte) 0x12, (byte) 0x12, (byte) 0x31, (byte) 0x00, (byte) 0xE4, 
			(byte) 0xEC, (byte) 0x9E, (byte) 0x52, (byte) 0x00
			};
	//	80 A8 00 00 23 83 21 / 32 00 00 00 00 / 00 00 00 00 00 / 00 00 00 00 00 / 00 02 50 00 00 / 00 00 00 09  / 12 12 31 00 E4 / EC 9E 52 00 
	private static byte read_SFI_11_data[] = {(byte)0x00, (byte)0xB2, (byte)0x01, (byte)0x0C, (byte)0x00}; // Record 01 / SFI 01
	private static byte read_SFI_12_data[] = {(byte)0x00, (byte)0xB2, (byte)0x02, (byte)0x0C, (byte)0x00}; // Record 02 / SFI 01
	private static byte read_SFI_13_data[] = {(byte)0x00, (byte)0xB2, (byte)0x03, (byte)0x0C, (byte)0x00}; // Record 03 / SFI 01
	private static byte read_SFI_14_data[] = {(byte)0x00, (byte)0xB2, (byte)0x04, (byte)0x0C, (byte)0x00}; // Record 04 / SFI 01
	private static byte read_SFI_21_data[] = {(byte)0x00, (byte)0xB2, (byte)0x01, (byte)0x14, (byte)0x00}; // Record 01 / SFI 02
	private static byte read_SFI_22_data[] = {(byte)0x00, (byte)0xB2, (byte)0x02, (byte)0x14, (byte)0x00}; // Record 02 / SFI 02
	private static byte read_SFI_23_data[] = {(byte)0x00, (byte)0xB2, (byte)0x03, (byte)0x14, (byte)0x00}; // Record 03 / SFI 02
	private static byte read_SFI_24_data[] = {(byte)0x00, (byte)0xB2, (byte)0x04, (byte)0x14, (byte)0x00}; // Record 04 / SFI 02
	private static byte read_SFI_31_data[] = {(byte)0x00, (byte)0xB2, (byte)0x01, (byte)0x1C, (byte)0x00}; // Record 01 / SFI 03
	private static byte read_SFI_32_data[] = {(byte)0x00, (byte)0xB2, (byte)0x02, (byte)0x1C, (byte)0x00}; // Record 02 / SFI 03
	private static byte read_SFI_33_data[] = {(byte)0x00, (byte)0xB2, (byte)0x03, (byte)0x1C, (byte)0x00}; // Record 03 / SFI 03
	private static byte read_SFI_34_data[] = {(byte)0x00, (byte)0xB2, (byte)0x04, (byte)0x1C, (byte)0x00}; // Record 04 / SFI 03
	
	public CardHelper() {
		
	}

	public static CardHelper getCardHelper() {
		if (cardhelper == null) {
			cardhelper = new CardHelper();
		}
		return cardhelper;
	}
	
	public static void setCard(Tag tag)
	{
		card = IsoDep.get(tag); 								// on récupère l'object smartcard
	}
	
	public static void connect() {
		if (!card.isConnected()) {
			try {
				card.connect(); // connexion à la carte
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void selectCardApplication() throws IOException {
		card.transceive(selectCommand); 	// select applet
	}
	
	private static void readAllData() throws IOException
	{
		responseTab[0] = CardHelper.toHex(card.transceive(read_SFI_11_data));
		responseTab[1] = CardHelper.toHex(card.transceive(read_SFI_12_data));
		responseTab[2] = CardHelper.toHex(card.transceive(read_SFI_13_data));
		responseTab[3] = CardHelper.toHex(card.transceive(read_SFI_14_data));
		responseTab[4] = CardHelper.toHex(card.transceive(read_SFI_21_data));
		responseTab[5] = CardHelper.toHex(card.transceive(read_SFI_22_data));
		responseTab[6] = CardHelper.toHex(card.transceive(read_SFI_23_data));
		responseTab[7] = CardHelper.toHex(card.transceive(read_SFI_24_data));
		responseTab[8] = CardHelper.toHex(card.transceive(read_SFI_31_data));
		responseTab[9] = CardHelper.toHex(card.transceive(read_SFI_32_data));
		responseTab[10] = CardHelper.toHex(card.transceive(read_SFI_33_data));
		responseTab[11] = CardHelper.toHex(card.transceive(read_SFI_34_data));
	}
	
	public static void searchForPANTag() throws IOException
	{
		readAllData();	// on lit toutes les pistes de la carte
		for(int i = 0; i< responseTab.length ; i++)
		{
			if(responseTab[i].endsWith("90 00"))
			{
				if(responseTab[i].contains("57 13"))
				{
					pdolResponse = responseTab[i];
					RESPONSE_AVAILABLE = true;
					return;
				}
			}
			else
			{
				getResponse();
			}
		}
	}
	
	public static String sendPdol() throws IOException {
		String response = "";
		try {
			response = toHex(card.transceive(pdol)); // init transac
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public static void getResponse() throws IOException {
		response = sendPdol();
		
		if (response != null)
		{
			if(response.endsWith("90 00"))
			{
				pdolResponse = response;
				RESPONSE_AVAILABLE = true;
			}
		}
	}
	
	public static String toHex(byte[] bytes) {
		String temp = "";
		for (int index = 0; index < bytes.length; index++) {
			temp = temp + " " + String.format("%02x", bytes[index]);
		}
		return temp;
	}
	
	public static String getPAN()
	{
// 		ex : 77 41 82 02 20 00 57 13 49 78 XX XX XX XX XX XX dY YM M2 01 21 84 57 19 60 00 0f 5f 34 01 10 5f 20 02
//		20 2f 9f 6c 02 10 00 9f 26 08 2f 14 cf b7 a2 e2 70 fd 9f 36 02 00 72 9f 10 07 06 38 0a 03 80 00 00 90 00
		String[] split1 = pdolResponse.split("57 13 ");
		String[] split2 = split1[1].split("d");
		String PAN = split2[0];
//		ex : XX XX XX XX XX XX XX XX
		String temp[] = PAN.split(" ");
		PAN = temp[0] + temp[1] + " " + temp[2] + temp[3] + " " + temp[4] + temp[5] + " " + temp[6] + temp[7];
//		ex : XXXX XXXX XXXX XXXX
		return PAN;
	}
	
	public static String getBank()
	{
// 		ex : 77 41 82 02 20 00 57 13 49 78 XX XX XX XX XX XX dY YM M2 01 21 84 57 19 60 00 0f 5f 34 01 10 5f 20 02
//		20 2f 9f 6c 02 10 00 9f 26 08 2f 14 cf b7 a2 e2 70 fd 9f 36 02 00 72 9f 10 07 06 38 0a 03 80 00 00 90 00
		String[] split1 = pdolResponse.split("57 13 ");
		String[] split2 = split1[1].split("d");
		String bank = split2[0];
//		ex : XX XX XX XX XX XX XX XX
		String temp[] = bank.split(" ");
		bank = temp[0] + temp[1];
//		ex : XXXX
		if(bank.equals("4970"))
			return "Banque Postale";
		if(bank.equals("4971"))
			return "Banque Populaire / HSBC / Banque Accord";
		if(bank.equals("4972"))
			return "Crédit Lyonnais (LCL)";
		if(bank.equals("4973"))
			return "Société Générale";
		if(bank.equals("4974"))
			return "BNP";
		if(bank.equals("4975"))
			return "La Bred";
		if(bank.equals("4976"))
			return "Sofinco";
		if(bank.equals("4978"))
			return "Caisse d'Épargne";
		return "Banque inconnue";
	}
	
	public static String getExpDate()
	{
		String[] split1 = pdolResponse.split("57 13");
		String[] split2 = split1[1].split("d");
		String date = split2[1].substring(0, 6);
		// ex : 1 60 9
		date = date.replace(" ", "");
		// ex : 1609
		date = date.substring(2,4) + " / 20" + date.substring(0, 2);
		// ex : 09 / 2016
		return date;
	}
	
	/*public static String getCardHolderName(String response)
	{
		String[] split1 = response.split("5f 20 ");
		String[] split2 = split1[1].split(" ");
		String taille = split2[0];
		String cardholdername_hex = "";
		for(int i = 0 ; i < Integer.parseInt(taille) ; i++)
		{
			cardholdername_hex = cardholdername_hex + split2[i+1];
		}
		StringBuilder output = new StringBuilder();
		for(int j = 0 ; j < cardholdername_hex.length() ; j=j+2)
		{
			String str = cardholdername_hex.substring(j, j+2);
			output.append((char)Integer.parseInt(str,16));
		}
		return output.toString();
	}*/
}
