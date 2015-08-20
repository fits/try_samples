package sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SampleService {
	private static final String SQL_SELECT = "select id, rkey from sample " +
			"where registered_date < now() - interval ? second";

	private static final String SQL_DELETE = "delete from sample where id = ?";

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_RKEY = "rkey";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${sample.expired.second}")
	private int expiredSecond;

	@Transactional
	public List<String> deleteExpired() {
		return findExpired()
				.filter(this::isNotExists)
				.map(this::delete)
				.collect(Collectors.toList());
	}

	private String delete(Map<String, Object> item) {
		String id = item.get(COLUMN_ID).toString();

		jdbcTemplate.update(SQL_DELETE, id);

		return id;
	}

	private boolean isNotExists(Map<String, Object> item) {
		return !redisTemplate.hasKey(item.get(COLUMN_RKEY).toString());
	}

	private Stream<Map<String, Object>> findExpired() {
		return jdbcTemplate.queryForList(SQL_SELECT, expiredSecond).stream();
	}
}
