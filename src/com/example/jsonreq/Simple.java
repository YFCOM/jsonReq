package com.example.jsonreq;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class Simple extends Activity {
	
//	public static String IP = "http://192.168.1.101:8080/Json/Simple";
//	public static String IP = "http://202.108.5.132/testAndroid.php";
	public static String IP = "http://1.appprogram.sinaapp.com/testAndroid.php";
	private TextView content;
	private Button show;
	private JSONObject jsonObject;
	
	ImageView imageView;
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			RequestQueue mQueue = Volley.newRequestQueue(Simple.this);
			String url = null;
			if (msg.what == 0x123) {
				String s = msg.getData().getString("content");
				Log.i("Simple", "msg = " + s);
				try {
					jsonObject = new JSONObject(s);
					if(jsonObject.has("URL")){
						content.setText(jsonObject.getString("URL"));
						url = jsonObject.getString("URL");
					}
					ImageRequest imageRequest = new ImageRequest(
							url,
							new Response.Listener<Bitmap>() {
								@Override
								public void onResponse(Bitmap response) {
									imageView.setImageBitmap(response);
								}
							}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									imageView.setImageResource(R.drawable.ic_launcher);
								}
							});
					mQueue.add(imageRequest);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple);
		
		show = (Button)findViewById(R.id.show);
		content = (TextView)findViewById(R.id.content);
		
		imageView = (ImageView) findViewById(R.id.imageView);
		show.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
					public void run() {
						HttpClient client = new DefaultHttpClient();
						HttpPost post = new HttpPost(IP);
//						JSONObject jsonObject = new JSONObject();
						try {
//							jsonObject.put("index", 0);
//							jsonObject.put("name", "Jack");
//							StringEntity entity = new StringEntity(jsonObject.toString());
							List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
							        nvps.add(new BasicNameValuePair("name", "linzhouzhi"));  
							        nvps.add(new BasicNameValuePair("password", "secret"));

							post.setEntity(new UrlEncodedFormEntity(nvps));
							HttpResponse response = client.execute(post);
							int tap = response.getStatusLine().getStatusCode();
							if (tap == 200) {
								Message msg = new Message();
								msg.what = 0x123;
								Bundle bundle = new Bundle();
								InputStream is = response.getEntity().getContent();
								InputStreamReader isr = new InputStreamReader(is);
								
								String temp = "";
								char[] c = new char[100];
								int i = 0;
								while((i = isr.read(c)) != -1){
									temp += new String(c);
								}
								Log.i("Simple", "temp = " +temp);
								bundle.putString("content", temp.trim());
								msg.setData(bundle);
								handler.sendMessage(msg);
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			}
		});
	}

}
