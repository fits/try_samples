dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
//    username = "root"
//    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
    show_sql = true
    dialect = 'org.hibernate.dialect.MySQLDialect'
}
// environment specific settings
environments {
    development {
        dataSource {
     //       dbCreate = "update"
            url = "jdbc:mysql://localhost/information_schema?user=root"
        }
    }
    test {
        dataSource {
     //       dbCreate = "update"
            url = "jdbc:mysql://localhost/information_schema?user=root"
        }
    }
    production {
        dataSource {
     //       dbCreate = "update"
            url = "jdbc:mysql://localhost/information_schema?user=root"
        }
    }
}
