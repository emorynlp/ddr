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
package edu.emory.mathcs.nlp.component.morph.english;

import edu.emory.mathcs.nlp.component.morph.util.AbstractAffixMatcher;
import edu.emory.mathcs.nlp.component.morph.util.AbstractAffixReplacer;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishSuffixMatcher extends AbstractAffixMatcher
{
	public EnglishSuffixMatcher(String affixCanonicalForm, String affixPOS, Pattern originalPOS)
	{
		super(affixCanonicalForm, affixPOS, originalPOS);
	}
	
	@Override
	public String getBaseForm(Map<String,Set<String>> baseMap, String form, String pos)
	{
		if (!matchesOriginalPOS(pos)) return null;
		String base;
		
		for (AbstractAffixReplacer replacer : l_replacers)
		{
			base = replacer.getBaseForm(baseMap, form);
			if (base != null) return base;
		}
		
		return null;
	}
	
	@Override
	public String getBaseForm(Set<String> baseSet, String form, String pos)
	{
		return matchesOriginalPOS(pos) ? getBaseForm(baseSet, form) : null; 
	}
	
	@Override
	public String getBaseForm(Set<String> baseSet, String form)
	{
		String base;
		
		for (AbstractAffixReplacer replacer : l_replacers)
		{
			base = replacer.getBaseForm(baseSet, form);
			if (base != null) return base;
		}
		
		return null;
	}
}
