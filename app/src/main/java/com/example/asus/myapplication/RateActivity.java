package com.example.asus.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {
    EditText rmb;
    TextView show;
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    public final String TAG = "RateActivity";
    Handler handler;
    private  String updateDate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);
        //获取sp中的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        //或者 SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
        updateDate=sharedPreferences.getString("update_rate","");

        //获取当天日期
        Date today= Calendar.getInstance().getTime();
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-mm-dd");
        final String todaystr=spf.format(today);
        Log.d(TAG, "onCreate: today==>"+todaystr);
        Log.d(TAG, "onCreate: updateDate==>"+updateDate);
        //Log.d(TAG, "onCreate: sp dollarRate=" + dollarRate);
        Log.d(TAG, "onCreate:  dollarRate=" + dollarRate);
        Log.d(TAG, "onCreate:  euroRate=" + euroRate);
        Log.d(TAG, "onCreate:  wonRate=" + wonRate);
        //判断时间
        if (!todaystr.equals(updateDate)){
            //开启子线程
            Thread t = new Thread(this);
            t.start();
            Log.d(TAG, "onCreate: 需要更新日期");
        }else {
            Log.d(TAG, "onCreate: 不需要更新");
        }



       handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    Bundle bdl=(Bundle)msg.obj;
                    dollarRate=bdl.getFloat("dollar-rate");
                    euroRate=bdl.getFloat("euro-rate");
                    wonRate=bdl.getFloat("won-rate");
                    Log.d(TAG, "handleMessage: dollarRate="+dollarRate);
                    //保存更新日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("updateDte", todaystr);
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.apply();
                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    public void onClick(View btn) {
        String str = rmb.getText().toString();
        float r = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        if (btn.getId() == R.id.btn_Dollar) {
            show.setText(String.format("%.2f", r * dollarRate));
        } else if (btn.getId() == R.id.btn_Euro) {
            show.setText(String.format("%.2f", r * euroRate));
            //或者show.setText(val+“”);
        } else {
            show.setText(String.format("%.2f", r * wonRate));
        }
    }


    private void openconfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dkey", dollarRate);
        config.putExtra("ekey", euroRate);
        config.putExtra("wkey", wonRate);
        startActivityForResult(config, 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    @Override
    // 打开菜单项
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_set){
            openconfig();
        }else if (item.getItemId()==R.id.open_list)
        {
            Intent list = new Intent(this, RateListActivity.class);
        startActivity(list);
            //测试数据库
//            RateItem item1=new RateItem("aaaaa","1111");
//            RateManager manager=new RateManager(this);
//            manager.add(item1);
//            manager.add(new RateItem("BBBB","1233"));
//            Log.i(TAG, "onOptionsItemSelected: 写入数据完毕");
//            //查询所有数据
//            List<RateItem> testList=manager.listAll();
//            for (RateItem i:testList){
//                Log.i(TAG, "onOptionsItemSelected: 取出数据[id="+i.getId()+"]Name[="+i.getCurName()+"]Rate[="+i.getCurRate()+"]");
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            //bdl.putFloat("key_dollar",newDollar);
            //        bdl.putFloat("euro_dollar",newEuro);
            //        bdl.putFloat("won_dollar",newWon);
            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("euro_dollar", 0.1f);
            wonRate = bundle.getFloat("won_dollar", 0.1f);
            Log.d(TAG, "onActivityResult:dollarRate =" + dollarRate);
            //将新设置的汇率值写到sp中
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
            Log.d(TAG, "onActivityResult: 数据已保存到sharedPreferences");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Bundle bundle=new Bundle();
        for (int i = 1; i < 4; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        bundle=fromobc();

        //获取网络数据
       /* try {
            URL url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();
            String html = inputstream2string(in);
            Log.d(TAG, "run: " + html);
            Document doc = Jsoup.parse(html);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        //或者用jsoup获取*/
    }

    private Bundle fromobc() {
        Bundle bundle=new Bundle();
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
                if ("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if ("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if ("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
            Message msg=handler.obtainMessage(5);
            // msg.obj = "hello run from()";
            msg.obj=bundle;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle fromcny() {
        Bundle bundle=new Bundle();
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
                if ("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if ("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if ("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
            Message msg=handler.obtainMessage(5);
            // msg.obj = "hello run from()";
            msg.obj=bundle;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    private String inputstream2string(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (; ; ) {
            int rsz = 0;
            try {
                rsz = in.read(buffer, 0, buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}

