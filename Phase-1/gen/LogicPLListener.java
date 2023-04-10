// Generated from D:/Lessons/Compiler/Projects/Compiler-Course-Projects/Phase-1/src\LogicPL.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LogicPLParser}.
 */
public interface LogicPLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LogicPLParser#logicPL}.
	 * @param ctx the parse tree
	 */
	void enterLogicPL(LogicPLParser.LogicPLContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicPLParser#logicPL}.
	 * @param ctx the parse tree
	 */
	void exitLogicPL(LogicPLParser.LogicPLContext ctx);
}