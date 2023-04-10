// Generated from D:/Lessons/Compiler/Projects/Compiler-Course-Projects/Phase-1/src\LogicPL.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LogicPLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LogicPLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LogicPLParser#logicPL}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicPL(LogicPLParser.LogicPLContext ctx);
}