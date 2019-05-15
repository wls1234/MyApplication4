package com.example.asus.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemLongClickListener {
    Handler handler;
    private ArrayList<HashMap<String, String>> listItems;//存放文字图片信息
    private SimpleAdapter listItemApdater;//适配器
    public final String TAG = "MyList2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();
        this.setListAdapter(listItemApdater);
        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    List<HashMap<String, String>> list2 = (List<HashMap<String, String>>) msg.obj;
                    listItemApdater = new SimpleAdapter(MyList2Activity.this, list2,
                            R.layout.list_item//listitems的xml布局实现
                            , new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    setListAdapter(listItemApdater);
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemLongClickListener(this);
    }

    protected void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "RateActivity:" + i);//标题文字
            map.put("ItemDetail", "detail:" + i);//详情描述
            listItems.add(map);
        }
        //生成适配器的Item和动态数组对应元素
        listItemApdater = new SimpleAdapter(this, listItems//listitems数据源
                , R.layout.list_item//listitems的xml布局实现
                , new String[]{"ItemTitle", "ItemDetail"},
                new int[]{R.id.itemTitle, R.id.itemDetail}
        );
        setListAdapter(listItemApdater);
    }

    public void run() {
        //获取网络数据，放入list带回到主线程中
        List<HashMap<String, String>> retlist = new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/").get();
            Log.d(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");
            /*for(Element table:tables){
                Log.d(TAG, "run: table["+i+"]="+table);
                i++;
            }*/
            Element table1 = tables.get(0);
            //Log.d(TAG, "run: table6="+table6);
            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 7) {
                //获取第一列数据
                Element td1 = tds.get(i);
                //获取第1+5列数据
                Element td2 = tds.get(i + 5);
                Log.d(TAG, "run: " + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", str1);
                map.put("ItemDetail", val);
                retlist.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);
        msg.obj = retlist;
        handler.sendMessage(msg);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap<String,String> map=(HashMap<String,String>) getListView().getItemAtPosition(position);
        String titleStr=map.get("ItemTitle");
        String detailStr=map.get("ItemDetail");
        Log.d(TAG, "onItemClick: titleStr:"+titleStr);
        TextView title=findViewById(R.id.itemTitle);
        TextView detail=findViewById(R.id.itemDetail);
        String title2=String.valueOf(title.getText());
        String detail2=String.valueOf(detail.getText());
        //打开新的页面，传入参数
        Intent rateCalc =new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        Log.d(TAG, "onListItemClick: "+title2);
        startActivity(rateCalc);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position:"+position);
        //删除操作

        return false;
    }
}
