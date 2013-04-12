/*
 * Copyright 2010 (C) Tom Parker <thpr@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.utils;

import java.util.Stack;
import java.util.StringTokenizer;

public class LastGroupSeparator
{

	private final String startingString;
	private StringBuilder root = null;

	public LastGroupSeparator(String baseString)
	{
		if (baseString == null)
		{
			throw new IllegalArgumentException(
					"Choose Separator cannot take null initialization String");
		}
		startingString = baseString;
	}

	public String process() throws GroupingMismatchException
	{
		StringTokenizer base = new StringTokenizer(startingString, "()", true);
		root = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		boolean isValid = false;
		Stack<String> expected = new Stack<String>();
		while (base.hasMoreTokens())
		{
			String working = base.nextToken();
			if (expected.isEmpty())
			{
				if (isValid)
				{
					root.append('(');
					root.append(temp);
					root.append(')');
				}
				temp = new StringBuilder();
				isValid = false;
			}
			if ("(".equals(working))
			{
				if (!expected.isEmpty())
				{
					temp.append(working);
				}
				isValid = true;
				expected.push(")");
			}
			else if (")".equals(working))
			{
				if (expected.isEmpty())
				{
					throw new GroupingMismatchException(startingString
							+ " did not have an open parenthesis "
							+ "before close: " + temp.toString());
				}
				else if (!")".equals(expected.pop()))
				{
					throw new GroupingMismatchException(startingString
							+ " did not have matching parenthesis "
							+ "inside of brackets: " + temp.toString());
				}
				else if (!expected.isEmpty())
				{
					temp.append(working);
				}
			}
			else if (expected.isEmpty())
			{
				root.append(working);
			}
			else
			{
				temp.append(working);
			}
		}
		if (expected.isEmpty())
		{
			if (!isValid)
			{
				return null;
			}
			return temp.toString();
		}
		throw new GroupingMismatchException(startingString
				+ " reached end of String while attempting to match: "
				+ expected.pop());
	}

	public String getRoot()
	{
		if (root == null)
		{
			throw new IllegalStateException();
		}
		return root.toString();
	}

	public static class GroupingMismatchException extends IllegalStateException
	{

		public GroupingMismatchException(String base)
		{
			super(base);
		}

	}
}
