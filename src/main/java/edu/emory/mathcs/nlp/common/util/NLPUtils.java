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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.nlp.structure.conversion.C2DConverter;
import edu.emory.mathcs.nlp.structure.conversion.EnglishC2DConverter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	public static final Logger LOG = LoggerFactory.getLogger(NLPUtils.class);
	static public String FEAT_POS_2ND   = "pos2";
	static public String FEAT_PREDICATE = "pred";
	
	/** The feat-key of semantic function tags. */
	static public final String FEAT_SEM	= "sem";
	/** The feat-key of syntactic function tags. */
	static public final String FEAT_SYN	= "syn";
	/** The feat-key of sentence types. */
	static public final String FEAT_SNT	= "snt";
	/** The feat-key of PropBank rolesets. */
	static public final String FEAT_PB	= "pb";
	/** The feat-key of VerbNet classes. */
	static public final String FEAT_VN	= "vn";
	/** The feat-key of word senses. */
	static public final String FEAT_WS	= "ws";
	/** The feat-key of 2nd pos tag. */
	static public final String FEAT_POS2 = "p2";
	/** The feat-key of 2nd ner tag. */
	static public final String FEAT_NER2 = "n2";
	/** The feat-key of sentiments (for root). */
	static public final String FEAT_FUTURE = "fut";
	
	static public C2DConverter getC2DConverter(Language language)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishC2DConverter();
		default: new IllegalArgumentException("Invalid language: "+language);
		}
		
		return null;
	}
}
