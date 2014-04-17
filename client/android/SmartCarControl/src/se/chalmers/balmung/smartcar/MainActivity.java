/** Main class. This establishes a connection to the server, sends to the server
 *  and also listens to the camera feed.
 * @author Team Pegasus (simeon)
 */

package se.chalmers.balmung.smartcar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import se.chalmers.balmung.smartcar.JoystickView;
import se.chalmers.balmung.smartcar.JoystickView.OnJoystickMoveListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
    private SharedPreferences settings;
	
	private Socket socket;
	private String serverPort;
	private String serverIp;	
	private String prevMsg = "";
    private JoystickView joystick;
	
	ImageView videoView;
    private int displayWidth;
    private int displayHeight;
	private boolean videoPlaying = false;	
	Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            new DownloadImageTask(videoView).execute("http://" + serverIp + "/pic.jpg");
            videoView.getLayoutParams().width = displayWidth;
    		videoView.getLayoutParams().height = displayHeight;
            
            timerHandler.postDelayed(this, 40);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		videoView = (ImageView) findViewById(R.id.videoView);
		displayWidth = videoView.getLayoutParams().width;
		displayHeight = videoView.getLayoutParams().height;
		
		joystick = (JoystickView) findViewById(R.id.joystickView);

		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
            	// Scale speed
            	if (angle < -90 || angle > 90) {
            		power *= -1;
            	}
            	
            	// X to Y adapter
            	if (angle > 90) {
            		angle = 180 - angle;
            	} else if (angle < -90) {
            		angle = -(180 + angle);
            	}
                	send("!" + angle + ":" + power + "*");
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }
	
	@Override
	protected void onResume() {
		super.onResume();	
		
		settings = getSharedPreferences("DefaultPrefs", 0);
		serverIp = settings.getString("serverIp", "10.0.2.2");
		serverPort = settings.getString("serverPort", "8787");
		
		new Thread(new ClientThread()).start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}
	
	public void onAction(View view) {
		String message = "";
		send(message);
	}
	
	public void onVideo(View view) {	
		videoView.setImageResource(android.R.color.transparent);
		
		if (videoPlaying == true) {
			timerHandler.removeCallbacks(timerRunnable);
			videoPlaying = false;
		} else {
			videoPlaying = true;
			new DownloadImageTask(videoView).execute("http://" + serverIp + "/pic.jpg");
			timerHandler.postDelayed(timerRunnable, 0);					
		}
	}

	public void send(String message) {
		if (!prevMsg.contains(message)) {
			prevMsg = message;
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				out.println(message);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class ClientThread implements Runnable {

		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				socket = new Socket(serverAddr, Integer.parseInt(serverPort));
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
}
