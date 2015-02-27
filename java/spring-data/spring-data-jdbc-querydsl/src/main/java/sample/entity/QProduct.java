package sample.entity;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QProduct is a Querydsl query type for QProduct
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QProduct extends com.mysema.query.sql.RelationalPathBase<QProduct> {

    private static final long serialVersionUID = -1794960187;

    public static final QProduct product = new QProduct("product");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final DateTimePath<java.sql.Timestamp> releaseDate = createDateTime("releaseDate", java.sql.Timestamp.class);

    public final com.mysema.query.sql.PrimaryKey<QProduct> primary = createPrimaryKey(id);

    public QProduct(String variable) {
        super(QProduct.class, forVariable(variable), "null", "product");
        addMetadata();
    }

    public QProduct(String variable, String schema, String table) {
        super(QProduct.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QProduct(Path<? extends QProduct> path) {
        super(path.getType(), path.getMetadata(), "null", "product");
        addMetadata();
    }

    public QProduct(PathMetadata<?> metadata) {
        super(QProduct.class, metadata, "null", "product");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(30).notNull());
        addMetadata(price, ColumnMetadata.named("price").withIndex(3).ofType(Types.DECIMAL).withSize(10).notNull());
        addMetadata(releaseDate, ColumnMetadata.named("release_date").withIndex(4).ofType(Types.TIMESTAMP).withSize(19).notNull());
    }

}

