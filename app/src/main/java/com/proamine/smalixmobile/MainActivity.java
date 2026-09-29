package com.proamine.smalixmobile;

import android.app.*;import android.os.*;import android.content.*;import android.net.Uri;import android.provider.OpenableColumns;import android.view.*;import android.widget.*;import java.io.*;import java.util.zip.*;

public class MainActivity extends Activity {
 LinearLayout box; TextView status; final int PICK=7;
 @Override public void onCreate(Bundle b){super.onCreate(b); box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(32,40,32,24); TextView title=new TextView(this);title.setText("Smalix Mobile");title.setTextSize(28);title.setPadding(0,0,0,20);box.addView(title); Button open=new Button(this);open.setText("Open APK");open.setOnClickListener(v->pick());box.addView(open);status=new TextView(this);status.setText("Select an APK to inspect its package contents.");status.setTextSize(16);status.setPadding(0,24,0,0);box.addView(status);setContentView(box);}
 void pick(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/vnd.android.package-archive");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);}
 @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r!=PICK||c!=RESULT_OK||d==null)return;Uri u=d.getData();try{String name=getName(u);long size=0;int count=0;InputStream in=getContentResolver().openInputStream(u);ZipInputStream z=new ZipInputStream(in);ZipEntry e;StringBuilder s=new StringBuilder();while((e=z.getNextEntry())!=null){count++;if(!e.isDirectory()){if(e.getSize()>0)size+=e.getSize();}if(count<=80)s.append(e.getName()).append("\\n");}z.close();status.setText("APK: "+name+"\\nEntries: "+count+"\\n\\n"+s); }catch(Exception e){status.setText("Cannot read APK: "+e.getMessage());}}
 String getName(Uri u){Cursor c=getContentResolver().query(u,null,null,null,null);try{if(c!=null&&c.moveToFirst()){int n=c.getColumnIndex(OpenableColumns.DISPLAY_NAME);if(n>=0)return c.getString(n);}}finally{if(c!=null)c.close();}return "APK";}
}
