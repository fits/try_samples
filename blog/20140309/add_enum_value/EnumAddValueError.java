
import java.lang.reflect.Constructor;

public class EnumAddValueError {
	public static void main(String... args) throws Exception {
		// EType のコンストラクタ（private）取得
		Constructor<EType> cls = EType.class.getDeclaredConstructor(String.class, int.class);
		cls.setAccessible(true);

		// 列挙型は newInstance できないのでエラー
		EType t2 = cls.newInstance("Second", 1);
	}

	enum EType {
		First
	}
}
