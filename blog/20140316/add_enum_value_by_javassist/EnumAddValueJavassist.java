
import javassist.*;

public class EnumAddValueJavassist {
	public static void main(String... args) throws Exception {
		ClassPool pool = ClassPool.getDefault();

		CtClass cc = pool.get("EType");

		// 静的初期化子（static イニシャライザ）へ $VALUES を書き換える処理を追加
		cc.getClassInitializer().insertAfter("$VALUES = new EType[] { First, new EType(\"Second\", 1) };");

		cc.toClass();

		System.out.println(EType.valueOf("Second"));

		System.out.println("-----");

		for (EType type : EType.values()) {
			System.out.println(type);
		}
	}
}
