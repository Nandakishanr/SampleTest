// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.manvish.sampletest.R;
import com.morpho.morphosample.info.FingerPrintInfo;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosample.info.subtype.FingerPrintMode;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FingerPrintActivity extends MorphoTabActivity
{

	private CheckBox			chkLatentDetect;
	private boolean				defaultExportImageValue		= true;
	private static final String	COMPRESSION_RATE_DEFAULT	= "10";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fingerprint);
		FingerPrintInfo.getInstance();
		initFingerPrintMode();
		addListenerOnChkVerifyAndUpdate();
		initOnExportImageSpinner();
		initCompressionRateValue();
	}

	private void initFingerPrintMode()
	{
		FingerPrintMode fingerPrintMode = FingerPrintInfo.getInstance().getFingerPrintMode();
		int id = R.id.verifymode;
		if (fingerPrintMode == FingerPrintMode.Enroll)
		{
			id = R.id.enrollmode;
		}
		RadioButton rb = (RadioButton) findViewById(id);
		rb.setChecked(true);
	}

	public void addListenerOnChkVerifyAndUpdate()
	{
		chkLatentDetect = (CheckBox) findViewById(R.id.latentdetect);
		chkLatentDetect.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FingerPrintInfo.getInstance().setLatentDetect(((CheckBox) v).isChecked());
			}
		});
	}

	public void onFingerPrintModeClicked(View view)
	{
		RadioButton rb = (RadioButton) view;
		FingerPrintMode fingerPrintMode = FingerPrintMode.Verify;
		if (rb.getId() == R.id.enrollmode)
		{
			fingerPrintMode = FingerPrintMode.Enroll;
		}
		FingerPrintInfo.getInstance().setFingerPrintMode(fingerPrintMode);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initOnExportImageSpinner()
	{
		Spinner spinner = (Spinner) findViewById(R.id.formatimage);

		// Set spinner content from enum value
		CharSequence[] itemArray = new CharSequence[CompressionAlgorithm.values().length - 1];

		int i = 0;
		for (CompressionAlgorithm value : CompressionAlgorithm.values())
		{
			if (value != CompressionAlgorithm.NO_IMAGE)
				itemArray[i++] = value.toString();
		}
		List<CharSequence> itemList = new ArrayList<CharSequence>(Arrays.asList(itemArray));
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		//set the default according to value
		int spinnerPosition = adapter.getPosition(FingerPrintInfo.getInstance().getCompressionAlgorithm().toString());
		spinner.setSelection(spinnerPosition);

		// listen to the event
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultExportImageValue == true)
				{
					defaultExportImageValue = false;
				}
				else
				{
					String compressionAlgo = (String) parent.getItemAtPosition(pos);
					EditText editText = (EditText) findViewById(R.id.idcompressionrate);
					if (compressionAlgo.equals(CompressionAlgorithm.MORPHO_COMPRESS_WSQ.toString()))
					{
						editText.setEnabled(true);
					}
					else
					{
						editText.setEnabled(false);
					}

					FingerPrintInfo.getInstance().setCompressionAlgorithm(Enum.valueOf(CompressionAlgorithm.class, compressionAlgo));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}

	public void initCompressionRateValue()
	{
		EditText editText = (EditText) findViewById(R.id.idcompressionrate);
		editText.setText(COMPRESSION_RATE_DEFAULT);
		editText.setEnabled(false);
	}

	@Override
	public MorphoInfo retrieveSettings()
	{
		MorphoInfo ret = null;		
		if (ProcessInfo.getInstance().isForceFingerPlacementOnTop() && FingerPrintInfo.getInstance().getFingerPrintMode().equals(FingerPrintMode.Verify))
		{
			alert("GetImage en \"Verify detection mode\" and \"Force finger Placement on Top\" option cannot be used together.\nUncheck this option vefore proceeding ...");
		}
		else
		{
			EditText compressionrate = (EditText) findViewById(R.id.idcompressionrate);
			FingerPrintInfo.getInstance().setCompressRatio(Integer.parseInt(compressionrate.getText().toString().trim()));
			ret = FingerPrintInfo.getInstance();
		}
		return ret;
	}
}
