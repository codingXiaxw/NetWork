package com.example.codingboy.networktestpractice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputMessage;
    private static final int SHOW_RESPONSE=0;
    private Button sendMessage;
    private ListView lv;
    private ArrayAdapter<String>adapter;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    adapter.add(response.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputMessage=(EditText)findViewById(R.id.inputMessage);
        sendMessage=(Button)findViewById(R.id.sendMessage);
        lv=(ListView)findViewById(R.id.list);

        sendMessage.setOnClickListener(this);
        adapter=new ArrayAdapter<String>(this,R.layout.cell,R.id.list_textView);
        lv.setAdapter(adapter);


    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.sendMessage)
        {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   HttpURLConnection con=null;
                   try
                   {
                       URL url=new URL(inputMessage.getText().toString());
                       con=(HttpURLConnection)url.openConnection();
                       con.setRequestMethod("GET");
                       con.setConnectTimeout(8000);
                       con.setReadTimeout(8000);
                       if(con.getResponseCode()==200) {
                           InputStream in = con.getInputStream();
                           BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                           StringBuilder response = new StringBuilder();
                           String line;
                           while ((line = reader.readLine()) != null) {
                               response.append(line);
                           }

                           Message message = new Message();
                           message.what = SHOW_RESPONSE;
                           message.obj =response.toString();
                           handler.sendMessage(message);


                       }
                   }catch (Exception e)
                   {
                       e.printStackTrace();
                   }finally {
                       if(con!=null)

                           con.disconnect();
                   }
               }
           }).start();
        }
    }


}
