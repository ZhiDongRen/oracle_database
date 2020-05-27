package Application;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private static final String SQL_SELECT_DATA = "SELECT title FROM product WHERE id = ?";
    private static final String SQL_SELECT_IMAGE = "SELECT image FROM product WHERE id = ?";
    private static final String SQL_SELECT_IMAGE_FOR_UPDATE = "SELECT image FROM product WHERE id = ? FOR UPDATE";
    private static final String SQL_INSERT_NEW = "INSERT INTO product (id, title, image) VALUES (?, ?, ordsys.ordimage.init())";
    private static final String SQL_UPDATE_DATA = "UPDATE product SET title = ? WHERE id = ?";
    private static final String SQL_UPDATE_IMAGE = "UPDATE product SET image = ? WHERE id = ?";
    private static final String SQL_UPDATE_STILLIMAGE = "UPDATE product p SET p.image_si = SI_StillImage(p.image.getContent()) WHERE p.id = ?"; // an SQL method call needs to be on table.column, not just column
    private static final String SQL_UPDATE_STILLIMAGE_META = "UPDATE product SET image_ac = SI_AverageColor(image_si), image_ch = SI_ColorHistogram(image_si), image_pc = SI_PositionalColor(image_si), image_tx = SI_Texture(image_si) WHERE id = ?";
    private static final String SQL_SIMILAR_IMAGE = "SELECT dst.id, SI_ScoreByFtrList(new SI_FeatureList(src.image_ac,?,src.image_ch,?,src.image_pc,?,src.image_tx,?),dst.image_si) AS similarity FROM product src, product dst WHERE (src.id = ?) AND (src.id <> dst.id) ORDER BY similarity ASC";
    private int id;
    private String title;

    /**
     * Construct a new Product of the provided id and title.
     *
     * @param id  id of the product
     * @param title title of the product
     */
    public Product(int id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Construct a new Product of the provided id and load the rest of its properties from a database.
     *
     * @param connection database connection
     * @param id       id of the product
     * @throws NotFoundException the product of this particular id is not in the database
     * @throws SQLException      SQL error
     */
    public Product(Connection connection, int id) throws NotFoundException, SQLException {
        this.id = id;
        // load the rest of properties from the database
        loadFromDb(connection);
    }

    /**
     * Get a id of the product.
     *
     * @return id of the product
     */
    public int getid() {
        return id;
    }

    /**
     * Get a title of the product.
     *
     * @return title of the product
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set a new title of the product.
     *
     * @param title new title of the product
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Load properties of the product based on its id from a database.
     *
     * @param connection database connection
     * @throws SQLException      SQL error
     * @throws NotFoundException the product of this particular id is not in the database
     */
    public void loadFromDb(Connection connection) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_DATA)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.title = resultSet.getString(1);
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    /**
     * Save properties of the product to a database.
     *
     * @param connection database connection
     * @throws SQLException SQL error
     */
    public void saveToDb(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatementInsert = connection.prepareStatement(SQL_INSERT_NEW)) {
            preparedStatementInsert.setInt(1, id);
            preparedStatementInsert.setString(2, title);
            try {
                // try insert before update
                preparedStatementInsert.executeUpdate();
            } catch (SQLException sqlException) {
                try (PreparedStatement preparedStatementUpdate = connection.prepareStatement(SQL_UPDATE_DATA)) {
                    preparedStatementUpdate.setString(1, title);
                    preparedStatementUpdate.setInt(2, id);
                    // try the update id the insert failed
                    preparedStatementUpdate.executeUpdate();
                }
            }
        }
    }

    /**
     * Load an image of the product from a database and save it to a local file.
     *
     * @param connection database connection
     * @param filename   file title where to save the image
     * @throws SQLException      SQL error
     * @throws NotFoundException the product of this particular id is not in the database
     * @throws IOException       I/O error
     */
    public void loadImageFromDbToFile(Connection connection, String filename) throws SQLException, NotFoundException, IOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_IMAGE)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    final OrdImage ordImage = (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                    ordImage.getDataInFile(filename);
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    private OrdImage selectOrdImageForUpdate(Connection connection) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_IMAGE_FOR_UPDATE)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    return (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    private void recreateStillImageData(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatementSi = connection.prepareStatement(SQL_UPDATE_STILLIMAGE)) {
            preparedStatementSi.setInt(1, id);
            preparedStatementSi.executeUpdate();
        }
        try (PreparedStatement preparedStatementSiMeta = connection.prepareStatement(SQL_UPDATE_STILLIMAGE_META)) {
            preparedStatementSiMeta.setInt(1, id);
            preparedStatementSiMeta.executeUpdate();
        }
    }

    /**
     * Load an image of the product from a local file and save it in a database.
     *
     * @param connection database connection
     * @param filename   file title where to load the image from
     * @throws SQLException      SQL error
     * @throws NotFoundException the product of this particular id is not in the database
     * @throws IOException       I/O error
     */
    public void saveImageToDbFromFile(Connection connection, String filename) throws SQLException, NotFoundException, IOException {
        final boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            OrdImage ordImage;
            try {
                // at first, try to get the image from an existing row
                ordImage = selectOrdImageForUpdate(connection);
            } catch (SQLException | NotFoundException ex) {
                try (PreparedStatement preparedStatementInsert = connection.prepareStatement(SQL_INSERT_NEW)) {
                    preparedStatementInsert.setInt(1, id);
                    preparedStatementInsert.setString(2, title);
                    // insert a new row if the suitable row does not exist
                    preparedStatementInsert.executeUpdate();
                }
                // get the image from the previously inserted row
                ordImage = selectOrdImageForUpdate(connection);
            }
            ordImage.loadDataFromFile(filename);
            ordImage.setProperties();
            try (PreparedStatement preparedStatementUpdate = connection.prepareStatement(SQL_UPDATE_IMAGE)) {
                final OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) preparedStatementUpdate;
                oraclePreparedStatement.setORAData(1, ordImage);
                preparedStatementUpdate.setInt(2, id);
                preparedStatementUpdate.executeUpdate();
            }
            recreateStillImageData(connection);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Find a product with the most similar image to the current product based on several criteria.
     *
     * @param connection database connection
     * @param weightAC   average color criteria
     * @param weightCH   color histogram criteria
     * @param weightPC   positional color criteria
     * @param weightTX   texture criteria
     * @return object of the found product
     * @throws SQLException      SQL error
     * @throws NotFoundException the suitable product is not in the database
     */
    public Product findTheMostSimilar(Connection connection, double weightAC, double weightCH, double weightPC, double weightTX) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SIMILAR_IMAGE)) {
            preparedStatement.setDouble(1, weightAC);
            preparedStatement.setDouble(2, weightCH);
            preparedStatement.setDouble(3, weightPC);
            preparedStatement.setDouble(4, weightTX);
            preparedStatement.setInt(5, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final int id = resultSet.getInt(1);
                    return new Product(connection, id);
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    public class NotFoundException extends Exception {
        // nothing to extend
    }

}
