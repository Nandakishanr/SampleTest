package com.manvish.sampletest.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.manvish.sampletest.R;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.FalseAcceptanceRate;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.StrategyAcquisitionMode;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import static com.morpho.morphosmart.sdk.FalseAcceptanceRate.MORPHO_FAR_5;


/**
 * Created by kishan on 8/1/16.
 */
public class SensorInitializationClass extends Activity implements Observer {

    MorphoDevice morphoDevice = null;
    private String sensorName = "";
//    static Bitmap mFinalCapturedImageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymorphoprocess);

    }

    Context context;
    public SensorInitializationClass(Context context){

        this.context=context;
    }

    public MorphoDevice initializeDevice() {

        morphoDevice = new MorphoDevice();
        USBManager.getInstance().initialize(context, "com.manvish.morphosample.USB_ACTION");
//        USBManager.getInstance().initialize(context, "com.manvish.morphosample.USB_ACTION");

        int retenum = enumerate();//morphoDevice);
        System.out.println("kishan: return from enumerate " + retenum);
        if (retenum != 0) {
//            finish();
            int  ret = morphoDevice.rebootSoft(30, this);
            morphoDevice.resumeConnection(30, this);
            return null;
        }
        int retcon = connection(morphoDevice);//morphoDevice);
        System.out.println("kishan: return from connection " + retcon);
        if (retcon != 0) {
            /*if(retcon == ErrorCodes.MORPHOERR_INTERNAL)
            {
                disconnectotg();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectotg();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                morphoDevice = null;
                initializeDevice();
            }
            else
            {
                finish();
                return null;
            }*/
//            finish();
            return null;

        }


        return morphoDevice;
    }


    public int enumerate() {//MorphoDevice morphoDevice) {
        Integer nbUsbDevice = new Integer(0);

        int ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);

        System.out.println("kishan " + ret);
        if (ret == ErrorCodes.MORPHO_OK) {

            Log.d("kishan", "enumerate: " + nbUsbDevice);
            if (nbUsbDevice > 0) {
                sensorName = morphoDevice.getUsbDeviceName(0);
                Log.d("kishan", "sensor name " + sensorName);
                return 0;
            } else {
                /*Toast.makeText(context,
                        "No device found", Toast.LENGTH_LONG).show();*/
                return -1;
            }
        } else {
            /*Toast.makeText(context, ErrorCodes.getError(ret,
                    morphoDevice.getInternalError()), Toast.LENGTH_LONG).show();*/
            return -1;
        }
//        return -1;
    }


    public static void disconnectotg() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String cmd = "ioctl /dev/yydj 37378";
            Log.e("command", cmd);
            os.writeBytes(cmd);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("kishan exception in disconnectotg " + e.toString());
        }
    }

    public static void connectotg() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String cmd = "ioctl /dev/yydj 37377";
            Log.e("command", cmd);
            os.writeBytes(cmd);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("kishan exception in connectotg " + e.toString());
        }

    }

    public int connection(MorphoDevice morphoDevice) {
        int ret = morphoDevice.openUsbDevice(sensorName, 0);

        System.out.println("kishan connection " + ret + morphoDevice.getUsbDeviceName(0));
        if (ret != ErrorCodes.MORPHO_OK)
            closeSensor(morphoDevice);
        else {
            Log.d("kishan", "Usbdevice is opened");
            ProcessInfo.getInstance().setMSOSerialNumber(sensorName);

            ret = morphoDevice.openUsbDevice(sensorName, 0);
            if (ret == ErrorCodes.MORPHO_OK) {
                boolean isParamSet=setParameter(morphoDevice);

                if(isParamSet){
                    ProcessInfo.getInstance().setMorphoDevice(morphoDevice);
                    return 0;
                }else{
                    return -1;
                }

            } else {
//                ManvishCommonUtil.showCustomToast((Activity) context, "Device is Busy ,Try Again");
                closeSensor(morphoDevice);
                return -1;
            }

            /*String productDescriptor = morphoDevice.getProductDescriptor();
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(productDescriptor, "\n");
            if (tokenizer.hasMoreTokens()) {
                String l_s_current = tokenizer.nextToken();
                if (l_s_current.contains("FINGER VP") || l_s_current.contains("FVP")) {
                    MorphoInfo.m_b_fvp = true;
                }
            }
            return 0;*/
        }

        return -1;
    }

    private boolean setParameter(MorphoDevice morphoDevice) {

        byte[] sensorWindowPosition = morphoDevice.getConfigParam(MorphoDevice.CONFIG_SENSOR_WIN_POSITION_TAG);
        int position = 0;
        System.out.println("kishan@@@@@@@@@@@@@@@@@@@@@@@@Step 1 " + sensorWindowPosition[0]);
        if (sensorWindowPosition != null) {
            System.out.println("kishan@@@@@@@@@@@@@@@@@@@@@@@@Step 1 " + sensorWindowPosition[0]);
            position = sensorWindowPosition[0];
            if (position != 3) {
                byte[] fpOrient = new byte[1];
                fpOrient[0] = 0x03;
                int ret1 = morphoDevice.setConfigParam(MorphoDevice.CONFIG_SENSOR_WIN_POSITION_TAG, fpOrient);
                if (ret1 != ErrorCodes.MORPHO_OK) {

                    System.out.println("kishan failed setting reverse params");
                } else {
                    int  ret = morphoDevice.rebootSoft(30, this);
                    morphoDevice.resumeConnection(30, this);
                    System.out.println("kishan Succes setting reverse paramas");
                    return true;
                }
            }else{
                return true;
            }
        }
        return false;
    }


    /*public int connection() {//MorphoDevice morphoDevice) {
        int ret = morphoDevice.openUsbDevice(sensorName, 0);

        Log.d("kishan ", Integer.toString(ret) + " " + sensorName);
        if (ret != ErrorCodes.MORPHO_OK) {
//            ret = morphoDevice.rebootSoft(30,this);
            closeSensor(morphoDevice);

            *//*disconnectotg();
            connectotg();
            System.out.println("kishan connect_disconnect done ");*//*
        } else {
            Log.d("kishan", "Usbdevice is opened");
            ProcessInfo.getInstance().setMSOSerialNumber(sensorName);
            String productDescriptor = morphoDevice.getProductDescriptor();
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(productDescriptor, "\n");
            if (tokenizer.hasMoreTokens()) {
                *//*String l_s_current = tokenizer.nextToken();
                if (l_s_current.contains("FINGER VP") || l_s_current.contains("FVP")) {
                    MorphoInfo.m_b_fvp = true;
                }*//*
                String l_s_current = tokenizer.nextToken();
                if (l_s_current.contains("FINGER VP") || l_s_current.contains("FVP")) {
                    MorphoInfo.m_b_fvp = true;
                }

            }

            return 0;
        }

        return -1;
    }*/

    public void closeSensor(MorphoDevice morphoDevice) {

        try {
            if (morphoDevice != null) {//&& ProcessInfo.getInstance().isStarted()) {
                morphoDevice.cancelLiveAcquisition();
                morphoDevice.closeDevice();
                if (ProcessInfo.getInstance() != null) {
                    ProcessInfo.getInstance().setMorphoDevice(null);
                }
                Thread.sleep(2000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public TemplateList CaptureFinger(final Observer observer, MorphoDevice morphodevice) {

        int timeout = 20;
        int acquisitionThreshold = 0;
        int nbFinger = 1;
        int maxSizeTemplate = 255;
        int advancedSecurityLevelsRequired = 0xFF;
//        MorphoDevice morphoDevice;

        if (morphodevice == null)
            return null;

        morphoDevice = morphodevice;
        ProcessInfo.getInstance().setMorphoDevice(morphoDevice);

        EnrollmentType enrollType = EnrollmentType.ONE_ACQUISITIONS;

        TemplateType templateType;
        templateType = TemplateType.MORPHO_PK_ISO_FMR;//MORPHO_PK_COMP;
        TemplateFVPType templateFVPType;
//        templateFVPType = TemplateFVPType.MORPHO_PK_FVP;
        templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;
        int callbackCmd = 0;//ProcessInfo.getInstance().getCallbackCmd();
        //callbackCmd value 199

//        callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();
        LatentDetection latentDetection;
        latentDetection = LatentDetection.LATENT_DETECT_ENABLE;

        int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();


//        MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
        ProcessInfo processInfo = ProcessInfo.getInstance();
        Coder coderChoice;
        coderChoice = processInfo.getCoder();

        final TemplateList templateList = new TemplateList();
        ///////////////////to get fingerprint image//////////////
        templateList.setActivateFullImageRetrieving(true);
        ///////////////////to get fingerprint image//////////////

        int ret = morphoDevice.setStrategyAcquisitionMode(ProcessInfo.getInstance().getStrategyAcquisitionMode());
        if (morphoDevice != null) {

            ret = morphoDevice.capture(timeout, acquisitionThreshold, advancedSecurityLevelsRequired,
                    nbFinger, templateType, templateFVPType, maxSizeTemplate, enrollType,
                    latentDetection, coderChoice,
                    detectModeChoice, CompressionAlgorithm.MORPHO_NO_COMPRESS, 0, templateList,
                    callbackCmd, observer);

            Log.d("kishan errorcode", String.valueOf(ret));
            if (ret == ErrorCodes.MORPHO_OK) {
                return templateList;
            }

        }
        return null;
    }


    public Bitmap getRawToBmp(byte[] rawImage, int imageWidth, int imageHeight) {

        byte[] Src = null;
        byte[] Bits = null;
        Bitmap bm = null;
        Src = rawImage;
        Bits = new byte[Src.length * 4];
        int i;

        for (i = 0; i < Src.length; i++) {

            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = ((byte) Src[i]);
            // Invert the source bits
            Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
        }

        // Now put these nice RGBA pixels into a Bitmap object
        bm = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bm;
    }

    public void closeDevice(final MorphoDevice morphoDevice) {
        try {
            if (morphoDevice != null && ProcessInfo.getInstance().isStarted()) {
                System.out.println("kishan closedevice " + morphoDevice + " " + ProcessInfo.getInstance().isStarted());
                morphoDevice.cancelLiveAcquisition();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(" kishan ERROR", e.toString());
        } finally {

        }

    }


    public int verifyFinger(Observer observer, MorphoDevice morphodevice, TemplateList templateList, int timeOut) {
        /*TemplateList templateList = new TemplateList();
        templateList.putTemplate(template);*/
//        MorphoDevice morphoDevice;
        MorphoDevice morphoDevice = morphodevice;
//        ProcessInfo.getInstance().setMorphoDevice(morphoDevice);

        /*byte[] b;
        b = templateList.getTemplate(0).getData();
//        b = template.getData();
        System.out.println(b);
        for(int i=0;i<228;i++)
        {
            System.out.printf("iiiii%02x",b[i]);
        }*/
        int far = MORPHO_FAR_5;
        Coder coderChoice = Coder.MORPHO_DEFAULT_CODER;
        int detectModeChoice = DetectionMode.MORPHO_VERIF_DETECT_MODE.getValue();
        int matchingStrategy = 0;

        int callbackCmd = 0;//ProcessInfo.getInstance().getCallbackCmd();

//        callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

        ResultMatching resultMatching = new ResultMatching();

        int ret = morphoDevice.setStrategyAcquisitionMode(StrategyAcquisitionMode.MORPHO_ACQ_EXPERT_MODE);
//        System.out.println("tag 1111111111 "+ Arrays.toString(template.getData()));

        if (ret == 0) {
            ret = morphoDevice.verify(timeOut, far, coderChoice, detectModeChoice, matchingStrategy, templateList, callbackCmd, observer, resultMatching);
//            resultMatching.
        }
        Log.d("kishan", "verifyFinger: after verify");
        String message = "";


        if (ret == ErrorCodes.MORPHO_OK) {
            message = "Matching Score = " + resultMatching.getMatchingScore() + "\nPK Number = " + resultMatching.getMatchingPKNumber();
            Log.d("kishan verify", message);
            return 0;
//            MainActivity.verifytextView.setText("verification success");
        } else //if(ret == ErrorCodes.MORPHOERR_NO_HIT)
        {
            return -1;
//            MainActivity.verifytextView.setText("verification failure");
        }

//        System.out.println("verify "+ret);
//        Log.d("verify",message);
//        return 0;
    }


    @Override
    public void update(final Observable observable, final Object data) {
        /*try {
            // convert the object to a callback back message.
            CallbackMessage message = (CallbackMessage) data;

            int type = message.getMessageType();

//            System.out.println("kishan update "+type);
//            Log.d("kishan", "update: "+type);
            switch (type) {

                case 1:
                    // message is a command.
                    Integer command = (Integer) message.getMessage();
                    break;
                case 2:
                    // message is a low resolution image, display it.
                    byte[] image = (byte[]) message.getMessage();

                    MorphoImage morphoImage = MorphoImage.getMorphoImageFromLive(image);
                    int imageRowNumber = morphoImage.getMorphoImageHeader()
                            .getNbRow();
                    int imageColumnNumber = morphoImage.getMorphoImageHeader()
                            .getNbColumn();
                    final Bitmap imageBmp = Bitmap.createBitmap(imageColumnNumber,
                            imageRowNumber, Bitmap.Config.ALPHA_8);

//                    System.out.println("kishan image size "+imageRowNumber+" "+imageColumnNumber);
                    CompressionAlgorithm al=morphoImage.getCompressionAlgorithm();
                    al.getCode();
//                    System.out.println("compression code=="+ al.getCode());
                    imageBmp.copyPixelsFromBuffer(ByteBuffer.wrap(
                            morphoImage.getImage(), 0,
                            morphoImage.getImage().length));

                    DefinesClass.mFinalCapturedImageBitmap = imageBmp;

//                    fingerByteArray = ManvishFileUtils.getBytesFromBitmap(imageBmp);

                    break;
                case 3:

                    break;
                // case 4:
                // byte[] enrollcmd = (byte[]) message.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString() + "kishan");
        }*/
    }


    public int verifyMatch(TemplateList templateListCaptured, TemplateList templateListToVerify, MorphoDevice morphoDevice) {

//        Log.d("kishan", "verifyMatch: getMatchingThreshold "+ProcessInfo.getInstance().getMatchingThreshold());
        int far = FalseAcceptanceRate.MORPHO_FAR_5;

        Integer matchingScore = new Integer(0);

        int ret = morphoDevice.verifyMatch(far, templateListCaptured, templateListToVerify, matchingScore);
        String message = "";
        Log.d("kishan", "verifyMatch: return value " + ret);
        if (ret == 0) {
            Log.d("kishan", "verifyMatch score : " + matchingScore);
        }

        Log.d("kishan", "verifyMatch: message " + morphoDevice.getInternalError());

        return matchingScore;
    }
}
