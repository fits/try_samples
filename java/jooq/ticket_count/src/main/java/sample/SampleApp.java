package sample;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static sample.model.ticket.Tables.*;

public class SampleApp {
    public static void main(String... args) throws SQLException {
		String dbUrl = "jdbc:mysql://localhost/ticket?user=root";
		String userId = "user1";

        try (Connection con = DriverManager.getConnection(dbUrl)) {
			DSLContext ctx = DSL.using(con, SQLDialect.MYSQL);

			Condition usedWhere = USED_TICKETS.TICKET_CODE.eq(TICKETS.TICKET_CODE);

			Select<Record1<Integer>> totalCount = ctx.selectCount().from(USED_TICKETS)
					.where(usedWhere);

			Select<Record1<Integer>> usersCount = ctx.selectCount().from(USED_TICKETS)
					.where(usedWhere.and(USED_TICKETS.USER_ID.eq(userId)));

			Select<Record1<String>> query = ctx.select(TICKETS.TICKET_CODE).from(TICKETS).where(
					TICKETS.TOTAL_LIMIT.lt(1).or(TICKETS.TOTAL_LIMIT.gt(totalCount))
							.and(TICKETS.USERS_LIMIT.lt(1).or(TICKETS.USERS_LIMIT.gt(usersCount)))
			);

			System.out.println(query.getSQL());

			System.out.println("------");

			query.forEach(r -> System.out.println(r.getValue(0)));
		}
    }
}
