// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.manvish.sampletest.R;
import com.morpho.morphosample.database.DatabaseItem;
import com.morpho.morphosample.info.CaptureInfo;
import com.morpho.morphosample.info.EnrollInfo;
import com.morpho.morphosample.info.FingerPrintInfo;
import com.morpho.morphosample.info.IdentifyInfo;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosample.info.VerifyInfo;
import com.morpho.morphosample.info.subtype.AuthenticationMode;
import com.morpho.morphosample.info.subtype.CaptureType;
import com.morpho.morphosample.info.subtype.FingerPrintMode;
import com.morpho.morphosample.tools.MorphoTools;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.FalseAcceptanceRate;
import com.morpho.morphosmart.sdk.ITemplateType;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MatchingStrategy;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoImage;
import com.morpho.morphosmart.sdk.MorphoUser;
import com.morpho.morphosmart.sdk.MorphoWakeUpMode;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVP;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ProcessActivity extends MorphoTabActivity implements Observer
{

	private int				currentCaptureBitmapId	= 0;
	private boolean			isCaptureVerif			= false;
	private Handler			mHandler				= new Handler();
	String					strMessage				= new String();
	private int				index;
	private MorphoDevice	morphoDevice;
	private MorphoDatabase	morphoDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		morphoDatabase = ProcessInfo.getInstance().getMorphoDatabase();
		morphoDevice = ProcessInfo.getInstance().getMorphoDevice();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{		
		getMenuInflater().inflate(R.menu.activity_process, menu);
		return true;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		LinearLayout ll = (LinearLayout) findViewById(R.id.content_process);
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int layoutId = R.layout.activity_process_capture;
		if ((ProcessInfo.getInstance().getMorphoInfo().getClass() == VerifyInfo.class) || (ProcessInfo.getInstance().getMorphoInfo().getClass() == IdentifyInfo.class)
				|| (ProcessInfo.getInstance().getMorphoInfo().getClass() == FingerPrintInfo.class))
		{
			layoutId = R.layout.activity_process_verify;
		}
		ViewGroup vg = (ViewGroup) vi.inflate(layoutId, null);
		ll.removeAllViews();
		ll.addView(vg);
		final MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
		final ProcessActivity processActivity = this;
		currentCaptureBitmapId = R.id.imageView1;

		if (morphoInfo.getClass() == VerifyInfo.class)
		{
			verify(processActivity);
		}
		else if (morphoInfo.getClass() == CaptureInfo.class)
		{
			morphoDeviceCapture(processActivity);
		}
		else if (morphoInfo.getClass() == EnrollInfo.class)
		{
			morphoUserEnroll(processActivity);
		}
		else if (morphoInfo.getClass() == IdentifyInfo.class)
		{
			morphoDatabaseIdentify(processActivity);
		}
		else if (morphoInfo.getClass() == FingerPrintInfo.class)
		{
			morphoDeviceGetImage(processActivity);
		}
	}

	public void morphoDeviceGetImage(final Observer observer)
	{
		Thread commandThread = (new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();				
				int timeOut = ProcessInfo.getInstance().getTimeout();
				int acquisitionThreshold = 0;
				CompressionAlgorithm compressAlgo;
				int compressRate = 0;
				int detectModeChoice;
				LatentDetection latentDetection;
				MorphoImage morphoImage = new MorphoImage();
				int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

				callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

				compressAlgo = ((FingerPrintInfo) morphoInfo).getCompressionAlgorithm();

				if(ProcessInfo.getInstance().isFingerprintQualityThreshold())
				{
					acquisitionThreshold = ProcessInfo.getInstance().getFingerprintQualityThresholdvalue();
				}
				
				if (((FingerPrintInfo) morphoInfo).getCompressionAlgorithm().equals(CompressionAlgorithm.MORPHO_COMPRESS_WSQ))
				{
					compressRate = ((FingerPrintInfo) morphoInfo).getCompressRatio();
				}

				detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();

				if (((FingerPrintInfo) morphoInfo).getFingerPrintMode().equals(FingerPrintMode.Verify))
				{
					detectModeChoice = DetectionMode.MORPHO_VERIF_DETECT_MODE.getValue();
				}

				if (ProcessInfo.getInstance().isForceFingerPlacementOnTop())
				{
					detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
				}

				if (ProcessInfo.getInstance().isWakeUpWithLedOff())
				{
					detectModeChoice |= MorphoWakeUpMode.MORPHO_WAKEUP_LED_OFF.getCode();
				}

				latentDetection = (((FingerPrintInfo) morphoInfo).isLatentDetect() == true ? LatentDetection.LATENT_DETECT_ENABLE : LatentDetection.LATENT_DETECT_DISABLE);

				final int ret = morphoDevice.getImage(timeOut, acquisitionThreshold, compressAlgo, compressRate, detectModeChoice, latentDetection, morphoImage, callbackCmd, observer);
				ProcessInfo.getInstance().setCommandBioStart(false);

				getAndWriteFFDLogs();

				String message = "";
				if (ret == ErrorCodes.MORPHO_OK)
				{
					try
					{
						String fileName = "sdcard/Image" + morphoImage.getCompressionAlgorithm().getExtension();

						if (compressAlgo.equals(CompressionAlgorithm.MORPHO_NO_COMPRESS))
						{
							FileOutputStream fos = new FileOutputStream(fileName);
							fos.write(morphoImage.getImage());
							message = "Image RAW successfully exported in file [" + fileName + "]";
							fos.close();
						}
						else if (compressAlgo.equals(CompressionAlgorithm.MORPHO_COMPRESS_V1))
						{
							fileName = "sdcard/Image" + CompressionAlgorithm.MORPHO_COMPRESS_V1.getExtension();
							FileOutputStream fos = new FileOutputStream(fileName);
							fos.write(morphoImage.getCompressedImage());
							message = "Image SAGEM_V1 successfully exported in file [" + fileName + "]";
							fos.close();
						}
						else if (compressAlgo.equals(CompressionAlgorithm.MORPHO_COMPRESS_WSQ))
						{
							FileOutputStream fos = new FileOutputStream(fileName);
							fos.write(morphoImage.getCompressedImage());
							message = "Image WSQ successfully exported in file [" + fileName + "]";
							fos.close();
						}

					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Exception ex)
					{
						alert(ex.getMessage());
					}
				}

				final String alertMessage = message;
				final int internalError = morphoDevice.getInternalError();
				mHandler.post(new Runnable()
				{
					@Override
					public synchronized void run()
					{
						alert(ret, internalError, "GetImage", alertMessage);
					}
				});
				notifyEndProcess();
			}
		}));
		commandThread.start();
	}

	public void verify(final Observer observer)
	{
		MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
		AuthenticationMode am = ((VerifyInfo) morphoInfo).getAuthenticationMode();
		if (am == AuthenticationMode.File)
		{
			final String fileName = ((VerifyInfo) morphoInfo).getFileName();
			morphoDeviceVerifyWithFile(observer, fileName);
		}
		else
		{
			morphoUserVerify(observer);
		}
	}

	public void morphoDatabaseIdentify(final Observer observer)
	{
		Thread commandThread = (new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				index = 0;
				int timeout = ProcessInfo.getInstance().getTimeout();
				int far = ProcessInfo.getInstance().getMatchingThreshold();
				Coder coder = ProcessInfo.getInstance().getCoder();
				int detectModeChoice;
				MatchingStrategy matchingStrategy = ProcessInfo.getInstance().getMatchingStrategy();
				int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();
				callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();
				ResultMatching resultMatching = new ResultMatching();
				final MorphoUser morphoUser = new MorphoUser();

				if (ProcessInfo.getInstance().isForceFingerPlacementOnTop())
				{
					detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();
					detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
				}
				else
				{
					detectModeChoice = DetectionMode.MORPHO_VERIF_DETECT_MODE.getValue();
					if (ProcessInfo.getInstance().isWakeUpWithLedOff())
					{
						detectModeChoice |= MorphoWakeUpMode.MORPHO_WAKEUP_LED_OFF.getCode();
					}
				}
				
				int ret = morphoDevice.setStrategyAcquisitionMode(ProcessInfo.getInstance().getStrategyAcquisitionMode());
						
				if(ret == 0)
				{
					ret = morphoDatabase.identify(timeout, far, coder, detectModeChoice, matchingStrategy, callbackCmd, observer, resultMatching, 2, morphoUser);
				}

				ProcessInfo.getInstance().setCommandBioStart(false);
				getAndWriteFFDLogs();

				final int retvalue = ret;
				mHandler.post(new Runnable()
				{
					@Override
					public synchronized void run()
					{
						String message = "";
						if (retvalue == 0)
						{
							String userID = morphoUser.getField(0);
							String firstName = morphoUser.getField(1);
							String lastName = morphoUser.getField(2);
							message = "User identified";
							message += "\r\nUser ID   : \t\t" + userID;
							message += "\r\nFirstName : \t\t" + firstName;
							message += "\r\nLastName  : \t\t" + lastName;
						}
						alert(retvalue, morphoDevice.getInternalError(), "Identify", message);
					}
				});

				notifyEndProcess();
			}
		}));
		commandThread.start();
	}

	@SuppressLint("SimpleDateFormat")
	public void getAndWriteFFDLogs()
	{
		String ffdLogs = morphoDevice.getFFDLogs();
		
		if(ffdLogs != null)
		{
			String serialNbr = ProcessInfo.getInstance().getMSOSerialNumber();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String currentDateandTime = sdf.format(new Date());
			String saveFile = "sdcard/" + serialNbr + "_" + currentDateandTime + "_Audit.log";
	
			try
			{
				FileWriter fstream = new FileWriter(saveFile,true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(ffdLogs);
				out.close();
			}
			catch (IOException e)
			{
				Log.e("getAndWriteFFDLogs", e.getMessage());
			}
		}
	}

	public void morphoUserEnroll(final Observer observer)
	{
		Thread commandThread = (new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				index = 0;
				MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
				String idUser = ((EnrollInfo) morphoInfo).getIDNumber();
				String firstName = ((EnrollInfo) morphoInfo).getFirstname();
				String lastName = ((EnrollInfo) morphoInfo).getLastName();
				MorphoUser morphoUser = new MorphoUser();
				int ret = morphoDatabase.getUser(idUser, morphoUser);
				if (ret == 0)
				{
					ret = morphoUser.putField(1, MorphoTools.checkfield(firstName,((EnrollInfo) morphoInfo).isUpdateTemplate()));
				}

				if (ret == 0)
				{
					ret = morphoUser.putField(2, MorphoTools.checkfield(lastName,((EnrollInfo) morphoInfo).isUpdateTemplate()));
				}

				if (ret == 0)
				{
					ProcessInfo processInfo = ProcessInfo.getInstance();
					if (processInfo.isNoCheck())
					{
						morphoUser.setNoCheckOnTemplateForDBStore(true);
					}

					int timeout = processInfo.getTimeout();

					int acquisitionThreshold = 0;
					int advancedSecurityLevelsRequired = 0;
					CompressionAlgorithm compressAlgo = ((EnrollInfo) morphoInfo).getCompressionAlgorithm();

					if(processInfo.isFingerprintQualityThreshold())
					{
						acquisitionThreshold = processInfo.getFingerprintQualityThresholdvalue();
					}
					
					int compressRate = 0;
					TemplateList templateList = new TemplateList();
					if (!compressAlgo.equals(CompressionAlgorithm.NO_IMAGE))
					{
						templateList.setActivateFullImageRetrieving(true);
						if (compressAlgo.equals(CompressionAlgorithm.MORPHO_COMPRESS_WSQ))
						{
							compressRate = 15;
						}
					}

					boolean exportMinutiae = false;
					TemplateType templateType = ((EnrollInfo) morphoInfo).getTemplateType();
					TemplateFVPType templateFVPType = ((EnrollInfo) morphoInfo).getFVPTemplateType();

					if (templateType.compareTo(TemplateType.MORPHO_NO_PK_FP) != 0 || templateFVPType.compareTo(TemplateFVPType.MORPHO_NO_PK_FVP) != 0)
					{
						exportMinutiae = true;
					}

					int fingerNumber = ((EnrollInfo) morphoInfo).getFingerNumber();

					boolean saveRecord = ((EnrollInfo) morphoInfo).isSavePKinDatabase();
					Coder coder = processInfo.getCoder();

					int detectModeChoice;
					detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();
					if (processInfo.isForceFingerPlacementOnTop())
					{
						detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
					}
					if (processInfo.isWakeUpWithLedOff())
					{
						detectModeChoice |= MorphoWakeUpMode.MORPHO_WAKEUP_LED_OFF.getCode();
					}
					int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();
					
					// Prepare finger update if necessary
					if(saveRecord &&  ((EnrollInfo) morphoInfo).isUpdateTemplate())						
					{						
						if(fingerNumber == 2) // Update both fingers
						{
							boolean[] mask = {true,true};
							ret = morphoUser.setTemplateUpdateMask(mask);
						}
						else if(((EnrollInfo) morphoInfo).getFingerIndex() == 1) // Update first finger only
						{
							boolean[] mask = {true};
							ret = morphoUser.setTemplateUpdateMask(mask);
						}
						else // Update second finger
						{
							boolean[] mask = {false,true};
							ret = morphoUser.setTemplateUpdateMask(mask);
						}
					}
					
					if(ret == 0)
					{
						ret = morphoDevice.setStrategyAcquisitionMode(ProcessInfo.getInstance().getStrategyAcquisitionMode());
					}
				
					if (ret == 0)
					{
						ret = morphoUser.enroll(
												timeout,
												acquisitionThreshold,
												advancedSecurityLevelsRequired, 
												compressAlgo,
												compressRate, 
												exportMinutiae, 
												fingerNumber,
												templateType,
												templateFVPType,
												saveRecord, 
												coder,
												detectModeChoice, 
												templateList, 
												callbackCmd, 
												observer
												);
					}

					ProcessInfo.getInstance().setCommandBioStart(false);

					getAndWriteFFDLogs();

					if (ret == 0)
					{
						if (saveRecord)
						{
							DatabaseItem databaseItemsItem = new DatabaseItem(idUser, firstName, lastName);
							List<DatabaseItem> databaseItems = processInfo.getDatabaseItems();
							databaseItems.add(databaseItemsItem);
							processInfo.setDatabaseItems(databaseItems);
						}
					}

					boolean l_activateFullImageRetrieve = templateList.isActivateFullImageRetrieving();
					String message = "";
					try
					{
						for (int fingerIndex = 0; fingerIndex < fingerNumber; ++fingerIndex)
						{
							if (ret == 0 && ((templateType != TemplateType.MORPHO_NO_PK_FP)))
							{
								FileOutputStream fos = new FileOutputStream("sdcard/TemplateFP_" + idUser + "_finger" + fingerIndex + "_" + (fingerIndex + 1) + templateType.getExtension());
								Template t = templateList.getTemplate(fingerIndex);
								byte[] data = t.getData();
								fos.write(data);
								fos.close();
								message += "Finger #" + (fingerIndex + 1) + " - FP Template successfully exported in file [sdcard/TemplateFP_" + idUser + "_finger" + fingerIndex + "_"
										+ (fingerIndex + 1) + templateType.getExtension() + "]\n";
							}
							if (ret == 0 && ((MorphoInfo.m_b_fvp) && (templateFVPType != TemplateFVPType.MORPHO_NO_PK_FVP)))
							{
								FileOutputStream fos = new FileOutputStream("sdcard/TemplateFVP_" + idUser + "_finger" + fingerIndex + "_" + (fingerIndex + 1) + templateFVPType.getExtension());
								TemplateFVP t = templateList.getFVPTemplate(fingerIndex);
								byte[] data = t.getData();
								fos.write(data);
								fos.close();
								message += "Finger #" + (fingerIndex + 1) + " - FVP Template successfully exported in file [sdcard/TemplateFVP_" + idUser + "_finger" + fingerIndex + "_"
										+ (fingerIndex + 1) + templateFVPType.getExtension() + "]\n";
							}
							if (ret == 0 && l_activateFullImageRetrieve)
							{
								FileOutputStream fos = new FileOutputStream("sdcard/Image_" + idUser + "_" + (fingerIndex + 1) + compressAlgo.getExtension());

								byte[] data;
								if (compressAlgo.equals(CompressionAlgorithm.MORPHO_COMPRESS_WSQ) || compressAlgo.equals(CompressionAlgorithm.MORPHO_COMPRESS_V1))
								{
									//Case of WSQ or morpho_v1 image
									data = templateList.getImage(fingerIndex).getCompressedImage();
								}
								else
								{
									//Case of RAW Image
									data = templateList.getImage(fingerIndex).getImage();
								}
								fos.write(data);
								fos.close();
								message += "Finger #" + (fingerIndex + 1) + " - Image successfully exported in file [sdcard/Image_" + idUser + "_" + (fingerIndex + 1) + compressAlgo.getExtension()
										+ "]\n";
							}
						}
					}
					catch (FileNotFoundException e)
					{
						Log.i("ENROLL", e.getMessage());
					}
					catch (IOException e)
					{
						Log.i("ENROLL", e.getMessage());
					}
					catch (Exception e)
					{
						Log.i("ENROLL", e.getMessage());
					}
					final int internalError = morphoDevice.getInternalError();
					final int retvalue = ret;
					final String alerMessage = message;
					mHandler.post(new Runnable()
					{
						@Override
						public synchronized void run()
						{
							alert(retvalue, internalError, "Enroll", alerMessage);
						}
					});
				}
				notifyEndProcess();
			}
		}));
		commandThread.start();
	}
	
	public void morphoUserVerify(final Observer observer)
	{
		Thread commandThread = (new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (ProcessInfo.getInstance().getDatabaseSelectedIndex() != -1)
					{
						int i = ProcessInfo.getInstance().getDatabaseSelectedIndex();

						List<DatabaseItem> databaseItems = ProcessInfo.getInstance().getDatabaseItems();

						String userID = databaseItems.get(i).getId();

						MorphoUser morphoUser = new MorphoUser();

						int ret = morphoDatabase.getUser(userID, morphoUser);
						if (ret == 0)
						{
							ProcessInfo processInfo = ProcessInfo.getInstance();
							int timeout = processInfo.getTimeout();
							int far = processInfo.getMatchingThreshold();
							Coder coder = processInfo.getCoder();
							int detectModeChoice;
							MatchingStrategy matchingStrategy = processInfo.getMatchingStrategy();

							int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

							callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

							ResultMatching resultMatching = new ResultMatching();

							detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();

							if (processInfo.isForceFingerPlacementOnTop())
							{
								detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
							}

							if (processInfo.isWakeUpWithLedOff())
							{
								detectModeChoice |= MorphoWakeUpMode.MORPHO_WAKEUP_LED_OFF.getCode();
							}

							ret = morphoDevice.setStrategyAcquisitionMode(ProcessInfo.getInstance().getStrategyAcquisitionMode());
							
							if(ret == 0)
							{
								ret = morphoUser.verify(timeout, far, coder, detectModeChoice, matchingStrategy, callbackCmd, observer, resultMatching);
							}

							getAndWriteFFDLogs();

							String message = "";
							if (ret == ErrorCodes.MORPHO_OK)
							{
								String user_authenticated = "";
								for (int j = 0; j <= 2; j++)
								{
									String mem = morphoUser.getField(j);
									user_authenticated = user_authenticated + " " + mem;
								}
								
								message = "User authenticated :\n";
								message += "\t" + getString(R.string.idnumber) + " : " + morphoUser.getField(0) + "\n";
								message += "\t" + getString(R.string.firstname) + " : " + morphoUser.getField(1) + "\n";
								message += "\t" + getString(R.string.lastname) + " : " + morphoUser.getField(2) + "\n";
								message += "\tMatching Score = " + resultMatching.getMatchingScore() + "\n";
								message += "\tPK Number = " + resultMatching.getMatchingPKNumber();								
							}
							final String msg = message;
							final int l_ret = ret;
							final int internalError = morphoDevice.getInternalError();

							mHandler.post(new Runnable()
							{
								@Override
								public synchronized void run()
								{
									alert(l_ret, internalError, "Verify", msg);
								}
							});
						}
					}
					else
					{
						mHandler.post(new Runnable()
						{
							@Override
							public synchronized void run()
							{
								alert("Select a user in the list view.");
							}
						});
					}
				}
				catch (Exception e)
				{
					Log.e("ERROR", e.getMessage());
				}
				ProcessInfo.getInstance().setCommandBioStart(false);

				notifyEndProcess();
			}
		}));
		commandThread.start();
	}

	public static ITemplateType getTemplateTypeFromExtention(String extention)
	{
		for (TemplateType templateType : TemplateType.values())
		{
			if (templateType.getExtension().equalsIgnoreCase(extention))
			{
				return templateType;
			}
		}
		for (TemplateFVPType templateFVPType : TemplateFVPType.values())
		{
			if (templateFVPType.getExtension().equalsIgnoreCase(extention))
			{
				return templateFVPType;
			}
		}
		return TemplateType.MORPHO_NO_PK_FP;
	}

	public static String getFileExtension(String fileName)
	{
		String extension = "";
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex >= 0)
		{
			extension = fileName.substring(dotIndex);
		}
		return extension;
	}

	public void morphoDeviceVerifyWithFile(final Observer observer, final String fileName)
	{
		DataInputStream dis;
		try
		{
			dis = new DataInputStream(new FileInputStream(fileName));

			int length = dis.available();
			final byte[] buffer = new byte[length];
			dis.readFully(buffer);

			Thread commandThread = (new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					Template template = new Template();
					TemplateFVP templateFVP = new TemplateFVP();

					TemplateList templateList = new TemplateList();

					ITemplateType iTemplateType = getTemplateTypeFromExtention(getFileExtension(fileName));
					if (iTemplateType instanceof TemplateFVPType)
					{
						templateFVP.setData(buffer);
						templateFVP.setTemplateFVPType((TemplateFVPType) iTemplateType);
						templateList.putFVPTemplate(templateFVP);
					}
					else
					{
						template.setData(buffer);
						template.setTemplateType((TemplateType) iTemplateType);
						templateList.putTemplate(template);
					}

					int timeOut = 0;
					int far = FalseAcceptanceRate.MORPHO_FAR_5;
					Coder coderChoice = Coder.MORPHO_DEFAULT_CODER;
					int detectModeChoice = DetectionMode.MORPHO_VERIF_DETECT_MODE.getValue();
					int matchingStrategy = 0;

					int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

					callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

					ResultMatching resultMatching = new ResultMatching();

					int ret = morphoDevice.setStrategyAcquisitionMode(ProcessInfo.getInstance().getStrategyAcquisitionMode());					
					
					if(ret ==0)
					{
						ret = morphoDevice.verify(timeOut, far, coderChoice, detectModeChoice, matchingStrategy, templateList, callbackCmd, observer, resultMatching);
					}

					ProcessInfo.getInstance().setCommandBioStart(false);

					getAndWriteFFDLogs();

					String message = "";

					if (ret == ErrorCodes.MORPHO_OK)
					{
						message = "Matching Score = " + resultMatching.getMatchingScore() + "\nPK Number = " + resultMatching.getMatchingPKNumber();
					}
					final String msg = message;
					final int l_ret = ret;
					final int internalError = morphoDevice.getInternalError();
					mHandler.post(new Runnable()
					{
						@Override
						public synchronized void run()
						{
							alert(l_ret, internalError, "Verify", msg);
						}
					});
					notifyEndProcess();
				}
			}));

			commandThread.start();

			if (dis != null)
			{
				dis.close();
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
		catch (Exception e)
		{
			alert(e.getMessage());
		}
	}

	public void morphoDeviceCapture(final Observer observer)
	{
		Thread commandThread = (new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				index = 0;
				isCaptureVerif = false;
				final TemplateList templateList = new TemplateList();
				MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
				ProcessInfo processInfo = ProcessInfo.getInstance();
				int timeout;
				int acquisitionThreshold = 0;
				int advancedSecurityLevelsRequired;
				TemplateType templateType;
				TemplateFVPType templateFVPType;
				int maxSizeTemplate = 255;
				EnrollmentType enrollType;
				LatentDetection latentDetection;
				Coder coderChoice;
				int detectModeChoice;

				boolean exportFVP = false, exportFP = false;
				timeout = processInfo.getTimeout();

				if(processInfo.isFingerprintQualityThreshold())
				{
					acquisitionThreshold = processInfo.getFingerprintQualityThresholdvalue();
				}

				templateType = ((CaptureInfo) morphoInfo).getTemplateType();
				templateFVPType = ((CaptureInfo) morphoInfo).getTemplateFVPType();

				if (templateType != TemplateType.MORPHO_NO_PK_FP)
				{
					exportFP = true;
					if (templateType == TemplateType.MORPHO_PK_MAT || templateType == TemplateType.MORPHO_PK_MAT_NORM || templateType == TemplateType.MORPHO_PK_PKLITE)
					{
						maxSizeTemplate = 1;
					}
					else
					{
						maxSizeTemplate = 255;
					}
				}
				else
				{
					if (MorphoInfo.m_b_fvp == false)
					{
						templateType = TemplateType.MORPHO_PK_COMP;
					}
					maxSizeTemplate = 255;
				}

				if (templateFVPType != TemplateFVPType.MORPHO_NO_PK_FVP)
				{
					exportFVP = true;
				}

				if (MorphoInfo.m_b_fvp)
				{
					if (((CaptureInfo) morphoInfo).getCaptureType() != CaptureType.Verif)
					{
						templateFVPType = TemplateFVPType.MORPHO_PK_FVP;
					}
					else
					{
						templateFVPType = TemplateFVPType.MORPHO_PK_FVP_MATCH;
					}
				}
				else
				{
					templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;
				}

				if (((CaptureInfo) morphoInfo).getCaptureType() == CaptureType.Enroll)
				{
					enrollType = EnrollmentType.THREE_ACQUISITIONS;
				}
				else
				{
					isCaptureVerif = true;
					currentCaptureBitmapId = R.id.imageView2;
					enrollType = EnrollmentType.ONE_ACQUISITIONS;
				}

				if (((CaptureInfo) morphoInfo).isLatentDetect())
				{
					latentDetection = LatentDetection.LATENT_DETECT_ENABLE;
				}
				else
				{
					latentDetection = LatentDetection.LATENT_DETECT_DISABLE;
				}

				coderChoice = processInfo.getCoder();

				detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();

				if (processInfo.isForceFingerPlacementOnTop())
				{
					detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
				}

				if (processInfo.isWakeUpWithLedOff())
				{
					detectModeChoice |= MorphoWakeUpMode.MORPHO_WAKEUP_LED_OFF.getCode();
				}

				advancedSecurityLevelsRequired = 0;
				if (((CaptureInfo) morphoInfo).getCaptureType() != CaptureType.Verif)
				{

					if (processInfo.isAdvancedSecLevCompReq())
					{
						advancedSecurityLevelsRequired = 1;
					}
					else
					{
						advancedSecurityLevelsRequired = 0;
					}
				}
				else
				{
					advancedSecurityLevelsRequired = 0xFF;
					if (processInfo.isAdvancedSecLevCompReq())
					{
						advancedSecurityLevelsRequired = 1;
					}
				}

				int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

				int nbFinger = ((CaptureInfo) morphoInfo).getFingerNumber();
				final String idUser = ((CaptureInfo) morphoInfo).getIDNumber();
				
				int ret = morphoDevice.setStrategyAcquisitionMode(ProcessInfo.getInstance().getStrategyAcquisitionMode());
				
				if(ret == 0)
				{
					ret = morphoDevice.capture(timeout, acquisitionThreshold, advancedSecurityLevelsRequired,
													nbFinger, templateType, templateFVPType, maxSizeTemplate, enrollType,
													latentDetection, coderChoice, detectModeChoice,
							CompressionAlgorithm.MORPHO_NO_COMPRESS, 0, templateList, callbackCmd, observer);
				}

				ProcessInfo.getInstance().setCommandBioStart(false);

				getAndWriteFFDLogs();

				String message = "";
				try
				{
					if (ret == ErrorCodes.MORPHO_OK)
					{
						int NbTemplateFVP = templateList.getNbFVPTemplate();
						int NbTemplate = templateList.getNbTemplate();
						if (MorphoInfo.m_b_fvp)
						{
							if (NbTemplateFVP > 0)
							{
								TemplateFVP t = templateList.getFVPTemplate(0);
								message += "Advanced Security Levels Compatibility: " + (t.getAdvancedSecurityLevelsCompatibility() == true ? "Yes" : "NO") + "\n";
								for (int i = 0; i < NbTemplateFVP; i++)
								{
									t = templateList.getFVPTemplate(i);
									message += "Finger #" + (i + 1) + " - Quality Score: " + t.getTemplateQuality() + "\n";
								}
							}
						}
						else
						{
							if (NbTemplate > 0)
							{
								for (int i = 0; i < NbTemplateFVP; i++)
								{
									Template t = templateList.getTemplate(i);
									message += "Finger #" + (i + 1) + " - Quality Score: " + t.getTemplateQuality() + "\n";
								}
							}
						}

						if (exportFVP)
						{
							for (int i = 0; i < NbTemplateFVP; i++)
							{
								TemplateFVP t = templateList.getFVPTemplate(i);
								FileOutputStream fos = new FileOutputStream("sdcard/TemplateFVP_" + idUser + "_f" + (i + 1) + templateFVPType.getExtension());
								byte[] data = t.getData();
								fos.write(data);
								fos.close();
								message += "Finger #" + (i + 1) + " - FVP Template successfully exported in file [sdcard/TemplateFVP_" + idUser + "_f" + (i + 1) + templateFVPType.getExtension()
										+ "]\n";
							}
						}

						if (exportFP)
						{

							for (int i = 0; i < NbTemplate; i++)
							{
								Template t = templateList.getTemplate(i);
								FileOutputStream fos = new FileOutputStream("sdcard/TemplateFP_" + idUser + "_f" + (i + 1) + templateType.getExtension());
								byte[] data = t.getData();
								fos.write(data);
								fos.close();
								message += "Finger #" + (i + 1) + " - FP Template successfully exported in file [sdcard/TemplateFP_" + idUser + "_f" + (i + 1) + templateType.getExtension() + "]\n";
							}
						}
					}

				}
				catch (FileNotFoundException e)
				{
					Log.i("CAPTURE", e.getMessage());
				}
				catch (IOException e)
				{
					Log.i("CAPTURE", e.getMessage());
				}
				catch (Exception e)
				{
					Log.i("CAPTURE", e.getMessage());
				}

				final String alertMessage = message;
				final int internalError = morphoDevice.getInternalError();
				final int retvalue = ret;
				mHandler.post(new Runnable()
				{
					@Override
					public synchronized void run()
					{
						alert(retvalue, internalError, "Capture", alertMessage);
					}
				});
				notifyEndProcess();
			}
		}));

		commandThread.start();
	}

	private void notifyEndProcess()
	{
		mHandler.post(new Runnable()
		{
			@Override
			public synchronized void run()
			{
				try
				{

					ProcessInfo.getInstance().getMorphoSample().stopProcess();
				}
				catch (Exception e)
				{
					Log.d("notifyEndProcess", e.getMessage());
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void updateSensorProgressBar(int level)
	{
		try
		{
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.vertical_progressbar);

			final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
			ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

			int color = Color.GREEN;

			if (level <= 25)
			{
				color = Color.RED;
			}
			else if (level <= 50)
			{
				color = Color.YELLOW;
			}
			pgDrawable.getPaint().setColor(color);
			ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
			progressBar.setProgressDrawable(progress);
			progressBar.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
			progressBar.setProgress(level);
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	private void updateSensorMessage(String sensorMessage)
	{
		try
		{
			TextView tv = (TextView) findViewById(R.id.textViewMessage);
			tv.setText(sensorMessage);
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	private void updateImage(Bitmap bitmap, int id)
	{
		try
		{
			ImageView iv = (ImageView) findViewById(id);
			iv.setImageBitmap(bitmap);
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	@Override
	public synchronized void update(Observable o, Object arg)
	{
		try
		{
			// convert the object to a callback back message.
			CallbackMessage message = (CallbackMessage) arg;

			int type = message.getMessageType();

			switch (type)
			{

				case 1:
					// message is a command.
					Integer command = (Integer) message.getMessage();

					// Analyze the command.
					switch (command)
					{
						case 0:
							strMessage = "move-no-finger";
							break;
						case 1:
							strMessage = "move-finger-up";
							break;
						case 2:
							strMessage = "move-finger-down";
							break;
						case 3:
							strMessage = "move-finger-left";
							break;
						case 4:
							strMessage = "move-finger-right";
							break;
						case 5:
							strMessage = "press-harder";
							break;
						case 6:
							strMessage = "move-latent";
							break;
						case 7:
							strMessage = "remove-finger";
							break;
						case 8:
							strMessage = "finger-ok";
							// switch live acquisition ImageView
							if (isCaptureVerif)
							{
								isCaptureVerif = false;
								index = 4; //R.id.imageView5;
							}
							else
							{
								index++;
							}

							switch (index)
							{
								case 1:
									currentCaptureBitmapId = R.id.imageView2;
									break;
								case 2:
									currentCaptureBitmapId = R.id.imageView3;
									break;
								case 3:
									currentCaptureBitmapId = R.id.imageView4;
									break;
								case 4:
									currentCaptureBitmapId = R.id.imageView5;
									break;
								case 5:
									currentCaptureBitmapId = R.id.imageView6;
									break;
								default:
								case 0:
									currentCaptureBitmapId = R.id.imageView1;
									break;
							}
							break;
					}

					mHandler.post(new Runnable()
					{
						@Override
						public synchronized void run()
						{
							updateSensorMessage(strMessage);
						}
					});

					break;
				case 2:
					// message is a low resolution image, display it.
					byte[] image = (byte[]) message.getMessage();

					MorphoImage morphoImage = MorphoImage.getMorphoImageFromLive(image);
					int imageRowNumber = morphoImage.getMorphoImageHeader().getNbRow();
					int imageColumnNumber = morphoImage.getMorphoImageHeader().getNbColumn();
					final Bitmap imageBmp = Bitmap.createBitmap(imageColumnNumber, imageRowNumber, Config.ALPHA_8);


					imageBmp.copyPixelsFromBuffer(ByteBuffer.wrap(morphoImage.getImage(), 0, morphoImage.getImage().length));
					mHandler.post(new Runnable()
					{
						@Override
						public synchronized void run()
						{
							updateImage(imageBmp, currentCaptureBitmapId);
						}
					});
					break;
				case 3:
					// message is the coded image quality.
					final Integer quality = (Integer) message.getMessage();
					mHandler.post(new Runnable()
					{
						@Override
						public synchronized void run()
						{
							updateSensorProgressBar(quality);
						}
					});
					break;
			//case 4:
			//byte[] enrollcmd = (byte[]) message.getMessage();
			}
		}
		catch (Exception e)
		{
			alert(e.getMessage());
		}
	}
}

