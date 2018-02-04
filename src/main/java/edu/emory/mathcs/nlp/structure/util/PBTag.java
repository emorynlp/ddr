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
package edu.emory.mathcs.nlp.structure.util;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PBTag
{
	String REL  = "rel";
	String C_V  = "C-V";
	
	String ARG0 = "ARG0";
	String ARG1 = "ARG1";
	String ARG2 = "ARG2";
	String ARG3 = "ARG3";
	String ARG4 = "ARG4";
	/** External causer. */
	String ARGA = "ARGA";

	/** Adjectival. */
	String ARGM_ADJ = "ARGM-ADJ";
	/** Adverbial. */
	String ARGM_ADV = "ARGM-ADV";
	/** Cause. */
	String ARGM_CAU = "ARGM-CAU";
	/** Comitative. */
	String ARGM_COM = "ARGM-COM";
	/** Direction. */
	String ARGM_DIR = "ARGM-DIR";
	/** Discourse. */
	String ARGM_DIS = "ARGM-DIS";
	/** Goal. */
	String ARGM_GOL = "ARGM-GOL";
	/** Extent. */
	String ARGM_EXT = "ARGM-EXT";
	/** Location. */
	String ARGM_LOC = "ARGM-LOC";
	/** Manner. */
	String ARGM_MNR = "ARGM-MNR";
	/** Modal. */
	String ARGM_MOD = "ARGM-MOD";
	/** Negation. */
	String ARGM_NEG = "ARGM-NEG";
	/** Secondary predication. */
	String ARGM_PRD = "ARGM-PRD";
	/** Purpose. */
	String ARGM_PRP = "ARGM-PRP";
	/** Compound noun of light verb. */
	String ARGM_PRR = "ARGM-PRR";
	/** Recipricol. */
	String ARGM_REC = "ARGM-REC";
	/** Temporal. */
	String ARGM_TMP = "ARGM-TMP";
	
	/** Link caused by reduced relative clauses. */
	String LINK_SLC	= "LINK-SLC";
	/** Link caused by *PRO*. */
	String LINK_PRO	= "LINK-PRO";
	/** Link caused by passive constructions. */
	String LINK_PSV	= "LINK-PSV";
}