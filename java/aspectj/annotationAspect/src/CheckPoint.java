
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPoint {
	String name();
}
