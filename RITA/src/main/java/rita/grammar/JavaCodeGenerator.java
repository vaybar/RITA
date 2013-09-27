/*
 * Copyright 2013 Vanessa Aybar Rosales
 * This file is part of RITA.
 * RITA is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 * RITA is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with 
 * RITA. If not, see http://www.gnu.org/licenses/.
 */

package rita.grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import renderable.RenderableBlock;
import rita.grammar.exception.IncompleteSentenceException;
import rita.grammar.robocode.Method;
import rita.grammar.robocode.MethodWithEvent;
import rita.grammar.robocode.SimpleCommand;
import rita.grammar.simplesentence.Adapter;
import rita.grammar.simplesentence.Caller;
import rita.grammar.simplesentence.DataBlock;
import rita.grammar.simplesentence.EventMethod;
import rita.grammar.simplesentence.Getter;
import rita.grammar.simplesentence.InfixOperator;
import rita.grammar.simplesentence.MethodWithBracket;
import rita.grammar.simplesentence.PrefixOperator;
import rita.grammar.simplesentence.Setter;
import rita.grammar.simplesentence.VariableDeclaration;
import rita.settings.HelperEditor;
import rita.settings.LabelMap;
import rita.settings.RobocodeSetting;
import rita.settings.Settings;
import rita.util.RitaUtilities;
import rita.widget.SourceCode;
import workspace.Page;
import codeblocks.Block;
import codeblocks.BlockConnector;
/**
 * Esta clase tiene la responsabilidad de traducir un conjunto de bloques Openblocks
 * en c�digo Java
 *
 * @author Vanessa Aybar Rosales
 * */
public class JavaCodeGenerator {

	public static String nl = System.getProperty("line.separator");
	public static int indentLevel = 0;

	/**
	 * Returns a string repeating the space character for <code>count</code>
	 * times.
	 */
	public static String spaces(int count) {
		return chars(' ', count);
	}

	/**
	 * Returns a string repeating the character <code>mark</code> for
	 * <code>count</code> times.
	 */
	public static String chars(char mark, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			sb.append(mark);
		}
		return sb.toString();
	}

	private static Set<String> importSet = new TreeSet<String>();
        private static Set<String> importStaticSet = new TreeSet<String>();

	public static List<StringBuffer> generateCode(Page page) {
		importSet.clear();
	
		List<StringBuffer> listCompleteCode = new ArrayList<StringBuffer>();

		inconsistent = false;


		StringBuffer classDeclaration = new StringBuffer();
		classDeclaration.append("public class " + HelperEditor.currentRobotName
				+ " extends "
				+ RobocodeSetting.get(HelperEditor.currentRobotType) + "{ "
				+ nl);
		StringBuffer endClassDeclaration = new StringBuffer("}");
		RenderableBlock nextRBlock = null;

		for (RenderableBlock rBlock : getListStarterBlock(page)) {
			nextRBlock = rBlock;
			listCompleteCode.addAll(generateCode(nextRBlock));
			listCompleteCode.add(new StringBuffer(nl));
		}
		// add import before code
		List<StringBuffer> listImport = new ArrayList<StringBuffer>();

		StringBuffer sbImport = null;
		Iterator<String> it = importSet.iterator();
		while (it.hasNext()) {
			sbImport = new StringBuffer();
			sbImport.append("import ");
			sbImport.append(it.next());
			sbImport.append(";");
			sbImport.append(nl);
			listImport.add(sbImport);
//			list.add(sbImport);
		}


		List<StringBuffer> listStaticImport = new ArrayList<StringBuffer>();
		it = importStaticSet.iterator();
		while (it.hasNext()) {
			sbImport = new StringBuffer();
			sbImport.append("import static ");
			sbImport.append(it.next());
			sbImport.append(";");
			sbImport.append(nl);
			listStaticImport.add(sbImport);
		}
		listCompleteCode.add(0, classDeclaration);
		listCompleteCode.addAll(0, listStaticImport);
		listCompleteCode.addAll(0, listImport);
		listCompleteCode.add(0, getRobocodeImports());
		listCompleteCode.add(0, getPackageName());
		listCompleteCode.add(endClassDeclaration);

		return listCompleteCode;
	}

	private static StringBuffer getRobocodeImports() {
		List<String> imports = RobocodeSetting
				.getRobocodeImports(HelperEditor.currentRobotType);
		StringBuffer sb = new StringBuffer();
		for (String aImport : imports) {
			sb.append("import ");
			sb.append(aImport);
			sb.append(";");
			sb.append(nl);
		}
		return sb;
	}

	private static StringBuffer getPackageName() {
		StringBuffer sb = new StringBuffer();
		sb.append("package ");
		sb.append(HelperEditor.currentRobotPackage);
		sb.append(";");
		sb.append(nl);
		return sb;
	}

	private static boolean inconsistent = false;

	/**
	 * Genera c�digo java de manera recursiva a partir de un RenderableBlock
	 *
	 * @param rBlock
	 *            el Renderable block a partir del cual se genera el c�digo Java
	 */
	public static List<StringBuffer> generateCode(RenderableBlock rBlock) {

		List<StringBuffer> contentList = new ArrayList<StringBuffer>();
		if (rBlock != null && rBlock.getComment() != null)
			contentList.add(new StringBuffer(JavaCodeGenerator
					.getAsComment(rBlock.getComment().getText())));

		if (rBlock.getBlock() != null)
			rBlock.setLabelInstanceNumber(LabelMap.getNextNumber(rBlock
					.getBlock().getBlockLabel()));

		Command cs = null;
		Block block = rBlock.getBlock();
		if (block.isControlBlock())
			cs = SentenceMap.getTemplateSentence(rBlock);
		// special case of Command
		if (block.getGenusName().equals("callerprocedure")  || RitaUtilities.isMathFunction(block))
			cs = new Caller();
		// TODO Vanessa verificar todos los posibles getter y setter
		// getterdeclaration getterproc-param-number
		
		if (block.isDataBlock() && block.getGenusName().contains("getter"))
			cs = new Getter();
		if (block.isCommandBlock() && block.getGenusName().contains("setter"))
			cs = new Setter();
		if (block.isCommandBlock()
				&& !block.getGenusName().equals("callerprocedure")
				&& !block.getGenusName().contains("getter")
				&& !block.getGenusName().contains("setter"))
			cs = new SimpleCommand(block);
		if (block.isMethodDeclBlock())
			cs = new Method(block);
		if (block.isProcedureDeclBlock())
			cs = new CustomMethod(block);
		// PERERATARRAGONA
		if (block.isMethodWithEvent()) {
			if (block.getProperty("import") != null) {
				importSet.add(block.getProperty("import"));
			}
			cs = new MethodWithEvent(block);
		}
		  
		//PERERATARRAGONA
		if (block.isMiddleOperator())
			cs = new InfixOperator();
		if (block.isPrefixOperator())
			cs = new PrefixOperator();
		if (block.isVariableDeclBlock())
			cs = new VariableDeclaration();
		if (block.isReturnBlock())
			cs = new ReturnBlock();
	

		if (block.isDataBlock() && !block.getGenusName().contains("getter")

		    && !block.getGenusName().contains("caller")
		    && !block.getGenusName().startsWith("cast"))
			cs = new DataBlock();

		if (block.isDataBlock() && block.getGenusName().startsWith("cast")) {
			cs = new Adapter();
		}

		if (rBlock.getBlock().getProperty("import") != null) {
		    importSet.add(rBlock.getBlock().getProperty("import"));
		}

		if (rBlock.getBlock().getProperty("import-static") != null) {
		    importStaticSet.add(rBlock.getBlock().getProperty("import-static"));
		}

//=======
		
		// PERERATARRAGONA
		if (block.isMethod()){
			cs = new MethodWithBracket();
		}
		if (block.isEventMethod()){
			cs = new EventMethod();
		}
		// END PERERATARRAGONA

		if (cs == null) {
			StringBuffer sb = null;
			if (rBlock.getBlock().getProperty("vm-cmd-name") != null) {
				sb = new StringBuffer(rBlock.getBlock().getProperty(
						"vm-cmd-name"));
			} else {
				if (rBlock.getBlock().getProperty("vm-value-type").contains("Color.")){
					sb = new StringBuffer(block.getProperty("vm-value-type"));
				} else {
					sb = new StringBuffer(block.getBlockLabel());
				}
			}
				

			contentList.add(sb);
			return contentList;

		}

		try {
			contentList.addAll(cs.render(rBlock));
			if (cs.isCaller()
					&& (Block.getBlock(block.getAfterBlockID()) != null || Block
							.getBlock(block.getBeforeBlockID()) != null)) {
				contentList.add(new StringBuffer(";"));
			}
			if (!block.isProcedureDeclBlock()
					&& Block.getBlock(block.getAfterBlockID()) != null) {
				contentList.addAll(generateCode(RenderableBlock
						.getRenderableBlock(block.getAfterBlockID())));
			}

			inconsistent = inconsistent || false;
		} catch (IncompleteSentenceException e) {
			/* esto puede pasar si el panel de codigo esta visible mientras el usuario esta construyendo una sentencia */
			inconsistent = true;
		}
		SourceCode.getInstance().setInconsistentCode(inconsistent);
		return contentList;
	}

	public static List<StringBuffer> generateCode(Block block) {
		List<BlockConnector> tempList = new ArrayList<BlockConnector>();
		List<StringBuffer> contentList = new ArrayList<StringBuffer>();

		int start = 0;
		for (BlockConnector bc : block.getSockets()) {
			if (bc.hasDefArg()) {
				Block tempBlock = Block.getBlock(bc.getBlockID());
				if (tempBlock.getProperty("import") != null) {
					importSet.add(tempBlock.getProperty("import"));
				}

				if (tempBlock.getProperty("import-static") != null) {
					importSet.add(tempBlock.getProperty("import-static"));
				}
			}
			if (start > 0) {
				// como hay varios argumentos se inserta coma
				contentList.add(new StringBuffer(","));
			}
			start++;
			tempList.add(bc);
			contentList.addAll(generateCode(tempList));
			// }
		}
		if (contentList != null
				&& contentList.size() > 0
				&& contentList.get(contentList.size() - 1).charAt(
						contentList.get(contentList.size() - 1).length() - 1) == ';')
			contentList.add(new StringBuffer(nl));

		return contentList;
	}

	public static List<StringBuffer> generateCode(List<BlockConnector> bcList) {

		List<StringBuffer> tempList = new ArrayList<StringBuffer>();
		RenderableBlock rBlock = null;
		@SuppressWarnings("unused")
		StringBuffer sb = null;
		List<StringBuffer> processRBlock = new ArrayList<StringBuffer>();

		for (BlockConnector bc : bcList) {
			if (bc.getBlockID() != -1) {
				rBlock = RenderableBlock.getRenderableBlock(bc.getBlockID());
				processRBlock = generateCode(rBlock);
				if (processRBlock.size() > 0) {
					sb = processRBlock.get(0);
					tempList.addAll(processRBlock);
				}
			}
		}
		return tempList;
	}

	private static List<RenderableBlock> getListStarterBlock(Page page) {

		List<RenderableBlock> rBlocks = new ArrayList<RenderableBlock>();
		if (page.getBlocks() != null) { // puede ser null si estoy abriendo una
										// nueva pagina
			for (RenderableBlock rBlock : page.getBlocks()) {
				if (rBlock.getBlock().isMethodDeclBlock()
						&& rBlock.getBlock().isStarter()
						|| (rBlock.getBlock().isDeclaration() && rBlock
								.getBlock().getBeforeBlockID() == Block.NULL)) {
					rBlocks.add(rBlock);
				}
			}
		}
		return rBlocks;
	}

	public static String getAsComment(String text) {
		return JavaCodeGenerator.nl + "/*" + text + "*/";
	}

}
