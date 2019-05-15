package com.example.asus.myapplication;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    private static final String TAG ="RatelistActivity" ;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_rate_list);
        List<String> list1 = new ArrayList<String>();
        for (int i = 1; i < 100; i++) {
            list1.add("item" + i);
        }
        //第一步，构造adapter 对象
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list1);
        setListAdapter(adapter);
        Thread t=new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==7){
                    List<String> list2= (List<String>) msg.obj;
                    ListAdapter adapter=new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };
    }


    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> retlist=new ArrayList<String>();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/").get();
            Log.d(TAG, "run: " + doc.title());
            Elements tables=doc.getElementsByTag("table");
            /*for(Element table:tables){
                Log.d(TAG, "run: table["+i+"]="+table);
                i++;
            }*/
            Element table1=tables.get(0);
            //Log.d(TAG, "run: table6="+table6);
            //获取td中的数据
            Elements tds=table1.getElementsByTag("td");
            for (int i=0;i<tds.size();i+=7){
                //获取第一列数据
                Element td1=tds.get(i);
                //获取第1+5列数据
                Element td2=tds.get(i+5);
                Log.d(TAG, "run: "+td1.text()+"==>"+td2.text());
                String str1=td1.text();
                String val=td2.text();
                retlist.add(str1+"==>"+val);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7) ;
        msg.obj=retlist;
        handler.sendMessage(msg);

    }
}

