// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.manvish.sampletest.R;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.FieldAttribute;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoField;
import com.morpho.morphosmart.sdk.TemplateType;



public class ConnectionActivity extends Activity
{

	MorphoDevice	morphoDevice;
	private String	sensorName	= "";
	Button			buttonConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (MorphoSample.isRebootSoft)
		{
			MorphoSample.isRebootSoft = false;
			finish();
		}

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_connection);

		buttonConnection = (Button) findViewById(R.id.btn_ok);

		buttonConnection.setEnabled(false);

		morphoDevice = new MorphoDevice();	
		
		USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");
		
		if(USBManager.getInstance().isDevicesHasPermission() == true)
		{
			Button buttonGrantPermission = (Button) findViewById(R.id.btn_grantPermission);
			buttonGrantPermission.setEnabled(false);
		}
	}

	public void grantPermission(View v)
	{
		USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");
	}
		
	@SuppressLint("UseValueOf")
	public void enumerate(View v)
	{
		Integer nbUsbDevice = new Integer(0);		
		
		int ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);

		if (ret == ErrorCodes.MORPHO_OK)
		{
			TextView textViewSensorName = (TextView) findViewById(R.id.textView_serialNumber);

			if (nbUsbDevice > 0)
			{
				sensorName = morphoDevice.getUsbDeviceName(0);
				textViewSensorName.setText(sensorName);
				buttonConnection.setEnabled(true);
			}
			else
			{				
				final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
				alertDialog.setMessage("The device is not detected, or you have not asked USB permissions, please click the button 'Grant Permission'");
				alertDialog.setCancelable(false);
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						//finish();							
					}
				});
				alertDialog.show();
			}
		}
		else
		{
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
			alertDialog.setMessage(ErrorCodes.getError(ret, morphoDevice.getInternalError()));
			alertDialog.setCancelable(false);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					//finish();
				}
			});
			alertDialog.show();
		}
	}

	@SuppressLint("UseValueOf")
	public void connection(View v)
	{
		int ret = morphoDevice.openUsbDevice(sensorName, 0);

		if (ret != 0)
		{
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(this.getResources().getString(R.string.morphosample));
			alertDialog.setMessage(ErrorCodes.getError(ret, morphoDevice.getInternalError()));
			alertDialog.setCancelable(false);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});
			alertDialog.show();
		}
		else
		{
			ProcessInfo.getInstance().setMSOSerialNumber(sensorName);
			String productDescriptor = morphoDevice.getProductDescriptor();
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(productDescriptor, "\n");
			if (tokenizer.hasMoreTokens())
			{
				String l_s_current = tokenizer.nextToken();
				if (l_s_current.contains("FINGER VP") || l_s_current.contains("FVP"))
				{
					MorphoInfo.m_b_fvp = true;
				}
			}

			final MorphoDatabase morphoDatabase = new MorphoDatabase();
			ret = morphoDevice.getDatabase(0, morphoDatabase);

			if (ret != ErrorCodes.MORPHO_OK)
			{
				if (ret == ErrorCodes.MORPHOERR_BASE_NOT_FOUND)
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
							finish();
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
							
							final int ret = morphoDatabase.dbCreate(maxRecord, maxNbFinger, TemplateType.MORPHO_PK_COMP,0,encryptDatabase);
							if (ret == ErrorCodes.MORPHO_OK)
							{
								ProcessInfo.getInstance().setBaseStatusOk(true);
								morphoDevice.closeDevice();
								Intent dialogActivity = new Intent(ConnectionActivity.this, MorphoSample.class);
								startActivity(dialogActivity);
								finish();
							}
							else
							{
								Handler mHandler = new Handler();
								mHandler.post(new Runnable()
								{
									@Override
									public synchronized void run()
									{										
										AlertDialog alertDialog = new AlertDialog.Builder(ConnectionActivity.this).create();
										alertDialog.setTitle("DataBase : dbCreate");
										String msg = getString(R.string.OP_FAILED) + "\n" +  getString(R.string.MORPHOERR_BADPARAMETER);
										alertDialog.setMessage(msg);
										alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener()
										{
											public void onClick(DialogInterface dialog, int which)
											{
											}
										});
										alertDialog.show();
									}
								});								
							}
						}
					});
					
					alertDialog.show();
				}
			}
			else
			{
				morphoDevice.closeDevice();
				Intent dialogActivity = new Intent(ConnectionActivity.this, MorphoSample.class);
				startActivity(dialogActivity);
				finish();
			}
		}
	}

	public void finishDialog(View v)
	{
		this.finish();
	}
}
