// Generated from /Volumes/workspace/zhy/workspace/yisql/yisql-dsl/src/main/resources/DSLSQL.g4 by ANTLR 4.9.1

package com.zhy.yisql.dsl.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DSLSQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, AS=11, INTO=12, LOAD=13, SAVE=14, SELECT=15, INSERT=16, CREATE=17, 
		DROP=18, REFRESH=19, SET=20, CONNECT=21, TRAIN=22, RUN=23, PREDICT=24, 
		REGISTER=25, UNREGISTER=26, INCLUDE=27, OPTIONS=28, WHERE=29, PARTITIONBY=30, 
		OVERWRITE=31, APPEND=32, ERRORIfExists=33, IGNORE=34, COMPLETE=35, UPDATE=36, 
		STRING=37, BLOCK_STRING=38, IDENTIFIER=39, BACKQUOTED_IDENTIFIER=40, EXECUTE_COMMAND=41, 
		EXECUTE_TOKEN=42, SIMPLE_COMMENT=43, BRACKETED_EMPTY_COMMENT=44, BRACKETED_COMMENT=45, 
		WS=46, UNRECOGNIZED=47;
	public static final int
		RULE_statement = 0, RULE_sql = 1, RULE_as = 2, RULE_into = 3, RULE_saveMode = 4, 
		RULE_where = 5, RULE_whereExpressions = 6, RULE_overwrite = 7, RULE_append = 8, 
		RULE_errorIfExists = 9, RULE_ignore = 10, RULE_complete = 11, RULE_update = 12, 
		RULE_booleanExpression = 13, RULE_expression = 14, RULE_ender = 15, RULE_format = 16, 
		RULE_path = 17, RULE_commandValue = 18, RULE_rawCommandValue = 19, RULE_setValue = 20, 
		RULE_setKey = 21, RULE_db = 22, RULE_asTableName = 23, RULE_tableName = 24, 
		RULE_functionName = 25, RULE_colGroup = 26, RULE_col = 27, RULE_qualifiedName = 28, 
		RULE_identifier = 29, RULE_strictIdentifier = 30, RULE_quotedIdentifier = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"statement", "sql", "as", "into", "saveMode", "where", "whereExpressions", 
			"overwrite", "append", "errorIfExists", "ignore", "complete", "update", 
			"booleanExpression", "expression", "ender", "format", "path", "commandValue", 
			"rawCommandValue", "setValue", "setKey", "db", "asTableName", "tableName", 
			"functionName", "colGroup", "col", "qualifiedName", "identifier", "strictIdentifier", 
			"quotedIdentifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", "';'", "'='", "'and'", "'-'", "'/'", "'>'", "'<'", "'~'", 
			"','", "'as'", "'into'", "'load'", "'save'", "'select'", "'insert'", 
			"'create'", "'drop'", "'refresh'", "'set'", "'connect'", "'train'", "'run'", 
			"'predict'", "'register'", "'unregister'", "'include'", "'options'", 
			"'where'", null, "'overwrite'", "'append'", "'errorIfExists'", "'ignore'", 
			"'complete'", "'update'", null, null, null, null, null, "'!'", null, 
			"'/**/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "AS", 
			"INTO", "LOAD", "SAVE", "SELECT", "INSERT", "CREATE", "DROP", "REFRESH", 
			"SET", "CONNECT", "TRAIN", "RUN", "PREDICT", "REGISTER", "UNREGISTER", 
			"INCLUDE", "OPTIONS", "WHERE", "PARTITIONBY", "OVERWRITE", "APPEND", 
			"ERRORIfExists", "IGNORE", "COMPLETE", "UPDATE", "STRING", "BLOCK_STRING", 
			"IDENTIFIER", "BACKQUOTED_IDENTIFIER", "EXECUTE_COMMAND", "EXECUTE_TOKEN", 
			"SIMPLE_COMMENT", "BRACKETED_EMPTY_COMMENT", "BRACKETED_COMMENT", "WS", 
			"UNRECOGNIZED"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "DSLSQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DSLSQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class StatementContext extends ParserRuleContext {
		public List<SqlContext> sql() {
			return getRuleContexts(SqlContext.class);
		}
		public SqlContext sql(int i) {
			return getRuleContext(SqlContext.class,i);
		}
		public List<EnderContext> ender() {
			return getRuleContexts(EnderContext.class);
		}
		public EnderContext ender(int i) {
			return getRuleContext(EnderContext.class,i);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LOAD) | (1L << SAVE) | (1L << SELECT) | (1L << INSERT) | (1L << CREATE) | (1L << DROP) | (1L << REFRESH) | (1L << SET) | (1L << CONNECT) | (1L << TRAIN) | (1L << RUN) | (1L << PREDICT) | (1L << REGISTER) | (1L << UNREGISTER) | (1L << INCLUDE) | (1L << EXECUTE_COMMAND) | (1L << SIMPLE_COMMENT))) != 0)) {
				{
				{
				setState(64);
				sql();
				setState(65);
				ender();
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SqlContext extends ParserRuleContext {
		public TerminalNode LOAD() { return getToken(DSLSQLParser.LOAD, 0); }
		public FormatContext format() {
			return getRuleContext(FormatContext.class,0);
		}
		public PathContext path() {
			return getRuleContext(PathContext.class,0);
		}
		public AsContext as() {
			return getRuleContext(AsContext.class,0);
		}
		public TableNameContext tableName() {
			return getRuleContext(TableNameContext.class,0);
		}
		public WhereContext where() {
			return getRuleContext(WhereContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<BooleanExpressionContext> booleanExpression() {
			return getRuleContexts(BooleanExpressionContext.class);
		}
		public BooleanExpressionContext booleanExpression(int i) {
			return getRuleContext(BooleanExpressionContext.class,i);
		}
		public TerminalNode SAVE() { return getToken(DSLSQLParser.SAVE, 0); }
		public List<OverwriteContext> overwrite() {
			return getRuleContexts(OverwriteContext.class);
		}
		public OverwriteContext overwrite(int i) {
			return getRuleContext(OverwriteContext.class,i);
		}
		public List<AppendContext> append() {
			return getRuleContexts(AppendContext.class);
		}
		public AppendContext append(int i) {
			return getRuleContext(AppendContext.class,i);
		}
		public List<ErrorIfExistsContext> errorIfExists() {
			return getRuleContexts(ErrorIfExistsContext.class);
		}
		public ErrorIfExistsContext errorIfExists(int i) {
			return getRuleContext(ErrorIfExistsContext.class,i);
		}
		public List<IgnoreContext> ignore() {
			return getRuleContexts(IgnoreContext.class);
		}
		public IgnoreContext ignore(int i) {
			return getRuleContext(IgnoreContext.class,i);
		}
		public List<CompleteContext> complete() {
			return getRuleContexts(CompleteContext.class);
		}
		public CompleteContext complete(int i) {
			return getRuleContext(CompleteContext.class,i);
		}
		public List<UpdateContext> update() {
			return getRuleContexts(UpdateContext.class);
		}
		public UpdateContext update(int i) {
			return getRuleContext(UpdateContext.class,i);
		}
		public TerminalNode PARTITIONBY() { return getToken(DSLSQLParser.PARTITIONBY, 0); }
		public ColContext col() {
			return getRuleContext(ColContext.class,0);
		}
		public List<ColGroupContext> colGroup() {
			return getRuleContexts(ColGroupContext.class);
		}
		public ColGroupContext colGroup(int i) {
			return getRuleContext(ColGroupContext.class,i);
		}
		public TerminalNode SELECT() { return getToken(DSLSQLParser.SELECT, 0); }
		public TerminalNode INSERT() { return getToken(DSLSQLParser.INSERT, 0); }
		public TerminalNode CREATE() { return getToken(DSLSQLParser.CREATE, 0); }
		public TerminalNode DROP() { return getToken(DSLSQLParser.DROP, 0); }
		public TerminalNode REFRESH() { return getToken(DSLSQLParser.REFRESH, 0); }
		public TerminalNode SET() { return getToken(DSLSQLParser.SET, 0); }
		public SetKeyContext setKey() {
			return getRuleContext(SetKeyContext.class,0);
		}
		public SetValueContext setValue() {
			return getRuleContext(SetValueContext.class,0);
		}
		public TerminalNode CONNECT() { return getToken(DSLSQLParser.CONNECT, 0); }
		public DbContext db() {
			return getRuleContext(DbContext.class,0);
		}
		public TerminalNode TRAIN() { return getToken(DSLSQLParser.TRAIN, 0); }
		public TerminalNode RUN() { return getToken(DSLSQLParser.RUN, 0); }
		public TerminalNode PREDICT() { return getToken(DSLSQLParser.PREDICT, 0); }
		public IntoContext into() {
			return getRuleContext(IntoContext.class,0);
		}
		public List<AsTableNameContext> asTableName() {
			return getRuleContexts(AsTableNameContext.class);
		}
		public AsTableNameContext asTableName(int i) {
			return getRuleContext(AsTableNameContext.class,i);
		}
		public TerminalNode REGISTER() { return getToken(DSLSQLParser.REGISTER, 0); }
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public TerminalNode UNREGISTER() { return getToken(DSLSQLParser.UNREGISTER, 0); }
		public TerminalNode INCLUDE() { return getToken(DSLSQLParser.INCLUDE, 0); }
		public TerminalNode EXECUTE_COMMAND() { return getToken(DSLSQLParser.EXECUTE_COMMAND, 0); }
		public List<CommandValueContext> commandValue() {
			return getRuleContexts(CommandValueContext.class);
		}
		public CommandValueContext commandValue(int i) {
			return getRuleContext(CommandValueContext.class,i);
		}
		public List<RawCommandValueContext> rawCommandValue() {
			return getRuleContexts(RawCommandValueContext.class);
		}
		public RawCommandValueContext rawCommandValue(int i) {
			return getRuleContext(RawCommandValueContext.class,i);
		}
		public TerminalNode SIMPLE_COMMENT() { return getToken(DSLSQLParser.SIMPLE_COMMENT, 0); }
		public SqlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSql(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSql(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSql(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SqlContext sql() throws RecognitionException {
		SqlContext _localctx = new SqlContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_sql);
		int _la;
		try {
			int _alt;
			setState(291);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOAD:
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				match(LOAD);
				setState(73);
				format();
				setState(74);
				match(T__0);
				setState(75);
				path();
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(76);
					where();
					}
				}

				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(79);
					expression();
					}
				}

				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(82);
					booleanExpression();
					}
					}
					setState(87);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(88);
				as();
				setState(89);
				tableName();
				}
				break;
			case SAVE:
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				match(SAVE);
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OVERWRITE) | (1L << APPEND) | (1L << ERRORIfExists) | (1L << IGNORE) | (1L << COMPLETE) | (1L << UPDATE))) != 0)) {
					{
					setState(98);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case OVERWRITE:
						{
						setState(92);
						overwrite();
						}
						break;
					case APPEND:
						{
						setState(93);
						append();
						}
						break;
					case ERRORIfExists:
						{
						setState(94);
						errorIfExists();
						}
						break;
					case IGNORE:
						{
						setState(95);
						ignore();
						}
						break;
					case COMPLETE:
						{
						setState(96);
						complete();
						}
						break;
					case UPDATE:
						{
						setState(97);
						update();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					setState(102);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(103);
				tableName();
				setState(104);
				as();
				setState(105);
				format();
				setState(106);
				match(T__0);
				setState(107);
				path();
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(108);
					where();
					}
				}

				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(111);
					expression();
					}
				}

				setState(117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(114);
					booleanExpression();
					}
					}
					setState(119);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PARTITIONBY) {
					{
					setState(120);
					match(PARTITIONBY);
					setState(122);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
						{
						setState(121);
						col();
						}
					}

					setState(127);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__9) {
						{
						{
						setState(124);
						colGroup();
						}
						}
						setState(129);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 3);
				{
				setState(132);
				match(SELECT);
				setState(136);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(133);
						_la = _input.LA(1);
						if ( _la <= 0 || (_la==T__1) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						}
					}
					setState(138);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				}
				setState(139);
				as();
				setState(140);
				tableName();
				}
				break;
			case INSERT:
				enterOuterAlt(_localctx, 4);
				{
				setState(142);
				match(INSERT);
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << AS) | (1L << INTO) | (1L << LOAD) | (1L << SAVE) | (1L << SELECT) | (1L << INSERT) | (1L << CREATE) | (1L << DROP) | (1L << REFRESH) | (1L << SET) | (1L << CONNECT) | (1L << TRAIN) | (1L << RUN) | (1L << PREDICT) | (1L << REGISTER) | (1L << UNREGISTER) | (1L << INCLUDE) | (1L << OPTIONS) | (1L << WHERE) | (1L << PARTITIONBY) | (1L << OVERWRITE) | (1L << APPEND) | (1L << ERRORIfExists) | (1L << IGNORE) | (1L << COMPLETE) | (1L << UPDATE) | (1L << STRING) | (1L << BLOCK_STRING) | (1L << IDENTIFIER) | (1L << BACKQUOTED_IDENTIFIER) | (1L << EXECUTE_COMMAND) | (1L << EXECUTE_TOKEN) | (1L << SIMPLE_COMMENT) | (1L << BRACKETED_EMPTY_COMMENT) | (1L << BRACKETED_COMMENT) | (1L << WS) | (1L << UNRECOGNIZED))) != 0)) {
					{
					{
					setState(143);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==T__1) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					setState(148);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case CREATE:
				enterOuterAlt(_localctx, 5);
				{
				setState(149);
				match(CREATE);
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << AS) | (1L << INTO) | (1L << LOAD) | (1L << SAVE) | (1L << SELECT) | (1L << INSERT) | (1L << CREATE) | (1L << DROP) | (1L << REFRESH) | (1L << SET) | (1L << CONNECT) | (1L << TRAIN) | (1L << RUN) | (1L << PREDICT) | (1L << REGISTER) | (1L << UNREGISTER) | (1L << INCLUDE) | (1L << OPTIONS) | (1L << WHERE) | (1L << PARTITIONBY) | (1L << OVERWRITE) | (1L << APPEND) | (1L << ERRORIfExists) | (1L << IGNORE) | (1L << COMPLETE) | (1L << UPDATE) | (1L << STRING) | (1L << BLOCK_STRING) | (1L << IDENTIFIER) | (1L << BACKQUOTED_IDENTIFIER) | (1L << EXECUTE_COMMAND) | (1L << EXECUTE_TOKEN) | (1L << SIMPLE_COMMENT) | (1L << BRACKETED_EMPTY_COMMENT) | (1L << BRACKETED_COMMENT) | (1L << WS) | (1L << UNRECOGNIZED))) != 0)) {
					{
					{
					setState(150);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==T__1) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					setState(155);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case DROP:
				enterOuterAlt(_localctx, 6);
				{
				setState(156);
				match(DROP);
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << AS) | (1L << INTO) | (1L << LOAD) | (1L << SAVE) | (1L << SELECT) | (1L << INSERT) | (1L << CREATE) | (1L << DROP) | (1L << REFRESH) | (1L << SET) | (1L << CONNECT) | (1L << TRAIN) | (1L << RUN) | (1L << PREDICT) | (1L << REGISTER) | (1L << UNREGISTER) | (1L << INCLUDE) | (1L << OPTIONS) | (1L << WHERE) | (1L << PARTITIONBY) | (1L << OVERWRITE) | (1L << APPEND) | (1L << ERRORIfExists) | (1L << IGNORE) | (1L << COMPLETE) | (1L << UPDATE) | (1L << STRING) | (1L << BLOCK_STRING) | (1L << IDENTIFIER) | (1L << BACKQUOTED_IDENTIFIER) | (1L << EXECUTE_COMMAND) | (1L << EXECUTE_TOKEN) | (1L << SIMPLE_COMMENT) | (1L << BRACKETED_EMPTY_COMMENT) | (1L << BRACKETED_COMMENT) | (1L << WS) | (1L << UNRECOGNIZED))) != 0)) {
					{
					{
					setState(157);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==T__1) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					setState(162);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case REFRESH:
				enterOuterAlt(_localctx, 7);
				{
				setState(163);
				match(REFRESH);
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << AS) | (1L << INTO) | (1L << LOAD) | (1L << SAVE) | (1L << SELECT) | (1L << INSERT) | (1L << CREATE) | (1L << DROP) | (1L << REFRESH) | (1L << SET) | (1L << CONNECT) | (1L << TRAIN) | (1L << RUN) | (1L << PREDICT) | (1L << REGISTER) | (1L << UNREGISTER) | (1L << INCLUDE) | (1L << OPTIONS) | (1L << WHERE) | (1L << PARTITIONBY) | (1L << OVERWRITE) | (1L << APPEND) | (1L << ERRORIfExists) | (1L << IGNORE) | (1L << COMPLETE) | (1L << UPDATE) | (1L << STRING) | (1L << BLOCK_STRING) | (1L << IDENTIFIER) | (1L << BACKQUOTED_IDENTIFIER) | (1L << EXECUTE_COMMAND) | (1L << EXECUTE_TOKEN) | (1L << SIMPLE_COMMENT) | (1L << BRACKETED_EMPTY_COMMENT) | (1L << BRACKETED_COMMENT) | (1L << WS) | (1L << UNRECOGNIZED))) != 0)) {
					{
					{
					setState(164);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==T__1) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					setState(169);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case SET:
				enterOuterAlt(_localctx, 8);
				{
				setState(170);
				match(SET);
				setState(171);
				setKey();
				setState(172);
				match(T__2);
				setState(173);
				setValue();
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(174);
					where();
					}
				}

				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(177);
					expression();
					}
				}

				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(180);
					booleanExpression();
					}
					}
					setState(185);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case CONNECT:
				enterOuterAlt(_localctx, 9);
				{
				setState(186);
				match(CONNECT);
				setState(187);
				format();
				setState(189);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(188);
					where();
					}
				}

				setState(192);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(191);
					expression();
					}
				}

				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(194);
					booleanExpression();
					}
					}
					setState(199);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(203);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(200);
					as();
					setState(201);
					db();
					}
				}

				}
				break;
			case TRAIN:
			case RUN:
			case PREDICT:
				enterOuterAlt(_localctx, 10);
				{
				setState(205);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRAIN) | (1L << RUN) | (1L << PREDICT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(206);
				tableName();
				setState(209);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case AS:
					{
					setState(207);
					as();
					}
					break;
				case INTO:
					{
					setState(208);
					into();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(211);
				format();
				setState(212);
				match(T__0);
				setState(213);
				path();
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(214);
					where();
					}
				}

				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(217);
					expression();
					}
				}

				setState(223);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(220);
					booleanExpression();
					}
					}
					setState(225);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AS) {
					{
					{
					setState(226);
					asTableName();
					}
					}
					setState(231);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case REGISTER:
				enterOuterAlt(_localctx, 11);
				{
				setState(232);
				match(REGISTER);
				setState(233);
				format();
				setState(234);
				match(T__0);
				setState(235);
				path();
				setState(236);
				as();
				setState(237);
				functionName();
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(238);
					where();
					}
				}

				setState(242);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(241);
					expression();
					}
				}

				setState(247);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(244);
					booleanExpression();
					}
					}
					setState(249);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case UNREGISTER:
				enterOuterAlt(_localctx, 12);
				{
				setState(250);
				match(UNREGISTER);
				setState(251);
				format();
				setState(252);
				match(T__0);
				setState(253);
				path();
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(254);
					where();
					}
				}

				setState(258);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(257);
					expression();
					}
				}

				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(260);
					booleanExpression();
					}
					}
					setState(265);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case INCLUDE:
				enterOuterAlt(_localctx, 13);
				{
				setState(266);
				match(INCLUDE);
				setState(267);
				format();
				setState(268);
				match(T__0);
				setState(269);
				path();
				setState(271);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPTIONS || _la==WHERE) {
					{
					setState(270);
					where();
					}
				}

				setState(274);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
					{
					setState(273);
					expression();
					}
				}

				setState(279);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(276);
					booleanExpression();
					}
					}
					setState(281);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case EXECUTE_COMMAND:
				enterOuterAlt(_localctx, 14);
				{
				setState(282);
				match(EXECUTE_COMMAND);
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << STRING) | (1L << BLOCK_STRING) | (1L << IDENTIFIER) | (1L << BACKQUOTED_IDENTIFIER))) != 0)) {
					{
					setState(285);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
					case 1:
						{
						setState(283);
						commandValue();
						}
						break;
					case 2:
						{
						setState(284);
						rawCommandValue();
						}
						break;
					}
					}
					setState(289);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case SIMPLE_COMMENT:
				enterOuterAlt(_localctx, 15);
				{
				setState(290);
				match(SIMPLE_COMMENT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AsContext extends ParserRuleContext {
		public TerminalNode AS() { return getToken(DSLSQLParser.AS, 0); }
		public AsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_as; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterAs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitAs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitAs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AsContext as() throws RecognitionException {
		AsContext _localctx = new AsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_as);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(AS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntoContext extends ParserRuleContext {
		public TerminalNode INTO() { return getToken(DSLSQLParser.INTO, 0); }
		public IntoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_into; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterInto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitInto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitInto(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntoContext into() throws RecognitionException {
		IntoContext _localctx = new IntoContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_into);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			match(INTO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SaveModeContext extends ParserRuleContext {
		public TerminalNode OVERWRITE() { return getToken(DSLSQLParser.OVERWRITE, 0); }
		public TerminalNode APPEND() { return getToken(DSLSQLParser.APPEND, 0); }
		public TerminalNode ERRORIfExists() { return getToken(DSLSQLParser.ERRORIfExists, 0); }
		public TerminalNode IGNORE() { return getToken(DSLSQLParser.IGNORE, 0); }
		public TerminalNode COMPLETE() { return getToken(DSLSQLParser.COMPLETE, 0); }
		public TerminalNode UPDATE() { return getToken(DSLSQLParser.UPDATE, 0); }
		public SaveModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_saveMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSaveMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSaveMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSaveMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SaveModeContext saveMode() throws RecognitionException {
		SaveModeContext _localctx = new SaveModeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_saveMode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OVERWRITE) | (1L << APPEND) | (1L << ERRORIfExists) | (1L << IGNORE) | (1L << COMPLETE) | (1L << UPDATE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereContext extends ParserRuleContext {
		public TerminalNode OPTIONS() { return getToken(DSLSQLParser.OPTIONS, 0); }
		public TerminalNode WHERE() { return getToken(DSLSQLParser.WHERE, 0); }
		public WhereContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterWhere(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitWhere(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitWhere(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereContext where() throws RecognitionException {
		WhereContext _localctx = new WhereContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_where);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			_la = _input.LA(1);
			if ( !(_la==OPTIONS || _la==WHERE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereExpressionsContext extends ParserRuleContext {
		public WhereContext where() {
			return getRuleContext(WhereContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<BooleanExpressionContext> booleanExpression() {
			return getRuleContexts(BooleanExpressionContext.class);
		}
		public BooleanExpressionContext booleanExpression(int i) {
			return getRuleContext(BooleanExpressionContext.class,i);
		}
		public WhereExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereExpressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterWhereExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitWhereExpressions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitWhereExpressions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereExpressionsContext whereExpressions() throws RecognitionException {
		WhereExpressionsContext _localctx = new WhereExpressionsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_whereExpressions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			where();
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER || _la==BACKQUOTED_IDENTIFIER) {
				{
				setState(302);
				expression();
				}
			}

			setState(308);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(305);
				booleanExpression();
				}
				}
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OverwriteContext extends ParserRuleContext {
		public TerminalNode OVERWRITE() { return getToken(DSLSQLParser.OVERWRITE, 0); }
		public OverwriteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_overwrite; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterOverwrite(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitOverwrite(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitOverwrite(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OverwriteContext overwrite() throws RecognitionException {
		OverwriteContext _localctx = new OverwriteContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_overwrite);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			match(OVERWRITE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AppendContext extends ParserRuleContext {
		public TerminalNode APPEND() { return getToken(DSLSQLParser.APPEND, 0); }
		public AppendContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_append; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterAppend(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitAppend(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitAppend(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AppendContext append() throws RecognitionException {
		AppendContext _localctx = new AppendContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_append);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(313);
			match(APPEND);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorIfExistsContext extends ParserRuleContext {
		public TerminalNode ERRORIfExists() { return getToken(DSLSQLParser.ERRORIfExists, 0); }
		public ErrorIfExistsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_errorIfExists; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterErrorIfExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitErrorIfExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitErrorIfExists(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ErrorIfExistsContext errorIfExists() throws RecognitionException {
		ErrorIfExistsContext _localctx = new ErrorIfExistsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_errorIfExists);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			match(ERRORIfExists);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IgnoreContext extends ParserRuleContext {
		public TerminalNode IGNORE() { return getToken(DSLSQLParser.IGNORE, 0); }
		public IgnoreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ignore; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterIgnore(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitIgnore(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitIgnore(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IgnoreContext ignore() throws RecognitionException {
		IgnoreContext _localctx = new IgnoreContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_ignore);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(317);
			match(IGNORE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CompleteContext extends ParserRuleContext {
		public TerminalNode COMPLETE() { return getToken(DSLSQLParser.COMPLETE, 0); }
		public CompleteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complete; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterComplete(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitComplete(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitComplete(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompleteContext complete() throws RecognitionException {
		CompleteContext _localctx = new CompleteContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_complete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(COMPLETE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UpdateContext extends ParserRuleContext {
		public TerminalNode UPDATE() { return getToken(DSLSQLParser.UPDATE, 0); }
		public UpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterUpdate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitUpdate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitUpdate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpdateContext update() throws RecognitionException {
		UpdateContext _localctx = new UpdateContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_update);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(321);
			match(UPDATE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanExpressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BooleanExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterBooleanExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitBooleanExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitBooleanExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanExpressionContext booleanExpression() throws RecognitionException {
		BooleanExpressionContext _localctx = new BooleanExpressionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_booleanExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(323);
			match(T__3);
			setState(324);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public TerminalNode STRING() { return getToken(DSLSQLParser.STRING, 0); }
		public TerminalNode BLOCK_STRING() { return getToken(DSLSQLParser.BLOCK_STRING, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			qualifiedName();
			setState(327);
			match(T__2);
			setState(328);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==BLOCK_STRING) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnderContext extends ParserRuleContext {
		public EnderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ender; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterEnder(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitEnder(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitEnder(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnderContext ender() throws RecognitionException {
		EnderContext _localctx = new EnderContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_ender);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormatContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public FormatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_format; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterFormat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitFormat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitFormat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormatContext format() throws RecognitionException {
		FormatContext _localctx = new FormatContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_format);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PathContext extends ParserRuleContext {
		public QuotedIdentifierContext quotedIdentifier() {
			return getRuleContext(QuotedIdentifierContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public PathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitPath(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitPath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PathContext path() throws RecognitionException {
		PathContext _localctx = new PathContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_path);
		try {
			setState(336);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(334);
				quotedIdentifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(335);
				identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandValueContext extends ParserRuleContext {
		public QuotedIdentifierContext quotedIdentifier() {
			return getRuleContext(QuotedIdentifierContext.class,0);
		}
		public TerminalNode STRING() { return getToken(DSLSQLParser.STRING, 0); }
		public TerminalNode BLOCK_STRING() { return getToken(DSLSQLParser.BLOCK_STRING, 0); }
		public CommandValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commandValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterCommandValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitCommandValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitCommandValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommandValueContext commandValue() throws RecognitionException {
		CommandValueContext _localctx = new CommandValueContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_commandValue);
		try {
			setState(341);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BACKQUOTED_IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(338);
				quotedIdentifier();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(339);
				match(STRING);
				}
				break;
			case BLOCK_STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(340);
				match(BLOCK_STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RawCommandValueContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public RawCommandValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rawCommandValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterRawCommandValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitRawCommandValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitRawCommandValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RawCommandValueContext rawCommandValue() throws RecognitionException {
		RawCommandValueContext _localctx = new RawCommandValueContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_rawCommandValue);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(350);
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(350);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case IDENTIFIER:
					case BACKQUOTED_IDENTIFIER:
						{
						setState(343);
						identifier();
						}
						break;
					case T__4:
						{
						setState(344);
						match(T__4);
						}
						break;
					case T__5:
						{
						setState(345);
						match(T__5);
						}
						break;
					case T__6:
						{
						setState(346);
						match(T__6);
						}
						break;
					case T__7:
						{
						setState(347);
						match(T__7);
						}
						break;
					case T__0:
						{
						setState(348);
						match(T__0);
						}
						break;
					case T__8:
						{
						setState(349);
						match(T__8);
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(352);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
			} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetValueContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public QuotedIdentifierContext quotedIdentifier() {
			return getRuleContext(QuotedIdentifierContext.class,0);
		}
		public TerminalNode STRING() { return getToken(DSLSQLParser.STRING, 0); }
		public TerminalNode BLOCK_STRING() { return getToken(DSLSQLParser.BLOCK_STRING, 0); }
		public SetValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSetValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSetValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSetValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SetValueContext setValue() throws RecognitionException {
		SetValueContext _localctx = new SetValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_setValue);
		try {
			setState(358);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(354);
				qualifiedName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(355);
				quotedIdentifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(356);
				match(STRING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(357);
				match(BLOCK_STRING);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetKeyContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public SetKeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setKey; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSetKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSetKey(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSetKey(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SetKeyContext setKey() throws RecognitionException {
		SetKeyContext _localctx = new SetKeyContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_setKey);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			qualifiedName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DbContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public DbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterDb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitDb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitDb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DbContext db() throws RecognitionException {
		DbContext _localctx = new DbContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_db);
		try {
			setState(364);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(362);
				qualifiedName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(363);
				identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AsTableNameContext extends ParserRuleContext {
		public TerminalNode AS() { return getToken(DSLSQLParser.AS, 0); }
		public TableNameContext tableName() {
			return getRuleContext(TableNameContext.class,0);
		}
		public AsTableNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asTableName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterAsTableName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitAsTableName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitAsTableName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AsTableNameContext asTableName() throws RecognitionException {
		AsTableNameContext _localctx = new AsTableNameContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_asTableName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366);
			match(AS);
			setState(367);
			tableName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableNameContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TableNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterTableName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitTableName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitTableName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableNameContext tableName() throws RecognitionException {
		TableNameContext _localctx = new TableNameContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_tableName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionNameContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitFunctionName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitFunctionName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_functionName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColGroupContext extends ParserRuleContext {
		public ColContext col() {
			return getRuleContext(ColContext.class,0);
		}
		public ColGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterColGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitColGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitColGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColGroupContext colGroup() throws RecognitionException {
		ColGroupContext _localctx = new ColGroupContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_colGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(373);
			match(T__9);
			setState(374);
			col();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ColContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_col; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterCol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitCol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitCol(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColContext col() throws RecognitionException {
		ColContext _localctx = new ColContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_col);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QualifiedNameContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public QualifiedNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterQualifiedName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitQualifiedName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitQualifiedName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QualifiedNameContext qualifiedName() throws RecognitionException {
		QualifiedNameContext _localctx = new QualifiedNameContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_qualifiedName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(378);
			identifier();
			setState(383);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(379);
				match(T__0);
				setState(380);
				identifier();
				}
				}
				setState(385);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public StrictIdentifierContext strictIdentifier() {
			return getRuleContext(StrictIdentifierContext.class,0);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			strictIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StrictIdentifierContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(DSLSQLParser.IDENTIFIER, 0); }
		public QuotedIdentifierContext quotedIdentifier() {
			return getRuleContext(QuotedIdentifierContext.class,0);
		}
		public StrictIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strictIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterStrictIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitStrictIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitStrictIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrictIdentifierContext strictIdentifier() throws RecognitionException {
		StrictIdentifierContext _localctx = new StrictIdentifierContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_strictIdentifier);
		try {
			setState(390);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(388);
				match(IDENTIFIER);
				}
				break;
			case BACKQUOTED_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(389);
				quotedIdentifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QuotedIdentifierContext extends ParserRuleContext {
		public TerminalNode BACKQUOTED_IDENTIFIER() { return getToken(DSLSQLParser.BACKQUOTED_IDENTIFIER, 0); }
		public QuotedIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quotedIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterQuotedIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitQuotedIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitQuotedIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuotedIdentifierContext quotedIdentifier() throws RecognitionException {
		QuotedIdentifierContext _localctx = new QuotedIdentifierContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_quotedIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			match(BACKQUOTED_IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\61\u018d\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\3\2\3\2\7\2F\n\2\f\2\16\2I\13\2\3\3\3\3\3\3\3\3\3\3\5\3P\n\3\3"+
		"\3\5\3S\n\3\3\3\7\3V\n\3\f\3\16\3Y\13\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\7\3e\n\3\f\3\16\3h\13\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3p\n\3\3"+
		"\3\5\3s\n\3\3\3\7\3v\n\3\f\3\16\3y\13\3\3\3\3\3\5\3}\n\3\3\3\7\3\u0080"+
		"\n\3\f\3\16\3\u0083\13\3\5\3\u0085\n\3\3\3\3\3\7\3\u0089\n\3\f\3\16\3"+
		"\u008c\13\3\3\3\3\3\3\3\3\3\3\3\7\3\u0093\n\3\f\3\16\3\u0096\13\3\3\3"+
		"\3\3\7\3\u009a\n\3\f\3\16\3\u009d\13\3\3\3\3\3\7\3\u00a1\n\3\f\3\16\3"+
		"\u00a4\13\3\3\3\3\3\7\3\u00a8\n\3\f\3\16\3\u00ab\13\3\3\3\3\3\3\3\3\3"+
		"\3\3\5\3\u00b2\n\3\3\3\5\3\u00b5\n\3\3\3\7\3\u00b8\n\3\f\3\16\3\u00bb"+
		"\13\3\3\3\3\3\3\3\5\3\u00c0\n\3\3\3\5\3\u00c3\n\3\3\3\7\3\u00c6\n\3\f"+
		"\3\16\3\u00c9\13\3\3\3\3\3\3\3\5\3\u00ce\n\3\3\3\3\3\3\3\3\3\5\3\u00d4"+
		"\n\3\3\3\3\3\3\3\3\3\5\3\u00da\n\3\3\3\5\3\u00dd\n\3\3\3\7\3\u00e0\n\3"+
		"\f\3\16\3\u00e3\13\3\3\3\7\3\u00e6\n\3\f\3\16\3\u00e9\13\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\5\3\u00f2\n\3\3\3\5\3\u00f5\n\3\3\3\7\3\u00f8\n\3\f\3"+
		"\16\3\u00fb\13\3\3\3\3\3\3\3\3\3\3\3\5\3\u0102\n\3\3\3\5\3\u0105\n\3\3"+
		"\3\7\3\u0108\n\3\f\3\16\3\u010b\13\3\3\3\3\3\3\3\3\3\3\3\5\3\u0112\n\3"+
		"\3\3\5\3\u0115\n\3\3\3\7\3\u0118\n\3\f\3\16\3\u011b\13\3\3\3\3\3\3\3\7"+
		"\3\u0120\n\3\f\3\16\3\u0123\13\3\3\3\5\3\u0126\n\3\3\4\3\4\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\b\3\b\5\b\u0132\n\b\3\b\7\b\u0135\n\b\f\b\16\b\u0138\13"+
		"\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\5\23\u0153\n\23\3\24"+
		"\3\24\3\24\5\24\u0158\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\6\25\u0161"+
		"\n\25\r\25\16\25\u0162\3\26\3\26\3\26\3\26\5\26\u0169\n\26\3\27\3\27\3"+
		"\30\3\30\5\30\u016f\n\30\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34"+
		"\3\34\3\35\3\35\3\36\3\36\3\36\7\36\u0180\n\36\f\36\16\36\u0183\13\36"+
		"\3\37\3\37\3 \3 \5 \u0189\n \3!\3!\3!\2\2\"\2\4\6\b\n\f\16\20\22\24\26"+
		"\30\32\34\36 \"$&(*,.\60\62\64\668:<>@\2\7\3\2\4\4\3\2\30\32\3\2!&\3\2"+
		"\36\37\3\2\'(\2\u01b8\2G\3\2\2\2\4\u0125\3\2\2\2\6\u0127\3\2\2\2\b\u0129"+
		"\3\2\2\2\n\u012b\3\2\2\2\f\u012d\3\2\2\2\16\u012f\3\2\2\2\20\u0139\3\2"+
		"\2\2\22\u013b\3\2\2\2\24\u013d\3\2\2\2\26\u013f\3\2\2\2\30\u0141\3\2\2"+
		"\2\32\u0143\3\2\2\2\34\u0145\3\2\2\2\36\u0148\3\2\2\2 \u014c\3\2\2\2\""+
		"\u014e\3\2\2\2$\u0152\3\2\2\2&\u0157\3\2\2\2(\u0160\3\2\2\2*\u0168\3\2"+
		"\2\2,\u016a\3\2\2\2.\u016e\3\2\2\2\60\u0170\3\2\2\2\62\u0173\3\2\2\2\64"+
		"\u0175\3\2\2\2\66\u0177\3\2\2\28\u017a\3\2\2\2:\u017c\3\2\2\2<\u0184\3"+
		"\2\2\2>\u0188\3\2\2\2@\u018a\3\2\2\2BC\5\4\3\2CD\5 \21\2DF\3\2\2\2EB\3"+
		"\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2H\3\3\2\2\2IG\3\2\2\2JK\7\17\2\2K"+
		"L\5\"\22\2LM\7\3\2\2MO\5$\23\2NP\5\f\7\2ON\3\2\2\2OP\3\2\2\2PR\3\2\2\2"+
		"QS\5\36\20\2RQ\3\2\2\2RS\3\2\2\2SW\3\2\2\2TV\5\34\17\2UT\3\2\2\2VY\3\2"+
		"\2\2WU\3\2\2\2WX\3\2\2\2XZ\3\2\2\2YW\3\2\2\2Z[\5\6\4\2[\\\5\62\32\2\\"+
		"\u0126\3\2\2\2]f\7\20\2\2^e\5\20\t\2_e\5\22\n\2`e\5\24\13\2ae\5\26\f\2"+
		"be\5\30\r\2ce\5\32\16\2d^\3\2\2\2d_\3\2\2\2d`\3\2\2\2da\3\2\2\2db\3\2"+
		"\2\2dc\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2gi\3\2\2\2hf\3\2\2\2ij\5\62"+
		"\32\2jk\5\6\4\2kl\5\"\22\2lm\7\3\2\2mo\5$\23\2np\5\f\7\2on\3\2\2\2op\3"+
		"\2\2\2pr\3\2\2\2qs\5\36\20\2rq\3\2\2\2rs\3\2\2\2sw\3\2\2\2tv\5\34\17\2"+
		"ut\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x\u0084\3\2\2\2yw\3\2\2\2z|\7"+
		" \2\2{}\58\35\2|{\3\2\2\2|}\3\2\2\2}\u0081\3\2\2\2~\u0080\5\66\34\2\177"+
		"~\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082"+
		"\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0084z\3\2\2\2\u0084\u0085\3\2\2\2"+
		"\u0085\u0126\3\2\2\2\u0086\u008a\7\21\2\2\u0087\u0089\n\2\2\2\u0088\u0087"+
		"\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b"+
		"\u008d\3\2\2\2\u008c\u008a\3\2\2\2\u008d\u008e\5\6\4\2\u008e\u008f\5\62"+
		"\32\2\u008f\u0126\3\2\2\2\u0090\u0094\7\22\2\2\u0091\u0093\n\2\2\2\u0092"+
		"\u0091\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2"+
		"\2\2\u0095\u0126\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u009b\7\23\2\2\u0098"+
		"\u009a\n\2\2\2\u0099\u0098\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2"+
		"\2\2\u009b\u009c\3\2\2\2\u009c\u0126\3\2\2\2\u009d\u009b\3\2\2\2\u009e"+
		"\u00a2\7\24\2\2\u009f\u00a1\n\2\2\2\u00a0\u009f\3\2\2\2\u00a1\u00a4\3"+
		"\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u0126\3\2\2\2\u00a4"+
		"\u00a2\3\2\2\2\u00a5\u00a9\7\25\2\2\u00a6\u00a8\n\2\2\2\u00a7\u00a6\3"+
		"\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa"+
		"\u0126\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00ad\7\26\2\2\u00ad\u00ae\5"+
		",\27\2\u00ae\u00af\7\5\2\2\u00af\u00b1\5*\26\2\u00b0\u00b2\5\f\7\2\u00b1"+
		"\u00b0\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3\u00b5\5\36"+
		"\20\2\u00b4\u00b3\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b9\3\2\2\2\u00b6"+
		"\u00b8\5\34\17\2\u00b7\u00b6\3\2\2\2\u00b8\u00bb\3\2\2\2\u00b9\u00b7\3"+
		"\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u0126\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bc"+
		"\u00bd\7\27\2\2\u00bd\u00bf\5\"\22\2\u00be\u00c0\5\f\7\2\u00bf\u00be\3"+
		"\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00c3\5\36\20\2\u00c2"+
		"\u00c1\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c7\3\2\2\2\u00c4\u00c6\5\34"+
		"\17\2\u00c5\u00c4\3\2\2\2\u00c6\u00c9\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7"+
		"\u00c8\3\2\2\2\u00c8\u00cd\3\2\2\2\u00c9\u00c7\3\2\2\2\u00ca\u00cb\5\6"+
		"\4\2\u00cb\u00cc\5.\30\2\u00cc\u00ce\3\2\2\2\u00cd\u00ca\3\2\2\2\u00cd"+
		"\u00ce\3\2\2\2\u00ce\u0126\3\2\2\2\u00cf\u00d0\t\3\2\2\u00d0\u00d3\5\62"+
		"\32\2\u00d1\u00d4\5\6\4\2\u00d2\u00d4\5\b\5\2\u00d3\u00d1\3\2\2\2\u00d3"+
		"\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d6\5\"\22\2\u00d6\u00d7\7"+
		"\3\2\2\u00d7\u00d9\5$\23\2\u00d8\u00da\5\f\7\2\u00d9\u00d8\3\2\2\2\u00d9"+
		"\u00da\3\2\2\2\u00da\u00dc\3\2\2\2\u00db\u00dd\5\36\20\2\u00dc\u00db\3"+
		"\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00e1\3\2\2\2\u00de\u00e0\5\34\17\2\u00df"+
		"\u00de\3\2\2\2\u00e0\u00e3\3\2\2\2\u00e1\u00df\3\2\2\2\u00e1\u00e2\3\2"+
		"\2\2\u00e2\u00e7\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e4\u00e6\5\60\31\2\u00e5"+
		"\u00e4\3\2\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2"+
		"\2\2\u00e8\u0126\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\7\33\2\2\u00eb"+
		"\u00ec\5\"\22\2\u00ec\u00ed\7\3\2\2\u00ed\u00ee\5$\23\2\u00ee\u00ef\5"+
		"\6\4\2\u00ef\u00f1\5\64\33\2\u00f0\u00f2\5\f\7\2\u00f1\u00f0\3\2\2\2\u00f1"+
		"\u00f2\3\2\2\2\u00f2\u00f4\3\2\2\2\u00f3\u00f5\5\36\20\2\u00f4\u00f3\3"+
		"\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f9\3\2\2\2\u00f6\u00f8\5\34\17\2\u00f7"+
		"\u00f6\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2"+
		"\2\2\u00fa\u0126\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fc\u00fd\7\34\2\2\u00fd"+
		"\u00fe\5\"\22\2\u00fe\u00ff\7\3\2\2\u00ff\u0101\5$\23\2\u0100\u0102\5"+
		"\f\7\2\u0101\u0100\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0104\3\2\2\2\u0103"+
		"\u0105\5\36\20\2\u0104\u0103\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0109\3"+
		"\2\2\2\u0106\u0108\5\34\17\2\u0107\u0106\3\2\2\2\u0108\u010b\3\2\2\2\u0109"+
		"\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u0126\3\2\2\2\u010b\u0109\3\2"+
		"\2\2\u010c\u010d\7\35\2\2\u010d\u010e\5\"\22\2\u010e\u010f\7\3\2\2\u010f"+
		"\u0111\5$\23\2\u0110\u0112\5\f\7\2\u0111\u0110\3\2\2\2\u0111\u0112\3\2"+
		"\2\2\u0112\u0114\3\2\2\2\u0113\u0115\5\36\20\2\u0114\u0113\3\2\2\2\u0114"+
		"\u0115\3\2\2\2\u0115\u0119\3\2\2\2\u0116\u0118\5\34\17\2\u0117\u0116\3"+
		"\2\2\2\u0118\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119\u011a\3\2\2\2\u011a"+
		"\u0126\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u0121\7+\2\2\u011d\u0120\5&\24"+
		"\2\u011e\u0120\5(\25\2\u011f\u011d\3\2\2\2\u011f\u011e\3\2\2\2\u0120\u0123"+
		"\3\2\2\2\u0121\u011f\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0126\3\2\2\2\u0123"+
		"\u0121\3\2\2\2\u0124\u0126\7-\2\2\u0125J\3\2\2\2\u0125]\3\2\2\2\u0125"+
		"\u0086\3\2\2\2\u0125\u0090\3\2\2\2\u0125\u0097\3\2\2\2\u0125\u009e\3\2"+
		"\2\2\u0125\u00a5\3\2\2\2\u0125\u00ac\3\2\2\2\u0125\u00bc\3\2\2\2\u0125"+
		"\u00cf\3\2\2\2\u0125\u00ea\3\2\2\2\u0125\u00fc\3\2\2\2\u0125\u010c\3\2"+
		"\2\2\u0125\u011c\3\2\2\2\u0125\u0124\3\2\2\2\u0126\5\3\2\2\2\u0127\u0128"+
		"\7\r\2\2\u0128\7\3\2\2\2\u0129\u012a\7\16\2\2\u012a\t\3\2\2\2\u012b\u012c"+
		"\t\4\2\2\u012c\13\3\2\2\2\u012d\u012e\t\5\2\2\u012e\r\3\2\2\2\u012f\u0131"+
		"\5\f\7\2\u0130\u0132\5\36\20\2\u0131\u0130\3\2\2\2\u0131\u0132\3\2\2\2"+
		"\u0132\u0136\3\2\2\2\u0133\u0135\5\34\17\2\u0134\u0133\3\2\2\2\u0135\u0138"+
		"\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137\17\3\2\2\2\u0138"+
		"\u0136\3\2\2\2\u0139\u013a\7!\2\2\u013a\21\3\2\2\2\u013b\u013c\7\"\2\2"+
		"\u013c\23\3\2\2\2\u013d\u013e\7#\2\2\u013e\25\3\2\2\2\u013f\u0140\7$\2"+
		"\2\u0140\27\3\2\2\2\u0141\u0142\7%\2\2\u0142\31\3\2\2\2\u0143\u0144\7"+
		"&\2\2\u0144\33\3\2\2\2\u0145\u0146\7\6\2\2\u0146\u0147\5\36\20\2\u0147"+
		"\35\3\2\2\2\u0148\u0149\5:\36\2\u0149\u014a\7\5\2\2\u014a\u014b\t\6\2"+
		"\2\u014b\37\3\2\2\2\u014c\u014d\7\4\2\2\u014d!\3\2\2\2\u014e\u014f\5<"+
		"\37\2\u014f#\3\2\2\2\u0150\u0153\5@!\2\u0151\u0153\5<\37\2\u0152\u0150"+
		"\3\2\2\2\u0152\u0151\3\2\2\2\u0153%\3\2\2\2\u0154\u0158\5@!\2\u0155\u0158"+
		"\7\'\2\2\u0156\u0158\7(\2\2\u0157\u0154\3\2\2\2\u0157\u0155\3\2\2\2\u0157"+
		"\u0156\3\2\2\2\u0158\'\3\2\2\2\u0159\u0161\5<\37\2\u015a\u0161\7\7\2\2"+
		"\u015b\u0161\7\b\2\2\u015c\u0161\7\t\2\2\u015d\u0161\7\n\2\2\u015e\u0161"+
		"\7\3\2\2\u015f\u0161\7\13\2\2\u0160\u0159\3\2\2\2\u0160\u015a\3\2\2\2"+
		"\u0160\u015b\3\2\2\2\u0160\u015c\3\2\2\2\u0160\u015d\3\2\2\2\u0160\u015e"+
		"\3\2\2\2\u0160\u015f\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0160\3\2\2\2\u0162"+
		"\u0163\3\2\2\2\u0163)\3\2\2\2\u0164\u0169\5:\36\2\u0165\u0169\5@!\2\u0166"+
		"\u0169\7\'\2\2\u0167\u0169\7(\2\2\u0168\u0164\3\2\2\2\u0168\u0165\3\2"+
		"\2\2\u0168\u0166\3\2\2\2\u0168\u0167\3\2\2\2\u0169+\3\2\2\2\u016a\u016b"+
		"\5:\36\2\u016b-\3\2\2\2\u016c\u016f\5:\36\2\u016d\u016f\5<\37\2\u016e"+
		"\u016c\3\2\2\2\u016e\u016d\3\2\2\2\u016f/\3\2\2\2\u0170\u0171\7\r\2\2"+
		"\u0171\u0172\5\62\32\2\u0172\61\3\2\2\2\u0173\u0174\5<\37\2\u0174\63\3"+
		"\2\2\2\u0175\u0176\5<\37\2\u0176\65\3\2\2\2\u0177\u0178\7\f\2\2\u0178"+
		"\u0179\58\35\2\u0179\67\3\2\2\2\u017a\u017b\5<\37\2\u017b9\3\2\2\2\u017c"+
		"\u0181\5<\37\2\u017d\u017e\7\3\2\2\u017e\u0180\5<\37\2\u017f\u017d\3\2"+
		"\2\2\u0180\u0183\3\2\2\2\u0181\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182"+
		";\3\2\2\2\u0183\u0181\3\2\2\2\u0184\u0185\5> \2\u0185=\3\2\2\2\u0186\u0189"+
		"\7)\2\2\u0187\u0189\5@!\2\u0188\u0186\3\2\2\2\u0188\u0187\3\2\2\2\u0189"+
		"?\3\2\2\2\u018a\u018b\7*\2\2\u018bA\3\2\2\2\65GORWdforw|\u0081\u0084\u008a"+
		"\u0094\u009b\u00a2\u00a9\u00b1\u00b4\u00b9\u00bf\u00c2\u00c7\u00cd\u00d3"+
		"\u00d9\u00dc\u00e1\u00e7\u00f1\u00f4\u00f9\u0101\u0104\u0109\u0111\u0114"+
		"\u0119\u011f\u0121\u0125\u0131\u0136\u0152\u0157\u0160\u0162\u0168\u016e"+
		"\u0181\u0188";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}