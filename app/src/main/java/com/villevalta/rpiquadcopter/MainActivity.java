package com.villevalta.rpiquadcopter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {


	SeekBar x_seek,y_seek,z_seek,r_seek,p_seek,t_seek;
	TextView x_value,y_value,z_value,r_value,p_value,t_value;
	EditText ip_input, port_input;
	Button connect_button;

	udpThread udpClient;

	byte x,y,z,r,p,t,cmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    x_seek = (SeekBar) findViewById(R.id.x_seek);
	    y_seek = (SeekBar) findViewById(R.id.y_seek);
	    z_seek = (SeekBar) findViewById(R.id.z_seek);
	    r_seek = (SeekBar) findViewById(R.id.r_seek);
	    p_seek = (SeekBar) findViewById(R.id.p_seek);
	    t_seek = (SeekBar) findViewById(R.id.t_seek);

	    x_value = (TextView) findViewById(R.id.x_value);
	    y_value = (TextView) findViewById(R.id.y_value);
	    z_value = (TextView) findViewById(R.id.z_value);
	    r_value = (TextView) findViewById(R.id.r_value);
	    p_value = (TextView) findViewById(R.id.p_value);
	    t_value = (TextView) findViewById(R.id.t_value);

	    ip_input = (EditText) findViewById(R.id.server_ip);
	    port_input = (EditText) findViewById(R.id.server_port);
	    connect_button = (Button) findViewById(R.id.connect);

		x_seek.setMax(255);
	    y_seek.setMax(255);
	    z_seek.setMax(255);
	    r_seek.setMax(255);
	    p_seek.setMax(255);
	    t_seek.setMax(255);

	    x_seek.setProgress(127);
	    y_seek.setProgress(127);
	    z_seek.setProgress(127);
	    r_seek.setProgress(127);
	    p_seek.setProgress(127);
	    t_seek.setProgress(127);

	    x_seek.setOnSeekBarChangeListener(this);
	    y_seek.setOnSeekBarChangeListener(this);
	    z_seek.setOnSeekBarChangeListener(this);
	    r_seek.setOnSeekBarChangeListener(this);
	    p_seek.setOnSeekBarChangeListener(this);
	    t_seek.setOnSeekBarChangeListener(this);

		connect_button.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		TextView value = null;
		if(seekBar == x_seek){ value = x_value; x = (byte)(progress - 127);}
		if(seekBar == y_seek){ value = y_value; y = (byte)(progress - 127);}
		if(seekBar == z_seek){ value = z_value; z = (byte)(progress - 127);}
		if(seekBar == r_seek){ value = r_value; r = (byte)(progress - 127);}
		if(seekBar == p_seek){ value = p_value; p = (byte)(progress - 127);}
		if(seekBar == t_seek){ value = t_value; t = (byte)(progress - 127);}



		value.setText(""+(progress-127));

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == connect_button.getId()){
			String ip = ip_input.getText().toString();
			int port = Integer.parseInt(port_input.getText().toString());

			Toast.makeText(this,ip+":"+port,Toast.LENGTH_LONG);
			udpClient = new udpThread(ip,port);
			udpClient.start();
		}
	}


	class udpThread extends Thread{

		DatagramSocket socket;
		InetAddress server;
		int server_port;

		public udpThread(String IP, int port){
			try {
				socket = new DatagramSocket();
				server = InetAddress.getByName(IP);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}


		@Override
		public void run() {
			while(true){
				try {

					// Create the message
					byte[] msg = new byte[8];

					msg[0] = cmd;
					msg[1] = x;
					msg[2] = y;
					msg[3] = z;
					msg[4] = r;
					msg[5] = p;
					msg[6] = t;

					int check = 0;
					for(int i = 0; i < msg.length -1; i++){
						check += msg[i] * i+1;
					}

					msg[7] = (byte)(check % 255);

					DatagramPacket packet = new DatagramPacket(msg, msg.length,server,server_port);
					socket.send(packet);

					cmd = 0;

					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}


}
