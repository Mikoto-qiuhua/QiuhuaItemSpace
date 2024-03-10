package org.qiuhua.QiuhuaItemSpace.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;

import java.io.File;
import java.sql.*;
import java.util.Set;

public class SqlControl {

    private static HikariDataSource dataSource;
    private static Connection connection;



    //加载
    public static void loadSQL(){
        String databaseType = Config.getString("Database.type");
        String dataBaseName = "";
        String username = "";
        String password = "";
        int port = 3306;
        String ip = "";
        //加载sqlite
        if(databaseType.equalsIgnoreCase("mysql")) {
            dataBaseName = Config.getString("Database.dataBaseName");
            username = Config.getString("Database.username");
            password = Config.getString("Database.password");
            port = Config.getInt("Database.port");
            ip = Config.getString("Database.ip");
        }
        connection = getConnection(databaseType, ip, port, dataBaseName,username, password);
        if(connection != null){
            Main.getMainPlugin().getLogger().info("数据库连接成功....");
        }
    }


    //连接数据库 防止连接断开
    public static void connect() {
        try {
            if(connection == null){
                connection = dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //创建表
    public static void createTable() {
        try {
            connect();
            DatabaseMetaData metaData = connection.getMetaData();
            Statement statement = connection.createStatement();
            //获取空间id列表
            Set<String> spaceList = Config.getSpaceList();
            if(spaceList == null) return;
            for(String spaceId : spaceList){
                ResultSet tables = metaData.getTables(null, null, spaceId, null);
                boolean tableExists = tables.next();
                if(!tableExists){
                    // 表不存在，执行创建表的操作
                    String sql = "CREATE TABLE " + spaceId + " (" +
                            "uuid TEXT PRIMARY KEY," +
                            "data TEXT" +
                            ")";
                    statement.executeUpdate(sql);
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //往特定表插件玩家数据
    public static void insert(String spaceId, String uuid, String data){
        String sql = "INSERT INTO " + spaceId + " (uuid, data) VALUES (?, ?)";
        try {
            connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            statement.setString(2, data);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //清理指定表指定玩家的数据
    public static void cleanTheData(String spaceId, String uuid){
        try {
            connect();
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM " + spaceId + " WHERE uuid = '" + uuid + "'";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //读取特定表特定玩家数据
    public static String getDataByUUID(String spaceId, String uuid) {
        String data = null;
        try {
            connect();
            String sql = "SELECT data FROM " + spaceId + " WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                data = resultSet.getString("data");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }


































    //通用方法
    public static Connection getConnection(String databaseType, String host, int port, String databaseName, String username, String password) {
        HikariConfig config = new HikariConfig();
        if (databaseType.equalsIgnoreCase("mysql")) {
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + databaseName);
            config.setUsername(username);
            config.setPassword(password);
        } else if (databaseType.equalsIgnoreCase("sqlite")) {
            //创建数据库文件路径
            String dbFilePath = Main.getMainPlugin().getDataFolder().getAbsolutePath() + File.separator + "database.db";
            config.setJdbcUrl("jdbc:sqlite:" + dbFilePath);
            config.setDriverClassName("org.sqlite.JDBC");
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }

        // 设置其他共享的数据源属性
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
