package sample.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QTickets is a Querydsl query type for QTickets
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QTickets extends com.mysema.query.sql.RelationalPathBase<QTickets> {

    private static final long serialVersionUID = 634163007;

    public static final QTickets tickets = new QTickets("tickets");

    public final StringPath ticketCode = createString("ticketCode");

    public final NumberPath<Integer> totalLimit = createNumber("totalLimit", Integer.class);

    public final NumberPath<Integer> usersLimit = createNumber("usersLimit", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QTickets> primary = createPrimaryKey(ticketCode);

    public QTickets(String variable) {
        super(QTickets.class, forVariable(variable), "null", "tickets");
        addMetadata();
    }

    public QTickets(String variable, String schema, String table) {
        super(QTickets.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTickets(Path<? extends QTickets> path) {
        super(path.getType(), path.getMetadata(), "null", "tickets");
        addMetadata();
    }

    public QTickets(PathMetadata<?> metadata) {
        super(QTickets.class, metadata, "null", "tickets");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(ticketCode, ColumnMetadata.named("ticket_code").withIndex(1).ofType(Types.VARCHAR).withSize(10).notNull());
        addMetadata(totalLimit, ColumnMetadata.named("total_limit").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(usersLimit, ColumnMetadata.named("users_limit").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

