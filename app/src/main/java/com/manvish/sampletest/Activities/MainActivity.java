package com.manvish.sampletest.Activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.manvish.sampletest.Adapters.FolderListAdapter;
import com.manvish.sampletest.Application.AadharTimeAttendanceApplication;
import com.manvish.sampletest.R;
import com.manvish.sampletest.Structures.FingerDetails;
import com.manvish.sampletest.Utils.CommanUtils;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosmart.sdk.TemplateList;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button capture;
    EditText editText;
    TextView foldernameTV;
    int folderNo = 0;
    int clickIteration = 0;
    File folderpath = null;
    private static TemplateList templateList;
    private static FingerDetails fingerDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capture= (Button) findViewById(R.id.caputefingurebtn);
        foldernameTV= (TextView) findViewById(R.id.foldernameTV);
        editText= (EditText) findViewById(R.id.mainpageIDEnterEditText);
        final ListPopupWindow lpw = new ListPopupWindow(this);

        capture.setOnClickListener(this);

        File rootFoldername = new File("/storage/sdcard0/");
        final File[] files = rootFoldername.listFiles();
        final ArrayList<File> filesList = new ArrayList<File>();
        for(File file : files)
        {
            if(file.getName().contains("EMP"))
                filesList.add(file);
        }


        final Button getfolderlistbutton = (Button) findViewById(R.id.getfolderlistbutton);
        getfolderlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File rootFoldername = new File("/storage/sdcard0/");
                final File[] files = rootFoldername.listFiles();
                final ArrayList<File> filesList = new ArrayList<File>();
                for(File file : files)
                {
                    if(file.getName().contains("EMP"))
                        filesList.add(file);
                }

                FolderListAdapter folderListAdapter = new FolderListAdapter(MainActivity.this, filesList);
                lpw.setAdapter(folderListAdapter);
                lpw.setAnchorView(getfolderlistbutton);
                int width = MainActivity.this.getResources().getDimensionPixelSize(R.dimen.overflow_width);
                int height= MainActivity.this.getResources().getDimensionPixelSize(R.dimen.overflow_height);
                lpw.setWidth(width);
                lpw.setHeight(height);
                lpw.show();
            }
        });

        getfolderlistbutton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lpw.dismiss();
            }
        });


        folderNo = filesList.size();
        Log.d("kishan", "onCreate: folderNo "+folderNo);

        final File file=new File("/storage/sdcard0/EMP"+folderNo+"/");
        if(!file.exists()){
            file.mkdir();
        }
        folderpath = file.getAbsoluteFile();
        foldernameTV.setText("Folder Name is "+file.getName());

        final Button getfilelistbutton = (Button)findViewById(R.id.getfilelistbutton);
        getfilelistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File("/storage/sdcard0/EMP"+folderNo+"/");
                File folder = new File(file.getAbsolutePath()+"/Image/");
                Log.d("kishan", "onClick: "+folder.getAbsolutePath());
                final File[] files = folder.listFiles();
                final ArrayList<File> filesList = new ArrayList<File>();
                if(files!=null && files.length > 0 )
                    Collections.addAll(filesList,files);

                FolderListAdapter folderListAdapter = new FolderListAdapter(MainActivity.this, filesList);
                lpw.setAdapter(folderListAdapter);
                lpw.setAnchorView(getfilelistbutton);
                int width = MainActivity.this.getResources().getDimensionPixelSize(R.dimen.overflow_width);
                int height= MainActivity.this.getResources().getDimensionPixelSize(R.dimen.overflow_height);
                lpw.setWidth(width);
                lpw.setHeight(height);
                lpw.show();
            }
        });

        getfilelistbutton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lpw.dismiss();
            }
        });

        CommanUtils.initializeSensor(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.caputefingurebtn:
                Log.d("Gajanand", "onClick: ");
                clickIteration = 0;
                captureFinger();
                break;
            default:
                break;
        }
    }


    public void filemethod()
    {
        File rootFoldername = new File("/storage/sdcard0/");
        final File[] files = rootFoldername.listFiles();
        final ArrayList<File> filesList = new ArrayList<File>();
        for(File file : files)
        {
            if(file.getName().contains("EMP"))
                filesList.add(file);
        }


        folderNo = filesList.size();
        Log.d("kishan", "onCreate: folderNo "+folderNo);

        final File file=new File("/storage/sdcard0/EMP"+folderNo+"/");
        if(!file.exists()){
            file.mkdir();
        }
        folderpath = file.getAbsoluteFile();
        foldernameTV.setText("Folder Name is "+file.getName());
    }


    private void captureFinger() {

        final String str = editText.getText().toString().trim();
        if(str.length() == 0)
            CommanUtils.ShowCustomToast(this,"cannot be empty",false);
        else
        {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    templateList = null;
                    templateList = new TemplateList();
                    fingerDetails = new FingerDetails();
                    CommanUtils.showProgressBar("Scanning...", MainActivity.this);
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    SensorInitializationClass initiateSensor = AadharTimeAttendanceApplication.getInstance().getInitializationClass();
                    templateList = initiateSensor.CaptureFinger(initiateSensor, ProcessInfo.getInstance().getMorphoDevice());
                    fingerDetails.setEmpid("1");
                    if (templateList != null) {
                        byte[] templateData = templateList.getTemplate(0).getData();
                        byte[] templateImageData = templateList.getImage(0).getImage();
                        fingerDetails.setFingerTemplatee(templateData);
                        fingerDetails.setFingerImage(templateImageData);
                        return true;
                    } else {
                        return false;
                    }


                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    CommanUtils.dismissProgressBar();

                    if (aBoolean) {

                        if(fingerDetails != null)
                        {
                            String Image = folderpath.getAbsolutePath()+"/Image/"+str;
                            String RawImage = folderpath.getAbsolutePath()+"/RawImage/"+str;
                            String Template = folderpath.getAbsolutePath()+"/Template/"+str;
                            if(str.contains(".")){
                                Image = folderpath.getAbsolutePath()+"/Image/"+str.substring(0, str.indexOf("."));
                                RawImage = folderpath.getAbsolutePath()+"/RawImage/"+str.substring(0, str.indexOf("."));
                                Template = folderpath.getAbsolutePath()+"/Template/ "+str.substring(0, str.indexOf("."));
                            }

                            try {
                                if(new File(Template+"_TEMPLATE").exists())
                                {
                                    Template = Template+"_TEMPLATE_1";
                                    RawImage = RawImage+"_RAWIMAGE_1";
                                    Image = Image+"_IMAGE_1.JPEG";
                                }
                                else
                                {
                                    Template = Template+"_TEMPLATE";
                                    RawImage = RawImage+"_RAWIMAGE";
                                    Image = Image+"_IMAGE.JPEG";
                                }

                                FileUtils.writeByteArrayToFile(new File(Template),fingerDetails.getFingerTemplatee());
                                FileUtils.writeByteArrayToFile(new File(RawImage),fingerDetails.getFingerImage());

                                Bitmap mFinalCapturedImageBitmap = null;
                                mFinalCapturedImageBitmap  = CommanUtils.getRawToBmp(fingerDetails.getFingerImage(), 256, 400);

                                byte[] fingerImageByteArray = CommanUtils.getBytes(mFinalCapturedImageBitmap);

                                FileUtils.writeByteArrayToFile(new File(Image),fingerImageByteArray);

                                fingerDetails = null;
//                                editText.setText("");

                                if(new File(folderpath.getAbsolutePath()+"/Image/").listFiles().length == 52)
                                {
                                    CommanUtils.ShowCustomToast(MainActivity.this,"Creating New folder",true);
                                    filemethod();
                                    return;
                                }

                                if(clickIteration == 0)
                                {
                                    clickIteration++;
                                    captureFinger();
                                }
                                else
                                {
                                    CommanUtils.ShowCustomToast(MainActivity.this,"saved succesfully",true);
                                    editText.setText("");
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            CommanUtils.ShowCustomToast(MainActivity.this,"Please capture fingure",false);
                    } else {
                        CommanUtils.ShowCustomToast(MainActivity.this, "Finger not captured", false);
                    }


                }
            }.execute();
        }

    }
}
