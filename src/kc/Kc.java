package kc;

/**
 * 字句解析プログラムのコンパイラ本体
 *
 * @author 18-1-037-0051 松村圭悟
 * 問題番号: 問題4.1
 * 再提出日: 2020年7月22日
 *
 */
public class Kc {
	private LexicalAnalyzer lexer; // 字句解析器
	private Token token; // 字句解析器からもらうトークン
	private static boolean dbg = true; // テスト用

	/**
	 * コンストラクタ
	 */
	Kc(String sourceFileName) {
		/* 字句解析器のインスタンス生成と lexer による参照 */
		lexer = new LexicalAnalyzer(sourceFileName);
		token = lexer.nextToken();
	}

	/**
	 * 再帰下降型構文解析を行うメソッド． 入力が構文規則に違反していることを検出した場合，エラーの内容を表示する文字列を引数として，
	 * syntaxErrorメソッドを呼び出す． <program>の解析
	 */
	void parseProgram() {
		if (dbg)System.out.print("parseProgram()\n");
		if (isMainFirst(token))parseMainFunction(); else syntaxError("mainがありません");
		if (!(token.checkSymbol(Symbol.EOF)))syntaxError("ファイル末ではありません");
		if (dbg)System.out.print("parseProgramおわり()\n");
	}

	/**
	 * <MainFunction>の解析
	 */
	void parseMainFunction() {
		if (dbg)System.out.print("parseMainFunc()\n");
		if (token.checkSymbol(Symbol.MAIN))nextTkn(); else syntaxError("'main'がありません");
		if (token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if (token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (isBlockFirst(token))parseBlock(); else syntaxError("'Block'がありません");
		if (dbg)System.out.print("parseMainFunctionおわり()\n");
	}

	/**
	 * <Bloclk>の解析
	 */
	void parseBlock() {
		if (dbg)System.out.print("parseBlock()\n");
		if (token.checkSymbol(Symbol.LBRACE))nextTkn(); else syntaxError("'{'がありません");
		while (isDeclFirst(token))parseDecl();
		while (isStFirst(token))parseSt();
		if (token.checkSymbol(Symbol.RBRACE))nextTkn(); else syntaxError("'}'がありません");
		if (dbg)System.out.print("parseBlockおわり()\n");
	}

	/**
	 * <Var_decl>の解析
	 */
	void parseDecl() {
		if (dbg)System.out.print("parseDecl()\n");
		if (token.checkSymbol(Symbol.INT))nextTkn(); else syntaxError("'int'がありません");
		if (isNameFirst(token))parseNameList(); else syntaxError("'NameList'がありません");
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';'がありません");
		if (dbg)System.out.print("parseDeclおわり()\n");
	}

	/**
	 * <Name_list>の解析
	 */
	void parseNameList() {
		if (dbg)System.out.print("parseNameList()\n");
		if (isNameFirst(token))parseName(); else syntaxError("nameがありません");
		while (token.checkSymbol(Symbol.COMMA)) {
			nextTkn();
			if (isNameFirst(token))parseName(); else syntaxError("'Name'がありません");
		}
		if (dbg)System.out.print("parseNameListおわり()\n");
	}

	/**
	 * <Name>の解析
	 */
	void parseName() {
		if (dbg)System.out.print("parseName()\n");
		if (token.checkSymbol(Symbol.NAME))nextTkn();
		if (token.checkSymbol(Symbol.ASSIGN) || token.checkSymbol(Symbol.LBRACKET)) {
			if (token.checkSymbol(Symbol.ASSIGN)){
				nextTkn();
				if (isConstantFirst(token))parseConstant(); else syntaxError("'Constant'がありません");
			} else if (token.checkSymbol(Symbol.LBRACKET)) {
				nextTkn();
				if (token.checkSymbol(Symbol.INTEGER)) {
					nextTkn();
					if (token.checkSymbol(Symbol.RBRACKET))nextTkn(); else syntaxError("']'がありません");
				} else if (token.checkSymbol(Symbol.RBRACKET)) {
					nextTkn();
					if (token.checkSymbol(Symbol.ASSIGN))nextTkn(); else syntaxError("'='がありません");
					if (token.checkSymbol(Symbol.LBRACE))nextTkn(); else syntaxError("'{'がありません");
					if (isConstantFirst(token))parseConstantList(); else syntaxError("'ConstantList'がありません");
					if (token.checkSymbol(Symbol.RBRACE))nextTkn(); else syntaxError("'}'がありません");
				}
			}
		}
		if (dbg)System.out.print("parseNameおわり()\n");
	}

	/**
	 * <Constant_list> の解析
	 */
	void parseConstantList() {
		if (dbg)System.out.print("parseConstantList()\n");
		if (isConstantFirst(token))parseConstant(); else syntaxError("constantがありません");
		while (token.checkSymbol(Symbol.COMMA)) {
			nextTkn();
			if (isConstantFirst(token))parseConstant();
		}
		if (dbg)System.out.print("parseConstantListおわり()\n");
	}

	/**
	 * <Constant>の解析
	 */
	void parseConstant() {
		if (dbg)System.out.print("parseConstant()\n");
		if (token.checkSymbol(Symbol.SUB)) {
			nextTkn();
			if (token.checkSymbol(Symbol.INTEGER))nextTkn();
			else syntaxError("integerがありません");
		} else if (token.checkSymbol(Symbol.INTEGER))nextTkn();
		else if (token.checkSymbol(Symbol.CHARACTER))nextTkn();
		if (dbg)System.out.print("parseConstantおわり()\n");
	}

	/**
	 * <Statement>の解析
	 */
	void parseSt() {
		if (dbg)System.out.print("parseSt()\n");
		if (isIfStFirst(token))parseIfSt();
		else if (isWhileStFirst(token))parseWhileSt();
		else if (isForStFirst(token))parseForSt();
		else if (isExpFirst(token))parseExpStatement();
		else if (isOutputcharStFirst(token))parseOutputcharSt();
		else if (isOutputintStFirst(token))parseOutputintSt();
		else if (isBreakStFirst(token))parseBreakSt();
		else if (token.checkSymbol(Symbol.LBRACE)) {
			nextTkn();
			while (isStFirst(token))parseSt();
			if (token.checkSymbol(Symbol.RBRACE))nextTkn(); else syntaxError("'}'がありません");
		} else if (token.checkSymbol(Symbol.SEMICOLON))nextTkn();
		else syntaxError("'Statement'がありません");
		if (dbg)System.out.print("parseStおわり()\n");
	}

	/**
	 * <If_statement>の解析
	 */
	void parseIfSt() {
		if (dbg)System.out.print("parseIfSt()\n");
		if (token.checkSymbol(Symbol.IF))nextTkn(); else syntaxError("'if'がありません");
		if (token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (isStFirst(token))parseSt(); else syntaxError("'Statement'がありません");
		if (dbg)System.out.print("parseIfSt()おわり\n");
	}
	/**
	 * while_Statementの解析
	 */
	void parseWhileSt() {
		if (dbg)System.out.print("parseWhileSt()\n");
		if (token.checkSymbol(Symbol.WHILE))nextTkn(); else syntaxError("'while'がありません");
		if (token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (isStFirst(token))parseSt(); else syntaxError("'Statement'がありません");
		if (dbg)System.out.print("parseWhileSt()おわり\n");
	}
	/**
	 * Exp_Statementの解析
	 */
	void parseExpStatement() {
		if (dbg)System.out.print("parseExpStatement()\n");
		if (isExpFirst(token))parseExpression();
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';'がありません");
		if (dbg)System.out.print("parseExpStatementおわり()\n");
	}
	/**
	 * For _Statementの解析
	 */
	void parseForSt() {
		if (dbg)System.out.print("parseForSt()\n");
		if (token.checkSymbol(Symbol.FOR))nextTkn(); else syntaxError("'for'がありません");
		if (token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';'がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';'がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (isStFirst(token))parseSt();
		if (dbg)System.out.print("parseForStおわり()\n");
	}
	/**
	 * OutputCharStatement の解析
	 */
	void parseOutputcharSt() {
		if (dbg)System.out.print("parseOutputcharSt()\n");
		if (token.checkSymbol(Symbol.OUTPUTCHAR))nextTkn(); else syntaxError("'outputchar'がありません");
		if (token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';がありません");
		if (dbg)System.out.print("parseOutputcharStおわり()\n");
	}
	/**
	 * OutputintStatementの解析
	 */
	void parseOutputintSt() {
		if (dbg)System.out.print("parseOutputintSt()\n");
		if (token.checkSymbol(Symbol.OUTPUTINT))nextTkn(); else syntaxError("'outputint'がありません");
		if (token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		if (token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';'がありません");
		if (dbg)System.out.print("parseOutputintStおわり()\n");
	}
	/**
	 * Breal Statementの解析
	 */
	void parseBreakSt() {
		if (dbg)System.out.print("parseBreak()\n");
		if (token.checkSymbol(Symbol.BREAK))nextTkn(); else syntaxError("'break'がありません");
		if (token.checkSymbol(Symbol.SEMICOLON))nextTkn(); else syntaxError("';がありません");
		if (dbg)System.out.print("parseBreakおわり()\n");
	}
	/**
	 * Expressionの解析
	 */
	void parseExpression() {
		if (dbg)System.out.print("parseExpression\n");
		if(isExpFirst(token))parseExp(); 
		if (token.checkSymbol(Symbol.ASSIGN) || token.checkSymbol(Symbol.ASSIGNADD)
				|| token.checkSymbol(Symbol.ASSIGNSUB) || token.checkSymbol(Symbol.ASSIGNMUL)
				|| token.checkSymbol(Symbol.ASSIGNDIV)) {
			switch (token.getSymbol()) {
			case ASSIGN:
				nextTkn();
				break;
			case ASSIGNADD:
				nextTkn();
				break;
			case ASSIGNSUB:
				nextTkn();
				break;
			case ASSIGNMUL:
				nextTkn();
				break;
			case ASSIGNDIV:
				nextTkn();
				break;
			default:
				break;
			}
			if(isExpFirst(token))parseExpression(); 
		}
		if (dbg)System.out.print("parseExpression()おわり\n");
	}
	/**
	 * Expの解析
	 */
	void parseExp() {
		if (isExpFirst(token))parseLogicalTerm();
		if (token.checkSymbol(Symbol.OR)) {
			nextTkn();
			if (isExpFirst(token))parseExp(); 
		}
		if (dbg)System.out.print("parseExp()おわり\n");
	}
	/**
	 * Logaicaltermの解析
	 */
	void parseLogicalTerm() {
		if (dbg)System.out.print("parseLogicalTerm()\n");
		if (isExpFirst(token))parseLogicalFactor();
		if (token.checkSymbol(Symbol.AND)) {
			nextTkn();
			if (isExpFirst(token))parseLogicalTerm(); 
		}
		if (dbg)System.out.print("parseLgicalTerm()おわり\n");
	}
	/**
	 * logicalFactorの解析
	 */
	void parseLogicalFactor() {
		if (dbg)System.out.print("parseLogicalFactor()\n");
		if (isExpFirst(token))parseArithmeticExpression();
		if(token.checkSymbol(Symbol.EQUAL) || token.checkSymbol(Symbol.NOTEQ)
				|| token.checkSymbol(Symbol.LESS) || token.checkSymbol(Symbol.GREAT)){
			switch (token.getSymbol()) {
			case EQUAL:
				nextTkn();
				break;
			case NOTEQ:
				nextTkn();
				break;
			case LESS:
				nextTkn();
				break;
			case GREAT:
				nextTkn();
				break;
			default:
				return;
			}
			if (isExpFirst(token))parseArithmeticExpression(); else syntaxError("'ArithmetricExpression'がありません");
		}
		if (dbg)System.out.print("parseLgicalFactor()おわり\n");
	}
	/**
	 * arithmeticExpresiionの解析
	 */
	void parseArithmeticExpression() {
		if (dbg)System.out.print("parseArithmeticExpression()\n");
		if (isExpFirst(token))parseArithmeticTerm();
		while (token.checkSymbol(Symbol.ADD) || token.checkSymbol(Symbol.SUB)) {
			nextTkn();
			if (isExpFirst(token))parseArithmeticTerm(); else syntaxError("'ArithmetricTerm'がありません");
		}
		if (dbg)System.out.print("parseArithmeticExpression()おわり\n");
	}
	/**
	 * ArithmeticTermの解析
	 */
	void parseArithmeticTerm() {
		if (dbg)System.out.print("parseArithmeticTerm()\n");
		if (isExpFirst(token))parseArithmeticFactor();
		while (token.checkSymbol(Symbol.MUL) || token.checkSymbol(Symbol.DIV) || token.checkSymbol(Symbol.MOD)) {
			nextTkn();
			if (isExpFirst(token))parseArithmeticFactor(); else syntaxError("'ArithmetricFactor'がありません");
		}
		if (dbg)System.out.print("parseArithmeticTerm()おわり\n");
	}
	/**
	 * ArithmeticFactorの解析
	 */
	void parseArithmeticFactor() {
		if (dbg)System.out.print("parseArithmeticFactor()\n");
		if (isUnsignedFactorFirst(token))parseUnsignedFactor();
		else if (token.checkSymbol(Symbol.SUB)) {
			nextTkn();
			if (isExpFirst(token))parseArithmeticFactor(); else syntaxError("'ArithmetricFactor'がありません");
		} else if (token.checkSymbol(Symbol.NOT)) {
			nextTkn();
			if (isExpFirst(token))parseArithmeticFactor(); else syntaxError("'ArithmetricFactor'がありません");
		}
		if (dbg)System.out.print("parseArithmeticFactor()おわり\n");
	}
	/**
	 * UnsignedFactorの解析
	 */
	void parseUnsignedFactor() {
		if (dbg)System.out.print("parseUnsignedFactor()\n");
		if(token.checkSymbol(Symbol.NAME)){
			nextTkn();
			if(token.checkSymbol(Symbol.INC))nextTkn();
			else if(token.checkSymbol(Symbol.DEC))nextTkn();
			else if(token.checkSymbol(Symbol.LBRACKET)){
				nextTkn();
				if(isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
				if(token.checkSymbol(Symbol.RBRACKET))nextTkn(); else syntaxError("']'がありません");
			}
		}else if(token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC)){
			nextTkn();
			if(token.checkSymbol(Symbol.NAME))nextTkn();
			if(token.checkSymbol(Symbol.LBRACKET)){
				nextTkn();
				if(isExpFirst(token))parseExpression();
				if(token.checkSymbol(Symbol.RBRACKET))nextTkn(); else syntaxError("']'がありません");
			}
		}else if(token.checkSymbol(Symbol.INTEGER))nextTkn();
		else if(token.checkSymbol(Symbol.CHARACTER))nextTkn();
		else if(token.checkSymbol(Symbol.LPAREN)){
			nextTkn();
			if(isExpFirst(token))parseExpression(); 
			if(token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		}else if(token.checkSymbol(Symbol.INPUTCHAR))nextTkn();
		else if(token.checkSymbol(Symbol.INPUTINT))nextTkn();
		else if(isSumFuncFirst(token))parseSumFunc();
		else if(isProductFuncFirst(token))parseProductFunc();
		else syntaxError("'UnsgnedFactor'がありません");
		if (dbg)System.out.print("parseUnsignedFactor()おわり\n");
	}
	/**
	 * sumFunctionの解析
	 */
	void parseSumFunc() {
		if (dbg)System.out.print("parseSumFunction()\n");
		if(token.checkSymbol(Symbol.ADD))nextTkn(); else syntaxError("'+'がありません");
		if(token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if(isExpFirst(token))parseExpressionList(); else syntaxError("'ExpressionList'がありません");
		if(token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (dbg)System.out.print("parseSumFunctionおわり()\n");
	}
	/**
	 * ProductFunctionの解析
	 */
	void parseProductFunc() {
		if (dbg)System.out.print("parseProductFunction()\n");
		if(token.checkSymbol(Symbol.MUL))nextTkn();
		if(token.checkSymbol(Symbol.LPAREN))nextTkn(); else syntaxError("'('がありません");
		if(isExpFirst(token))parseExpressionList(); else syntaxError("'ExpressionList'がありません");
		if(token.checkSymbol(Symbol.RPAREN))nextTkn(); else syntaxError("')'がありません");
		if (dbg)System.out.print("parseProductFunctionおわり()\n");
	}
	/**
	 * ExpressionListの解析
	 */
	void parseExpressionList() {
		if (dbg)System.out.print("parseExpressionList()\n");
		if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		while (token.checkSymbol(Symbol.COMMA)) {
			nextTkn();
			if (isExpFirst(token))parseExpression(); else syntaxError("'Expression'がありません");
		}
		if (dbg)System.out.print("parseExpressionListおわり()\n");
	}
	/**
	 * MainのFirst集合をチェックする
	 * @param token
	 * @return
	 */
	boolean isMainFirst(Token token) {
		return token.checkSymbol(Symbol.MAIN);
	}
	/**
	 * Blockのfist集合をチェックする
	 * @param token
	 * @return
	 */
	boolean isBlockFirst(Token token) {
		return token.checkSymbol(Symbol.LBRACE);
	}
	/**
	 * declのfirst集合をチェックする
	 * @param token
	 * @return
	 */
	boolean isDeclFirst(Token token) {
		return token.checkSymbol(Symbol.INT);
	}

	/**
	 * NameListとNameのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isNameFirst(Token token) {
		return token.checkSymbol(Symbol.NAME);
	}

	/**
	 * ConstantListとConstantのfirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isConstantFirst(Token token) {
		return (token.checkSymbol(Symbol.SUB) || token.checkSymbol(Symbol.INTEGER) || token.checkSymbol(Symbol.CHARACTER));
	}

	/**
	 * StatementのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isStFirst(Token token) {
		return (token.checkSymbol(Symbol.IF) || token.checkSymbol(Symbol.WHILE) || token.checkSymbol(Symbol.FOR)
				|| token.checkSymbol(Symbol.SUB) || token.checkSymbol(Symbol.NOT) || token.checkSymbol(Symbol.NAME)
				|| token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC) || token.checkSymbol(Symbol.NAME)
				|| token.checkSymbol(Symbol.INTEGER) || token.checkSymbol(Symbol.CHARACTER)
				|| token.checkSymbol(Symbol.LPAREN) || token.checkSymbol(Symbol.ADD) || token.checkSymbol(Symbol.MUL)
				|| token.checkSymbol(Symbol.INPUTCHAR) || token.checkSymbol(Symbol.INPUTINT)
				|| token.checkSymbol(Symbol.OUTPUTCHAR) || token.checkSymbol(Symbol.OUTPUTINT)
				|| token.checkSymbol(Symbol.BREAK) || token.checkSymbol(Symbol.LBRACE)
				|| token.checkSymbol(Symbol.SEMICOLON));
	}

	/**
	 * IfのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isIfStFirst(Token token) {
		return token.checkSymbol(Symbol.IF);
	}

	/**
	 * whileのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isWhileStFirst(Token token) {
		return token.checkSymbol(Symbol.WHILE);
	}

	/**
	 * ForのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isForStFirst(Token token) {
		return token.checkSymbol(Symbol.FOR);
	}

	/**
	 * Exp_Statement, Expression, Exp, Logical_term, Logical_factor,
	 * Arithmetic_expression, Arithmetic_term, Arithmetic_factor,
	 * Expression_listのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isExpFirst(Token token) {
		//if (dbg)System.out.print("isExpFirst()\n");
		return (token.checkSymbol(Symbol.SUB) || token.checkSymbol(Symbol.NOT) || token.checkSymbol(Symbol.NAME)
				|| token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC) || token.checkSymbol(Symbol.INTEGER)
				|| token.checkSymbol(Symbol.CHARACTER) || token.checkSymbol(Symbol.LPAREN) || token.checkSymbol(Symbol.ADD)
				|| token.checkSymbol(Symbol.MUL) || token.checkSymbol(Symbol.INPUTCHAR) || token.checkSymbol(Symbol.INPUTINT));
	}
	boolean isUnsignedFactorFirst(Token token) {
		//if (dbg)System.out.print("isUnsignedFactorFirst()\n");
		return (token.checkSymbol(Symbol.NAME) || token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC)
				|| token.checkSymbol(Symbol.INTEGER) || token.checkSymbol(Symbol.CHARACTER) || token.checkSymbol(Symbol.LPAREN)
				|| token.checkSymbol(Symbol.ADD) || token.checkSymbol(Symbol.MUL) || token.checkSymbol(Symbol.INPUTCHAR)
				|| token.checkSymbol(Symbol.INPUTINT));
	}

	/**
	 * OutputcharのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isOutputcharStFirst(Token token) {
		return token.checkSymbol(Symbol.OUTPUTCHAR);
	}

	/**
	 * OutputintのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isOutputintStFirst(Token token) {
		return token.checkSymbol(Symbol.OUTPUTINT);
	}

	/**
	 * Break_StatementのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isBreakStFirst(Token token) {
		return token.checkSymbol(Symbol.BREAK);
	}

	/**
	 * SumFunctionのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isSumFuncFirst(Token token) {
		return token.checkSymbol(Symbol.ADD);
	}

	/**
	 * ProduceFunctionのFirst集合
	 *
	 * @param token
	 * @return
	 */
	boolean isProductFuncFirst(Token token) {
		return token.checkSymbol(Symbol.MUL);
	}

	/**
	 * token = lexer.nextToken()を実行する
	 *
	 * @return
	 */
	Token nextTkn() {
		return token = lexer.nextToken();
	}

	/**
	 * 現在読んでいるファイルを閉じる (lexerのcloseFile()に委譲)
	 */
	void closeFile() {
		lexer.closeFile();
	}

	/**
	 * アセンブラコードをファイルに出力する (isegのdump2file()に委譲)
	 */
	void dump2file() {
		//		iseg.dump2file();
	}

	/**
	 * アセンブラコードをファイルに出力する (isegのdump2file()に委譲)
	 *
	 * @param fileName 出力ファイル名
	 */
	void dump2file(String fileName) {
		//		iseg.dump2file(fileName);
	}

	/**
	 * エラーメッセージを出力しプログラムを終了するメソッド
	 *
	 * @param message 出力エラーメッセージ
	 */
	private void syntaxError(String message) {
		System.out.print(lexer.analyzeAt());
		// 下記の文言は自動採点で使用するので変更しないでください。
		System.out.println("で構文解析プログラムが構文エラーを検出");
		System.out.println(message);
		closeFile();
		System.exit(1);
	}

	/**
	 * 引数で指定したK20言語ファイルを解析する 読み込んだファイルが文法上正しければアセンブラコードを出力する
	 */
	public static void main(String[] args) {
		Kc parser; // 構文解析器

		if (args.length == 0) { // 実行時パラメータのチェック
			System.out.println("Usage: java kc.Kc20 file [objectfile]");
			System.exit(0);
		}

		parser = new Kc(args[0]);

		parser.parseProgram(); // 構文解析
		parser.closeFile(); // 入力ファイルのクローズ

		//		if (args.length == 1)
		//			parser.dump2file();
		//		else
		//			parser.dump2file(args[1]);
	}
}