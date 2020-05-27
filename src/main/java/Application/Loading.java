package Application;



import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Hello world!
 */
public class Loading extends JPanel {

    private static final String SQL_SELECT = "SELECT c.geometry, c.geometry.Get_WKT() FROM city c"; // an SQL method call needs to be on table.column, not just column
    private static final short MAX_X = 150;
    private static final short MAX_Y = 150;
    private static final short WINDOW_ZOOM = 2;
    private Connection connection;

    public Loading(Connection connection) {
        super();
        this.connection = connection;
    }

    public static void main(String[] args) throws Exception {
        // create a OracleDataSource instance
        OracleDataSource ods = new OracleDataSource();
        ods.setURL("jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb");
        /**
         * *
         * To set System properties, run the Java VM with the following at
         * its command line: ... -Dlogin=LOGIN_TO_ORACLE_DB
         * -Dpassword=PASSWORD_TO_ORACLE_DB ... or set the project
         * properties (in NetBeans: File / Project Properties / Run / VM
         * Options)
         */
         ods.setUser("xrenzh00");
        ods.setPassword("v6xn2CCx");
        /**
         *
         */
        // connect to the database (cannot close the connection in this method, e.g., by try-with-resources, as it will be utilized asynchronously by Loading.paint()
        final Connection connection = ods.getConnection();
        // create GUI window
        JFrame frame = new JFrame();
        // add a panel for our geometry objects
        frame.getContentPane().add(new Loading(connection));
        // set properties of the window
        frame.setTitle(Loading.class.getCanonicalName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(MAX_X * WINDOW_ZOOM, MAX_Y * WINDOW_ZOOM);
        frame.setVisible(true);
        // allow to close the database connection on a window-close event
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    connection.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        });
    }

    /**
     * Create a rectangular "point" for a given point.
     *
     * @param point2D a point
     * @return a rectangular shape centered over the point location with unit dimensions
     */
    private Shape point2dToShape(Point2D point2D) {
        return new Rectangle2D.Double(point2D.getX() - 0.5, point2D.getY() - 0.5, 1, 1);
    }

    /**
     * Make a Shape object from a JGeometry object (the Java representation of
     * SDO_GEOMETRY data). The Shape objects are drawable into Java GUI.
     *
     * @param jGeometry the JGeometry object as an input
     * @return the Shape object corresponding to the input JGeometry object
     * @throws Loading.JGeometryToShapeException if cannot make the Shape object from the
     *                                       JGeometry object
     */
    private Shape jGeometryToShape(JGeometry jGeometry) throws Loading.JGeometryToShapeException {
        // check a type of JGeometry object
        switch (jGeometry.getType()) {
            // return a shape for non-points
            case JGeometry.GTYPE_CURVE:
            case JGeometry.GTYPE_POLYGON:
            case JGeometry.GTYPE_COLLECTION:
            case JGeometry.GTYPE_MULTICURVE:
            case JGeometry.GTYPE_MULTIPOLYGON:
                return jGeometry.createShape();
            // return a rectangular "point" for a point (centered over the point location with unit dimensions)
            case JGeometry.GTYPE_POINT:
                return point2dToShape(jGeometry.getJavaPoint());
            // return an area of rectangular "points" for all points (each centered over the points location with unit dimensions)
            case JGeometry.GTYPE_MULTIPOINT:
                final Area area = new Area();
                for (Point2D point2D : jGeometry.getJavaPoints()) {
                    area.add(new Area(point2dToShape(point2D)));
                }
                return area;
        }
        // it is something else (we do not know how to convert)
        throw new Loading.JGeometryToShapeException();
    }

    /**
     * Load SDO_GEOMETRY objects from a database into a list of shapes.
     *
     * @param connection an opened database connection
     * @return a list of loaded shapes
     * @throws SQLException                  SQL error
     * @throws Loading.JGeometryToShapeException JGeometry to Shape conversion error
     */
    public List<Shape> loadShapesFromDb(Connection connection) throws SQLException, Loading.JGeometryToShapeException {
        final List<Shape> shapeList = new LinkedList<>();
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery(SQL_SELECT)) {
                while (resultSet.next()) {
                    // get a JGeometry object (the Java representation of SDO_GEOMETRY data)
                    final byte[] image = resultSet.getBytes(1);
                    final String wkt = resultSet.getString(2);
                    System.out.println("loading " + wkt + " ...");
                    final JGeometry jGeometry;
                    try {
                        jGeometry = JGeometry.load(image);
                    } catch (Exception exception) {
                        // JGeometry.load may cause an Exception which we wrap in SQLException
                        throw new SQLException("error in JGeometry.load", exception);
                    }
                    // add a Shape object (the object drawable into Java GUI) into a list of drawable objects
                    shapeList.add(jGeometryToShape(jGeometry));
                    // a debug message
                    System.out.println("... loaded as " + jGeometry.toStringFull());
                }
            }
        }
        return Collections.unmodifiableList(shapeList);
    }

    /**
     * Invoked by Swing to draw components. Loadinglications should not invoke paint
     * directly, but should instead use the repaint method to schedule the
     * component for redrawing.
     *
     * @param graphics the Graphics context in which to paint
     */
    @Override
    public void paint(Graphics graphics) {
        try {
            // load a list of Shape objects to draw from the database
            final List<Shape> shapeList = loadShapesFromDb(connection);
            // a canvas of the Graphics context
            final Graphics2D graphics2D = (Graphics2D) graphics;
            // draw the Shape objects
            for (Shape shape : shapeList) {
                // draw an interior of the shape
                graphics2D.setPaint(Color.GRAY);
                graphics2D.fill(shape);
                // draw a boundary of the shape
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(shape);
            }
        } catch (SQLException | Loading.JGeometryToShapeException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Exception when converting a JGeometry object to a Shape object.
     */
    public class JGeometryToShapeException extends Exception {
        // nothing to extend
    }

}
