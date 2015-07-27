package sample.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import sample.model.Product;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

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

    public void update(String id, UnaryOperator<Product> proc) {

        Object res = redisTemplate.execute(new SessionProc(id, proc));

        if (res == null) {
            throw new RuntimeException("failed redis transaction");
        }
    }


    private class SessionProc implements SessionCallback<Object> {
        private String id;
        private UnaryOperator<Product> proc;

        SessionProc(String id, UnaryOperator<Product> proc) {
            this.id = id;
            this.proc = proc;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> Object execute(RedisOperations<K, V> op) throws DataAccessException {
            return executeOps((RedisOperations<String, Product>)op);
        }

        private Object executeOps(RedisOperations<String, Product> op) throws DataAccessException {
            op.watch(id);

            Product value = op.opsForValue().get(id);

            op.multi();

            value = proc.apply(value);

            op.opsForValue().set(id, value);

            return op.exec();
        }
    }
}
