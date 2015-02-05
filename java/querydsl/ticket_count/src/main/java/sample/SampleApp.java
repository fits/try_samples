package sample;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.types.Projections;
import sample.model.QTickets;
import sample.model.QUsedTickets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SampleApp {
    public static void main(String... args) throws SQLException {
		String dbUrl = "jdbc:mysql://localhost/ticket?user=root";

        Configuration conf = new Configuration(new MySQLTemplates());

        try (Connection con = DriverManager.getConnection(dbUrl)) {
			QTickets qt = QTickets.tickets;
			QUsedTickets qu = QUsedTickets.usedTickets;

			SQLQuery query = new SQLQuery(con, conf)
					.from(qt)
					.where(
						qt.totalLimit.lt(1).or(
							qt.totalLimit.gt(
								new SQLSubQuery().from(qu).where(
									qu.ticketCode.eq(qt.ticketCode)
								).count()
							)
						).and(
							qt.usersLimit.lt(1).or(
								qt.usersLimit.gt(
									new SQLSubQuery().from(qu).where(
										qu.ticketCode.eq(qt.ticketCode).and(qu.userId.eq("user1"))
									).count()
								)
							)
						)
					);

			System.out.println(query);

			System.out.println("------");

			query.list(qt.ticketCode).forEach(System.out::println);
		}
    }
}
