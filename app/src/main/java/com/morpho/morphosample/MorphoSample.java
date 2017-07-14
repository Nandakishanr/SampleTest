// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.manvish.sampletest.R;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosample.database.DatabaseArrayAdapter;
import com.morpho.morphosample.database.DatabaseItem;
import com.morpho.morphosample.info.EnrollInfo;
import com.morpho.morphosample.info.FingerPrintInfo;
import com.morpho.morphosample.info.IdentifyInfo;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosample.info.VerifyInfo;
import com.morpho.morphosample.info.subtype.SecurityOption;
import com.morpho.morphosample.info.subtype.SensorWindowPosition;
import com.morpho.morphosample.tools.MorphoTools;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.DescriptorID;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.FieldAttribute;
import com.morpho.morphosmart.sdk.ITemplateType;
import com.morpho.morphosmart.sdk.MatchingStrategy;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoField;
import com.morpho.morphosmart.sdk.MorphoLogLevel;
import com.morpho.morphosmart.sdk.MorphoLogMode;
import com.morpho.morphosmart.sdk.MorphoTypeDeletion;
import com.morpho.morphosmart.sdk.MorphoUser;
import com.morpho.morphosmart.sdk.MorphoUserList;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.SecuConfig;
import com.morpho.morphosmart.sdk.SecurityLevel;
import com.morpho.morphosmart.sdk.StrategyAcquisitionMode;
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVP;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
@SuppressLint("UseValueOf")
public class MorphoSample extends TabActivity implements Observer
{
	static private String		captureTag							= "Capture";
	static private String		enrollTag							= "Enroll";
	static private String		verifyTag							= "Verify";
	static private String		fingerPrintTag						= "FingerPrint";
	static private String		identifyTag							= "Identify";

	static private int			secondPartTabIndex					= 0;

	private boolean				defaultWindowSensorPositionValue	= true;
	private boolean				defaultCoderChoiceValue				= true;
	private boolean				defaultSecurityLevelValue			= true;
	private boolean				defaultMatchingStrategyValue		= true;
	private boolean				defaultStrategyAcquisitionModeValue	= true;
	private Handler				mHandler							= new Handler();
	
	private boolean				defaultLogModeValue					= true;
	private boolean				defaultLogLevelValue				= true;

	static int					nbFile								= 0;
	static String				template1							= "";
	static String				template2							= "";

	static private String		processTag							= "Process";
	static private int			PROCESS_TAB_INDEX					= 5;	
	public static boolean		isRebootSoft						= false;
	// Declare the UI components
	private ListView			databaseListView					= null;

	static MorphoDevice morphoDevice						= new MorphoDevice();
	static MorphoDatabase morphoDatabase						= new MorphoDatabase();
	ArrayList<SecurityOption>	sol									= null;
	private MenuItem 			menuItemMSOConfiguration			= null;
	private MenuItem 			menuItemLoggingParameters			= null;


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		closeDeviceAndFinishActivity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_morpho_sample);
		
		if (ProcessInfo.getInstance().getMorphoDevice() == null)
		{
			String sensorName = ProcessInfo.getInstance().getMSOSerialNumber();
			int ret = morphoDevice.openUsbDevice(sensorName, 0);
			if (ret == ErrorCodes.MORPHO_OK)
			{
				ret = morphoDevice.getDatabase(0, morphoDatabase);

				if (ret != ErrorCodes.MORPHO_OK)
				{
					finish();
				}
			}
			else
			{
				finish();
			}
		}

		Button btn_CreeateDB = (Button) findViewById(R.id.btn_createbase);

		btn_CreeateDB.setEnabled(false);

		ProcessInfo.getInstance().setMorphoDevice(morphoDevice);
		ProcessInfo.getInstance().setMorphoDatabase(morphoDatabase);

		initDatabaseStatus();
		initNoCheck();
		loadDatabaseItem();
		initTabHost();
		initDatabaseInformations();
		initBioSettingsInformations();
		initOptionsInformations();
	
		switch (secondPartTabIndex)
		{
			case 0:
				onDatabaseInfoClick(null);
				break;
			case 1:
				onGeneralBioClick(null);
				break;
			case 2:
				onOptionsClick(null);
				break;
		}
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();

		morphoDevice.resumeConnection(30,this);
	}

	@Override
	protected void onPause()
	{
		if (morphoDevice != null && ProcessInfo.getInstance().isStarted())
		{	
			morphoDevice.cancelLiveAcquisition();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_morpho_sample, menu);
		return true;
	}

	/**
	 * On options item selected.
	 * 
	 * @param item
	 *            the item
	 * @return true, if successful
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 * @since 1.0
	 */
	@Override
	public final boolean onOptionsItemSelected(final MenuItem item)
	{
		int itemId = item.getItemId();
		if (R.id.msoconfiguration == itemId)
		{
			SecuConfig secuConfig = new SecuConfig();
			int ret = morphoDevice.getSecuConfig(secuConfig);
			if (ret == 0)
			{
				String str_secuConfig;

				str_secuConfig = "\r\n" + morphoDevice.getSoftwareDescriptor() + "\r\n\r\n";

				str_secuConfig += "MSO License(s): " + morphoDevice.getStringDescriptorBin(DescriptorID.BINDESC_LICENSES) + "\r\n\r\n";
				
				str_secuConfig += "USB Daemon Version: " + USBManager.getInstance().getUsbDaemonVersion() + "\r\n\r\n";

				str_secuConfig += getResources().getString(R.string.msoserialnumber) + secuConfig.getSerialNumber() + "\r\n\r\n";
				str_secuConfig += getResources().getString(R.string.maxfar) + secuConfig.getSecurityFARDescription() + "\r\n\r\n";

				str_secuConfig += getResources().getString(R.string.securityoptions) + "\r\n";

				ArrayList<SecurityOption> securityOptions = new ArrayList<SecurityOption>();
				securityOptions.add(new SecurityOption(secuConfig.isDownloadIsProtected(), "Download is protected with a signature"));
				securityOptions.add(new SecurityOption(secuConfig.isModeTunneling(), "Mode Tunneling"));
				securityOptions.add(new SecurityOption(secuConfig.isModeOfferedSecurity(), "Mode Offered Security"));
				securityOptions.add(new SecurityOption(secuConfig.isAcceptsOnlySignedTemplates(), "Sensor accepts only signed templates"));
				securityOptions.add(new SecurityOption(secuConfig.isExportScore(), "Export Score"));
				ProcessInfo.getInstance().setSecurityOptions(securityOptions);

				for (SecurityOption so : securityOptions)
				{
					str_secuConfig += so.toString(getResources().getString(R.string.no), getResources().getString(R.string.yes)) + "\r\n";
				}

				alert(ret, 0, "Sensor Configuration", str_secuConfig);
			}
			else
			{
				alert(ret, morphoDevice.getInternalError(), "Sensor Configuration", "");
			}
		}
		else if(R.id.sdkLoggingParameters == itemId)
		{
			copyLogFileParam();
			LayoutInflater factory = LayoutInflater.from(this);
			final View logParamView = factory.inflate(R.layout.log_param, null);
									
			// Set spinner content from enum value for Log Level
			// -----------------------------------------------------------------------------------------
			Spinner spinnerLL = (Spinner) logParamView.findViewById(R.id.spinnerLoggingLevel);
			CharSequence[] itemLLArray = new CharSequence[MorphoLogLevel.values().length];
			int iCC = 0;
			for (MorphoLogLevel value : MorphoLogLevel.values())
			{
				itemLLArray[iCC++] = value.getLabel();
			}
			List<CharSequence> itemLLList = new ArrayList<CharSequence>(Arrays.asList(itemLLArray));
			ArrayAdapter adapterLL = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemLLList);
			adapterLL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerLL.setAdapter(adapterLL);
			// set the default according to value
			int spinnerLLPosition = adapterLL.getPosition(ProcessInfo.getInstance().getLogLevel().getLabel());
			spinnerLL.setSelection(spinnerLLPosition);
			// listen to the event
			spinnerLL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					if (defaultLogLevelValue == true)
					{
						defaultLogLevelValue = false;
					}
					else
					{
						String item = (String) parent.getItemAtPosition(pos);
						ProcessInfo.getInstance().setLogLevel(MorphoLogLevel.fromString(item));
					}
				}

				public void onNothingSelected(AdapterView<?> parent)
				{
				}
			});
			
			// Set spinner content from enum value for Log Mode
			// -----------------------------------------------------------------------------------------
			Spinner spinnerLM = (Spinner) logParamView.findViewById(R.id.spinnerLoggingMode);
			CharSequence[] itemLMArray = new CharSequence[2];			
			itemLMArray[0] = MorphoLogMode.MORPHO_LOG_ENABLE.getLabel();
			itemLMArray[1] = MorphoLogMode.MORPHO_LOG_DISABLE.getLabel();
			
			List<CharSequence> itemLMList = new ArrayList<CharSequence>(Arrays.asList(itemLMArray));
			ArrayAdapter adapterLM = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemLMList);
			adapterLM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerLM.setAdapter(adapterLM);
			// set the default according to value
			int spinnerLMPosition = adapterLM.getPosition(ProcessInfo.getInstance().getLogMode().getLabel());
			spinnerLM.setSelection(spinnerLMPosition);
			// listen to the event
			spinnerLM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					if (defaultLogModeValue == true)
					{
						defaultLogModeValue = false;
					}
					else
					{
						String item = (String) parent.getItemAtPosition(pos);
						ProcessInfo.getInstance().setLogMode(MorphoLogMode.fromString(item));
					}
				}

				public void onNothingSelected(AdapterView<?> parent)
				{
				}
			});
			
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
			alertDialog.setMessage("SDK Logging Parameters  : ");
			alertDialog.setCancelable(false);
			alertDialog.setView(logParamView);
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{				
				}
			});
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					int ret = morphoDevice.setLoggingMode(ProcessInfo.getInstance().getLogMode());

					if(ret != 0)
					{
						alert(ret,morphoDevice.getInternalError(),"SDK Logging Parameters","setLoggingMode");
					}
					else
					{					
						ret = morphoDevice.setLoggingLevelOfGroup(0,ProcessInfo.getInstance().getLogLevel());
						alert(ret,morphoDevice.getInternalError(),"SDK Logging Parameters","");
					}
				}
			});		
			alertDialog.show();
			
		}
		return super.onOptionsItemSelected(item);
	}

	
	private void copyLogFileParam()
	{
		String filename = "Log.ini";
		try {			
			File logFileParam = new File(Environment.getExternalStorageDirectory().getPath(), filename);
			if (!logFileParam.exists()) {
				AssetManager assetManager = getAssets();
				InputStream in = null;
				OutputStream out = null;

				in = assetManager.open(filename);
				out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			}
		} catch (IOException e) {
			Log.e("tag", "Failed to copy asset file: " + filename, e);
		}
	}
	
	private void copyFile(InputStream in, OutputStream out) throws IOException
	{
	      byte[] buffer = new byte[1024];
	      int read;
	      while((read = in.read(buffer)) != -1)
	      {
	            out.write(buffer, 0, read);
	      }
	}
	
	protected void alert(int codeError, int internalError, String title, String message)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		String msg;
		if (codeError == 0)
		{
			msg = "Operation completed successfully";
		}
		else
		{
			msg = "Operation failed\n" + ErrorCodes.getError(codeError, internalError);
		}
		msg += ((message.equalsIgnoreCase("")) ? "" : "\n" + message);
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.show();
	}

	protected void alertClose(String title, String message)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		});
		alertDialog.show();
	}
	
	private void initDatabaseInformations()
	{
		// setting maximum number of templates allowed per user 
		Integer nbFinger = new Integer(0);
		morphoDatabase.getNbFinger(nbFinger);
		ProcessInfo.getInstance().setNumberOfFingerPerRecord(nbFinger);

		//setting number of used records in database
		Long nbUsedRecord = new Long(0);
		morphoDatabase.getNbUsedRecord(nbUsedRecord);
		ProcessInfo.getInstance().setCurrentNumberOfUsedRecordValue(nbUsedRecord);

		Long nbTotalRecord = new Long(0);
		morphoDatabase.getNbTotalRecord(nbTotalRecord);
		ProcessInfo.getInstance().setMaximumNumberOfRecordValue(nbTotalRecord);
		
		Integer status = new Integer(0);
		int ret = morphoDatabase.getDbEncryptionStatus(status);
		
		if(ret != ErrorCodes.MORPHO_OK)
		{
			ProcessInfo.getInstance().setEncryptDatabaseValue("N/A");
		}
		else
		{
			ProcessInfo.getInstance().setEncryptDatabaseValue(status==1?"Yes":"NO");
		}

		try
		{
			TextView maxnb = (TextView) findViewById(R.id.maximumnumberofrecordvalue);
			maxnb.setText(Long.toString(ProcessInfo.getInstance().getMaximumNumberOfRecordValue()));
		}
		catch (Exception e)
		{
		}

		try
		{
			TextView curnb = (TextView) findViewById(R.id.currentnumberofusedrecordvalue);
			curnb.setText(Long.toString(ProcessInfo.getInstance().getCurrentNumberOfUsedRecordValue()));
		}
		catch (Exception e)
		{
		}

		try
		{
			TextView nbFin = (TextView) findViewById(R.id.numberoffingerperrecordvalue);
			nbFin.setText(Integer.toString(ProcessInfo.getInstance().getNumberOfFingerPerRecord()));
		}
		catch (Exception e)
		{
		}
		
		TextView encryptDatabaseStatus = (TextView) findViewById(R.id.encryptDatabase);
		encryptDatabaseStatus.setText(ProcessInfo.getInstance().getEncryptDatabaseValue());

		nbFinger = 0;
		nbUsedRecord = 0L;
		nbTotalRecord = 0L;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initOptionsInformations()
	{
		boolean iv = ProcessInfo.getInstance().isImageViewer();
		((CheckBox) findViewById(R.id.imageviewer)).setChecked(iv);
		boolean apc = ProcessInfo.getInstance().isAsyncPositioningCommand();
		((CheckBox) findViewById(R.id.asyncpositioningcommand)).setChecked(apc);
		boolean aec = ProcessInfo.getInstance().isAsyncEnrollmentCommand();
		((CheckBox) findViewById(R.id.asyncenrollmentcommand)).setChecked(aec);
		boolean adq = ProcessInfo.getInstance().isAsyncDetectQuality();
		((CheckBox) findViewById(R.id.asyncdetectquality)).setChecked(adq);
		boolean acq = ProcessInfo.getInstance().isAsyncCodeQuality();
		((CheckBox) findViewById(R.id.asynccodequality)).setChecked(acq);

		boolean emn = ProcessInfo.getInstance().isExportMatchingPkNumber();
		((CheckBox) findViewById(R.id.exportmatchingpknumber)).setChecked(emn);
		boolean wlo = ProcessInfo.getInstance().isWakeUpWithLedOff();
		((CheckBox) findViewById(R.id.wakeupwithledoff)).setChecked(wlo);

		// Set spinner content from enum value for SensorWindowPosition
		// -----------------------------------------------------------------------------------------
		Spinner spinner = (Spinner) findViewById(R.id.sensorwindowposition);
		CharSequence[] itemArray = new CharSequence[SensorWindowPosition.values().length];
		int i = 0;
		for (SensorWindowPosition value : SensorWindowPosition.values())
		{
			itemArray[i++] = value.getLabel();
		}
		List<CharSequence> itemList = new ArrayList<CharSequence>(Arrays.asList(itemArray));
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		byte[] sensorWindowPosition = morphoDevice.getConfigParam(MorphoDevice.CONFIG_SENSOR_WIN_POSITION_TAG);
		int position = 0;
		if (sensorWindowPosition != null)
		{
			position = sensorWindowPosition[0];
			if (position > 3)
				position = 0;
		}
		ProcessInfo.getInstance().setSensorWindowPosition(SensorWindowPosition.values()[position]);

		int spinnerPosition = adapter.getPosition(ProcessInfo.getInstance().getSensorWindowPosition().getLabel());
		spinner.setSelection(spinnerPosition);
		// listen to the event
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultWindowSensorPositionValue == true)
				{
					defaultWindowSensorPositionValue = false;
				}
				else
				{
					SensorWindowPosition item = SensorWindowPosition.fromString((String) parent.getItemAtPosition(pos));
					ProcessInfo.getInstance().setSensorWindowPosition(item);
					byte[] paramValue = new byte[1];
					paramValue[0] = (byte) item.getCode();
					int ret = morphoDevice.setConfigParam(MorphoDevice.CONFIG_SENSOR_WIN_POSITION_TAG, paramValue);
					if (ret != ErrorCodes.MORPHO_OK)
					{
						alert(ret, 0, "MorphoDevice.setConfigParam", "");
					}
					else
					{
						alert(ret,0,"Sensor Window Position","You must restart the sensor through \"Reboot soft\" function before using this parameter!");					
					}
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initBioSettingsInformations()
	{
		// Init MatchingThreshold
		// -----------------------------------------------------------------------------------------
		final EditText mt = (EditText) findViewById(R.id.matchingthresholdvalue);
		mt.setText(Integer.toString(ProcessInfo.getInstance().getMatchingThreshold()));
		mt.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable arg0)
			{
				try
				{
					final int set = ProcessInfo.getInstance().setMatchingThreshold(Integer.parseInt(arg0.toString().trim()));
					if (set != Integer.parseInt(arg0.toString().trim()))
					{
						mHandler.post(new Runnable()
						{
							@Override
							public synchronized void run()
							{
								mt.setText(Integer.toString(set));
							}
						});
					}
				}
				catch (Exception e)
				{
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}
		});

		// Init Timeout
		// -----------------------------------------------------------------------------------------
		EditText to = (EditText) findViewById(R.id.timeoutsecvalue);
		to.setText(Integer.toString(ProcessInfo.getInstance().getTimeout()));
		to.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable arg0)
			{
				try
				{
					ProcessInfo.getInstance().setTimeout(Integer.parseInt(arg0.toString().trim()));
				}
				catch (Exception e)
				{
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}
		});

		// Set spinner content from enum value for CoderChoice
		// -----------------------------------------------------------------------------------------
		Spinner spinnerCC = (Spinner) findViewById(R.id.coderchoice);
		CharSequence[] itemCCArray = new CharSequence[Coder.values().length];
		int iCC = 0;
		for (Coder value : Coder.values())
		{
			itemCCArray[iCC++] = value.getLabel();
		}
		List<CharSequence> itemCCList = new ArrayList<CharSequence>(Arrays.asList(itemCCArray));
		ArrayAdapter adapterCC = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemCCList);
		adapterCC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCC.setAdapter(adapterCC);
		// set the default according to value
		int spinnerCCPosition = adapterCC.getPosition(ProcessInfo.getInstance().getCoder().getLabel());
		spinnerCC.setSelection(spinnerCCPosition);
		// listen to the event
		spinnerCC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultCoderChoiceValue == true)
				{
					defaultCoderChoiceValue = false;
				}
				else
				{
					String item = (String) parent.getItemAtPosition(pos);
					ProcessInfo.getInstance().setCoder(Coder.fromString(item));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		// Set spinner content from enum value for SecurityLevel
		// -----------------------------------------------------------------------------------------
		Spinner spinnerSL = (Spinner) findViewById(R.id.securitylevel);
		CharSequence[] itemSLArray = new CharSequence[3];

		if (MorphoInfo.m_b_fvp) //FVP
		{
			itemSLArray[0] = SecurityLevel.MULTIMODAL_SECURITY_STANDARD.getLabel();
			itemSLArray[1] = SecurityLevel.MULTIMODAL_SECURITY_MEDIUM.getLabel();
			itemSLArray[2] = SecurityLevel.MULTIMODAL_SECURITY_HIGH.getLabel();
		}
		else
		// MSO
		{
			itemSLArray[0] = SecurityLevel.FFD_SECURITY_LEVEL_LOW_HOST.getLabel();
			itemSLArray[1] = SecurityLevel.FFD_SECURITY_LEVEL_MEDIUM_HOST.getLabel();
			itemSLArray[2] = SecurityLevel.FFD_SECURITY_LEVEL_HIGH_HOST.getLabel();
		}

		List<CharSequence> itemSLList = new ArrayList<CharSequence>(Arrays.asList(itemSLArray));
		ArrayAdapter adapterSL = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemSLList);
		adapterSL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSL.setAdapter(adapterSL);

		int securityLevelValue = morphoDevice.getSecurityLevel();

		SecurityLevel securityLevel = SecurityLevel.fromInt(securityLevelValue, MorphoInfo.m_b_fvp);

		int spinnerSLPosition = adapterSL.getPosition(securityLevel.getLabel());
		spinnerSL.setSelection(spinnerSLPosition);
		ProcessInfo.getInstance().setSecurityLevel(securityLevel);
		// listen to the event
		spinnerSL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultSecurityLevelValue == true)
				{
					defaultSecurityLevelValue = false;
				}
				else
				{
					String label = (String) parent.getItemAtPosition(pos);
					SecurityLevel securityLevel = SecurityLevel.fromString(label, MorphoInfo.m_b_fvp);

					int l_ret = morphoDevice.setSecurityLevel(securityLevel);

					if (l_ret != ErrorCodes.MORPHO_OK)
					{
						alert(l_ret, 0, "setSecurityLevel", "");
					}

					ProcessInfo.getInstance().setSecurityLevel(securityLevel);
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		// Set spinner content from enum value for MatchingStrategy
		// -----------------------------------------------------------------------------------------
		Spinner spinnerMS = (Spinner) findViewById(R.id.matchingstrategy);
		CharSequence[] itemMSArray = new CharSequence[MatchingStrategy.values().length];
		int i = 0;
		for (MatchingStrategy value : MatchingStrategy.values())
		{
			itemMSArray[i++] = value.getLabel();
		}
		List<CharSequence> itemMSList = new ArrayList<CharSequence>(Arrays.asList(itemMSArray));
		ArrayAdapter adapterMS = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMSList);
		adapterMS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMS.setAdapter(adapterMS);
		// set the default according to value
		int spinnerMSPosition = adapterMS.getPosition(ProcessInfo.getInstance().getMatchingStrategy().getLabel());
		spinnerMS.setSelection(spinnerMSPosition);
		// listen to the event
		spinnerMS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultMatchingStrategyValue == true)
				{
					defaultMatchingStrategyValue = false;
				}
				else
				{
					String item = (String) parent.getItemAtPosition(pos);
					ProcessInfo.getInstance().setMatchingStrategy(MatchingStrategy.fromString(item));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		
		// Set spinner content from enum value for Strategy Acquisition Mode
		// -----------------------------------------------------------------------------------------
		Spinner spinnerAS = (Spinner) findViewById(R.id.acquisitionStrategy);
		CharSequence[] itemASArray = new CharSequence[StrategyAcquisitionMode.values().length];
		i = 0;
		for (StrategyAcquisitionMode value : StrategyAcquisitionMode.values())
		{
			itemASArray[i++] = value.getLabel();
		}
		List<CharSequence> itemASList = new ArrayList<CharSequence>(Arrays.asList(itemASArray));
		ArrayAdapter adapterAS = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemASList);
		adapterAS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAS.setAdapter(adapterAS);
		// set the default according to value
		int spinnerASPosition = adapterAS.getPosition(ProcessInfo.getInstance().getStrategyAcquisitionMode().getLabel());
		spinnerAS.setSelection(spinnerASPosition);
		// listen to the event
		spinnerAS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultStrategyAcquisitionModeValue == true)
				{
					defaultStrategyAcquisitionModeValue = false;
				}
				else
				{
					String item = (String) parent.getItemAtPosition(pos);
					ProcessInfo.getInstance().setStrategyAcquisitionMode(StrategyAcquisitionMode.fromString(item));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		//-----------------------------------------------------------------------------------------
		CheckBox ffot = (CheckBox) findViewById(R.id.forcefingerplacementontop);
		ffot.setChecked(ProcessInfo.getInstance().isForceFingerPlacementOnTop());
		
		CheckBox adSecComReq = (CheckBox) findViewById(R.id.advancedseclevelcompatibilityreq);
		if(!MorphoInfo.m_b_fvp)
		{
			adSecComReq.setEnabled(false);
		}

		CheckBox fqt = (CheckBox) findViewById(R.id.fingerqualitythreshold);
		fqt.setChecked(ProcessInfo.getInstance().isFingerprintQualityThreshold());

		// Init Finger Quality Threshold Value
		// -----------------------------------------------------------------------------------------
		EditText fqv = (EditText) findViewById(R.id.fingerqualitythresholdvalue);
		fqv.setText(Integer.toString(ProcessInfo.getInstance().getFingerprintQualityThresholdvalue()));
		fqv.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable arg0)
			{
				try
				{
					ProcessInfo.getInstance().setFingerprintQualityThresholdvalue(Integer.parseInt(arg0.toString().trim()));
				}
				catch (Exception e)
				{
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}
		});
		fqv.setEnabled(ProcessInfo.getInstance().isFingerprintQualityThreshold());		
	}

	private void initTabHost()
	{
		// Initialize the tabs :
		// --------------------------------
		final TabHost tabHost = getTabHost();

		// Tab for Capture
		TabSpec capturespec = tabHost.newTabSpec(captureTag);
		// setting Title and Icon for the Tab
		capturespec.setIndicator(getResources().getString(R.string.title_activity_capture), getResources().getDrawable(R.drawable.ic_launcher));
		Intent captureIntent = new Intent(this, CaptureActivity.class);
		capturespec.setContent(captureIntent);

		// Tab for Verify
		TabSpec verifyspec = tabHost.newTabSpec(verifyTag);
		verifyspec.setIndicator(getResources().getString(R.string.title_activity_verify), getResources().getDrawable(R.drawable.ic_launcher));
		Intent verifyIntent = new Intent(this, VerifyActivity.class);
		verifyspec.setContent(verifyIntent);

		// Tab for GetFingerPrint Image
		TabSpec fingerPrintSpec = tabHost.newTabSpec(fingerPrintTag);
		fingerPrintSpec.setIndicator(getResources().getString(R.string.title_activity_fingerPrint), getResources().getDrawable(R.drawable.ic_launcher));
		Intent fingerPrintIntent = new Intent(this, FingerPrintActivity.class);
		fingerPrintSpec.setContent(fingerPrintIntent);

		// Tab for Enroll
		TabSpec enrollspec = tabHost.newTabSpec(enrollTag);
		enrollspec.setIndicator(getResources().getString(R.string.title_activity_enroll), getResources().getDrawable(R.drawable.ic_launcher));
		Intent enrollIntent = new Intent(this, EnrollActivity.class);
		enrollspec.setContent(enrollIntent);

		// Tab for Identify
		TabSpec identifyspec = tabHost.newTabSpec(identifyTag);
		identifyspec.setIndicator(getResources().getString(R.string.title_activity_identify), getResources().getDrawable(R.drawable.ic_launcher));
		Intent identifyIntent = new Intent(this, IdentifyActivity.class);
		identifyspec.setContent(identifyIntent);

		// Tab for Process
		TabSpec processspec = tabHost.newTabSpec(processTag);
		processspec.setIndicator(getResources().getString(R.string.title_activity_process), getResources().getDrawable(R.drawable.ic_launcher));
		Intent processIntent = new Intent(this, ProcessActivity.class);
		processspec.setContent(processIntent);

		// Adding all TabSpec to TabHost
		try
		{
			tabHost.addTab(capturespec); // Adding Capture tab
		}
		catch (Exception e1)
		{
			e1.getMessage();
		}
		try
		{
			tabHost.addTab(verifyspec); // Adding Verify tab
		}
		catch (Exception e2)
		{
			e2.getMessage();
		}
		try
		{
			tabHost.addTab(fingerPrintSpec); // Adding FingerPrint Image tab
		}
		catch (Exception e3)
		{
			e3.getMessage();
		}
		try
		{
			tabHost.addTab(enrollspec); // Adding Enroll tab
		}
		catch (Exception e4)
		{
			e4.getMessage();
		}
		try
		{
			tabHost.addTab(identifyspec); // Adding Identify tab
		}
		catch (Exception e5)
		{
			e5.getMessage();
		}

		try
		{
			tabHost.addTab(processspec); // Adding Process tab
			tabHost.getTabWidget().getChildTabViewAt(PROCESS_TAB_INDEX).setEnabled(false);
			tabHost.getTabWidget().getChildTabViewAt(PROCESS_TAB_INDEX).setBackgroundColor(Color.LTGRAY);
			tabHost.getTabWidget().getChildTabViewAt(PROCESS_TAB_INDEX).setVisibility(View.GONE);

		}
		catch (Exception e6)
		{
			e6.getMessage();
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			// invalidate Process tab if any other tab is clicked !
			public void onTabChanged(String tabId)
			{
				switch (tabHost.getCurrentTab())
				{
					case 0:
					case 1:
					case 2:
					case 3:
						tabHost.getTabWidget().getChildTabViewAt(PROCESS_TAB_INDEX).setBackgroundColor(Color.LTGRAY);
						break;
					default:
						break;
				}
			}
		});
	}

	static List<DatabaseItem>	databaseItems	= null;

	public int loadDatabaseItem()
	{
		int ret = 0;
		databaseItems = new ArrayList<DatabaseItem>();
		int[] indexDescriptor = new int[3];
		indexDescriptor[0] = 0;
		indexDescriptor[1] = 1;
		indexDescriptor[2] = 2;

		MorphoUserList morphoUserList = new MorphoUserList();
		ret = morphoDatabase.readPublicFields(indexDescriptor, morphoUserList);

		if (ret == 0)
		{
			int l_nb_user = morphoUserList.getNbUser();
			for (int i = 0; i < l_nb_user; i++)
			{
				MorphoUser morphoUser = morphoUserList.getUser(i);
				String userID = morphoUser.getField(0);
				String firstName = morphoUser.getField(1);
				String lastName = morphoUser.getField(2);
				databaseItems.add(new DatabaseItem(userID, firstName, lastName));
			}
		}
		ProcessInfo.getInstance().setDatabaseItems(databaseItems);
		initDatabaseItem();
		ProcessInfo.getInstance().setCurrentNumberOfRecordValue(databaseItems.size());
		return ret;
	}

	private void initDatabaseItem()
	{

		final List<DatabaseItem> databaseItems = ProcessInfo.getInstance().getDatabaseItems();

		// Initialize the database list
		// --------------------------------
		databaseListView = (ListView) findViewById(R.id.databaselist);
		
		DatabaseArrayAdapter databaseArrayAdapter = new DatabaseArrayAdapter(this, R.layout.database_view, databaseItems);
		databaseListView.setAdapter(databaseArrayAdapter);		

		databaseListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
			{
				// Take action here.
				DatabaseItem selected = (DatabaseItem) databaseListView.getItemAtPosition(position);
				if (databaseItems != null && selected != null)
				{
					for (int i = 0; i < databaseItems.size(); i++)
					{
						databaseItems.get(i).setSelected(false);
						if (selected.compareTo(databaseItems.get(i)) == 0)
						{
							ProcessInfo.getInstance().setDatabaseSelectedIndex(i);
							databaseItems.get(i).setSelected(true);
						}
					}
					
					for (int i = 0; i < databaseListView.getChildCount(); i++)
					{
						View v = databaseListView.getChildAt(i);
						if (v != null)
						{
							v.setBackgroundColor(Color.TRANSPARENT);
						}
					}
					view.setBackgroundColor(Color.CYAN);
				}				
			}
		});
		ProcessInfo.getInstance().setCurrentNumberOfRecordValue(databaseItems.size());

		if (databaseItems.size() == 0)
		{
			activateButton(true,false);
		}
		else
		{
			activateButton(true,true);
		}
	}

	public void onNoCheckClick(View view)
	{
		CheckBox noCheck = (CheckBox) view;
		ProcessInfo.getInstance().setNoCheck(noCheck.isChecked());
	}

	private void initNoCheck()
	{
		CheckBox savePKinDatabase = (CheckBox) findViewById(R.id.nocheck);
		savePKinDatabase.setChecked(ProcessInfo.getInstance().isNoCheck());
	}

	private void initDatabaseStatus()
	{
		ImageView iv = (ImageView) findViewById(R.id.basestatusimg);
		int databaseStatus = R.drawable.lederror;
		if (ProcessInfo.getInstance().isBaseStatusOk())
		{
			databaseStatus = R.drawable.ledok;
		}
		iv.setBackgroundResource(databaseStatus);
	}

	/**
	 * Alert.
	 * 
	 * @param msg
	 *            the msg
	 * @since 1.0
	 */
	private void alert(String msg)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.show();
	}
	
	public final void onRebootSoft(View view)
	{				
		enableDisableBoutton(false);
		
		isRebootSoft = true;
		int ret = morphoDevice.rebootSoft(30,this);
		
		if (ret != 0)
		{	
			alert(getString(R.string.msg_rebootfailure));
		}
	}

	public final void onCloseAndQuit(View view)
	{
		finish();
	}

	public void stopProcess()
	{
		if (ProcessInfo.getInstance().isStarted())
		{
			this.stop();
		}
		enableDisableIHM(true);
	}

	private void stop()
	{
		Button startbutton = (Button) findViewById(R.id.startstop);
		unlockScreenOrientation();
		getTabHost().getTabWidget().getChildTabViewAt(0).setEnabled(true);
		getTabHost().getTabWidget().getChildTabViewAt(1).setEnabled(true);
		getTabHost().getTabWidget().getChildTabViewAt(2).setEnabled(true);
		getTabHost().getTabWidget().getChildTabViewAt(3).setEnabled(true);
		getTabHost().getTabWidget().getChildTabViewAt(4).setEnabled(true);
		getTabHost().getTabWidget().getChildTabViewAt(5).setEnabled(true);

		startbutton.setText(getResources().getString(R.string.startstop));
		ProcessInfo.getInstance().setStarted(false);
		if (ProcessInfo.getInstance().isCommandBioStart())
		{
			ProcessInfo.getInstance().getMorphoDevice().cancelLiveAcquisition();
		}
		String currProcessTag = captureTag;
		if (ProcessInfo.getInstance().getMorphoInfo().getClass() == VerifyInfo.class)
		{
			currProcessTag = verifyTag;
		}
		else if (ProcessInfo.getInstance().getMorphoInfo().getClass() == IdentifyInfo.class)
		{
			currProcessTag = identifyTag;
		}
		else if (ProcessInfo.getInstance().getMorphoInfo().getClass() == EnrollInfo.class)
		{
			currProcessTag = enrollTag;
		}
		else if (ProcessInfo.getInstance().getMorphoInfo().getClass() == FingerPrintInfo.class)
		{
			currProcessTag = fingerPrintTag;
		}
		switchTab(currProcessTag);
		if (currProcessTag == enrollTag && EnrollInfo.getInstance().isSavePKinDatabase())
		{
			refreshNbrOfUsedRecord();
			loadDatabaseItem();
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

	    menuItemMSOConfiguration = menu.findItem(R.id.msoconfiguration);
	    menuItemLoggingParameters = menu.findItem(R.id.sdkLoggingParameters);
		return true;
	}

	private void enableDisableIHM(boolean enabled) {
		Button btn = (Button) findViewById(R.id.btn_identitymatch);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_removeall);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_removeuser);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_updateuser);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_closeandquit);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_createbase);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_destroybase);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_identitymatch);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_adduser);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_verifymatch);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_rebootsoft);
		btn.setEnabled(enabled);
		
		if(menuItemMSOConfiguration != null)
		{
			menuItemMSOConfiguration.setEnabled(enabled);
		}
		
		if(menuItemLoggingParameters != null)
		{
			menuItemLoggingParameters.setEnabled(enabled);
		}
	}
	
	public final void onStartStop(View view)
	{
		if (ProcessInfo.getInstance().isStarted())
		{			
			this.stop();
		}
		else
		{			
			MorphoTabActivity current = (MorphoTabActivity) getLocalActivityManager().getCurrentActivity();

			if (current.getClass() == IdentifyActivity.class)
			{
				if (ProcessInfo.getInstance().getDatabaseItems().size() == 0)
				{					
					alert(ErrorCodes.MORPHOERR_DB_EMPTY, 0, "Identify", "");
					return;
				}
				else
				{
					enableDisableIHM(false);
				}
			}

			if ((current.getClass() == CaptureActivity.class) || (current.getClass() == VerifyActivity.class) || (current.getClass() == EnrollActivity.class)
					|| (current.getClass() == IdentifyActivity.class) || (current.getClass() == FingerPrintActivity.class))
			{
				MorphoInfo info = current.retrieveSettings();
				if (info != null)
				{
					enableDisableIHM(false);
					Button startbutton = (Button) findViewById(R.id.startstop);
					ProcessInfo.getInstance().setMorphoInfo(info);
					ProcessInfo.getInstance().setMorphoSample(this);
					getTabHost().getTabWidget().getChildTabViewAt(PROCESS_TAB_INDEX).setBackgroundColor(Color.TRANSPARENT);
					switchTab(processTag);
					try
					{
						ProcessInfo.getInstance().setCommandBioStart(true);
						startbutton.setText(getResources().getString(R.string.stop));
						ProcessInfo.getInstance().setStarted(true);
						lockScreenOrientation();
					}
					catch (Exception e)
					{
					}

					getTabHost().getTabWidget().getChildTabViewAt(0).setEnabled(false);
					getTabHost().getTabWidget().getChildTabViewAt(1).setEnabled(false);
					getTabHost().getTabWidget().getChildTabViewAt(2).setEnabled(false);
					getTabHost().getTabWidget().getChildTabViewAt(3).setEnabled(false);
					getTabHost().getTabWidget().getChildTabViewAt(4).setEnabled(false);
					getTabHost().getTabWidget().getChildTabViewAt(5).setEnabled(false);
				}				
			}
		}
	}

	private void unlockScreenOrientation()
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

	private void lockScreenOrientation()
	{
		final int orientation = getResources().getConfiguration().orientation;
		final int rotation = getWindowManager().getDefaultDisplay().getOrientation();

		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
		{
			if (orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
		else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270)
		{
			if (orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			}
			else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			}
		}
	}

	public void switchTab(String tag)
	{
		getTabHost().setCurrentTabByTag(tag);
	}

	private enum MorphoSampleAction
	{
		onIdentityMatchClick, onVerifyMatchClick, onAddUserClick,
	}

	private MorphoSampleAction	currentAction	= MorphoSampleAction.onIdentityMatchClick;

	public void onIdentityMatchClick(View view)
	{
		currentAction = MorphoSampleAction.onIdentityMatchClick;
		try
		{
			Intent activityIntent = new Intent(this, FileChooserActivity.class);
			// Store the Workflow file in the intent
			startActivityForResult(activityIntent, 0);

		}
		catch (Exception e)
		{
		}
	}

	public void onVerifyMatchClick(View view)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
		alertDialog.setMessage(getString(R.string.message_choise_verify_first));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				currentAction = MorphoSampleAction.onVerifyMatchClick;
				try
				{
					Intent activityIntent = new Intent(MorphoSample.this, FileChooserActivity.class);
					// Store the Workflow file in the intent
					startActivityForResult(activityIntent, 0);

				}
				catch (Exception e)
				{
				}
			}
		});
		alertDialog.show();
	}

	public void onAddUserClick(View view)
	{
		currentAction = MorphoSampleAction.onAddUserClick;
		try
		{
			Intent activityIntent = new Intent(this, FileChooserActivity.class);
			// Store the Workflow file in the intent
			startActivityForResult(activityIntent, 0);

		}
		catch (Exception e)
		{
		}
	}

	/**
	 * Function called when the FileChooserActivity exits!
	 * 
	 * @param requestCode
	 *            : Activity result request code
	 * @param resultCode
	 *            : how did the activity end (Next, Cancel, Back, ...) ?
	 * @param data
	 *            : Activity result data
	 * @since 1.0
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try
		{
			super.onActivityResult(requestCode, resultCode, data);
			if (RESULT_OK == resultCode)
			{
				Bundle b = data.getExtras();
				if (b.containsKey("SelectedFile"))
				{
					if (currentAction == MorphoSampleAction.onAddUserClick)
					{
						addUser(b.getString("SelectedFile"));
					}
					else if (currentAction == MorphoSampleAction.onIdentityMatchClick)
					{
						identityMatch(b.getString("SelectedFile"));
					}
					else if (currentAction == MorphoSampleAction.onVerifyMatchClick)
					{
						nbFile++;
						if (nbFile == 2)
						{
							nbFile = 0;
							template2 = b.getString("SelectedFile");
							verifyMatch(template1, template2);
						}
						else
						{
							template1 = b.getString("SelectedFile");
							AlertDialog alertDialog = new AlertDialog.Builder(this).create();
							alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
							alertDialog.setMessage(getString(R.string.message_choise_verify_second));
							alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int which)
								{
									currentAction = MorphoSampleAction.onVerifyMatchClick;
									try
									{
										Intent activityIntent = new Intent(MorphoSample.this, FileChooserActivity.class);
										// Store the Workflow file in the intent
										startActivityForResult(activityIntent, 0);

									}
									catch (Exception e)
									{
									}
								}
							});
							alertDialog.show();

						}
					}
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	private void verifyMatch(String file1, String file2)
	{
		try
		{
			TemplateList templateListSearch = new TemplateList();
			TemplateList templateListReference = new TemplateList();

			ITemplateType iTemplateType = ProcessActivity.getTemplateTypeFromExtention(ProcessActivity.getFileExtension(file1));
			if (iTemplateType != TemplateType.MORPHO_NO_PK_FP)
			{
				DataInputStream dis = new DataInputStream(new FileInputStream(file1));
				int length = dis.available();
				byte[] buffer = new byte[length];
				dis.readFully(buffer);

				if (iTemplateType instanceof TemplateType)
				{
					Template template = new Template();
					template.setTemplateType((TemplateType) iTemplateType);
					template.setData(buffer);
					templateListSearch.putTemplate(template);
				}
				else
				{
					TemplateFVP template = new TemplateFVP();
					template.setTemplateFVPType((TemplateFVPType) iTemplateType);
					template.setData(buffer);
					templateListSearch.putFVPTemplate(template);
				}
				dis.close();
			}
			else
			{
				alert(file1 + " not valide!!");
				return;
			}

			iTemplateType = ProcessActivity.getTemplateTypeFromExtention(ProcessActivity.getFileExtension(file2));
			if (iTemplateType != TemplateType.MORPHO_NO_PK_FP)
			{
				DataInputStream dis = new DataInputStream(new FileInputStream(file2));
				int length = dis.available();
				byte[] buffer = new byte[length];
				dis.readFully(buffer);

				if (iTemplateType instanceof TemplateType)
				{
					Template template = new Template();
					template.setTemplateType((TemplateType) iTemplateType);
					template.setData(buffer);
					templateListReference.putTemplate(template);
				}
				else
				{
					TemplateFVP template = new TemplateFVP();
					template.setTemplateFVPType((TemplateFVPType) iTemplateType);
					template.setData(buffer);
					templateListReference.putFVPTemplate(template);
				}
				dis.close();
			}
			else
			{
				alert(file2 + " not valide!!");
				return;
			}

			int far = ProcessInfo.getInstance().getMatchingThreshold();
			Integer matchingScore = new Integer(0);

			int ret = morphoDevice.verifyMatch(far, templateListSearch, templateListReference, matchingScore);
			String message = "";
			if (ret == 0)
			{
				message = "Matching Score : " + matchingScore;
			}

			alert(ret, morphoDevice.getInternalError(), "Verify Match", message);

		}
		catch (IOException e)
		{
			alert(e.getMessage());
		}
	}

	private void identityMatch(String fileName)
	{
		try
		{
			DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
			int length = dis.available();
			byte[] buffer = new byte[length];
			dis.readFully(buffer);
			TemplateList templateList = new TemplateList();
			Template template = new Template();
			TemplateFVP templateFVP = new TemplateFVP();
			ITemplateType iTemplateType = ProcessActivity.getTemplateTypeFromExtention(ProcessActivity.getFileExtension(fileName));
			if (iTemplateType != TemplateType.MORPHO_NO_PK_FP)
			{
				if (iTemplateType instanceof TemplateType)
				{
					template.setTemplateType((TemplateType) iTemplateType);
					template.setData(buffer);
					templateList.putTemplate(template);
				}
				else
				{
					templateFVP.setTemplateFVPType((TemplateFVPType) iTemplateType);
					templateFVP.setData(buffer);
					templateList.putFVPTemplate(templateFVP);
				}
				dis.close();
			}
			else
			{
				alert(fileName + " not valide!!");
				dis.close();
				return;
			}

			dis.close();

			MorphoUser morphoUser = new MorphoUser();
			int far = ProcessInfo.getInstance().getMatchingThreshold();
			ResultMatching resultMatching = new ResultMatching();
			resultMatching.setMatchingScore(0);
			resultMatching.setMatchingPKNumber(255);
			int ret = morphoDatabase.identifyMatch(far, templateList, morphoUser, resultMatching);

			if (ret == 0)
			{
				String userID = morphoUser.getField(0);
				String firstName = morphoUser.getField(1);
				String lastName = morphoUser.getField(2);
				String message = "User identified";
				message += "\r\nUser ID   : \t\t" + userID;
				message += "\r\nFirstName : \t\t" + firstName;
				message += "\r\nLastName  : \t\t" + lastName;
				alert(ret, morphoDevice.getInternalError(), "Identify Match", message);
			}
			else
			{
				alert(ret, morphoDevice.getInternalError(), "Identify Match", "");
			}

		}
		catch (FileNotFoundException e)
		{
			alert(e.getMessage());
		}
		catch (IOException e)
		{
			alert(e.getMessage());
		}
	}

	public void addUser(String fileName)
	{
		try
		{
			DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
			int length = dis.available();
			final byte[] buffer = new byte[length];
			dis.readFully(buffer);
			dis.close();
			final Template template = new Template();
			final TemplateFVP templateFVP = new TemplateFVP();

			//Construct the TemplateType
			final ITemplateType iTemplateType = ProcessActivity.getTemplateTypeFromExtention(ProcessActivity.getFileExtension(fileName));
			// Set the interface TemplateType as TemplateFVPType if template is a FVP type
			// else set it to simple PK template
			if (iTemplateType != TemplateType.MORPHO_NO_PK_FP)
			{
				if (iTemplateType instanceof TemplateFVPType)
				{
					templateFVP.setTemplateFVPType((TemplateFVPType) iTemplateType);
				}
				else
				{
					template.setTemplateType((TemplateType) iTemplateType);
				}
			}
			else
			{
				alert(fileName + " not valide!!");
				return;
			}

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.user_information, null);
			final EditText idUser = (EditText) textEntryView.findViewById(R.id.idnumberUI);
			final EditText firstName = (EditText) textEntryView.findViewById(R.id.firstnameUI);
			final EditText lastName = (EditText) textEntryView.findViewById(R.id.lastnameUI);

			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
			alertDialog.setMessage("Add User : ");
			alertDialog.setCancelable(false);
			alertDialog.setView(textEntryView);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{					
					MorphoUser morphoUser = new MorphoUser();
					int ret = morphoDatabase.getUser(idUser.getText().toString(), morphoUser);
					if (ret == 0)
					{
						ret = morphoUser.putField(1, MorphoTools.checkfield(firstName.getText().toString(),false));
					}

					if (ret == 0)
					{
						ret = morphoUser.putField(2, MorphoTools.checkfield(lastName.getText().toString(),false));
					}
					String additionalMessage = "";
					if (ret == 0)
					{
						Integer index = new Integer(0);

						if (iTemplateType instanceof TemplateType)
						{
							template.setData(buffer);
							morphoUser.putTemplate(template, index);

						}
						else
						{
							templateFVP.setData(buffer);
							morphoUser.putFVPTemplate(templateFVP, index);
						}
					}
					ProcessInfo processInfo = ProcessInfo.getInstance();
					if (processInfo.isNoCheck())
					{
						morphoUser.setNoCheckOnTemplateForDBStore(true);
					}
					
					ret = morphoUser.dbStore();
					
					if (ret == 0)
					{
						DatabaseItem databaseItemsItem = new DatabaseItem(idUser.getText().toString(),
								MorphoTools.checkfield(firstName.getText().toString(),false),
								MorphoTools.checkfield(lastName.getText().toString(),false));
						List<DatabaseItem> databaseItems = processInfo.getDatabaseItems();
						databaseItems.add(databaseItemsItem);
						processInfo.setDatabaseItems(databaseItems);
						refreshNbrOfUsedRecord();
						loadDatabaseItem();
					}					
					
					alert(ret, morphoDevice.getInternalError(), "Add User", additionalMessage);
				}
			});
			alertDialog.show();

		}
		catch (FileNotFoundException e)
		{
			alert(e.getMessage());
		}
		catch (IOException e)
		{
			alert(e.getMessage());
		}
	}

	public void onUpdateUserClick(View view)
	{
		if (ProcessInfo.getInstance().getDatabaseSelectedIndex() != -1)
		{
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.user_information, null);
			final EditText idUser = (EditText) textEntryView.findViewById(R.id.idnumberUI);
			final EditText firstName = (EditText) textEntryView.findViewById(R.id.firstnameUI);
			final EditText lastName = (EditText) textEntryView.findViewById(R.id.lastnameUI);
			idUser.setEnabled(false);
			idUser.setText(ProcessInfo.getInstance().getDatabaseItems().get(ProcessInfo.getInstance().getDatabaseSelectedIndex()).getId());
			firstName.setText(ProcessInfo.getInstance().getDatabaseItems().get(ProcessInfo.getInstance().getDatabaseSelectedIndex()).getFirstName());
			lastName.setText(ProcessInfo.getInstance().getDatabaseItems().get(ProcessInfo.getInstance().getDatabaseSelectedIndex()).getLastName());

			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
			alertDialog.setMessage("Update User : ");
			alertDialog.setCancelable(false);
			alertDialog.setView(textEntryView);	
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{

				}
			});
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					try
					{
						MorphoUser morphoUser = new MorphoUser();
						int ret = morphoDatabase.getUser(idUser.getText().toString(), morphoUser);

						if (ret == 0)
						{
							ret = morphoUser.putField(1, MorphoTools.checkfield(firstName.getText().toString(),false));
						}

						if (ret == 0)
						{
							ret = morphoUser.putField(2, MorphoTools.checkfield(lastName.getText().toString(),false));
						}

						ret = morphoUser.dbUpdatePublicFields();

						if (ret == 0)
						{
							DatabaseItem databaseItemsItem = new DatabaseItem(idUser.getText().toString(), MorphoTools.checkfield(firstName.getText().toString(),false), MorphoTools.checkfield(lastName.getText().toString(),false));
							List<DatabaseItem> databaseItems = ProcessInfo.getInstance().getDatabaseItems();
							databaseItems.remove(ProcessInfo.getInstance().getDatabaseSelectedIndex());
							databaseItems.add(ProcessInfo.getInstance().getDatabaseSelectedIndex(), databaseItemsItem);
							ProcessInfo.getInstance().setDatabaseItems(databaseItems);
							loadDatabaseItem();
						}
						alert(ret, morphoDevice.getInternalError(), "Update User", "");
					}
					catch (Exception e)
					{
						Log.e("ERROR", e.toString());
					}
					finally
					{
						ProcessInfo.getInstance().setDatabaseSelectedIndex(-1);
					}
				}
			});
			
			alertDialog.show();
		}
		else
		{
			alert(this.getResources().getString(R.string.selectuserfirst));
		}
	}

	public void onRemoveUserClick(View view)
	{
		try
		{
			if (ProcessInfo.getInstance().getDatabaseSelectedIndex() != -1)
			{
				MorphoUser morphoUser = new MorphoUser();
				ProcessInfo.getInstance().getDatabaseSelectedIndex();
				String userID = ProcessInfo.getInstance().getDatabaseItems().get(ProcessInfo.getInstance().getDatabaseSelectedIndex()).getId();
				int ret = morphoDatabase.getUser(userID, morphoUser);
				if (ret == 0)
				{
					ret = morphoUser.dbDelete();
					alert(ret, morphoDevice.getInternalError(), "Remove User", "");
					if (ret == 0)
					{
						ProcessInfo.getInstance().getDatabaseItems().remove(ProcessInfo.getInstance().getDatabaseSelectedIndex());
						refreshNbrOfUsedRecord();
						loadDatabaseItem();
					}
				}
				else
				{
					alert(ret, morphoDevice.getInternalError(), "MorphoDatabase GetUser", "");
				}

			}
			else
			{
				alert(this.getResources().getString(R.string.selectuserfirst));
			}
		}
		catch (Exception e)
		{
			Log.e("ERROR", e.toString());
		}
		finally
		{
			ProcessInfo.getInstance().setDatabaseSelectedIndex(-1);
		}
	}

	public void activateButton(boolean isBaseAvailable, boolean isBaseNotEmpty)
	{
		Button btn = null;
		if(isBaseAvailable)
		{
			btn = (Button) findViewById(R.id.btn_createbase);
			btn.setEnabled(false);
			
			btn = (Button) findViewById(R.id.btn_adduser);
			btn.setEnabled(true);
			
			btn = (Button) findViewById(R.id.btn_destroybase);
			btn.setEnabled(true);
			
			if(isBaseNotEmpty)
			{
				btn = (Button) findViewById(R.id.btn_updateuser);
				btn.setEnabled(true);
				
				btn = (Button) findViewById(R.id.btn_removeuser);
				btn.setEnabled(true);
				
				btn = (Button) findViewById(R.id.btn_removeall);
				btn.setEnabled(true);
				
				btn = (Button) findViewById(R.id.btn_identitymatch);
				btn.setEnabled(true);
			}
			else
			{
				btn = (Button) findViewById(R.id.btn_updateuser);
				btn.setEnabled(false);
				
				btn = (Button) findViewById(R.id.btn_removeuser);
				btn.setEnabled(false);
				
				btn = (Button) findViewById(R.id.btn_removeall);
				btn.setEnabled(false);
				
				btn = (Button) findViewById(R.id.btn_identitymatch);
				btn.setEnabled(false);
			}
		}
		else
		{
			btn = (Button) findViewById(R.id.btn_createbase);
			btn.setEnabled(true);
			
			btn = (Button) findViewById(R.id.btn_updateuser);
			btn.setEnabled(false);
			
			btn = (Button) findViewById(R.id.btn_removeuser);
			btn.setEnabled(false);
			
			btn = (Button) findViewById(R.id.btn_removeall);
			btn.setEnabled(false);
			
			btn = (Button) findViewById(R.id.btn_identitymatch);
			btn.setEnabled(false);
			
			btn = (Button) findViewById(R.id.btn_adduser);
			btn.setEnabled(false);
			
			btn = (Button) findViewById(R.id.btn_destroybase);
			btn.setEnabled(false);
		}
	}

	public void onRemoveAllClick(View view)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
		alertDialog.setMessage(this.getResources().getString(R.string.eraseall));
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				int ret = morphoDatabase.dbDelete(MorphoTypeDeletion.MORPHO_ERASE_BASE);
				if (ret == 0)
				{
					ProcessInfo.getInstance().getDatabaseItems().clear();
					refreshNbrOfUsedRecord();
					loadDatabaseItem();
				}
				alert(ret, morphoDevice.getInternalError(), "Remove All", "");
			}
		});		
		alertDialog.show();
	}

	public void onCreateBaseClick(View view)
	{
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.base_config, null);
		final EditText input1 = (EditText) textEntryView.findViewById(R.id.editTextMaximumnumberofrecord);
		final EditText input2 = (EditText) textEntryView.findViewById(R.id.editTextNumberoffingerperrecord);
		input1.setText("500");
		input2.setText("2");
		final RadioGroup radioEncryptDatabase = (RadioGroup) textEntryView.findViewById(R.id.radioEncryptDatabase);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
		alertDialog.setMessage("Data Base configuration : ");
		alertDialog.setCancelable(false);
		alertDialog.setView(textEntryView);
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{				
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Integer index = new Integer(0);
				MorphoField morphoFieldFirstName = new MorphoField();
				morphoFieldFirstName.setName("First");
				morphoFieldFirstName.setMaxSize(15);
				morphoFieldFirstName.setFieldAttribute(FieldAttribute.MORPHO_PUBLIC_FIELD);
				morphoDatabase.putField(morphoFieldFirstName, index);
				MorphoField morphoFieldLastName = new MorphoField();
				morphoFieldLastName.setName("Last");
				morphoFieldLastName.setMaxSize(15);
				morphoFieldLastName.setFieldAttribute(FieldAttribute.MORPHO_PUBLIC_FIELD);
				morphoDatabase.putField(morphoFieldLastName, index);

				int maxRecord = Integer.parseInt(input1.getText().toString());
				int maxNbFinger = Integer.parseInt(input2.getText().toString());

				boolean encryptDatabase = false;
				
				if(radioEncryptDatabase.getCheckedRadioButtonId() == R.id.radioButtonencryptDatabaseYes)
				{
					encryptDatabase = true;
				}
				int ret = morphoDatabase.dbCreate(maxRecord, maxNbFinger, TemplateType.MORPHO_PK_COMP,0, encryptDatabase);
				if (ret == ErrorCodes.MORPHO_OK)
				{
					ProcessInfo.getInstance().setBaseStatusOk(true);
					initDatabaseStatus();

					Button btn = (Button) findViewById(R.id.btn_createbase);
					btn.setEnabled(false);
					btn = (Button) findViewById(R.id.btn_destroybase);
					btn.setEnabled(true);

					loadDatabaseItem();
					initDatabaseInformations();

					getTabHost().getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
					getTabHost().getTabWidget().getChildTabViewAt(4).setVisibility(View.VISIBLE);
				}
				alert(ret, morphoDevice.getInternalError(), "Create Base", "");
			}
		});		
		alertDialog.show();
	}

	public void onDestroyBaseClick(View view)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
		alertDialog.setMessage(this.getResources().getString(R.string.destroyconfirm));
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				int ret = morphoDatabase.dbDelete(MorphoTypeDeletion.MORPHO_DESTROY_BASE);
				if (ret == 0)
				{
					ProcessInfo.getInstance().setBaseStatusOk(false);
					initDatabaseStatus();

					loadDatabaseItem();
					initDatabaseInformations();

					activateButton(false,false);
					Button btn = (Button) findViewById(R.id.btn_createbase);
					btn.setEnabled(true);
					btn = (Button) findViewById(R.id.btn_destroybase);
					btn.setEnabled(false);

					getTabHost().getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);
					getTabHost().getTabWidget().getChildTabViewAt(4).setVisibility(View.GONE);
					String currProcessTag = getTabHost().getCurrentTabTag();
					if (enrollTag.equals(currProcessTag) || identifyTag.equals(currProcessTag))
					{
						currProcessTag = captureTag;
					}
					switchTab(currProcessTag);
				}
				alert(ret, morphoDevice.getInternalError(), "Destroy Base", "");
			}
		});		
		alertDialog.show();
	}

	private void hideLayout(int id, int button)
	{
		try
		{
			LinearLayout ll = (LinearLayout) findViewById(id);
			ll.setVisibility(LinearLayout.GONE);
			Button b = (Button) findViewById(button);
			b.setBackgroundColor(Color.TRANSPARENT);
		}
		catch (Exception e)
		{
		}
	}

	private void hideLayouts()
	{
		hideLayout(R.id.infodataselayout, R.id.databaseinformation);
		hideLayout(R.id.biosettingslayout, R.id.generalbiometricsettings);
		hideLayout(R.id.optionslayout, R.id.options);
	}

	private void showLayout(int id, int button)
	{
		try
		{
			LinearLayout ll = (LinearLayout) findViewById(id);
			ll.setVisibility(LinearLayout.VISIBLE);
			Button b = (Button) findViewById(button);
			b.setBackgroundColor(Color.WHITE);
		}
		catch (Exception e)
		{
		}
	}

	public void onDatabaseInfoClick(View view)
	{
		hideLayouts();
		showLayout(R.id.infodataselayout, R.id.databaseinformation);
		secondPartTabIndex = 0;
	}

	public void onGeneralBioClick(View view)
	{
		hideLayouts();
		showLayout(R.id.biosettingslayout, R.id.generalbiometricsettings);
		secondPartTabIndex = 1;
	}

	public void onOptionsClick(View view)
	{
		hideLayouts();
		showLayout(R.id.optionslayout, R.id.options);
		secondPartTabIndex = 2;
	}

	public void onImageViewerClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setImageViewer(((CheckBox) findViewById(R.id.imageviewer)).isChecked());

		}
		catch (Exception e)
		{
		}
	}

	public void onAsyncPositioningCommandClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setAsyncPositioningCommand(((CheckBox) findViewById(R.id.asyncpositioningcommand)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onAsyncEnrollmentCommandClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setAsyncEnrollmentCommand(((CheckBox) findViewById(R.id.asyncenrollmentcommand)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onAsyncDetectQualityClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setAsyncDetectQuality(((CheckBox) findViewById(R.id.asyncdetectquality)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onAsyncCodeQualityClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setAsyncCodeQuality(((CheckBox) findViewById(R.id.asynccodequality)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onExportMatchingPkNumberClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setExportMatchingPkNumber(((CheckBox) findViewById(R.id.asyncdetectquality)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onWakeUpWithLedOffClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setWakeUpWithLedOff(((CheckBox) findViewById(R.id.wakeupwithledoff)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onForceFingerPlacementOnTopClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setForceFingerPlacementOnTop(((CheckBox) findViewById(R.id.forcefingerplacementontop)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onAdvancedSecLevelCompatibilityReq(View view)
	{
		try
		{
			ProcessInfo.getInstance().setAdvancedSecLevCompReq(((CheckBox) findViewById(R.id.advancedseclevelcompatibilityreq)).isChecked());
		}
		catch (Exception e)
		{
		}
	}

	public void onFingerQualityThresholdClick(View view)
	{
		try
		{
			ProcessInfo.getInstance().setFingerprintQualityThreshold(((CheckBox) findViewById(R.id.fingerqualitythreshold)).isChecked());
			TextView fqtv = (TextView) findViewById(R.id.fingerqualitythresholdvalue);
			fqtv.setEnabled(ProcessInfo.getInstance().isFingerprintQualityThreshold());
		}
		catch (Exception e)
		{
		}
	}

	public void refreshNbrOfUsedRecord()
	{
		//setting number of used records in database
		Long nbUsedRecord = new Long(0);
		morphoDatabase.getNbUsedRecord(nbUsedRecord);
		ProcessInfo.getInstance().setCurrentNumberOfUsedRecordValue(nbUsedRecord);

		try
		{
			TextView curnb = (TextView) findViewById(R.id.currentnumberofusedrecordvalue);
			curnb.setText(Long.toString(ProcessInfo.getInstance().getCurrentNumberOfUsedRecordValue()));
		}
		catch (Exception e)
		{
		}
	}

	public void closeDeviceAndFinishActivity()
	{
		try
		{
			morphoDevice.closeDevice();
		}
		catch (Exception e)
		{
			Log.e("ERROR", e.toString());
		}
		finally
		{
			ProcessInfo.getInstance().setMorphoDevice(null);
			ProcessInfo.getInstance().setMorphoDatabase(null);
			ProcessInfo.getInstance().setDatabaseSelectedIndex(-1);
		}
	}

	@Override
	public synchronized void update(Observable observable, Object data)
	{
		Boolean isOpenOK = (Boolean) data;
		
		MorphoSample.isRebootSoft = false;		
		
		if(isOpenOK == true)
		{
			mHandler.post(new Runnable()
			{
				@Override
				public synchronized void run()
				{
					enableDisableBoutton(true);					
				}
			});		
		}
		else
		{
			mHandler.post(new Runnable()
			{
				@Override
				public synchronized void run()
				{
					Button btn = (Button) findViewById(R.id.btn_closeandquit);
					btn.setEnabled(true);
					alert(ErrorCodes.MORPHOERR_RESUME_CONNEXION,0,"Resume Connection","");
				}
			});
		}		
	}
	
	private void enableDisableBoutton(boolean enabled) {
		Button btn = (Button) findViewById(R.id.btn_identitymatch);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_removeall);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_removeuser);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_updateuser);
		btn.setEnabled(enabled);

		btn = (Button) findViewById(R.id.btn_closeandquit);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_createbase);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_destroybase);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_identitymatch);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_adduser);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_verifymatch);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.startstop);
		btn.setEnabled(enabled);
		
		btn = (Button) findViewById(R.id.btn_rebootsoft);
		btn.setEnabled(enabled);
		
		if(menuItemMSOConfiguration != null)
		{
			menuItemMSOConfiguration.setEnabled(enabled);
		}	
		
		if(menuItemLoggingParameters != null)
		{
			menuItemLoggingParameters.setEnabled(enabled);
		}
	}
}
