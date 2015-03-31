@Grab('org.flywaydb:flyway-core:3.2.1')
import org.flywaydb.core.internal.resolver.MigrationInfoHelper

def printPair = { println "left: ${it.left}, right: ${it.right}"}

// left: 1.1, right: test a b
printPair MigrationInfoHelper.extractVersionAndDescription('V1_1__test_a_b.sql', 'V', '__', '.sql')


