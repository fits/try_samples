package sample;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.query.NumberSubQuery;
import sample.model.QTickets;
import sample.model.QUsedTickets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SampleApp {
    public static void main(String... args) throws SQLException {
		String dbUrl = "jdbc:mysql://localhost/ticket?user=root";
		String userId = "user1";

        Configuration conf = new Configuration(new MySQLTemplates());

        try (Connection con = DriverManager.getConnection(dbUrl)) {
			QTickets qt = QTickets.tickets;
			QUsedTickets qu = QUsedTickets.usedTickets;

			BooleanExpression usedWhere = qu.ticketCode.eq(qt.ticketCode);

			NumberSubQuery<Long> totalCount = new SQLSubQuery().from(qu).where(usedWhere).count();

			NumberSubQuery<Long> usersCount = new SQLSubQuery().from(qu).where(
					usedWhere.and(qu.userId.eq(userId))).count();

			SQLQuery query = new SQLQuery(con, conf)
					.from(qt)
					.where(
						qt.totalLimit.lt(1).or(qt.totalLimit.gt(totalCount))
						.and(
							qt.usersLimit.lt(1).or(qt.usersLimit.gt(usersCount))
						)
					);

			System.out.println(query);

			System.out.println("------");

			query.list(qt.ticketCode).forEach(System.out::println);
		}
    }
}
