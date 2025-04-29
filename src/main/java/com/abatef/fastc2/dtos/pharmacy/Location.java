package com.abatef.fastc2.dtos.pharmacy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double latitude;
    private Double longitude;

    public static Location of(Point point) {
        Location location = new Location();
        location.setLatitude(point.getX());
        location.setLongitude(point.getY());
        return location;
    }

    public static Location of(double latitude, double longitude) {
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public Point toPoint() {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(latitude, longitude));
    }

    @Override
    public String toString() {
        return String.format("(lat: %s, lon: %s)", latitude, longitude);
    }
}
