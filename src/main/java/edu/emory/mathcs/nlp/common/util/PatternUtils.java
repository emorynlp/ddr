/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.PatternConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PatternUtils implements PatternConst
{
//	====================================== Getters ======================================

	static public Pattern createClosedPattern(String regex)
	{
		return Pattern.compile("^("+regex+")$");
	}
	
	static public Pattern createClosedORPattern(String... regex)
	{
		return createClosedPattern(createORString(regex));
	}
	
	static public Pattern createORPattern(String... regex)
	{
		return Pattern.compile(createORString(regex));
	}
		
	static public String createORString(String... regex)
	{
		StringBuilder build = new StringBuilder();
		
		for (String r : regex)
		{
			build.append("|");
			build.append(r);
		}
		
		return build.substring(1);		
	}
	
	/** @return {@code null} if not exists. */
	static public String getGroup(Pattern pattern, String str, int index)
	{
		Matcher m = pattern.matcher(str);
		return m.find() ? m.group(index) : null;
	}
	
//	====================================== Booleans ======================================
	
	/** @return {@code true} if the specific string contains only digits. */
	static public boolean containsPunctuation(String s)
	{
		return PUNCT.matcher(s).find();
	}

//	====================================== Punctuation ======================================
	
	/** Reverts coded brackets to their original forms (e.g., from {@code "-LBR-"} to {@code "("}). */
	static public String revertSymbols(String form)
	{
		return SYMBOL_CODE_MAP.getOrDefault(form, form);
	}
	
	/** Called by {@link #revertSymbols(String)}. */
	@SuppressWarnings("serial")
	static private final Map<String,String> SYMBOL_CODE_MAP = new HashMap<String,String>()
	{{
		put("-LRB-", "(");
		put("-LSB-", "[");
		put("-LCB-", "{");
		put("-RRB-", ")");
		put("-RSB-", "]");
		put("-RCB-", "}");
		put("``"   , "\"");
		put("''"   , "\"");
	}};
	
//	====================================== Replace ======================================

	static public String replaceAll(Pattern p, String s, String replacement)
	{
		Matcher m = p.matcher(s);
		return m.replaceAll(replacement);
	}
}