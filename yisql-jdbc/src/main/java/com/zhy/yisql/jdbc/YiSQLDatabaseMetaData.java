package com.zhy.yisql.jdbc;

import java.sql.*;

import static com.zhy.yisql.jdbc.Config.METHOD_NOT_SUPPORTED_STRING;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-01
 *  \* Time: 15:11
 *  \* Description: 
 *  \
 */
public class YiSQLDatabaseMetaData implements DatabaseMetaData {
  private String userName;

  public YiSQLDatabaseMetaData(String userName) {
    this.userName = userName;
  }

  @Override
  public boolean allProceduresAreCallable() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean allTablesAreSelectable() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getURL() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getUserName() throws SQLException {
    return userName;
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean nullsAreSortedHigh() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean nullsAreSortedLow() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean nullsAreSortedAtStart() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean nullsAreSortedAtEnd() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getDatabaseProductName() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getDatabaseProductVersion() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getDriverName() throws SQLException {
    return Config.DRIVER_NAME;
  }

  @Override
  public String getDriverVersion() throws SQLException {
    return String.format("%d.%d", Config.MAJOR_VERSION, Config.MINOR_VERSION);
  }

  @Override
  public int getDriverMajorVersion() {
    return Config.MAJOR_VERSION;
  }

  @Override
  public int getDriverMinorVersion() {
    return Config.MINOR_VERSION;
  }

  @Override
  public boolean usesLocalFiles() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean usesLocalFilePerTable() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsMixedCaseIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean storesUpperCaseIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean storesLowerCaseIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean storesMixedCaseIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getIdentifierQuoteString() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getSQLKeywords() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getNumericFunctions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getStringFunctions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getSystemFunctions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getTimeDateFunctions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getSearchStringEscape() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getExtraNameCharacters() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsAlterTableWithAddColumn() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsAlterTableWithDropColumn() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsColumnAliasing() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean nullPlusNonNullIsNull() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsConvert() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsConvert(int fromType, int toType) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsTableCorrelationNames() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsDifferentTableCorrelationNames() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsExpressionsInOrderBy() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsOrderByUnrelated() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsGroupBy() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsGroupByUnrelated() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsGroupByBeyondSelect() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsLikeEscapeClause() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsMultipleResultSets() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsMultipleTransactions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsNonNullableColumns() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsMinimumSQLGrammar() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCoreSQLGrammar() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsExtendedSQLGrammar() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsANSI92EntryLevelSQL() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsANSI92IntermediateSQL() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsANSI92FullSQL() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsIntegrityEnhancementFacility() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsOuterJoins() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsFullOuterJoins() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsLimitedOuterJoins() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getSchemaTerm() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getProcedureTerm() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getCatalogTerm() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean isCatalogAtStart() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public String getCatalogSeparator() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSchemasInDataManipulation() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSchemasInProcedureCalls() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSchemasInTableDefinitions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSchemasInIndexDefinitions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCatalogsInDataManipulation() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCatalogsInProcedureCalls() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCatalogsInTableDefinitions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsPositionedDelete() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsPositionedUpdate() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSelectForUpdate() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsStoredProcedures() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSubqueriesInComparisons() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSubqueriesInExists() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSubqueriesInIns() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSubqueriesInQuantifieds() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsCorrelatedSubqueries() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsUnion() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsUnionAll() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public int getMaxBinaryLiteralLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxCharLiteralLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxColumnNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxColumnsInGroupBy() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxColumnsInIndex() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxColumnsInOrderBy() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxColumnsInSelect() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxColumnsInTable() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxConnections() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxCursorNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxIndexLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxSchemaNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxProcedureNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxCatalogNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxRowSize() throws SQLException {
    return 0;
  }

  @Override
  public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public int getMaxStatementLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxStatements() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxTableNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxTablesInSelect() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxUserNameLength() throws SQLException {
    return 0;
  }

  @Override
  public int getDefaultTransactionIsolation() throws SQLException {
    return 0;
  }

  @Override
  public boolean supportsTransactions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getSchemas() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getCatalogs() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getTableTypes() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getTypeInfo() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsResultSetType(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean ownUpdatesAreVisible(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean ownDeletesAreVisible(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean ownInsertsAreVisible(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean othersUpdatesAreVisible(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean othersDeletesAreVisible(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean othersInsertsAreVisible(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean updatesAreDetected(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean deletesAreDetected(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean insertsAreDetected(int type) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsBatchUpdates() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public Connection getConnection() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsSavepoints() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsNamedParameters() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsMultipleOpenResults() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsGetGeneratedKeys() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsResultSetHoldability(int holdability) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return 0;
  }

  @Override
  public int getDatabaseMajorVersion() throws SQLException {
    return 0;
  }

  @Override
  public int getDatabaseMinorVersion() throws SQLException {
    return 0;
  }

  @Override
  public int getJDBCMajorVersion() throws SQLException {
    return 0;
  }

  @Override
  public int getJDBCMinorVersion() throws SQLException {
    return 0;
  }

  @Override
  public int getSQLStateType() throws SQLException {
    return 0;
  }

  @Override
  public boolean locatorsUpdateCopy() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsStatementPooling() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public RowIdLifetime getRowIdLifetime() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getClientInfoProperties() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean generatedKeyAlwaysReturned() throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new SQLException(METHOD_NOT_SUPPORTED_STRING);
  }
}
