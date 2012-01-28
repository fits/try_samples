package fits.sample;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

//メソッド検証対象用アノテーション
@Target(ElementType.METHOD)
public @interface ValidMethod {
}
