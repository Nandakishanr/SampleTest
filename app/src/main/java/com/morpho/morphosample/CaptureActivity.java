// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.manvish.sampletest.R;
import com.morpho.morphosample.info.CaptureInfo;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.subtype.CaptureType;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CaptureActivity extends MorphoTabActivity
{

	private boolean	defaultFPTemplateTypeValue	= true;
	private boolean	defaultFVPTemplateTypeValue	= true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		defaultFPTemplateTypeValue = true;
		defaultFVPTemplateTypeValue = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		CaptureInfo.getInstance();
		initFingerNumber();
		initCaptureType();
		initTemplateTypeSpinner();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_capture, menu);
		return true;
	}

	public final void onCaptureTypeClicked(View view)
	{
		RadioButton rb = (RadioButton) view;
		CaptureType et = CaptureType.Enroll;
		CheckBox latent = (CheckBox) findViewById(R.id.latentdetection);
		if (rb.getId() == R.id.enroll)
		{
			latent.setClickable(false);
			latent.setTextColor(Color.GRAY);
			latent.setChecked(false);
			CaptureInfo.getInstance().setLatentDetect(false);
		}
		else if (rb.getId() == R.id.verif)
		{
			latent.setClickable(true);
			latent.setTextColor(Color.BLACK);
			et = CaptureType.Verif;
			latent.setChecked(true);
			CaptureInfo.getInstance().setLatentDetect(true);
		}
		CaptureInfo.getInstance().setCaptureType(et);
		initTemplateTypeSpinner();
	}

	private void initCaptureType()
	{
		CaptureType captureType = CaptureInfo.getInstance().getCaptureType();
		int id = R.id.enroll;
		if (captureType == CaptureType.Verif)
		{
			id = R.id.verif;
			CheckBox latent = (CheckBox) findViewById(R.id.latentdetection);
			latent.setClickable(true);
			latent.setTextColor(Color.BLACK);
			latent.setChecked(CaptureInfo.getInstance().isLatentDetect());
		}
		RadioButton rb = (RadioButton) findViewById(id);
		rb.setChecked(true);
	}

	public final void onLatentClicked(View view)
	{
		CheckBox latent = (CheckBox) view;
		CaptureInfo.getInstance().setLatentDetect(latent.isChecked());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initTemplateTypeSpinner()
	{
		Spinner spinner = (Spinner) findViewById(R.id.fptemplatetypeCapture);

		// Set spinner content from enum value
		CharSequence[] itemArray = new CharSequence[TemplateType.values().length - 1];
		int i = 0;
		for (TemplateType value : TemplateType.values())
		{
			if (!value.equals(TemplateType.MORPHO_PK_ILO_FMR))
			{
				itemArray[i++] = value.toString();
			}
		}
		List<CharSequence> itemList = new ArrayList<CharSequence>(Arrays.asList(itemArray));
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		//set the default according to value
		int spinnerPosition = adapter.getPosition(CaptureInfo.getInstance().getTemplateType().toString());
		spinner.setSelection(spinnerPosition);

		// listen to the event
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultFPTemplateTypeValue == true)
				{
					defaultFPTemplateTypeValue = false;
				}
				else
				{
					String item = (String) parent.getItemAtPosition(pos);
					CaptureInfo.getInstance().setTemplateType(Enum.valueOf(TemplateType.class, item));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		Spinner spinner2 = (Spinner) findViewById(R.id.fvptemplatetypeCapture);

		// Set spinner content from enum value
		CharSequence[] itemArray2 = new CharSequence[TemplateFVPType.values().length - 1];
		int k = 0;
		for (TemplateFVPType value : TemplateFVPType.values())
		{
			if (CaptureInfo.getInstance().getCaptureType().equals(CaptureType.Verif))
			{
				if (!value.equals(TemplateFVPType.MORPHO_PK_FVP))
				{
					itemArray2[k++] = value.toString();
				}
			}
			else
			//type enroll
			{
				if (!value.equals(TemplateFVPType.MORPHO_PK_FVP_MATCH))
				{
					itemArray2[k++] = value.toString();
				}
			}
		}
		List<CharSequence> itemList2 = new ArrayList<CharSequence>(Arrays.asList(itemArray2));
		ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList2);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);

		//set the default according to value
		int spinnerPosition2 = adapter.getPosition(CaptureInfo.getInstance().getTemplateFVPType().toString());
		spinner2.setSelection(spinnerPosition2);

		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				if (defaultFVPTemplateTypeValue == true)
				{
					defaultFVPTemplateTypeValue = false;
				}
				else
				{
					String item = (String) parent.getItemAtPosition(pos);
					CaptureInfo.getInstance().setTemplateFVPType(Enum.valueOf(TemplateFVPType.class, item));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		spinner2.setEnabled(MorphoInfo.m_b_fvp);
	}

	public final void onNumberFingerClicked(View view)
	{
		RadioButton rb = (RadioButton) view;
		int action = 0;
		if (rb.getId() == R.id.onefingerCapture)
		{
			action = 1;
		}
		else if (rb.getId() == R.id.twofingerCapture)
		{
			action = 2;
		}
		CaptureInfo.getInstance().setFingerNumber(action);
	}

	private void initFingerNumber()
	{
		int fingerNb = CaptureInfo.getInstance().getFingerNumber();
		int id = R.id.onefingerCapture;
		if (fingerNb == 2)
		{
			id = R.id.twofingerCapture;
		}
		RadioButton rb = (RadioButton) findViewById(id);
		rb.setChecked(true);
	}

	@Override
	public MorphoInfo retrieveSettings()
	{
		EditText idNumber = (EditText) findViewById(R.id.idnumberCapture);
		MorphoInfo ret = null;
		if (idNumber.getText().toString().trim().equals(""))
		{
			Toast.makeText(this, getResources().getString(R.string.mustenteridnumber), Toast.LENGTH_SHORT).show();
			((TextView) findViewById(R.id.idnumberlabelCapture)).setTextColor(Color.RED);
		}
		else
		{
			((TextView) findViewById(R.id.idnumberlabelCapture)).setTextColor(Color.BLACK);
			EditText lastname = (EditText) findViewById(R.id.lastnameCapture);
			EditText firstname = (EditText) findViewById(R.id.firstnameCapture);
			CaptureInfo.getInstance().setIDNumber(idNumber.getText().toString().trim());
			CaptureInfo.getInstance().setLastName(lastname.getText().toString().trim());
			CaptureInfo.getInstance().setFirstName(firstname.getText().toString().trim());
			ret = CaptureInfo.getInstance();
		}
		return ret;
	}
}
