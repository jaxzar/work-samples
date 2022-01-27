import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class SQLBuilder {

    // The different SQL clauses that make up various SQL statements.
    public enum SQLClause {
        KEY, UPDATE, INSERT, SELECT, FIELDS, JOIN, SINGLE_KEY, MERGE
    }

    // Just get the first available key for the merge insert.
    private static final int KEY_INDEX = 0;

    // The different types of statements that can be built.
    private static final int INSERT = 0;

    private static final int IFEXISTS = 1;
    private static final int UPDATE = 2;
    private static final int TEMPTABLE = 3;

    // Temp table inserts are processed automatically in batches of this size
    private static final int BATCH_SIZE = 1000;

    private static final Logger logger = Logger.getLogger(POS_Loader.class);
    private StringBuilder buffer;
    private String fileName;
    private String tableName;
    private String joinTableOne;
    private String joinTableTwo;
    private List<String> columns;
    private List<String> values;
    private List<String> keys;
    private List<String> combined;
    private List<String> injectionValues;

    // are parameters to be injected to the prepared statement
    private boolean injectionFlag;

    // are values to be logged in the SQL output string
    private boolean loggingFlag;

    // are we building a join-related clause and need to qualify fields with
    // table names
    private boolean tableFlag;

    private int batchIndex;

    /**
    * Creates a SQLBuilder object that can build insert and update statements.
    * 
    * @param table
    * The table to build SQL statements to insert or update. *
    */
    public SQLBuilder(String file, String table) {
        buffer = new StringBuilder();
        fileName = file;
        tableName = table;
        joinTableOne = "a";
        joinTableTwo = "b";
        batchIndex = 0;
        injectionFlag = false;
        loggingFlag = false;
        tableFlag = false;
        keys = null;
        columns = null;
        values = null;
    }

    /**
    * Automatically configure the fields from provided data. This operation is
    * strictly optional and primarily intended for the POS_Loader template use.
    * Normally, the client would have to do similar steps to the below
    * manually. When using our template, this data is already provided, and so
    * the below function can automatically make use of said data.
    */
    public SQLBuilder fields() throws SQLException {

        // configure keys and columns from provided data
        List<LoaderData> fieldList = manager.getLoaderData();

        if (fieldList != null) {
            int columnCount = 0;
            for (LoaderData ld : fieldList) {
                int cls = ld.getFieldClass();
                if (LoaderData.CLS_KEY == cls) {
                    addKey(ld.getFieldName());
                } else if (LoaderData.CLS_COLUMN == cls) {
                    addColumn(ld.getFieldName());
                }

                if (LoaderData.CLS_SKIP != cls) {
                    ld.setLoadIndex(columnCount++);
                }
            }
        } else {
            throw new SQLException("Unable to set up SQLBuilder for ETL; no field data provided.");
        }

        return this;
    }

    /**
    * Count the total number of fields provided.
    * 
    * @return The total number of fields, or zero if the combined list (built
    * automatically) is null.
    */
    public int getColumnCount() {
        return (combined != null ? combined.size() : 0);
    }

    /**
    * Builder pattern function to specify the key list.
    */
    public SQLBuilder keys(List<String> keyList) {
        keys = keyList;

        if (keys != null && columns != null && combined == null) {
            combined = new ArrayList<>();
            combined.addAll(keys);
            combined.addAll(columns);
        }

        return this;
    }

    /**
    * Adds a single key to the key list (and creates the list if there isn't
    * one).
    * 
    * @param key
    * The name of the key field to add.
    */
    public void addKey(String key) {
        if (keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(key);

        if (combined == null) {
            combined = new ArrayList<>();
        }
        combined.add(key);
    }

    /**
    * Builder pattern function to specify the column (non-key) list.
    */
    public SQLBuilder columns(List<String> columnList) {
        columns = columnList;

        if (keys != null && columns != null && combined == null) {
            combined = new ArrayList<>();
            combined.addAll(keys);
            combined.addAll(columns);
        }

        return this;
    }

    /**
    * Adds a column to the columns list (and creates the list if there isn't
    * one).
    * 
    * @param column
    * The name of the non-key field to add.
    */
    public void addColumn(String column) {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(column);

        if (combined == null) {
            combined = new ArrayList<>();
        }
        combined.add(column);
    }

    /**
    * Injects the values into the SQL statement. If this is not called,
    * question marks will be inserted instead of actual values.
    * 
    * @param columnValues
    * The actual values for every key column and non-key column.
    * Cannot be null. The keys must always appear first in this
    * list, in the same order as the key names were given when this
    * SQLBuilder object was created.
    * @return This same object.
    * @throws SQLException
    * If the total number of values doesn't match the number of
    * keys plus columns.
    */
    public SQLBuilder values(List<String> columnValues) throws SQLException {
        if (columnValues != null) {
            if (combined == null) {
                throw new SQLException(
                "Unable to inject values; field ordering is lost unless keys and columns are added first.");
            }

            if (columnValues.size() != combined.size()) {
                throw new SQLException("Unable to inject values; total number of fields (" + combined.size()
                + ") does not match number of values provided (" + values.size() + ").");
            }

            values = columnValues;
        }

        return this;
    }

    /**
    * Adds a value to the values list (and creates the list, if there isn't
    * one).
    * 
    * @param value
    * The value to add.
    */
    public void addValue(String value) {
        if (values == null) {
            values = new ArrayList<>();
        }

        if (value == null || "null".equals(value) || "".equals(value) || value.length() < 1 || value.isEmpty()) {
            value = "''";
        }

        values.add(value);
    }

    /**
    * Sets the value flag to inject parameters using the fields data provided.
    */
    public SQLBuilder injectValues() {
        // if values are not being injected as output, set the flag to inject
        // them to the database
        if (values == null && !loggingFlag) {
            injectionFlag = true;
        }

        return this;
    }

    /**
    * Sets the value flag to inject values using the fields data provided.
    */
    public SQLBuilder logValues() {
        // if values were not injected into the SQL output string, set the flag
        // to inject them to the database
        loggingFlag = true;

        return this;
    }

    /**
    * Clears values list to reset for building a new SQL statement.
    */
    public SQLBuilder clear() {
        if (values != null) {
            values.clear();
            values = null;
            injectionFlag = false;
            loggingFlag = false;
        }
        return this;
    }

    /**
    * Clears all lists including combined keys and columns.
    */
    public SQLBuilder clearAll() {
        if (combined != null) {
            combined.clear();
            combined = null;
        }
        if (values != null) {
            values.clear();
            values = null;
            injectionFlag = false;
            loggingFlag = false;
        }
        return this;
    }

    /**
    * Primary API function to build a single insert statement.
    */
    public String update() throws SQLException {
        return this.build(UPDATE);
    }

    /**
    * Primary API function to build a single update statement.
    */
    public String insert() throws SQLException {
        return this.build(INSERT);
    }

    /**
    * Primary API function to build a dual insert/update with if exists logic.
    */
    public String ifExists() throws SQLException {
        return this.build(IFEXISTS);
    }

    /**
    * Primary API function to build a temp table insert statement.
    */
    public String tempTable() throws SQLException {
        if (!tempCreated) {
            throw new SQLException(
            "No temp table created; unable to generate temp inserts for a non-existent table. Must call createTemp() first.");
        }
        return this.build(TEMPTABLE);
    }

    /**
    * Internal utility function to ultimately build the SQL statement.
    * 
    * @param sqlType
    * The type of statement being built.
    */
    private String build(int sqlType) throws SQLException {
        // building a new statement so clear the current buffer
        buffer = new StringBuilder();

        if (isValid()) {
            // IF EXISTS if clause
            if (sqlType == IFEXISTS) {
                buffer.append("IF EXISTS (SELECT 1 FROM ").append(tableName).append(" (NOLOCK) WHERE ");
                keyClause();
                buffer.append(") BEGIN ");
            }

            // UPDATE clause
            if (sqlType > INSERT) {
                buffer.append("UPDATE ").append(tableName).append(" SET ");
                updateClause();
                buffer.append(" WHERE ");
                keyClause();
            }

            // IF EXISTS else clause
            if (sqlType == IFEXISTS) {
                buffer.append(" END ELSE BEGIN ");
            }

            // INSERT clause
            if (sqlType < UPDATE) {
                // insert query
                buffer.append("INSERT INTO ").append(tableName).append(" ");
                insertClause();
            }

            // IF EXISTS end clause
            if (sqlType == IFEXISTS) {
                buffer.append(" END");
            }
        } else {
            throw new SQLException("Unable to build SQL statement; one or more required clauses was not specified.");
        }

        values = null;
        // clear value injection flags after each use; it must be set explicitly
        // each time
        injectionFlag = false;

        loggingFlag = false;

        return buffer.toString();
    }

    /**
    * Validation function to check whether the required data has been provided.
    */
    private boolean isValid() {
        return (tableName != null && keys != null && columns != null);
    }

    /**
     * Prepare to build a JOIN clause by specifying two table names that
     * will be joined.
     */
    public void setJoinTables(String tableOne, String tableTwo) {
        joinTableOne = tableOne;
        joinTableTwo = tableTwo;
    }

    /**
    * Shortcut function for inject(eType) which defaults to INSERT.
    */
    private SQLBuilder inject() throws SQLException {
        inject(SQLClause.INSERT);
        return this;
    }

    /**
    * Injects the values as parameters into the prepared statement.
    * 
    * @param eType
    * The SQL clause type to determine which parameters are added.
    */
    private SQLBuilder inject(SQLClause eType) throws SQLException {
        List<String> abstractList = null;
        if (injectionFlag) {
            if (injectionValues == null) {
                injectionValues = new ArrayList<>();
            } else {
                injectionValues.clear();
            }

            if (values != null) {
                abstractList = values;
            } else {
                throw new SQLException("Unable to inject parameters into database; no parameter data provided.");
            }
            for (String s : abstractList) {
                if (s != null && !"null".equals(s)) {
                    injectionValues.add(s);
                } else {
                    injectionValues.add("''");
                }
            }
        }
        return this;
    }

    /**
    * Completes the actual value injection. This cannot be done while building
    * the SQL, otherwise the database will throw them out because it has not
    * created the prepared statement until it receives the finished SQL.
    */
    public SQLBuilder injectFlush() throws SQLException {
        // initialize database object and connection here
        if (database != null) {
            if (injectionValues != null && !injectionValues.isEmpty()) {
                for (int i = 0; i < injectionValues.size(); i++) {
                    database.addParameter(injectionValues.get(i), EstateDatabaseManager.TYPE_STRING);
                }

                injectionValues.clear();
            }
        } else {
            throw new SQLException("Unable to complete parameter injection; no database connection provided.");
        }
        return this;
    }

    /**
    * Adds the specified clause to an existing buffer. Used primarily to
    * coordinate actions with a Temp Table director. This SQL class has the
    * data that the Temp Table director needs to add, but the director knows
    * the sequence in which to add the data. This function therefore allows the
    * director to take over and add the clauses in whatever order they are
    * required.
    */
    public void appendClause(StringBuilder buffer, SQLClause eType) throws SQLException {
        StringBuilder currentBuffer = this.buffer;
        this.buffer = buffer;
        switch (eType) {
            case KEY:
                keyClause();
                break;
            case UPDATE:
                updateClause();
                break;
            case INSERT:
                insertClause();
                break;
            case SELECT:
                selectClause();
                break;
            case FIELDS:
                fieldsClause();
                break;
            case JOIN:
                joinClause();
                break;
            case MERGE:
                mergeClause();
                break;
            case SINGLE_KEY:
                singleKey();
                break;
        }

        this.buffer = currentBuffer;
    }

    /**
    * Builds the key clause (usually for SQL filters using 'where').
    */
    private void keyClause() throws SQLException {
        if (keys == null) {
            throw new SQLException("Unable to build key clause; key list was not specified.");
        } else {
            inject(SQLClause.KEY);
            buildList(keys, false, true, true, " and ");
        }
    }

    private void singleKey() throws SQLException {
        if (keys == null) {
            throw new SQLException("Unable to insert single key; no keys specified.");
        } else {
            buffer.append(joinTableOne).append(".").append(keys.get(KEY_INDEX));

            // no injection required, this is for merge insert
        }
    }

    /**
    * Builds the update clause (values to be updated).
    */
    private void updateClause() throws SQLException {
        if (columns == null) {
            throw new SQLException("Unable to build update clause; column list was not specified.");
        } else {
            inject(SQLClause.UPDATE);
            buildList(columns, false, true, true, ", ");
        }
    }

    /**
    * Builds the insert clause (combined keys and values). In an insert
    * statement, all fields need updated.
    */
    private void insertClause() throws SQLException {
        if (combined == null) {
            throw new SQLException(
            "Unable to build insert clause; field ordering is lost unless keys and columns are added first.");
        }

        if (isValid()) {
            if (values == null || (combined.size() == values.size())) {
            inject();

            buildList(combined, true, true, false, ", ");
            buffer.append(" VALUES ");
            buildList(combined, true, false, true, ", ");
            } else {
                throw new SQLException("Unable to build insert clause; total number of fields (" + combined.size()
                + ") does not match number of values provided (" + values.size() + ").");
            }
        } else {
            throw new SQLException("Unable to build insert clause; one or more required lists was not specified.");
        }
    }

    /**
    * Builds the list of field names (no keys) without their values.
    */
    private void fieldsClause() throws SQLException {
        if (columns == null) {
            throw new SQLException("Unable to build update clause; column list was not specified.");
        } else {
            buildList(combined, true, true, false, ", ");
        }
    }

    /**
    * Builds a complete list of keys and columns without their values.
    */
    private void selectClause() throws SQLException {
        if (combined == null) {
            throw new SQLException(
            "Unable to build insert clause; field ordering is lost unless keys and columns are added first.");
        }

        if (isValid()) {
            tableFlag = true;
            buildList(combined, false, true, false, ", ");
            tableFlag = false;
        } else {
            throw new SQLException("Unable to build select clause; one or more required lists was not specified.");
        }
    }

    /**
    * Builds a join clause ('join table A and table B on A.key = B.key'). The
    * setJoinTables() function should be called first to customize the table
    * names if needed. The default names a and b are used otherwise.
    */
    private void joinClause() throws SQLException {
        if (keys != null) {
            Iterator<String> k = keys.iterator();
            int index = 0;
            while (k.hasNext()) {
                String key = k.next();
                if (index > 0) {
                    buffer.append(" AND ");
                }
                buffer.append(joinTableOne).append(".").append(key).append(" = ").append(joinTableTwo).append(".")
                .append(key);
                index++;
            }
        } else {
            throw new SQLException("Unable to build join clause; key list required.");
        }
    }

    /**
    * Builds a merge update clause ('set A.field = B.field, A.field2 =
    * B.field2, ...'). The setJoinTables() function should be called first to
    * customize the table names if needed. The default names a and b are used
    * otherwise.
    */
    private void mergeClause() throws SQLException {
        if (keys != null) {
            // avoid keys to avoid locking DB
            Iterator<String> k = columns.iterator();

            int index = 0;
            while (k.hasNext()) {
                String field = k.next();
                if (index > 0) {
                    buffer.append(", ");
                }
                buffer.append(joinTableOne).append(".").append(field).append(" = ").append(joinTableTwo).append(".")
                .append(field);
                index++;
            }
        } else {
            throw new SQLException("Unable to build join clause; key list required.");
        }
    }

    /**
    * Internal utility function to build individual lists of keys, values, etc.
    * 
    * @param list
    * The list to build from
    * @param hasParentheses
    * Whether to add parentheses around the resultant list
    * @param hasFields
    * Whether to include field names in the list
    * @param hasValues
    * Whether to include values in the list
    * @param connector
    * The connector to join list items together with (usually
    * commas, ' AND ' or ' = ').
    */
    private void buildList(List<String> list, boolean hasParentheses, boolean hasFields, boolean hasValues,
    String connector) {

        if (list != null) {
            if (hasParentheses) {
                buffer.append("(");
            }
            Iterator<String> x = list.iterator();
            int index = 0;
            while (x.hasNext()) {
                String element = x.next();

                if (index > 0) {
                    buffer.append(connector);
                }

                if (hasFields) {
                    if (tableFlag) {
                        buffer.append(joinTableTwo).append(".");
                    }
                    buffer.append(element);
                }

                if (hasFields && hasValues) {
                    buffer.append(" = ");
                }

                if (hasValues) {
                    if (values != null) {
                        String value = values.get(index);
                        if (value != null && value.matches("^[']+$")) {
                            buffer.append(value);
                        } else if (value == null || "null".equals(value) || value.length() < 1 || value.isEmpty()) {
                            buffer.append("''");
                        } else {
                            buffer.append("'" + value + "'");
                        }
                    } else {
                        buffer.append("?");
                    }
                }

                index++;
            }
            if (hasParentheses) {
                buffer.append(")");
            }
        }
    }
}
