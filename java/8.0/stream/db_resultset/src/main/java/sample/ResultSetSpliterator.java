package sample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ResultSetSpliterator<T> implements Spliterator<T> {

	private ResultSet resultSet;
	private TryFunction<ResultSet, T, SQLException> converter;

	public ResultSetSpliterator(ResultSet resultSet, TryFunction<ResultSet, T, SQLException> converter) {
		this.resultSet = resultSet;
		this.converter = converter;
	}

	@Override
	public boolean tryAdvance(Consumer<? super T> consumer) {
		try {
			if (resultSet.next()) {
				consumer.accept(converter.apply(resultSet));
				return true;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	@Override
	public Spliterator<T> trySplit() {
		return null;
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return ORDERED;
	}
}
