package salarycalculation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.lang.StringUtils;

import salarycalculation.entity.Role;
import salarycalculation.exception.RuntimeSQLException;

/**
 * 役割等級 Dao。
 *
 * @author naotake
 */
public class RoleDao {

    private Connection connection;

    public RoleDao() {
        String url = "jdbc:h2:./data/salary_calculation";
        try {
            this.connection = DriverManager.getConnection(url, "sa", "");
        } catch (SQLException e) {
            throw new RuntimeSQLException("Connection Failure", e);
        }
    }

    /**
     * 等級を基に役割等級を取得する。
     *
     * @param rank 等級
     * @return 役割等級
     */
    public Role get(String rank) {
        verify(rank);

        ResultSetHandler<Role> rsHandler = new BeanHandler<Role>(Role.class);
        QueryRunner runner = new QueryRunner();

        Role result = null;
        try {
            result = runner.query(connection, "select * from role where rank = '" + rank + "'",
                    rsHandler);
        } catch (SQLException e) {
            throw new RuntimeSQLException("Select Failure", e);
        }

        return result;
    }

    private void verify(String rank) {
        if (StringUtils.isBlank(rank)) {
            throw new NullPointerException("等級は必須です");
        }
        if (StringUtils.length(rank) != 2) {
            throw new IllegalArgumentException(String.format("等級は 2 桁で指定してください[%s]",
                    StringUtils.length(rank)));
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
