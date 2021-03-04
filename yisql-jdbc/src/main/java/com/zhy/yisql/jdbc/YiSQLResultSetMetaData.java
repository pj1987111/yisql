package com.zhy.yisql.jdbc;

import net.sf.json.JSONArray;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-28
 *  \* Time: 11:42
 *  \* Description: 
 *  \
 */
public class YiSQLResultSetMetaData implements ResultSetMetaData {
  private JSONArray schema;

  public YiSQLResultSetMetaData(JSONArray schema) {
    this.schema = schema;
  }

  @Override
  public int getColumnCount() throws SQLException {
    return schema.size();
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isSearchable(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isCurrency(int column) throws SQLException {
    return false;
  }

  @Override
  public int isNullable(int column) throws SQLException {
    if (schema.getJSONObject(column-1).getBoolean("nullable")) {
      return ResultSetMetaData.columnNullable;
    } else {
      return ResultSetMetaData.columnNoNulls;
    }
  }

  @Override
  public boolean isSigned(int column) throws SQLException {
    return false;
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    return schema.getJSONObject(column-1).getString("name");
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    return schema.getJSONObject(column-1).getString("name");
  }

  @Override
  public String getSchemaName(int column) throws SQLException {
    return "default";
  }

  @Override
  public int getPrecision(int column) throws SQLException {
    return 0;
  }

  @Override
  public int getScale(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getTableName(int column) throws SQLException {
    return "default";
  }

  @Override
  public String getCatalogName(int column) throws SQLException {
    return "default";
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    String colType = schema.getJSONObject(column-1).getString("type");
    int res;
    switch (colType.toLowerCase()) {
      case "integer":
        res = Types.INTEGER;
        break;
      case "double":
        res = Types.DOUBLE;
        break;
      case "long":
//        res = Types.BIGINT;
        res = Types.INTEGER;
        break;
      case "float":
        res = Types.FLOAT;
        break;
      case "short":
        res = Types.SMALLINT;
        break;
      case "byte":
        res = Types.TINYINT;
        break;
      case "boolean":
        res = Types.BIT;
        break;
      case "string":
        res = Types.CLOB;
        break;
      case "binary":
        res = Types.BLOB;
        break;
      case "timestamp":
        res = Types.TIMESTAMP;
        break;
      case "date":
        res = Types.DATE;
        break;
      case "decimal":
        res = Types.DECIMAL;
        break;
      default:
        res = Types.VARCHAR;
    }
    return res;
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    return schema.getJSONObject(column-1).getString("type");
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException {
    return true;
  }

  @Override
  public boolean isWritable(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    return false;
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    String colType = schema.getJSONObject(column-1).getString("type");
    String res;
    switch (colType.toLowerCase()) {
      case "integer":
        res = "java.lang.Integer";
        break;
      case "double":
        res = "java.lang.Double";
        break;
      case "long":
//        res = "java.lang.Long";
        res = "java.lang.Integer";
        break;
      case "float":
        res = "java.lang.Float";
        break;
      case "short":
        res = "java.lang.Short";
        break;
      case "byte":
        res = "java.lang.Byte";
        break;
      case "boolean":
        res = "java.lang.Boolean";
        break;
      case "string":
        res = "java.lang.String";
        break;
      case "binary":
        res = "java.lang.Array[java.lang.Byte]";
        break;
      case "timestamp":
        res = "java.sql.Timestamp";
        break;
      case "date":
        res = "java.util.Date";
        break;
      case "decimal":
        res = "java.math.BigDecimal";
        break;
      default:
        res = "java.lang.String";
    }
    return res;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
