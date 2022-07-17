package kc;

import java.util.ArrayList;

/**
 * 変数名とDseg上の対応を管理するプログラム
 * @author 18-1-037-0051 松村圭悟
 * 問題番号: 問題2.11
 * 提出日: 2020年6月3日
 *
 */
class VarTable {
	private ArrayList<Var> varList;
	private int nextAddress; //次に登録されるアドレス

	/**
	 * ArrayList<Var>を一つ作り，varListで参照する．nextAddressを0に初期化する
	 */
	VarTable() {
		varList = new ArrayList<>();
		nextAddress = 0;
	}

	/**
	 * varList中から，引数で与えられた名前nameを持つ変数(Varクラスのインスタンス)を探し，
	 * その参照を戻り値として返す．そのような変数が存在しない場合null値を返す．VarTableクラス内部からのみ呼び出される
	 */
	private Var getVar(String name) {
		for(Var var: varList) {
			if(name.equals(var.getName()))
				return var;
		}
		return null;
	}

	/**
	 * 引数で与えられた名前nameを持つ変数が既に存在するかどうかを調べ，戻り値として返す．
	 */
	boolean exist(String name) {
		boolean flag = false;
		if(getVar(name) != null)flag = true;
		return flag;
	}

	/**
	 * 引数で与えられた型，名前，サイズを持つ変数を登録する．
	 * 登録できたら戻り値trueを返す．既にvarList中に同じ名前の変数が存在する場合は登録せず，戻り値falseを返す
	 */
	boolean registerNewVariable(Type type, String name, int size) {
		if(exist(name)) {
			return false;
		} else {
			varList.add(new Var(type, name, nextAddress, size));
			return true;
		}
	}

	/**
	 * 名前nameを持つ変数に与えられているDsegのアドレスを求めて，戻り値として返す
	 */
	int getAddress(String name) {
		Var var = getVar(name);
		if(var != null)
			return var.getAddress();
		else
			return -1;
	}

	/**
	 * 名前nameを持つ変数の型を戻り値として返す
	 */
	Type getType(String name) {
		Var var = getVar(name);
		if(var != null)
			return var.getType();
		else
			return Type.NULL;
	}

	/**
	 * 第1引数nameで与えられた変数の型が第2引数typeと一致するかを確認する
	 */
	boolean checkType(String name, Type type) {
		boolean flag = false;
		Var var = getVar(name);
		if(type.equals(var.getType()))
			flag = true;
		return flag;
	}

	/**
	 * 名前nameを持つ変数のサイズを返す
	 */
	int getSize(String name) {
		Var var = getVar(name);
		if(var != null)
			return var.getSize();
		else
			return -1;
	}

	/**
	 * 動作確認用のメインメソッド
	 * int型変数およびint型配列を表に登録し、その後登録された変数を表示する
	 */
	public static void main(String[] args) {
		VarTable varTable = new VarTable();
		for(int i=0; i < 4; i++) {
			varTable.registerNewVariable(Type.INT, "var" + i, 1);
		}
		varTable.registerNewVariable(Type.ARRAYOFINT, "var4", 10);

		for(int i=0; i < 5; i++) {
			String name = "var" + i;
			if(varTable.checkType(name, Type.INT)) {
				System.out.printf("変数: %s, タイプ: %s, 番地: %s, サイズ: %s \n"
						, name, varTable.getType(name), varTable.getAddress(name), varTable.getSize(name));
			} else if(varTable.checkType(name, Type.ARRAYOFINT)) {
				System.out.printf("変数: %s, タイプ: %s, 番地: %s, サイズ: %s \n"
						, name, varTable.getType(name), varTable.getAddress(name), varTable.getSize(name));
			}
		}
	}
}