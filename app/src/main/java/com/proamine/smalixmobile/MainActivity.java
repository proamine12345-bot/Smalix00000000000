package com.proamine.smalixmobile;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.provider.MediaStore;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.zip.*;
import android.util.Base64;
import com.android.apksig.ApkSigner;
import org.jf.baksmali.Baksmali;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

public class MainActivity extends Activity {
    final int PICK=7;
    File workspace;
    LinearLayout root, list;
    TextView status, title;
    Uri source;
    String apkName="modified.apk";
    int pad=16;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        workspace=new File(getCacheDir(),"workspace");
        showHome();
    }

    TextView tv(String s,int size){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.WHITE);
        t.setPadding(pad,pad,pad,pad); return t;
    }
    Button btn(String s){
        Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(15); return b;
    }
    void base(String heading){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(15,18,24));
        root.setPadding(12,12,12,12);
        title=tv(heading,24); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); root.addView(title);
        setContentView(root);
    }
    void showHome(){
        base("Smalix Mobile • APK Editor");
        TextView info=tv("محرر APK يعمل من الهاتف\n\nفتح APK → مساحة عمل → ملفات قابلة للمس → محرر نصوص → حفظ → إعادة بناء → توقيع → APK جاهز.",16);
        root.addView(info);
        Button open=btn("📦 فتح APK من الملفات");
        open.setOnClickListener(v->pick()); root.addView(open);
        Button installed=btn("📱 فتح تطبيق مثبت على الهاتف");
        installed.setOnClickListener(v->showInstalledApps()); root.addView(installed);
        Button about=btn("ℹ️ الوظائف");
        about.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Smalix Mobile").setMessage("• استعراض شجرة ملفات APK\n• بحث سريع\n• فتح وتعديل ملفات النص\n• حفظ التعديلات\n• DEX → Smali قابل للتعديل → DEX\n• محرر الملفات النصية\n• Hex Viewer للملفات الثنائية\n• إعادة بناء APK وإعادة توقيعها\n• حفظ الناتج في Downloads\n\nيتم استخدام محرك smali/baksmali لفك وتجميع DEX. تغييرات resources.arsc والـManifest الثنائي تحتاج معالجة موارد مخصصة، وليست مجرد تحرير نص خام.").setPositiveButton("حسنًا",null).show());
        root.addView(about);
        status=tv("الحالة: جاهز • يدعم DEX → Smali → DEX",14); root.addView(status);
    }
    void showInstalledApps(){
        base("📱 التطبيقات المثبتة");
        TextView info=tv("اختر أي تطبيق مثبت لنسخ ملف APK الخاص به وفتحه للتحليل والتعديل.",14);root.addView(info);
        EditText search=new EditText(this);search.setHint("🔎 ابحث باسم التطبيق");search.setTextColor(Color.WHITE);root.addView(search);
        LinearLayout apps=new LinearLayout(this);apps.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv=new ScrollView(this);sv.addView(apps);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        Button back=btn("↩ رجوع");root.addView(back);back.setOnClickListener(v->showHome());
        ArrayList<ApplicationInfo> all=new ArrayList<>(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));
        Collections.sort(all,(a,b)->getLabel(a).compareToIgnoreCase(getLabel(b)));
        Runnable fill=()->{
            apps.removeAllViews();String q=search.getText().toString().toLowerCase(Locale.ROOT);int count=0;
            for(ApplicationInfo a:all){
                if(a.sourceDir==null)continue;
                String label=getLabel(a);if(!q.isEmpty()&&!label.toLowerCase(Locale.ROOT).contains(q)&&!a.packageName.toLowerCase(Locale.ROOT).contains(q))continue;
                Button b=btn("📱 "+label+"\n"+a.packageName);b.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                b.setOnClickListener(v->openInstalledApp(a));apps.addView(b);if(++count>=300)break;
            }
            info.setText("التطبيقات المتاحة: "+count+" • اضغط على تطبيق لفتحه في المحرر");
        };
        search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){fill.run();}public void afterTextChanged(android.text.Editable e){}});
        fill.run();
    }
    String getLabel(ApplicationInfo a){try{return String.valueOf(a.loadLabel(getPackageManager()));}catch(Exception e){return a.packageName;}}
    void openInstalledApp(ApplicationInfo a){
        new Thread(()->{
            try{
                File src=new File(a.sourceDir);apkName=getLabel(a)+".apk";
                delete(workspace);workspace.mkdirs();
                File copied=new File(getCacheDir(),"selected-installed.apk");
                copy(new FileInputStream(src),new FileOutputStream(copied));
                source=Uri.fromFile(copied);
                extractFile(copied,apkName);
            }catch(Exception e){runOnUiThread(()->toast("فشل فتح التطبيق المثبت: "+e.getMessage()));}
        }).start();
    }
    void extractFile(File apk,String name)throws Exception{
        delete(workspace);workspace.mkdirs();
        ZipInputStream z=new ZipInputStream(new FileInputStream(apk));ZipEntry e;
        while((e=z.getNextEntry())!=null){
            if(e.getName().contains(".."))continue;
            File out=new File(workspace,e.getName());
            if(e.isDirectory()){out.mkdirs();continue;}
            File p=out.getParentFile();if(p!=null)p.mkdirs();
            FileOutputStream f=new FileOutputStream(out);byte[] buf=new byte[8192];int k;while((k=z.read(buf))>0)f.write(buf,0,k);f.close();
        }
        z.close();decodeAllDex();runOnUiThread(()->showWorkspace());
    }

    void pick(){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("application/vnd.android.package-archive"); i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i,PICK);
    }
    @Override protected void onActivityResult(int r,int c,Intent d){
        super.onActivityResult(r,c,d); if(r!=PICK||c!=RESULT_OK||d==null)return;
        source=d.getData(); String n=getName(source); apkName=n.endsWith(".apk")?n:"modified.apk";
        new Thread(()->extract(source,n)).start();
    }
    String getName(Uri u){
        Cursor c=getContentResolver().query(u,null,null,null,null);
        try{if(c!=null&&c.moveToFirst()){int n=c.getColumnIndex(OpenableColumns.DISPLAY_NAME);if(n>=0)return c.getString(n);}}
        finally{if(c!=null)c.close();} return "app.apk";
    }
    void extract(Uri u,String name){
        try{
            delete(workspace); workspace.mkdirs();
            InputStream in=getContentResolver().openInputStream(u); ZipInputStream z=new ZipInputStream(in); ZipEntry e;
            while((e=z.getNextEntry())!=null){
                if(e.getName().contains("..")) continue;
                File out=new File(workspace,e.getName());
                if(e.isDirectory()){out.mkdirs();continue;}
                File p=out.getParentFile();if(p!=null)p.mkdirs();
                FileOutputStream f=new FileOutputStream(out); byte[] buf=new byte[8192];int k;while((k=z.read(buf))>0)f.write(buf,0,k);f.close();
            }
            z.close();
            decodeAllDex();
            runOnUiThread(()->showWorkspace());
        }catch(Exception e){runOnUiThread(()->toast("فشل فتح APK: "+e.getMessage()));}
    }
    void showWorkspace(){
        base("📂 "+apkName);
        LinearLayout tools=new LinearLayout(this); tools.setOrientation(LinearLayout.HORIZONTAL);
        Button search=btn("🔎 بحث"); Button build=btn("⚙ إعادة بناء APK"); Button download=btn("⬇ تنزيل APK");
        tools.addView(search,new LinearLayout.LayoutParams(0,-2,1)); tools.addView(build,new LinearLayout.LayoutParams(0,-2,1)); tools.addView(download,new LinearLayout.LayoutParams(0,-2,1)); root.addView(tools);
        status=tv("المس أي ملف • DEX يتحول تلقائيًا إلى Smali • الملفات الثنائية لها Hex",13);root.addView(status);
        list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv=new ScrollView(this);sv.addView(list);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        search.setOnClickListener(v->searchDialog());
        build.setOnClickListener(v->new Thread(this::rebuild).start());
        download.setOnClickListener(v->new Thread(this::rebuild).start());
        populate("");
    }
    void populate(String q){
        list.removeAllViews(); ArrayList<File> files=new ArrayList<>(); collect(workspace,files);
        Collections.sort(files,(a,b)->a.getPath().compareToIgnoreCase(b.getPath()));
        int shown=0;
        for(File f:files){
            String rel=f.getAbsolutePath().substring(workspace.getAbsolutePath().length()+1);
            if(!q.isEmpty()&&!rel.toLowerCase(Locale.US).contains(q.toLowerCase(Locale.US)))continue;
            Button x=btn(icon(rel)+"  "+rel);
            x.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            x.setOnClickListener(v->openFile(f,rel)); list.addView(x);shown++;
            if(shown>=500)break;
        }
        status.setText("الملفات: "+files.size()+" • المعروض: "+shown);
    }
    String icon(String p){
        if(p.endsWith(".dex"))return "🧩"; if(p.endsWith(".xml"))return "📄"; if(p.endsWith(".smali"))return "🧠";
        if(p.endsWith(".png")||p.endsWith(".jpg")||p.endsWith(".webp"))return "🖼"; if(p.endsWith(".so"))return "⚙"; return "📃";
    }
    void collect(File d,List<File> out){File[] a=d.listFiles();if(a==null)return;for(File f:a){if(f.isDirectory())collect(f,out);else out.add(f);}}
    void searchDialog(){
        EditText e=new EditText(this);e.setHint("اسم الملف أو المسار");e.setSingleLine();
        new AlertDialog.Builder(this).setTitle("بحث").setView(e).setPositiveButton("بحث",(d,w)->populate(e.getText().toString().trim())).setNegativeButton("الكل",(d,w)->populate("")).show();
    }
    boolean textFile(String p){
        String x=p.toLowerCase(Locale.US);
        return x.endsWith(".smali")||x.endsWith(".xml")||x.endsWith(".json")||x.endsWith(".txt")||x.endsWith(".properties")||x.endsWith(".html")||x.endsWith(".js")||x.endsWith(".css")||x.endsWith(".kt")||x.endsWith(".java")||x.endsWith(".mf")||x.endsWith(".pro")||x.endsWith(".cfg")||x.endsWith(".ini");
    }
    void openFile(File f,String rel){
        if(f.length()>2*1024*1024){toast("الملف كبير جدًا للتحرير داخل الواجهة");return;}
        if(!textFile(rel)){
            if(rel.endsWith(".dex")){ decodeDexForFile(f, rel); return; }
            openBinary(f,rel);
            return;
        }
        try{
            byte[] data=read(f); String s=new String(data,"UTF-8");
            base("✏️ "+rel);
            LinearLayout bar=new LinearLayout(this);bar.setOrientation(LinearLayout.HORIZONTAL);
            Button save=btn("💾 حفظ");Button back=btn("↩ رجوع");bar.addView(save,new LinearLayout.LayoutParams(0,-2,1));bar.addView(back,new LinearLayout.LayoutParams(0,-2,1));root.addView(bar);
            EditText ed=new EditText(this);ed.setText(s);ed.setTextColor(Color.WHITE);ed.setTextSize(14);ed.setGravity(Gravity.TOP|Gravity.LEFT);ed.setTypeface(Typeface.MONOSPACE);ed.setSingleLine(false);ed.setInputType(0x20001|0x80000);ed.setPadding(12,12,12,12);
            ScrollView sv=new ScrollView(this);sv.addView(ed);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
            save.setOnClickListener(v->{try{write(f,ed.getText().toString());toast("تم الحفظ");}catch(Exception e){toast("فشل الحفظ: "+e.getMessage());}});
            back.setOnClickListener(v->showWorkspace());
            ed.requestFocus();
        }catch(Exception e){toast("تعذر فتح الملف: "+e.getMessage());}
    }
    void decodeAllDex() throws Exception {
        ArrayList<File> fs=new ArrayList<>(); collect(workspace,fs);
        for(File f:fs){
            String rel=f.getAbsolutePath().substring(workspace.getAbsolutePath().length()+1).replace(File.separatorChar,'/');
            if(rel.matches("classes[0-9]*\\.dex")) decodeDexForFile(f,rel);
        }
    }

    void decodeDexForFile(File dexFile,String rel) {
        try{
            String base=rel.substring(0,rel.length()-4);
            File out=new File(workspace,"__smali__/"+base);
            delete(out); out.mkdirs();
            DexBackedDexFile dex=DexFileFactory.loadDexFile(dexFile,Opcodes.forApi(35));
            BaksmaliOptions o=new BaksmaliOptions();
            o.apiLevel=35; o.parameterRegisters=true; o.localsDirective=true; o.sequentialLabels=true;
            o.debugInfo=true; o.codeOffsets=false; o.accessorComments=false; o.implicitReferences=false;
            int jobs=Math.max(1,Math.min(4,Runtime.getRuntime().availableProcessors()));
            if(!Baksmali.disassembleDexFile(dex,out,jobs,o)) throw new IOException("DEX disassembly failed");
        }catch(Exception e){ throw new RuntimeException("فشل تحويل "+rel+" إلى Smali: "+e.getMessage(),e); }
    }

    Map<String,File> assembleAllDex() throws Exception {
        Map<String,File> result=new HashMap<>();
        File root=new File(workspace,"__smali__");
        File[] dirs=root.listFiles();
        if(dirs==null)return result;
        for(File d:dirs){
            if(!d.isDirectory())continue;
            String dexName=d.getName()+".dex";
            File original=new File(workspace,dexName);
            if(!original.exists())continue;
            File out=new File(getCacheDir(),"assembled-"+dexName);
            if(out.exists())out.delete();
            SmaliOptions o=new SmaliOptions();
            o.apiLevel=35; o.jobs=Math.max(1,Math.min(4,Runtime.getRuntime().availableProcessors()));
            o.outputDexFile=out.getAbsolutePath();
            if(!Smali.assemble(o,d.getAbsolutePath())) throw new IOException("Smali assembly failed for "+dexName);
            result.put(dexName,out);
        }
        return result;
    }

    void openBinary(File f,String rel){
        try{
            byte[] data=read(f);
            int n=Math.min(data.length,4096);
            StringBuilder h=new StringBuilder();
            for(int i=0;i<n;i+=16){
                h.append(String.format(Locale.US,"%08X  ",i));
                for(int k=0;k<16;k++) h.append(i+k<n?String.format(Locale.US,"%02X ",data[i+k]&255):"   ");
                h.append(" | ");
                for(int k=0;k<16&&i+k<n;k++){int c=data[i+k]&255;h.append(c>=32&&c<127?(char)c:'.');}
                h.append('\n');
            }
            base("🔢 Hex • "+rel);
            LinearLayout bar=new LinearLayout(this);bar.setOrientation(LinearLayout.HORIZONTAL);
            Button back=btn("↩ رجوع");bar.addView(back,new LinearLayout.LayoutParams(-1,-2));root.addView(bar);
            TextView info=tv("Binary • "+data.length+" bytes • عرض أول "+n+" bytes",13);root.addView(info);
            EditText ed=new EditText(this);ed.setText(h.toString());ed.setTextColor(Color.WHITE);ed.setTextSize(12);ed.setTypeface(Typeface.MONOSPACE);
            ed.setGravity(Gravity.TOP|Gravity.LEFT);ed.setSingleLine(false);root.addView(new ScrollView(this){{addView(ed);}},new LinearLayout.LayoutParams(-1,0,1));
            back.setOnClickListener(v->showWorkspace());
        }catch(Exception e){toast("تعذر فتح الملف الثنائي: "+e.getMessage());}
    }

    void rebuild(){
        try{
            Map<String,File> rebuiltDex = assembleAllDex();
            File unsigned=new File(getCacheDir(),"rebuilt.apk"); if(unsigned.exists())unsigned.delete();
            ZipOutputStream z=new ZipOutputStream(new FileOutputStream(unsigned));ArrayList<File> fs=new ArrayList<>();collect(workspace,fs);
            byte[] buf=new byte[8192];
            for(File f:fs){
                String rel=f.getAbsolutePath().substring(workspace.getAbsolutePath().length()+1).replace(File.separatorChar,'/');
                if(rel.startsWith("META-INF/") || rel.startsWith("__smali__/"))continue;
                ZipEntry e=new ZipEntry(rel);z.putNextEntry(e);
                File src=rebuiltDex.containsKey(rel)?rebuiltDex.get(rel):f;
                FileInputStream in=new FileInputStream(src);int k;while((k=in.read(buf))>0)z.write(buf,0,k);in.close();z.closeEntry();
            }
            z.close();
            File signed=new File(getCacheDir(),"Smalix-Modified.apk");
            sign(unsigned,signed);
            saveDownload(signed);
            runOnUiThread(()->new AlertDialog.Builder(this).setTitle("✅ تم إنشاء APK").setMessage("تمت إعادة بناء APK وتوقيعها وحفظها داخل Downloads.\n\nمهم: إذا كانت التعديلات على ملفات resources.arsc أو DEX تحتاج إعادة ترجمة فعلية، فالنسخة الحالية لا تعيد ترجمة هذه الملفات الثنائية؛ محرر النصوص يعمل مباشرة على الملفات النصية الموجودة داخل APK.").setPositiveButton("حسنًا",null).show());
        }catch(Exception e){runOnUiThread(()->toast("فشل إعادة البناء: "+e.getMessage()));}
    }
    void sign(File in,File out)throws Exception{
        byte[] kb=Base64.decode("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC/cRJhZafX4puDQYjF/X+PTHSLpoIqxF0vwG/WCUeCb9U6ujbVNyNilMyO50g9Ss7+stKKM7sQ0zcUmhAcxA8swfNil00Dl1NesPvTuWjQa7dmNipkncS/uo45RUScia/mU8w80eBMGpTa7kWKji/W5Vu3oPtj3bbNfEURz6WuatNiw/5WEvwlgFoFpNydIWNnnlxbvpBZ94mjVzs34XyXu0JSgL8EJKRUmCKvtXr9kroQZ1hIeMZcPYC3Z/FVqFc8iYm/PTrJiQaxdlgS1gGvXjZrucTjGaW/Y7RtKIPDY8UoAnTTJeZdlr2VbY8MOfXP38GEpIDjgaT4Yi4LjYYPAgMBAAECggEAQB4BL3CqCcKCHjBNPC5+UgKjx//g+azhnvQfILrj4dpNuokSg0+fwM0gQ75PcgPDlwdSP2o6/VQYTwSYX+IESRO+Tadp6kl86SpydUNUSPXCq295vFAgzKwRJTo/VARDTuuC7F3IdwSyFS8XSGP9vsX7dWFrwNNJfcHgqin3DhZxgIw1zj6D9hbZhFgRt9v0kZJ4vmIphmByfDlxUXWmv+gRapNbP/3Dv0EftKRSODD9rIpf0iLVVDoUSDAa5stawBFid0C4ZodKr2y+1yrz0giYahgIiORlaY+9vzm9/rdknSkl96NsRN1gvsdAnpcy0+xhIuJUDzNxKsAmNL26xQKBgQDt3SQTe5FIcBvxL+K2jjbf/5KaMwdLK9E4K8JXjUMxJfuqDRqrTAQCsgBhBsWdGKqUoo94GS+LPYa7+IM3zVSQvBIaZ419wT1NkD47icWKOtIFic38+hRe4XTCrpfDeqT8CPMiHq+Ijk5cX9UK7z0Ksn0rdohyLKLyyq7fHJ68/QKBgQDOCdFpYDKKuRvwexgQfnRWYW03vh2t8aXB0OwMJP/+je88Jewuilf53FanQGBsaWXId7oqXjMAXELppQyckvqBVx/++nACXemiOgcJJX4iniaoetw+fX4+SggHMvmuiIm/3zCA0U/6crXxIu9NeqahMUG1AwewP5jJ6ScJS0VC+wKBgQCiA/GRr6XqgCoYIvS6Qa3Q7wSNMVqzcfoE15F9DwQIUwXS8zAKIzHMIDAv5mvtsCJoMT2loQoIPnUqTYAbHymNl/yAra3rTRcTYL9y0EmT+LKbAR0kRbZgVhKwlDlc9Ymw/euVfu8zBTMMql2zH/ck9SRa2Vbg5dRrqXBpGdZphQKBgQDNfpVSGBPQsBbhnqO9r6GwlbMsqX1Ig37i89C5vB1mcYFt6BTLxkdAeTYj0d1ZwzU039TZdOJBNcO27FrLvvwCDDdpQ7Z7ZdcjaeSihrZrKHk+CGuqF9fVoFipdXn5TUy2BZCGqjyuecJlIoayK+YNVudF9iOw8X0dxGCesGgACQKBgQDTtX2y2366DG84eoys0Crpj8aWU3jLOZT9bppFNH+QlOnbjM47xNb2/55AtY2CJNTNAZGalZo8iErqpcE8dXWyiAfLG5IzuzVsdgkkqeyBsrP9i7aflZjnM4EslspH5UPAjh9GcKF1lmYEp6XZ9XQnp4+Bxmjqzi67/GOJMBUDhw==",Base64.NO_WRAP); byte[] cb=Base64.decode("MIIDVTCCAj2gAwIBAgIUJIzuYG6ttxTSwOcfPd119NbLxygwDQYJKoZIhvcNAQELBQAwOjEWMBQGA1UEAwwNU21hbGl4IE1vYmlsZTEPMA0GA1UECgwGU21hbGl4MQ8wDQYDVQQLDAZNb2JpbGUwHhcNMjYwOTI5MTM1ODA4WhcNMzYwOTI2MTM1ODA4WjA6MRYwFAYDVQQDDA1TbWFsaXggTW9iaWxlMQ8wDQYDVQQKDAZTbWFsaXgxDzANBgNVBAsMBk1vYmlsZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL9xEmFlp9fim4NBiMX9f49MdIumgirEXS/Ab9YJR4Jv1Tq6NtU3I2KUzI7nSD1Kzv6y0oozuxDTNxSaEBzEDyzB82KXTQOXU16w+9O5aNBrt2Y2KmSdxL+6jjlFRJyJr+ZTzDzR4EwalNruRYqOL9blW7eg+2Pdts18RRHPpa5q02LD/lYS/CWAWgWk3J0hY2eeXFu+kFn3iaNXOzfhfJe7QlKAvwQkpFSYIq+1ev2SuhBnWEh4xlw9gLdn8VWoVzyJib89OsmJBrF2WBLWAa9eNmu5xOMZpb9jtG0og8NjxSgCdNMl5l2WvZVtjww59c/fwYSkgOOBpPhiLguNhg8CAwEAAaNTMFEwHQYDVR0OBBYEFEQc1r5YnnVRRGlzrotcu+6dgKmMMB8GA1UdIwQYMBaAFEQc1r5YnnVRRGlzrotcu+6dgKmMMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAB19R0ASKVHETIfD9+jh+MNj7AI1W6F0KYBCtJYRGiBP9YDOcFFBEP6giTgjCqMuXCjBGSyQwXm7kjCr340+dZzgfpZCGb6oFlDAFWMUOZGAO6H/SRC8rQIFbmumRNqL41ycREMiDFMDmHd3Nwpfepphg1T6Di1jO4hOFM7F+HeB1OUiYqMOcf2cfoHm9PQ9Ks3q9l2AtBSFkzsWBUz/A6x7XjbtYUdSR+XG7HYvxO7OaM3nlDnJDQwimsSyH3JfCK7jBkcSs6lj8V1lVtHgfuSRSU44io1QaQ5ta7V1U5VV85LGefu8C3CxpCRt8UWJAwQA1Y9BUL6ds8eExD8S50=",Base64.NO_WRAP);
        KeyFactory kf=KeyFactory.getInstance("RSA"); PrivateKey pk=kf.generatePrivate(new PKCS8EncodedKeySpec(kb));
        CertificateFactory cf=CertificateFactory.getInstance("X.509"); X509Certificate cert=(X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cb));
        List<X509Certificate> certs=Collections.singletonList(cert);
        ApkSigner.SignerConfig sc=new ApkSigner.SignerConfig.Builder("smalix",pk,certs).build();
        List<ApkSigner.SignerConfig> ss=Collections.singletonList(sc);
        new ApkSigner.Builder(ss).setInputApk(in).setOutputApk(out).setV1SigningEnabled(true).setV2SigningEnabled(true).setV3SigningEnabled(true).setV4SigningEnabled(false).setMinSdkVersion(26).build().sign();
    }
    void saveDownload(File f)throws Exception{
        if(Build.VERSION.SDK_INT>=29){
            ContentValues v=new ContentValues();v.put(MediaStore.Downloads.DISPLAY_NAME,"Smalix-Modified.apk");v.put(MediaStore.Downloads.MIME_TYPE,"application/vnd.android.package-archive");v.put(MediaStore.Downloads.IS_PENDING,1);
            Uri u=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,v);if(u==null)throw new IOException("Downloads غير متاح");
            OutputStream o=getContentResolver().openOutputStream(u);copy(new FileInputStream(f),o);o.close();v.clear();v.put(MediaStore.Downloads.IS_PENDING,0);getContentResolver().update(u,v,null,null);
        }else{
            File d=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);d.mkdirs();copy(new FileInputStream(f),new FileOutputStream(new File(d,"Smalix-Modified.apk")));
        }
    }
    byte[] read(File f)throws Exception{ByteArrayOutputStream o=new ByteArrayOutputStream();FileInputStream i=new FileInputStream(f);byte[] b=new byte[8192];int k;while((k=i.read(b))>0)o.write(b,0,k);i.close();return o.toByteArray();}
    void write(File f,String s)throws Exception{FileOutputStream o=new FileOutputStream(f);o.write(s.getBytes("UTF-8"));o.close();}
    void copy(InputStream i,OutputStream o)throws Exception{byte[] b=new byte[8192];int k;while((k=i.read(b))>0)o.write(b,0,k);i.close();o.close();}
    void delete(File f){if(!f.exists())return;if(f.isDirectory()){File[] a=f.listFiles();if(a!=null)for(File x:a)delete(x);}f.delete();}
    void toast(String s){runOnUiThread(()->Toast.makeText(this,s,Toast.LENGTH_LONG).show());}
}