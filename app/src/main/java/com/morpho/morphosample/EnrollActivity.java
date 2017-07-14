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
import com.morpho.morphosample.info.EnrollInfo;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnrollActivity extends MorphoTabActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enroll);
		EnrollInfo.getInstance();
		initFingerNumber();
		initSavePKInDatabase();
		initOnExportImageSpinner();
		initTemplateTypeSpinner();
		initFingerIndexSpinner();
		initUpdateTemplate();
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_enroll, menu);
		return true;
	}

	public final void onNumberFingerClicked(View view)
	{
		RadioButton rb = (RadioButton) view;
		int action = 0;
		Spinner spinner = (Spinner) findViewById(R.id.fingerIndex);
		if (rb.getId() == R.id.onefingerEnroll)
		{
			action = 1;
			spinner.setEnabled(EnrollInfo.getInstance().isUpdateTemplate());
		}
		else if (rb.getId() == R.id.twofingerEnroll)
		{
			action = 2;
			spinner.setEnabled(false);
		}
		EnrollInfo.getInstance().setFingerNumber(action);
	}

	private void initFingerNumber()
	{
		int fingerNb = EnrollInfo.getInstance().getFingerNumber();
		int id = R.id.onefingerEnroll;
		if (fingerNb == 2)
		{
			id = R.id.twofingerEnroll;
		}
		RadioButton rb = (RadioButton) findViewById(id);
		rb.setChecked(true);
	}

	public void onSavePKInDatabaseClicked(View view)
	{
		CheckBox savePKinDatabase = (CheckBox) view;
		EnrollInfo.getInstance().setSavePKinDatabase(savePKinDatabase.isChecked());
		
		Spinner spinner = (Spinner) findViewById(R.id.fingerIndex);
		spinner.setEnabled(EnrollInfo.getInstance().isSavePKinDatabase() && EnrollInfo.getInstance().isUpdateTemplate());
		
		CheckBox updateTemplate = (CheckBox) findViewById(R.id.updateTemplate);
		updateTemplate.setEnabled(EnrollInfo.getInstance().isSavePKinDatabase());
	}
	
	private void initSavePKInDatabase()
	{
		CheckBox savePKinDatabase = (CheckBox) findViewById(R.id.savepkindatabase);
		savePKinDatabase.setChecked(EnrollInfo.getInstance().isSavePKinDatabase());		
	}	
	
	public void onUpdateTemplateClicked(View view)
	{
		CheckBox updateTemplate = (CheckBox) view;
		EnrollInfo.getInstance().setUpdateTemplate(updateTemplate.isChecked());
		Spinner spinner = (Spinner) findViewById(R.id.fingerIndex);
		spinner.setEnabled(EnrollInfo.getInstance().isUpdateTemplate());
	}
	
	private void initUpdateTemplate()
	{
		CheckBox updateTemplate = (CheckBox) findViewById(R.id.updateTemplate);
		updateTemplate.setChecked(EnrollInfo.getInstance().isUpdateTemplate());		
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initFingerIndexSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.fingerIndex);
		
		CharSequence[] itemArray = {"First Finger","Second Finger"};
		
		List<CharSequence> itemList = new ArrayList<CharSequence>(Arrays.asList(itemArray));
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		// listen to the event
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{				
				if(pos == 0) //firstFinger
				{
					EnrollInfo.getInstance().setFingerIndex(1);
				}
				else
				{
					EnrollInfo.getInstance().setFingerIndex(2);
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		
		spinner.setEnabled(EnrollInfo.getInstance().isUpdateTemplate());
	}
	
	private boolean	defaultExportImageValue	= true;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initOnExportImageSpinner()
	{
		Spinner spinner = (Spinner) findViewById(R.id.exportimage);

		// Set spinner content from enum value
		CharSequence[] itemArray = new CharSequence[CompressionAlgorithm.values().length];

		int i = 0;
		for (CompressionAlgorithm value : CompressionAlgorithm.values())
		{
			itemArray[i++] = value.getLabel();
		}
		List<CharSequence> itemList = new ArrayList<CharSequence>(Arrays.asList(itemArray));
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		//set the default according to value
		int spinnerPosition = adapter.getPosition(EnrollInfo.getInstance().getCompressionAlgorithm().getLabel());
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
					String compressionAlgo = "";
					switch (pos)
					{
						case 0:
							compressionAlgo = CompressionAlgorithm.NO_IMAGE.toString();
							break;
						case 1:
							compressionAlgo = CompressionAlgorithm.MORPHO_NO_COMPRESS.toString();
							break;
						case 2:
							compressionAlgo = CompressionAlgorithm.MORPHO_COMPRESS_V1.toString();
							break;
						case 3:
							compressionAlgo = CompressionAlgorithm.MORPHO_COMPRESS_WSQ.toString();
							break;
						default:
							break;
					}
					EnrollInfo.getInstance().setCompressionAlgorithm(Enum.valueOf(CompressionAlgorithm.class, compressionAlgo));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}

	private boolean	defaultFPTemplateTypeValue	= true;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initTemplateTypeSpinner()
	{
		Spinner spinner = (Spinner) findViewById(R.id.fptemplatetypeEnroll);
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
		int spinnerPosition = adapter.getPosition(EnrollInfo.getInstance().getTemplateType().toString());
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
					EnrollInfo.getInstance().setTemplateType(Enum.valueOf(TemplateType.class, item));
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		Spinner spinner2 = (Spinner) findViewById(R.id.fvptemplatetypeEnroll);

		// Set spinner content from enum value
		CharSequence[] itemArray2 = new CharSequence[TemplateFVPType.values().length - 1];
		int k = 0;
		for (TemplateFVPType value : TemplateFVPType.values())
		{
			if (!value.equals(TemplateFVPType.MORPHO_PK_FVP_MATCH))
			{
				itemArray2[k++] = value.toString();
			}
		}
		List<CharSequence> itemList2 = new ArrayList<CharSequence>(Arrays.asList(itemArray2));
		ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList2);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);

		int spinnerPosition2 = adapter.getPosition(EnrollInfo.getInstance().getFVPTemplateType().toString());
		spinner2.setSelection(spinnerPosition2);

		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				String item = (String) parent.getItemAtPosition(pos);
				EnrollInfo.getInstance().setFVPTemplateType(Enum.valueOf(TemplateFVPType.class, item));
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		spinner2.setEnabled(MorphoInfo.m_b_fvp);
	}

	@Override
	public MorphoInfo retrieveSettings()
	{
		EditText idNumber = (EditText) findViewById(R.id.idnumberEnroll);
		MorphoInfo ret = null;
		if (idNumber.getText().toString().trim().equals(""))
		{
			Toast.makeText(this, getResources().getString(R.string.mustenteridnumber), Toast.LENGTH_SHORT).show();
			((TextView) findViewById(R.id.idnumberlabelEnroll)).setTextColor(Color.RED);
		}
		else
		{
			((TextView) findViewById(R.id.idnumberlabelEnroll)).setTextColor(Color.BLACK);
			EditText lastname = (EditText) findViewById(R.id.lastnameEnroll);
			EditText firstname = (EditText) findViewById(R.id.firstnameEnroll);
			EnrollInfo.getInstance().setIDNumber(idNumber.getText().toString().trim());
			EnrollInfo.getInstance().setLastName(lastname.getText().toString().trim());
			EnrollInfo.getInstance().setFirstName(firstname.getText().toString().trim());
			ret = EnrollInfo.getInstance();
		}
		return ret;
	}

}
