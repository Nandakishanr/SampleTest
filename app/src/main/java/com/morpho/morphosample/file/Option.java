// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample.file;

public class Option implements Comparable<Option>
{

	public enum ItemType
	{
		File, Folder
	}

	private String		name;
	private String		data;
	private String		path;
	private ItemType	type;

	public Option(String n, String d, String p, ItemType t)
	{
		name = n;
		data = d;
		path = p;
		type = t;
	}

	public String getName()
	{
		return name;
	}

	public String getData()
	{
		return data;
	}

	public String getPath()
	{
		return path;
	}

	public ItemType getType()
	{
		return type;
	}

	@Override
	public int compareTo(Option o)
	{
		if (this.name != null)
		{
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
}