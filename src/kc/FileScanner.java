package kc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
*
* @author 18-1-037-0051 松村圭悟
*問題番号 問題2.5-2.7
*提出日 2020年5月26日
*再提出日 2020年6月24日
*/
class FileScanner {
	private BufferedReader sourceFile; //入力ファイルの参照
	private String line; //行バッファ
	private int lineNumber; //行カウンタ
	private int columnNumber; //列カウンタ
	private char currentCharacter; //読み取り文字
	private char nextCharacter; //先読み文字

	/**
	 * 引数 sourceFileName で指定されたファイルを開き, sourceFile で参照する．
	 * 教科書 p. 210 ソースコード 10.1 ではtry-with-resources 文を用いて
	 * ファイルの 参照と読み取りを一度に行っているが，このコンストラクタでは
	 * ファイルの参照 だけを行う．
	 * また lineNumber, columnNumber, currentCharacter, nextCharacter を初期化する
	 *
	 * @param sourceFileName ソースプログラムのファイル名
	 */
	FileScanner(String sourceFileName) {
		Path path = Paths.get(sourceFileName);
		// ファイルのオープン
		try {
			sourceFile = Files.newBufferedReader(path);
		} catch (IOException err_mes) {
			System.out.println(err_mes);
			System.exit(1);
		}

		// 各フィールドの初期化
		lineNumber = 0;
		columnNumber = -1;
		nextCharacter = '\n';

		//nextChar() を実行し，nextCharacter に sourceFile の1文字目を読み込む.
		//		nextCharacter = sourceFile.nextChar();
	}

	/**
	 * sourceFileで参照しているファイルを閉じる
	 */
	void closeFile() {
		try {
			sourceFile.close();
		} catch (IOException err_mes) {
			System.out.println(err_mes);
			System.exit(1);
		}
	}

	/**
	 * sourceFile で参照しているファイルから一行読み, フィールド line(文字列変数) に
	 * その行を格納する 教科書 p. 210 ソースコード10.1 では while文で全行を読み取って
	 * いるが，このメソッド内では while文は使わず1行だけ読み取りフィールドline に格納する．
	 */
	void readNextLine() {
		try {
			if (sourceFile.ready()) { // sourceFile中に未読の行があるかを確認 (例外:IllegalStateException)
				/*
				 * readLineメソッドでsourceFileから1行読み出し
				 * 読み出された文字列は改行コードを含まないので
				 * 改めて改行コードをつけ直す
				 */
				line = sourceFile.readLine() + "\n";
			} else {
				line = null;
			}

		} catch (IOException err_mes) { // 例外は Exception でキャッチしてもいい
			// ファイルの読み出しエラーが発生したときの処理
			System.out.println(err_mes);
			closeFile();
			System.exit(1);
		}
	}

	/**
	 *nextCharacter中の文字列を返すメソッド
	 * @return nextCharacter
	 */
	char lookAhead() {
		return nextCharacter;
	}

	/**
	 *lineフィールドの文字列を返すメソッド
	 * @return line
	 */
	String getLine() {
		return line;
	}

	/**
	 * 一文字切り出し用のメソッド
	 * currentCharacter に nextCharacter を代入し，その currentCharacter を返り値とする
	 * @return currentCharacter
	 */
	char nextChar() {
		//共通動作
		currentCharacter = nextCharacter;
		//ファイル末に到達　何もしない
		if(nextCharacter == '\0') {

		} else if(nextCharacter == '\n') {
			//行末に到達
			readNextLine();
			if(line != null) {
				//次の行がnullでない
				nextCharacter = line.charAt(0);
				lineNumber++;
				columnNumber = 0;
			} else {
				//次の行がnullである
				nextCharacter = '\0';
			}
			//行の途中の場合
		} else {
			nextCharacter = line.charAt(++columnNumber);
		}
		//共通動作
		return currentCharacter;
	}

	/**
	 * 現在入力ファイルのどの部分をスキャンしているのかを表現する文字列を返す．
	 */
    String scanAt() {
        String message = lineNumber + "行目\n" + line;
        for (int i = 0; i < columnNumber - 1; ++i)
            message += " ";
        message += "*\n";
        return message;
    }

	/**
	 * メインメソッド ファイルスキャンを実行する
	 * @param args
	 */
	public static void main(String args[]) {
		FileScanner filescanner = new FileScanner("bsort.k");
		//問題2.5　スキャンした文字を表示する
//				do {
//					filescanner.readNextLine();
//					if(filescanner.getLine() == null){
//						break;
//					}
//					System.out.print(filescanner.getLine());
//				} while(true);

		//問題2.6　スキャンした文字を表示する
		char temp;
		while((temp = filescanner.nextChar()) != '\0') {
			System.out.print(temp);
		}

		//問題2.7　現在スキャンしている行と文字を表示する出力
		System.out.print("\n" + filescanner.scanAt());

		filescanner.closeFile();
	}
}
