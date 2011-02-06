package fits.sample;

import java.math.BigInteger;
import java.util.List;
import org.springframework.data.document.mongodb.repository.MongoRepository;

public interface DataRepository extends MongoRepository<Data, BigInteger> {

	List<Data> findByName(String name);

	List<Data> findByNameLikeAndPointGreaterThan(String name, int point);

}
