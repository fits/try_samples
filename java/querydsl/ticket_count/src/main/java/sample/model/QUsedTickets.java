package sample.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUsedTickets is a Querydsl query type for QUsedTickets
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUsedTickets extends com.mysema.query.sql.RelationalPathBase<QUsedTickets> {

    private static final long serialVersionUID = 483004610;

    public static final QUsedTickets usedTickets = new QUsedTickets("used_tickets");

    public final StringPath ticketCode = createString("ticketCode");

    public final DateTimePath<java.sql.Timestamp> usedDate = createDateTime("usedDate", java.sql.Timestamp.class);

    public final StringPath userId = createString("userId");

    public QUsedTickets(String variable) {
        super(QUsedTickets.class, forVariable(variable), "null", "used_tickets");
        addMetadata();
    }

    public QUsedTickets(String variable, String schema, String table) {
        super(QUsedTickets.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUsedTickets(Path<? extends QUsedTickets> path) {
        super(path.getType(), path.getMetadata(), "null", "used_tickets");
        addMetadata();
    }

    public QUsedTickets(PathMetadata<?> metadata) {
        super(QUsedTickets.class, metadata, "null", "used_tickets");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(ticketCode, ColumnMetadata.named("ticket_code").withIndex(1).ofType(Types.VARCHAR).withSize(10).notNull());
        addMetadata(usedDate, ColumnMetadata.named("used_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(2).ofType(Types.VARCHAR).withSize(10).notNull());
    }

}

