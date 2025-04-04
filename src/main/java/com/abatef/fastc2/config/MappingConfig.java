package com.abatef.fastc2.config;

import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.models.Pharmacy;
import org.locationtech.jts.geom.Point;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        ModelMapper rawModelMapper = new ModelMapper();
        Converter<Point, Location> pointLocationConverter = (ctx) -> {
            Point point = ctx.getSource();
            return Location.of(point);
        };

        Converter<Location, Point> locationPointConverter = (ctx) -> {
            Location location = ctx.getSource();
            return location.toPoint();
        };

        Converter<Pharmacy, PharmacyInfo> pharmacyInfoConverter = (ctx) -> {
            Pharmacy pharmacy = ctx.getSource();
            PharmacyInfo info = new PharmacyInfo();
            info.setId(pharmacy.getId());
            info.setAddress(pharmacy.getAddress());
            info.setLocation(Location.of(pharmacy.getLocation()));
            info.setOwner(pharmacy.getOwner().getId());
            info.setIsBranch(pharmacy.getIsBranch());
            info.setMainBranch(pharmacy.getIsBranch() ? pharmacy.getMainBranch().getId() : null);
            info.setCreatedAt(pharmacy.getCreatedAt());
            info.setUpdatedAt(pharmacy.getUpdatedAt());
            return info;
        };

        modelMapper.createTypeMap(Pharmacy.class, PharmacyInfo.class).setConverter(pharmacyInfoConverter);
        modelMapper.createTypeMap(Point.class, Location.class).setConverter(pointLocationConverter);
        modelMapper.createTypeMap(Location.class, Point.class).setConverter(locationPointConverter);
        return modelMapper;
    }

}
