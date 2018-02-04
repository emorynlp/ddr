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
package edu.emory.mathcs.nlp.structure.verbnet;

import java.io.Serializable;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.common.util.XMLUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNFrame implements Serializable
{
	private static final long serialVersionUID = 1907495757606414993L;
	
	private VNSyntax    v_syntax;
	private VNSemantics v_semantics;
	
	public VNFrame(Element eFrame)
	{
		init(eFrame);
	}
	
	private void init(Element eFrame)
	{
		setSyntax(new VNSyntax(XMLUtils.getFirstElementByTagName(eFrame, VNXml.E_SYNTAX)));
		setSemantics(new VNSemantics(XMLUtils.getFirstElementByTagName(eFrame, VNXml.E_SEMANTICS)));
	}
	
	public VNSyntax getSyntax()
	{
		return v_syntax;
	}
	
	public VNSemantics getSemantics()
	{
		return v_semantics;
	}
	
	public void setSyntax(VNSyntax syntax)
	{
		v_syntax = syntax;
	}
	
	public void setSemantics(VNSemantics semantics)
	{
		v_semantics = semantics;
	}
}