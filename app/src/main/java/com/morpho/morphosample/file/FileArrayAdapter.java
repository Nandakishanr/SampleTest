// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.manvish.sampletest.R;
import com.morpho.morphosample.file.Option.ItemType;

import java.util.List;

public class FileArrayAdapter extends ArrayAdapter<Option>
{
	private Context			c;
	private int				id;
	private List<Option>	items;

	public FileArrayAdapter(Context context, int textViewResourceId, List<Option> objects)
	{
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}

	public Option getItem(int i)
	{
		return items.get(i);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
		}
		final Option o = items.get(position);
		if (o != null)
		{
			TextView t1 = (TextView) v.findViewById(R.id.numberofdatabasesvalue);
			TextView t2 = (TextView) v.findViewById(R.id.TextView02);
			ImageView i1 = (ImageView) v.findViewById(R.id.ImageView01);
			if (t1 != null)
			{
				t1.setText(o.getName());
			}
			if (t2 != null)
			{
				t2.setText(o.getData());
			}
			if (i1 != null)
			{
				int type = R.drawable.file;
				if (o.getType() == ItemType.Folder)
				{
					type = R.drawable.folder;
				}
				i1.setImageResource(type);
			}
		}
		return v;
	}
}