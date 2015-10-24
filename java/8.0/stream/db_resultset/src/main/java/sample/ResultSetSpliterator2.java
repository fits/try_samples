package sample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetSpliterator2<T> extends Spliterators.AbstractSpliterator<T> {

	private ResultSet resultSet;
	private TryFunction<ResultSet, T, SQLException> converter;

	public ResultSetSpliterator2(ResultSet resultSet, TryFunction<ResultSet, T, SQLException> converter) {
		super(Long.MAX_VALUE, ORDERED);

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

	public Stream<T> stream() {
		return StreamSupport.stream(this, false);
	}
}
