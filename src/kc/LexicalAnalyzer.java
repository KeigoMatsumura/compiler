package kc;

/**
 * 字句解析クラス
 * 空白とタブと改行文字を読んだときは読み飛ばし，
 * トークンを切り出すK20言語に対する字句解析プログラムのクラス
 * @author 18-1-037-0051 松村圭悟
 * 問題番号: 問題3.3
 * 提出日: 2020年6月24日
 *
 */
class LexicalAnalyzer {
	private FileScanner sourceFileScanner; // ソースファイルに対するスキャナ
	/**
	 * ファイル名を引数とするコンストラクタ
	 */
	LexicalAnalyzer(String sourceFileName) {
		sourceFileScanner = new FileScanner(sourceFileName);
	}

	/**
	 * 次のトークンを切り出す．ファイル末に達している場合はEOFを返す．
	 * 入力がマイクロ構文に違反したためトークンの切り出しに失敗した場合，
	 * syntaxErrorメソッドを呼び出す．
	 */
	Token nextToken() {
		char currentChar; //読み込んだ文字を保存する変数
		Token token = null; //返すシンボルのための変数

		/**
		 *  空白とタブと改行文字を読んだときの動作
		 */
		do {
			currentChar = sourceFileScanner.nextChar();
		} while (currentChar ==' ' || currentChar == '\t' || currentChar == '\n');

		//切り出した値を保存する変数
		int tmp = 0;
		String str = "";

		/**
		 * 文字列の読み込みを続ける処理
		 */
		if(Character.isLowerCase (currentChar) || Character.isUpperCase (currentChar) || currentChar == '_'){
			str += currentChar;
			/**
			 * 二桁目以降の繰り返し処理
			 */
			while(Character.isLowerCase(sourceFileScanner.lookAhead()) || Character.isUpperCase(sourceFileScanner.lookAhead())
					|| Character.isDigit(sourceFileScanner.lookAhead())
					|| sourceFileScanner.lookAhead() == '_'){
				currentChar = sourceFileScanner.nextChar();
				str += currentChar;
			}

			/**
			 * 読み込む文字列が登録された文字か判別する
			 */
			//MAINと切り出す
			if(str.equals("main")){
				token = new Token(Symbol.MAIN);
				//IFと切り出す
			}else if(str.equals("if")){
				token = new Token(Symbol.IF);
				//WHILEと切り出す
			}else if(str.equals("while")){
				token = new Token(Symbol.WHILE);
				//FORと切り出す
			}else if(str.equals("for")){
				token = new Token(Symbol.FOR);
				//INPUTINTと切り出す
			}else if(str.equals("inputint")){
				token = new Token(Symbol.INPUTINT);
				//INPUTCHARと切り出す
			}else if(str.equals("inputchar")){
				token = new Token(Symbol.INPUTCHAR);
				//OUTPUTINTと切り出す
			}else if(str.equals("outputint")){
				token = new Token(Symbol.OUTPUTINT);
				//OUTPUTCHARと切り出す
			}else if(str.equals("outputchar")){
				token = new Token(Symbol.OUTPUTCHAR);
				//OUTPUTSTRと切り出す
			}else if(str.equals("outputstr")){
				token = new Token(Symbol.OUTPUTSTR);
				//SETSTRと切り出す
			}else if(str.equals("setstr")){
				token = new Token(Symbol.SETSTR);
				//ELSEと切り出す
			}else if(str.equals("else")){
				token = new Token(Symbol.ELSE);
				//doと切り出す
			}else if(str.equals("do")){
				token = new Token(Symbol.DO);
				//switchと切り出す
			}else if(str.equals("switch")){
				token = new Token(Symbol.SWITCH);
				//caseと切り出す
			}else if(str.equals("case")){
				token = new Token(Symbol.CASE);
				//continueと切り出す
			}else if(str.equals("continue")){
				token = new Token(Symbol.CONTINUE);
				//INTと切り出す
			}else if(str.equals("int")){
				token = new Token(Symbol.INT);
				//charと切り出す
			}else if(str.equals("char")){
				token = new Token(Symbol.CHAR);
				//booleanと切り出す
			}else if(str.equals("boolean")){
				token = new Token(Symbol.BOOLEAN);
				//trueと切り出す
			}else if(str.equals("true")){
				token = new Token(Symbol.TRUE);
				//falseと切り出す
			}else if(str.equals("false")){
				token = new Token(Symbol.FALSE);
				//読み込む文字がSymbolに登録された文字でない場合
			} else {
				token = new Token(Symbol.NAME, str);
			}
			/**
			 * 0 から始まる文字を切り出すとき
			 */
		}else if(currentChar == '0'){
			// 16進数を読み込むとき: 0xは16進数を表す
			if(sourceFileScanner.lookAhead() == 'x'){
				sourceFileScanner.nextChar();
					tmp = Character.digit(sourceFileScanner.nextChar(), 16);
					while (Character.digit(sourceFileScanner.lookAhead(), 16) != -1) {
						tmp = tmp * 16 + Character.digit(sourceFileScanner.nextChar(), 16);
					}
					token = new Token(Symbol.INTEGER, tmp);
				}
				else {
					token = new Token(Symbol.INTEGER, 0);
				}		
			/**
			 * 0以外を切り出すとき
			 */
		} else if(Character.isDigit(currentChar)) {
			tmp = Character.digit(currentChar, 10);
			//二桁目以降の繰り返し処理
			while(Character.isDigit(sourceFileScanner.lookAhead())) {
				currentChar = sourceFileScanner.nextChar();
				tmp *= 10; //10進数は一桁繰り上げるために10倍する
				tmp += Character.digit(currentChar, 10);
			}
			token = new Token(Symbol.INTEGER, tmp);

			/**
			 * + から始まる文字を切り出すとき
			 */
		} else if(currentChar == '+') {
			//++を切り出す
			if(sourceFileScanner.lookAhead() == '+'){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.INC);
			}else if(sourceFileScanner.lookAhead() == '='){
				//+=を切り出す
				sourceFileScanner.nextChar();
				token = new Token(Symbol.ASSIGNADD);
			} else //+を切り出す
				token = new Token(Symbol.ADD);
			/**
			 * ! から始まる文字を切り出すとき
			 */
		} else if(currentChar == '!') {
			//!を切り出す
			if(sourceFileScanner.lookAhead() != '=') {
				token = new Token(Symbol.NOT);
			} else {
				//!=を切り出す
				currentChar = sourceFileScanner.nextChar();
				token = new Token(Symbol.NOTEQ);
			}
			/**
			 * = から始まる文字を切り出すとき
			 */
		} else if(currentChar == '=') {
			//=と切り出す
			if(sourceFileScanner.lookAhead() != '=') {
				token = new Token(Symbol.ASSIGN);
			} else {
				//==を切り出す
				currentChar = sourceFileScanner.nextChar();
				token = new Token(Symbol.EQUAL);
			}
			/**
			 * ＜から始まる文字を切り出すとき
			 */
		} else if(currentChar == '<'){
			//<と切り出す
			if(sourceFileScanner.lookAhead() != '=') {
				token = new Token(Symbol.LESS);
			} else {
				//<=を切り出す
				currentChar = sourceFileScanner.nextChar();
				token = new Token(Symbol.LESSEQ);
			}
			/**
			 * ＞から始まる文字を切り出すとき
			 */
		} else if(currentChar == '>'){
			//>と切り出す
			if(sourceFileScanner.lookAhead() != '=') {
				token = new Token(Symbol.GREAT);
			} else {
				//>=を切り出す
				currentChar = sourceFileScanner.nextChar();
				token = new Token(Symbol.GREATEQ);
			}
		} else if(currentChar == '-'){
			//-=と切り出す
			if(sourceFileScanner.lookAhead() == '='){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.ASSIGNSUB);
				//--と切り出す
			}else if(sourceFileScanner.lookAhead() == '-'){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.DEC);
			}else{
				//-と切り出す
				token = new Token(Symbol.SUB);
			}
		} else if(currentChar == '*'){
			// *=と切り出す
			if(sourceFileScanner.lookAhead() == '='){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.ASSIGNMUL);
			}else {
				// *と切り出す
				token = new Token(Symbol.MUL);
			}
		} else if(currentChar == '/'){
			///=と切り出す
			if(sourceFileScanner.lookAhead() == '='){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.ASSIGNDIV);
			}else {
				///と切り出す
				token = new Token(Symbol.DIV);
			}
		} else if(currentChar == '%'){
			//%=と切り出す
			if(sourceFileScanner.lookAhead() == '='){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.ASSIGNMOD);
			}else {
				//%と切り出す
				token = new Token(Symbol.MOD);
			}
		} else if(currentChar == '|'){
			//||と切り出す
			if(sourceFileScanner.lookAhead() == '|'){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.OR);
			}else {
				syntaxError();
			}
		} else if(currentChar == '&'){
			//&&と切り出す
			if(sourceFileScanner.lookAhead() == '&'){
				sourceFileScanner.nextChar();
				token = new Token(Symbol.AND);
			}else {
				syntaxError();
			}
			//;と切り出す
		} else if(currentChar == ';'){
			token = new Token(Symbol.SEMICOLON);
			//(と切り出す
		} else if(currentChar == '('){
			token = new Token(Symbol.LPAREN);
			//)と切り出す
		} else if(currentChar == ')'){
			token = new Token(Symbol.RPAREN);
			//{と切り出す
		} else if(currentChar == '{'){
			token = new Token(Symbol.LBRACE);
			//}と切り出す
		} else if(currentChar == '}'){
			token = new Token(Symbol.RBRACE);
			//[と切り出す
		} else if(currentChar == '['){
			token = new Token(Symbol.LBRACKET);
			//]と切り出す
		} else if(currentChar == ']'){
			token = new Token(Symbol.RBRACKET);
			//,と切り出す
		} else if(currentChar == ','){
			token = new Token(Symbol.COMMA);
			//''で括っている文字を読み込むとき
		} else if(currentChar == '\'') {
			currentChar = sourceFileScanner.nextChar();
			tmp = (int) currentChar; //
			currentChar = sourceFileScanner.nextChar();
			if(currentChar == '\'') {
				token = new Token(Symbol.CHARACTER, tmp);
			} else {
				syntaxError();
			}
		}

		/**
		 * ファイル末に到達したとき
		 */
		else if(currentChar == '\0') {
			token = new Token(Symbol.EOF);
		} else {
			syntaxError();
		}
		return token;
	}


	/**
	 * 読んでいるファイルを閉じる
	 */
	void closeFile() {
		sourceFileScanner.closeFile();
	}

	/**
	 * 現在，入力ファイルのどの部分を解析中であるのかを表現する文字列を返す．
	 */
	String analyzeAt() {
		return sourceFileScanner.scanAt();
	}

	/**
	 * 字句解析時に構文エラーを検出した時に呼ばれるメソッド
	 */
	private void syntaxError() {
		System.out.print (sourceFileScanner.scanAt());
		//下記の文言は自動採点で使用するので変更しないでください。
		System.out.println ("で字句解析プログラムが構文エラーを検出");
		closeFile();
		System.exit(1);
	}
}