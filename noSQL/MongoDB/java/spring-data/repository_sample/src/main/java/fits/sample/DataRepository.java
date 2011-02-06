package fits.sample;

import java.util.List;
import org.springframework.data.document.mongodb.repository.MongoRepository;

public interface DataRepository extends MongoRepository<Data, java.math.BigInteger> {
	//指定名の Data を取得する
	List<Data> findByName(String name);

	//指定の名前を含み、ポイントが指定値より大きい Data を取得する
	List<Data> findByNameLikeAndPointGreaterThan(String name, int point);

}
