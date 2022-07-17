package kc;

/**
 * 字句解析クラス
 * 空白とタブと改行文字を読んだときは読み飛ばし，最長一致の原則に基づいてトークンを切り出す．
 * @author 18-1-037-0051 松村圭悟
 * 問題番号: 問題3.2
 * 提出日: 2020年6月10日
 *
 */
class SLexicalAnalyzer {
	private FileScanner sourceFileScanner; // 入力ファイルのFileScannerへの参照

	/**
	 * ファイル名を引数とするコンストラクタ
	 */
	SLexicalAnalyzer(String sourceFileName) {
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

		// 空白とタブと改行文字を読んだときの動作
		do {
			currentChar = sourceFileScanner.nextChar();
		} while (currentChar ==' ' || currentChar == '\t' || currentChar == '\n');

		//切り出した値を保存する
		int value = 0;

		/**
		 * 0 を切り出すとき
		 */
		if(currentChar == '0'){
			token = new Token(Symbol.INTEGER, 0);
			/**
			 * 0以外を切り出すとき
			 */
		} else if(Character.isDigit(currentChar)) {
			value = Character.digit(currentChar, 10);

			/**
			 * 二桁目以降の繰り返し処理
			 */
			while(Character.isDigit(sourceFileScanner.lookAhead())) {
				currentChar = sourceFileScanner.nextChar();
				value *= 10;
				value += Character.digit(currentChar, 10);
			}
			token = new Token(Symbol.INTEGER, value);

			/**
			 * + を切り出すとき
			 */
		} else if(currentChar == '+') {
			token = new Token(Symbol.ADD);
			/**
			 * ! を切り出すとき
			 */
		} else if(currentChar == '!') {
			//　次の文字が = でない時に限って ! と切り出す
			if(sourceFileScanner.lookAhead() != '=') {
				token = new Token(Symbol.NOT);
			} else {
				// 次の文字が = なので != と切り出す
				currentChar = sourceFileScanner.nextChar();
				token = new Token(Symbol.NOTEQ);
			}
			/**
			 * = を切り出すとき
			 */
		} else if(currentChar == '=') {
			//　次の文字が = でない時に限って = と切り出す
			if(sourceFileScanner.lookAhead() != '=') {
				token = new Token(Symbol.ASSIGN);
			} else {
				// 次の文字が = なので == と切り出す
				currentChar = sourceFileScanner.nextChar();
				token = new Token(Symbol.EQUAL);
			}
			/**
			 * \0を切り出すとき
			 */
		} else if(currentChar == '\0') {
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
	 * 字句解析時に構文エラーを検出した時に呼ばれるメソッド
	 */
	void syntaxError() {
		System.out.print (sourceFileScanner.scanAt());
		//下記の文言は自動採点で使用するので変更しないでください。
		System.out.println ("で字句解析プログラムが構文エラーを検出");
		closeFile();
		System.exit(1);
	}
}