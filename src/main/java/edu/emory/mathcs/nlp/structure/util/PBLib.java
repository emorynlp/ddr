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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTArc;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.propbank.PBArgument;
import edu.emory.mathcs.nlp.structure.propbank.PBInstance;
import edu.emory.mathcs.nlp.structure.propbank.PBReader;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBLib
{
	static public final String PREFIX_CONCATENATION	= "C-";
	static public final String PREFIX_REFERENT		= "R-";
	static public final String PREFIX_LINK			= "LINK-";
	static public final String DELIM_FUNCTION_TAG	= StringConst.HYPHEN;

	static public final String ARG0_FUNCTION_TAG	= PBTag.ARG0 + StringConst.HYPHEN;

	static public final Pattern P_R_ARG	= Pattern.compile("^"+PREFIX_REFERENT+"(.+)");
	static public final Pattern P_ARGN	= Pattern.compile("^(A|C-A|R-A)(RG)?(\\d|A)");
	static public final Pattern P_ARGM	= Pattern.compile("^A(RG)?M-(.+)");
	
	static private final Pattern P_LINK			= Pattern.compile("^"+PREFIX_LINK+"(.+)");
	static private final Pattern P_ARGN_CORE	= Pattern.compile("^A(RG)?(\\d|A)");

	static private final Map<Map<String, CTTree>, Map<CTNode, CTArc>> NULL_TREES = new HashMap<>();
	static private final Map<String, Map<String, Integer>> LABEL_DISTRIBUTION = new HashMap<>();
	
	private PBLib() {}
	
	static public List<CTTree> getTreeList(InputStream treebank, InputStream propbank)
	{
		return getTreeList(treebank, propbank, false);
	}
	
	static public List<CTTree> getTreeList(InputStream treebank, InputStream propbank, boolean normalize)
	{
		List<CTTree> trees = new CTReader(treebank).readTrees();
		PBReader reader = new PBReader(propbank);
		PBInstance instance;

		while ((instance = reader.next()) != null)
		{
			if (!isIllegalRolesetID(instance.getFrameID()))
			{
				CTTree tree = trees.get(instance.getTreeID());
				if (normalize) tree.normalizeIndices();
				tree.set(instance);
			}
		}
		
		return trees;
	}
	
	/** @param out internally casted to {@code new PrintStream(new BufferedOutputStream(out))}. */
	static public void printInstances(List<PBInstance> instances, OutputStream out)
	{
		PrintStream stream = IOUtils.createBufferedPrintStream(out);
		
		for (PBInstance instance : instances)
			stream.println(instance.toString());
				
		stream.close();
	}

	static public void printLabelDistribution(boolean toFile) throws FileNotFoundException
	{
		int total;
		String msg;
		PrintWriter out = null;
		if (toFile)
			out = new PrintWriter("../LabelDistribution.txt");
		else
			System.out.println("\nPrinting Label Distribution:");
		for(Map.Entry<String, Map<String, Integer>> e : LABEL_DISTRIBUTION.entrySet()) {
			total = 0;
			msg = "Prop Label: " + e.getKey();
			if (toFile) out.println(msg);
			else 		System.out.println(msg);

			for (Map.Entry<String, Integer> ee: e.getValue().entrySet()){
				msg = "\tDep Label: " + ee.getKey() + ", " + ee.getValue();
				if (toFile)	out.println(msg);
				else		System.out.println(msg);
				total += ee.getValue();
			}
			msg = "\tTotal: " + total;
			if (toFile) out.println(msg);
			else		System.out.println(msg);
		}
		if (toFile)
			out.close();
	}

	static public void printLabelDistribution() throws FileNotFoundException
	{
		printLabelDistribution(false);
	}

	static public void printNullTrees() throws FileNotFoundException
	{
		int count;
		try (PrintStream out = new PrintStream(new FileOutputStream("../ARG0-list.txt"))) {
			count = 1;
			for(Map.Entry<Map<String, CTTree>, Map<CTNode, CTArc>> e: NULL_TREES.entrySet()){
				out.println(count++);
//				out.println(e.getKey()); // CTTree

				for(Map.Entry<String, CTTree> ee: e.getKey().entrySet()){
					out.println(ee.getKey());
					out.println(ee.getValue());
				}

				for(Map.Entry<CTNode, CTArc> ee: e.getValue().entrySet()){
					out.println();
					out.println(ee.getValue()); // semArc
					out.println(ee.getKey()); // CTNode
					out.println(ee.getValue().getNode()); // semArc's node
				}
				out.println();
			}
		}
		System.out.println("Total Number of Null Trees: " + NULL_TREES.size());
	}

	static public void collectLabelDistribution(String propLabel, String depLabel)
	{
		Map<String, Integer> countPerDepLabel = LABEL_DISTRIBUTION.get(propLabel);
		if(countPerDepLabel == null) {
			countPerDepLabel= new HashMap<>();
			LABEL_DISTRIBUTION.put(propLabel, countPerDepLabel);
		}

		Integer count = countPerDepLabel.get(depLabel);
		if(count == null) {
			countPerDepLabel.put(depLabel, 1);
		} else {
			countPerDepLabel.put(depLabel, count+1);
		}
	}

	static public void collectLabelDistribution(String propLabel)
	{
		collectLabelDistribution(propLabel, "no-match");
	}

	static public void collectLabelDistributionNull(CTNode node, CTTree tree, String name) throws FileNotFoundException
	{
		// for ARG0 only
		String propName = PBTag.ARG0;
		Map<CTNode, CTArc> null_nodes = new HashMap<>();
		for(CTArc semArc: node.getSemanticHeads()) {

			if (node.isEmptyCategory()) continue;
			if (PTBLib.isWhPhrase(node)) continue;
			collectLabelDistribution(semArc.getLabel());
			if (semArc.getLabel().equals(propName)) {
				null_nodes.put(node, semArc);
			}
		}

		if (null_nodes.size() > 0) {
			Map<String, CTTree> i = new HashMap<>();
			i.put(name, tree);
			NULL_TREES.put(i, null_nodes);
		}
	}

	static public void insertSemanticHeads(CTTree tree, CTNode node, CTNode head, CTNode dep, CTArc arc, String name, boolean isPrimary) throws FileNotFoundException
	{
		CTNode semHead;
		String propLabel, depLabel;
		List<CTArc> done = new ArrayList<>();
		List<CTArc> semanticHeads = node.getSemanticHeads();


		for(CTArc semArc: semanticHeads){
			semHead = semArc.getNode();
			propLabel= semArc.getLabel();
			depLabel = arc.getLabel();

			if (head == semHead){
//				String testLabel = PBTag.ARG0;
//				if (propLabel.equals(testLabel)){
//					String filename = null;
//					if (depLabel.equals(DDGTag.NUM))
//						filename = "../" + testLabel + "-NUM.txt";
//					else if (depLabel.equals(DDGTag.AUX))
//						filename = "../" + testLabel + "-AUX.txt";
//					else if (depLabel.equals(DDGTag.CONJ))
//						filename = "../" + testLabel + "-CONJ.txt";
//					else if (depLabel.equals(DDGTag.R + DDGTag.PPMOD))
//						filename = "../" + testLabel + "-R-PPMOD.txt";
//					else if (depLabel.equals(DDGTag.VOC))
//						filename = "../" + testLabel + "-VOC.txt";
//					else if (depLabel.equals(DDGTag.DEP))
//						filename = "../" + testLabel + "-DEP.txt";
//					else if (depLabel.equals(DDGTag.DET))
//						filename = "../" + testLabel + "-DET.txt";
//					else if (depLabel.equals(DDGTag.DAT))
//						filename = "../" + testLabel + "-DAT.txt";
//					else if (depLabel.equals(DDGTag.ADVCL))
//						filename = "../" + testLabel + "-ADVCL.txt";
//					else if (depLabel.equals(DDGTag.MODAL))
//						filename = "../" + testLabel + "-MODAL.txt";
//					else if (depLabel.equals(DDGTag.CASE))
//						filename = "../" + testLabel + "-CASE.txt";
//					else if (depLabel.equals(DDGTag.R + DDGTag.DAT))
//						filename = "../" + testLabel + "-R-DAT.txt";
//					else if (depLabel.equals(DDGTag.COMP))
//						filename = "../" + testLabel + "-COMP.txt";
//					else if (depLabel.equals(DDGTag.POSS))
//						filename = "../" + testLabel + "-POSS.txt";
//					else if (depLabel.equals(DDGTag.P))
//						filename = "../" + testLabel + "-P.txt";
//					else if (depLabel.equals(DDGTag.ADV))
//						filename = "../" + testLabel + "-ADV.txt";
//					else if (depLabel.equals(DDGTag.OBJ))
//						filename = "../" + testLabel + "-OBJ.txt";
//					else if (depLabel.equals(DDGTag.META))
//						filename = "../" + testLabel + "-META.txt";
//					else if (depLabel.equals(DDGTag.ADVNP))
//						filename = "../" + testLabel + "-ADVNP.txt";
//					else if (depLabel.equals(DDGTag.RELCL))
//						filename = "../" + testLabel + "-RELCL.txt";
//					else if (depLabel.equals(DDGTag.APPO))
//						filename = "../" + testLabel + "-APPO.txt";
//					else if (depLabel.equals(DDGTag.R + DDGTag.OBJ))
//						filename = "../" + testLabel + "-R-OBJ.txt";
//					else if (depLabel.equals(DDGTag.R + DDGTag.ADV))
//						filename = "../" + testLabel + "-R-ADV.txt";
//
//				}

				if (isPrimary) {
					// update primary head
					dep.setPrimaryHead(head, propLabel);
					collectLabelDistribution(propLabel, depLabel);
					done.add(semArc);

					// coordination
					for(CTNode headChild: head.getChildren()){
						for (CTArc secArc : headChild.getSecondaryHeads())
						{
							if (secArc.getNode() == null) continue;
							CTNode secHead = secArc.getNode().getTerminalHead();
							String secDep  = secArc.getLabel();
							if  ( secHead==semHead && secDep.equals(DDGTag.CONJ))
								secArc.setLabel(propLabel);
						}
					}

				}
				else {
					// update secondary head
					dep.addSecondaryHead(head, propLabel);
					collectLabelDistribution(propLabel, depLabel);
					done.add(semArc);
				}
			} else{
				{
					// handle be verb
					if (semHead.isLemma("be")){
						for (CTArc argSemArc : node.getSemanticHeads()) {
							CTNode argSemHead = argSemArc.getNode();
							if (argSemHead.isLemma("be")) {
//								if (semHead.isPredicate())
//									semHead.setFrameID(null); // nolonger a predicate

								if (isPrimary) {
									dep.setPrimaryHead(head, propLabel);

									boolean flag = false;
									for(CTArc testArc: head.getSemanticHeads()){
										if (argSemArc==testArc){
											flag = true;
											break;
										}
									}
									if (flag)
										semHead.setPrimaryHead(head, DDGTag.COP);
									collectLabelDistribution(propLabel, depLabel);
									done.add(semArc);
								} else {
									// update secondary head
									dep.addSecondaryHead(head, propLabel);
									semHead.addSecondaryHead(head, DDGTag.COP);
									collectLabelDistribution(propLabel, depLabel);
									done.add(semArc);
								}
							}
						}
					}
				}
			}
		}

		// clean-up
		for (CTArc d: done)
			semanticHeads.remove(d);
	}

	static public boolean isIllegalRolesetID(String rolesetID)
	{
		return StringUtils.endsWithAny(rolesetID, "ER","NN","IE","YY");
	}

	static public boolean isUndefinedLabel(String label)
	{
		return label.endsWith("UNDEF");
	}

	static public boolean isNumberedArgument(String label)
	{
		return P_ARGN.matcher(label).find();
	}

	static public boolean isCoreNumberedArgument(String label)
	{
		return P_ARGN_CORE.matcher(label).find();
	}
	
	static public boolean isLinkArgument(String label)
	{
		return label.startsWith(PREFIX_LINK);
	}
	
	static public boolean isConcatenatedArgument(String label)
	{
		return label.startsWith(PREFIX_CONCATENATION);
	}
	
	static public boolean isReferentArgument(String label)
	{
		return label.startsWith(PREFIX_REFERENT);
	}
	
	static public boolean isModifier(String label)
	{
		return P_ARGM.matcher(label).find();
	}

	static public boolean isRel(String label)
	{
		return label.equals(PBTag.REL);
	}

	static public boolean isArg0(String label) {
		return label.startsWith(PBTag.ARG0);
	}

	static public boolean isArg1(String label)
	{
		return label.startsWith(PBTag.ARG1);
	}

	static public boolean isArg2(String label) {
		return label.startsWith(PBTag.ARG2);
	}

	static public boolean isArg3(String label)
	{
		return label.startsWith(PBTag.ARG3);
	}

	static public boolean isArg4(String label) {
		return label.startsWith(PBTag.ARG4);
	}

	static public boolean isArgA(String label)
	{
		return label.startsWith(PBTag.ARGA);
	}

	static public boolean isLightVerbRoleset(String rolesetID)
	{
		return rolesetID.endsWith("LV");
	}
	
	static public String getShortLabel(String label)
	{
		return PBTag.REL.equals(label) ? PBTag.C_V : "A"+label.substring(3);
	}
	
	/**
	 * @return the number of an numbered argument (e.g., "0", "A").
	 * If the label is not a numbered argument, returns {@code null}. 
	 */
	static public String getNumber(String label)
	{
		return PatternUtils.getGroup(P_ARGN, label, 3);
	}
	
	static public String getLinkType(String label)
	{
		return PatternUtils.getGroup(P_LINK, label, 1);
	}
	
	/**@return the type of the modifier if exists (e.g., "TMP", "LOC"); otherwise, {@code null}. */
	static public String getModifierType(String label)
	{
		return PatternUtils.getGroup(P_ARGM, label, 2);
	}
	
	/** @return the label discarding prefixes such as C- or R-. */
	static public String getBaseLabel(String label)
	{
		if (label.startsWith(PREFIX_CONCATENATION))
			return label.substring(PREFIX_CONCATENATION.length());
		else if (label.startsWith(PREFIX_REFERENT))
			return label.substring(PREFIX_REFERENT.length());
		else
			return label;
	}
	
//	static public void toReferentArgument(SRLArc arc)
//	{
//		String label = arc.getLabel();
//		
//		if (label.startsWith("A"))
//			arc.setLabel(PREFIX_REFERENT + label);
//		else if (label.startsWith(PREFIX_CONCATENATION))
//			arc.setLabel(PREFIX_REFERENT + label.substring(PREFIX_CONCATENATION.length()));
//	}

	static public void main(String[] args){
		String testPath = "/Users/chunjy92/Documents/Research/NLP/MeaningRepresentation/sample";
		InputStream parse = IOUtils.createFileInputStream(testPath+"/sample.parse");
		InputStream prop  = IOUtils.createFileInputStream(testPath+"/sample.prop");
		List<CTTree> trees = getTreeList(parse, prop);

		System.out.println("First Tree:");
		System.out.println(trees.get(0));
		CTTree first = trees.get(0);
		System.out.println("Its Size?: " + trees.size());


		PBReader reader = new PBReader(IOUtils.createFileInputStream(testPath+"/sample.prop"));
//		List<PBInstance> instances = reader.getSortedInstanceList(testPath+"/sample.prop");
		List<PBInstance> instances = reader.getSortedInstanceList();

		PBInstance instance;
		PBArgument argument;
		CTTree tree;
		CTNode node;
		int height;

		instance = instances.get(0);
		tree = instance.getTree();

//		System.out.println("Tree:");
//		System.out.println(tree);
//
//		for(CTNode token: tree.getTokens())
//		{
//			System.out.println(token);
//		}
//
//		System.exit(0);


		System.out.println(tree==first);

		System.out.println("Len: " + instances.size());
		for(PBInstance i: instances){
			System.out.println(i);
		}

		System.out.println("Instancd:");
		System.out.println(instance);

		System.out.println("Tree:");
		System.out.println(tree);

		argument = instance.getArgument(0);
		height = argument.getLocation(0).getHeight();
		node = tree.getNode(argument.getLocation(0));

		System.out.println("\nArgument:" + argument);
		System.out.println("Node:" + node);
		System.out.println("Height: " + height);
//		System.out.println(tree.getNode(argument.getLocation(0, height)));

		argument = instance.getArgument(1);
		node = tree.getNode(argument.getLocation(0));

		System.out.println("\nArgument:" + argument);
		System.out.println("Node:" + node);

		argument = instance.getArgument(2);
		node = tree.getNode(argument.getLocation(0));

		System.out.println("\nArgument:" + argument);
		System.out.println("Node:" + node);

		argument = instance.getArgument(3);
		node = tree.getNode(argument.getLocation(0));

		System.out.println("\nArgument:" + argument);
		System.out.println("Node:" + node);


		System.out.println("Working");
	}
}