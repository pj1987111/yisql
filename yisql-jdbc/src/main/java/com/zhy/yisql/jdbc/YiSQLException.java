package com.zhy.yisql.jdbc;

import java.sql.SQLException;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-27
 *  \* Time: 11:17
 *  \* Description: 
 *  \
 */
public class YiSQLException extends SQLException {

  private static final long serialVersionUID = -5071922123822027267L;

  public YiSQLException(String reason) {
    super(reason);
  }
}
