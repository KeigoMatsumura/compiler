package kc;

/**
 * 切り出したトークンを格納するクラス
 * 名前，文字列を扱うことを想定している
 * @author 18-1-037-0051 松村圭悟
 * 問題番号: 問題3.1
 * 提出日: 2020年6月10日
 *
 */
class Token {
	private Symbol symbol; //そのトークの種別を表す
	private int intValue; //トークンの種類が NAEM,STRINGである時，整数値または文字コードを保持する
	private String strValue; //トークンの種類が NAEM,STRINGである時，，文字コードを保持する

	/**
	 * 整数，文字，名前以外のトークンを生成するための
	 * トークンの種別のみを引数とするコンストラクタ
	 */
	Token(Symbol symbol) {
		this.symbol = symbol;
		this.intValue = -1;
		this.strValue = "";
	}

	/**
	 * 整数，文字のトークンを生成するための
	 * トークンの種別と値を引数とするコンストラクタ
	 */
	Token(Symbol symbol, int intValue) {
		this.symbol = symbol;
		this.intValue = intValue;
		this.strValue = "";
	}


	/**
	 * 名前，文字列のトークンを生成するための
	 * トークンの種別と文字列を引数とするコンストラクタ
	 */
	Token(Symbol symbol, String strValue) {
		this.symbol = symbol;
		this.intValue = -1;
		this.strValue = strValue;
	}


	/**1
	 * symbolフィールドが引数symbolTypeとトークン種別と一致するかどうか調べる
	 */
	boolean checkSymbol(Symbol symbolType) {
		boolean flag = false;
		if(symbol.equals(symbolType))
			flag = true;
		else
			flag = false;
		return flag;
	}

	/**
	 * symbolフィールドのゲッター
	 */
	Symbol getSymbol() {
		return symbol;
	}


	/**
	 * intValueフィールドのゲッター
	 */
	int getIntValue() {
		return intValue;
	}

	/**
	 * strValueフィールドのゲッター
	 */
	String getStrValue() {
		return strValue;
	}
}