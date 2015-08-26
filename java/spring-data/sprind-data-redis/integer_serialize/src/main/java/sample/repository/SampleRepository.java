package sample.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.function.IntUnaryOperator;

@Service
public class SampleRepository {
	@Autowired
	private RedisTemplate<String, Integer> redisTemplate;

	public Integer get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void update(String key, IntUnaryOperator func) {
		BoundValueOperations<String, Integer> valueOps = redisTemplate.boundValueOps(key);

		valueOps.set(func.applyAsInt(valueOps.get()));
	}

	// enableTransactionSupport = false の場合はエラーが発生
	public Object updateWithCas1(String key, IntUnaryOperator func) {
		try {
			redisTemplate.watch(key);

			BoundValueOperations<String, Integer> valueOps = redisTemplate.boundValueOps(key);
			Integer value = valueOps.get();

			redisTemplate.multi();

			valueOps.set(func.applyAsInt(value));

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

				// 以下は不可。multi の後で get すると null が返る
				//Integer value = valueOps.get();

				valueOps.set(func.applyAsInt(value));

				return ops.exec();
			}
		});
	}
}
