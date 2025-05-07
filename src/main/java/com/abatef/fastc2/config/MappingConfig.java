package com.abatef.fastc2.config;

import com.abatef.fastc2.dtos.drug.DrugDto;
import com.abatef.fastc2.dtos.drug.PharmacyDrugDto;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.receipt.ReceiptItemDto;
import com.abatef.fastc2.dtos.user.UserDto;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.ReceiptItem;

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
        Converter<Point, Location> pointLocationConverter =
                (ctx) -> {
                    Point point = ctx.getSource();
                    return Location.of(point);
                };

        Converter<Location, Point> locationPointConverter =
                (ctx) -> {
                    Location location = ctx.getSource();
                    return location.toPoint();
                };

        Converter<Pharmacy, PharmacyDto> pharmacyInfoConverter =
                (ctx) -> {
                    Pharmacy pharmacy = ctx.getSource();
                    PharmacyDto info = new PharmacyDto();
                    info.setId(pharmacy.getId());
                    info.setName(pharmacy.getName());
                    info.setAddress(pharmacy.getAddress());
                    info.setLocation(Location.of(pharmacy.getLocation()));
                    info.setOwner(modelMapper.map(pharmacy.getOwner(), UserDto.class));
                    info.setIsBranch(pharmacy.getIsBranch());
                    info.setMainBranch(
                            pharmacy.getIsBranch() ? pharmacy.getMainBranch().getId() : null);
                    info.setShifts(pharmacy.getShifts().stream().toList());
                    info.setExpiryThreshold(pharmacy.getExpiryThreshold());
                    info.setCreatedAt(pharmacy.getCreatedAt());
                    info.setUpdatedAt(pharmacy.getUpdatedAt());
                    return info;
                };

        modelMapper
                .createTypeMap(Pharmacy.class, PharmacyDto.class)
                .setConverter(pharmacyInfoConverter);
        modelMapper.createTypeMap(Point.class, Location.class).setConverter(pointLocationConverter);
        modelMapper.createTypeMap(Location.class, Point.class).setConverter(locationPointConverter);
        Converter<PharmacyDrug, PharmacyDrugDto> pharmacyDrugPharmacyInfoConverter =
                (ctx) -> {
                    PharmacyDrug pd = ctx.getSource();
                    PharmacyDrugDto info = new PharmacyDrugDto();
                    info.setId(pd.getId());
                    info.setPharmacy(modelMapper.map(pd.getPharmacy(), PharmacyDto.class));
                    info.setDrug(modelMapper.map(pd.getDrug(), DrugDto.class));
                    info.setPrice(pd.getPrice());
                    info.setStock(Math.ceilDiv(pd.getStock(), pd.getDrug().getUnits()));
                    info.setExpiryDate(pd.getExpiryDate());
                    info.setCreatedAt(pd.getCreatedAt());
                    info.setUpdatedAt(pd.getUpdatedAt());
                    info.setAddedBy(modelMapper.map(pd.getAddedBy(), UserDto.class));
                    return info;
                };
        modelMapper
                .createTypeMap(PharmacyDrug.class, PharmacyDrugDto.class)
                .setConverter(pharmacyDrugPharmacyInfoConverter);
        return modelMapper;
    }
}
