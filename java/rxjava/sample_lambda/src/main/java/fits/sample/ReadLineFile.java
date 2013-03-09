package fits.sample;

import static java.nio.charset.StandardCharsets.*;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

public class ReadLineFile {
	public static void main(String... args) {

		fromFile(args[0]).skip(1).take(2).map( (s) -> "#" + s ).subscribe( (s) -> System.out.println(s) );
	}

	private static Observable<String> fromFile(final String file) {
		return Observable.create( (observer) -> {
			try (BufferedReader reader = Files.newBufferedReader(Paths.get(file), UTF_8)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					observer.onNext(line);
				}
			} catch (Exception ex) {
				observer.onError(ex);
			}
			observer.onCompleted();

			return Observable.noOpSubscription();
		});
	}
}
