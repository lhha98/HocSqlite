package com.nonghoc.hocsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String DATABASE_NAME = "dbContact.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;

    ListView lvDanhBa;
    ArrayList<String> dsDanhBa;
    ArrayAdapter<String> adapterDanhBa;

    Button btnThemDanhBa;
    Button btnChinhSua;
    Button btnXoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xuLySaoChepCSDLTuAssetsVaoHeThongMobile();

        addControls();
        addEvents();

        showAllContactOnListView();
    }

    private void showAllContactOnListView() {
        //Buoc 1: Mo csdl
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from Contact",null);
        dsDanhBa.clear();
        while(cursor.moveToNext())
        {

            int  id = cursor.getInt(0);
            String ma = cursor.getString(1);
            String ten = cursor.getString(2);

            dsDanhBa.add(id + " - " + ma + " - " + ten );
        }
        cursor.close();
        adapterDanhBa.notifyDataSetChanged();
    }

    private void addEvents() {
        btnThemDanhBa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThemDanhBa();
            }
        });


        btnChinhSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyChinhSuaDanhBa();

            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                xyLyXoaDanhBa();
            }
        });
    }

    private void xyLyXoaDanhBa() {
        database.delete("Contact","ma=?",new String[]{"nv04"});
        showAllContactOnListView();
    }

    private void xuLyChinhSuaDanhBa() {
        ContentValues row = new ContentValues();
        row.put("Ten", "Phan Chau Trinh");
        database.update("Contact", row, "ma=?", new String[]{"nv04"});
        showAllContactOnListView();
    }

    private void xuLyThemDanhBa() {
        ContentValues row = new ContentValues();
        row.put("Ma","nv04");
        row.put("Ten", "Huynh Thuc Khang");
        long r = database.insert("Contact",null,row);

        Toast.makeText(MainActivity.this, "Da them nhhan vien: r = " + r,Toast.LENGTH_LONG).show();
        showAllContactOnListView();
    }

    private void addControls() {
        lvDanhBa = (ListView) findViewById(R.id.lvDanhBa);
        dsDanhBa = new ArrayList<>();
        adapterDanhBa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,dsDanhBa);
        lvDanhBa.setAdapter(adapterDanhBa);

        btnThemDanhBa = (Button) findViewById(R.id.btnThemDanhBa);
        btnChinhSua = (Button) findViewById(R.id.btnChinhSua);
        btnXoa = (Button) findViewById(R.id.btnXoa);



    }

    private void xuLySaoChepCSDLTuAssetsVaoHeThongMobile() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if(!dbFile.exists())
        {
            try
            {
                copyDataBaseFromAsset();
                Toast.makeText(this,"Sao chep DB thanh cong",Toast.LENGTH_LONG).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(this, ex.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void copyDataBaseFromAsset() {
        try
        {
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = layDuongDanLuuTru();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if(!f.exists())
            {
                f.mkdir();
            }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0)
            {
                myOutput.write(buffer,0,length);

            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex)
        {
            Log.e("Loi_SaoChep",ex.toString());
        }
    }

    private String layDuongDanLuuTru() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

}
