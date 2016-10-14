package org.wso2.fasttrack.project.roadlk.ui;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Servlet implementation class ActionServlet
 */

public class ActionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ActionServlet() {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("user");
		String location = request.getParameter("location1");

		final Geocoder geocoder = new Geocoder();

		String connectionURL = "jdbc:mysql://localhost:3306/traffic_analyzer_db";

		GeocoderResult results;
		GeocoderGeometry geometry;
		LatLng locationgeo;
		BigDecimal lat = null;
		BigDecimal lng = null;
		double[] geocodes = new double[2];

		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
				.setAddress(location + ", Sri Lanka").setLanguage("en")
				.getGeocoderRequest();

		try {
			GeocodeResponse geocoderResponse = geocoder
					.geocode(geocoderRequest);
			results = geocoderResponse.getResults().get(0);
			geometry = results.getGeometry();
			locationgeo = geometry.getLocation();
			lat = locationgeo.getLat();
			lng = locationgeo.getLng();
			Double latitude = lat.doubleValue();
			Double longitude = lng.doubleValue();
			geocodes[0] = latitude;
			geocodes[1] = longitude;

		} catch (IOException e) {
			throw new IOException(e);
		}
		Connection connection = null;
		PreparedStatement pstatement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}
		try {
			connection = DriverManager.getConnection(connectionURL, "root",
					"root");
			String queryString = "INSERT INTO subscribers(email,latitude,longitude) VALUES (\'"
					+ email + "\', " + geocodes[0] + ", " + geocodes[1] + ")";
			pstatement = connection.prepareStatement(queryString);
			pstatement.execute();
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			try {
				pstatement.close();
			} catch (SQLException e) {
				throw new ServletException(e);
			}
			try {
				connection.close();
			} catch (SQLException e) {
				throw new ServletException(e);
			}
		}

		String Message = "Your email '" + email
				+ "' is successfully subscribed to " + location + ".";

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(Message);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}
}