package sample.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.util.function.IntUnaryOperator;

@Repository
public class SampleRepository {
	@Autowired
	private RedisTemplate<String, Integer> redisTemplate;

	public Integer load(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void save(String key, int value) {
		redisTemplate.boundValueOps(key).set(value);

		// 以下でも可
	//	redisTemplate.opsForValue().set(key, value);
	}

	public Object updateWithCas1(String key, IntUnaryOperator func) {
		try {
			redisTemplate.watch(key);

			BoundValueOperations<String, Integer> valueOps = redisTemplate.boundValueOps(key);

			Integer value = valueOps.get();

			redisTemplate.multi();

			valueOps.set(func.applyAsInt(value));

			// enableTransactionSupport = false の場合はエラーが発生
			return redisTemplate.exec();
		} catch (Exception e) {
			// InvalidDataAccessApiUsageException: ERR EXEC without MULTI
			System.out.println(e);
		}

		return null;
	}

	public Object updateWithCas2(String key, IntUnaryOperator func) {
		return redisTemplate.execute(new SessionCallback<Object>() {
			@Override
			public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
				@SuppressWarnings("unchecked")
				RedisOperations<String, Integer> ops = (RedisOperations<String, Integer>)operations;

				ops.watch(key);

				BoundValueOperations<String, Integer> valueOps = ops.boundValueOps(key);
				Integer value = valueOps.get();

				ops.multi();

				valueOps.set(func.applyAsInt(value));

				return ops.exec();
			}
		});
	}

	public Object updateWithCas3(String key, IntUnaryOperator func) {
		RedisConnectionUtils.bindConnection(redisTemplate.getConnectionFactory());
		try {
			redisTemplate.watch(key);

			BoundValueOperations<String, Integer> valueOps = redisTemplate.boundValueOps(key);
			Integer value = valueOps.get();

			redisTemplate.multi();

			valueOps.set(func.applyAsInt(value));

			return redisTemplate.exec();

		} finally {
			RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
		}
	}
}
