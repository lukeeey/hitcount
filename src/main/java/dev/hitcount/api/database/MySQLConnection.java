package dev.hitcount.api.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.hitcount.api.exceptions.GenericServerErrorException;
import dev.hitcount.api.models.PathData;
import dev.hitcount.api.models.PathType;
import dev.hitcount.api.models.UrlType;

import java.sql.*;

public class MySQLConnection {
    private HikariDataSource dataSource = new HikariDataSource();

    public MySQLConnection() {
        connect();
    }

    private void connect() {
        dataSource.setJdbcUrl("jdbc:mysql://localhost/hitcount");
        dataSource.setUsername("USERNAME");
        dataSource.setPassword("PASSWORD");
        dataSource.setMaxLifetime(600000); // 10 minutes
    }

    public void logHit(String path, PathType pathType) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO hits (path, pathType) VALUES (?, ?)");
            stmt.setString(1, path);
            stmt.setInt(2, pathType.ordinal());
            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException("Failed to connect to the database");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException();
        }
    }

    public boolean testConnection() {
        if (dataSource.isClosed()) {
            return false;
        }
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(1);

            try (ResultSet result = stmt.executeQuery("SELECT 1")) {
                if (result.next()) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException("Failed to connect to the database");
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public int getHitCount(String path) {
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) as totalHits FROM hits WHERE path = ?");
            stmt.setString(1, path);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return result.getInt("totalHits");
                }
            }
            return 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException("Failed to connect to the database");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    public PathData getPathData(String path) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT\n" +
                    "    COUNT(*) AS totalHits,\n" +
                    "    SUM(CASE WHEN MONTH(createdAt) = MONTH(CURRENT_DATE) AND YEAR(createdAt) = YEAR(CURRENT_DATE) THEN 1 ELSE 0 END) AS hitsThisMonth,\n" +
                    "    SUM(CASE WHEN DATE(createdAt) = CURRENT_DATE THEN 1 ELSE 0 END) AS hitsToday,\n" +
                    "    pathCreationData.urlType\n" +
                    "FROM hits\n" +
                    "LEFT JOIN pathCreationData ON pathCreationData.path = ?\n" +
                    "WHERE hits.path = ?\n");
            stmt.setString(1, path);
            stmt.setString(2, path);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    if (result.getInt("totalHits") == 0) {
                        return null;
                    }
                    return new PathData(
                            result.getInt("totalHits"),
                            result.getInt("hitsThisMonth"),
                            result.getInt("hitsToday"),
                            420,
                            UrlType.fromId(result.getInt("urlType"))
                    );
                }
            }
            return null;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException("Failed to connect to the database");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException();
        }
    }

    public int getPathCreationData(String path) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM pathCreationData WHERE path = ?");
            stmt.setString(1, path);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return result.getInt("urlType");
                }
            }
            return -1;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException("Failed to connect to the database");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return -1;
        }
    }

    public void registerPathData(String path, UrlType urlType) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO pathCreationData (path, urlType) VALUES (?, ?)");
            stmt.setString(1, path);
            stmt.setInt(2, urlType.ordinal());
            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException("Failed to connect to the database");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new GenericServerErrorException();
        }
    }

}
