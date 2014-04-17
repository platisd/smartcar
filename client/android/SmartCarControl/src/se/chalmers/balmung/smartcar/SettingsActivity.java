/**
 * This class handles what happens in the setting page
 * @author(simeon & dimi)
 */

package se.chalmers.balmung.smartcar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	SharedPreferences settings;
	String serverIp;
	String serverPort;
	EditText serverIpField;
	EditText serverPortField;
//	TextView connectedDeviceIpField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		settings = getSharedPreferences("DefaultPrefs", 0);
		serverIp = settings.getString("serverIp", connectedDeviceIp());
	//	serverIp = settings.getString("serverIp", "10.0.2.2");
		serverPort = settings.getString("serverPort", "8787");
		serverIpField = (EditText) findViewById(R.id.serverIpField);
		serverPortField = (EditText) findViewById(R.id.serverPortField);
	//	connectedDeviceIpField =(TextView)findViewById(R.id.connectedDeviceIpField);
	//	connectedDeviceIpField.setText(connectedDeviceIp());




		//		Toast.makeText(this, serverIp, Toast.LENGTH_LONG).show();
		serverIpField.setText(serverIp);
		serverPortField.setText(serverPort);
	}

	public void saveAction(View v) {
		Editor settingsEditor = settings.edit();
		settingsEditor.putString("serverIp", serverIpField.getText().toString());
		settingsEditor.putString("serverPort", serverPortField.getText().toString());
		settingsEditor.apply();
		this.finish();
	}

	public String connectedDeviceIp(){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				String[] tokens = inputLine.split(" +");
				if (tokens != null && tokens[0].length() > 7 && tokens[3].equalsIgnoreCase("e8:4e:06:19:c1:1f")){
					//if it's an ip and then the mac address of our car, use it
					return tokens[0];
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "192.168.";
	}
}
