package kc;

/**
 * VarTableで使用する変数のためのクラス
 * @author 18-1-037-0051 松村圭悟
 * 問題番号: 問題2.10
 * 提出日: 2020年6月3日
 *
 */
class Var {
	private Type type; // Typeはenum型のクラス
	private String name; //変数名
	private int address; //Dseg上のアドレス
	private int size; //配列の場合，そのサイズ

	/**
	 *　各フィールドを引数で与えられたもので初期化
	 */
	Var(Type type, String name, int address, int size) {
		this.type = type;
		this.name = name;
		this.address = address;
		this.size = size;
	}

	/**
	 * typeのゲッター
	 */
	Type getType() {
		return type;
	}

	/**
	 * nameのゲッター
	 */
	String getName() {
		return name;
	}

	/**
	 * addressのゲッター
	 */
	int getAddress() {
		return address;
	}

	/**
	 * sizeのゲッター
	 */
	int getSize() {
		return size;
	}
}