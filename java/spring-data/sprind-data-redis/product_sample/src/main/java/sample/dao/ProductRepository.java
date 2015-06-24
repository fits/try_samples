package sample.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import sample.model.Product;

import java.util.concurrent.TimeUnit;

@Repository
public class ProductRepository {
    @Autowired
    private RedisTemplate<String, Product> redisTemplate;

    public Product load(String id) {
        return redisTemplate.opsForValue().get(id);
    }

    public void save(Product data) {
        redisTemplate.opsForValue().set(data.getId(), data, 1, TimeUnit.MINUTES);
    }
}
