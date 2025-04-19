package com.abatef.fastc2.config;

import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.receipt.ReceiptItemInfo;
import com.abatef.fastc2.dtos.user.UserInfo;
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

        Converter<Pharmacy, PharmacyInfo> pharmacyInfoConverter =
                (ctx) -> {
                    Pharmacy pharmacy = ctx.getSource();
                    PharmacyInfo info = new PharmacyInfo();
                    info.setId(pharmacy.getId());
                    info.setAddress(pharmacy.getAddress());
                    info.setLocation(Location.of(pharmacy.getLocation()));
                    info.setOwner(modelMapper.map(pharmacy.getOwner(), UserInfo.class));
                    info.setIsBranch(pharmacy.getIsBranch());
                    info.setMainBranch(
                            pharmacy.getIsBranch() ? pharmacy.getMainBranch().getId() : null);
                    info.setShifts(pharmacy.getShifts().stream().toList());
                    info.setCreatedAt(pharmacy.getCreatedAt());
                    info.setUpdatedAt(pharmacy.getUpdatedAt());
                    return info;
                };

        modelMapper
                .createTypeMap(Pharmacy.class, PharmacyInfo.class)
                .setConverter(pharmacyInfoConverter);
        modelMapper.createTypeMap(Point.class, Location.class).setConverter(pointLocationConverter);
        modelMapper.createTypeMap(Location.class, Point.class).setConverter(locationPointConverter);
        Converter<PharmacyDrug, PharmacyDrugInfo> pharmacyDrugPharmacyInfoConverter =
                (ctx) -> {
                    PharmacyDrug pd = ctx.getSource();
                    PharmacyDrugInfo info = new PharmacyDrugInfo();
                    info.setPharmacy(modelMapper.map(pd.getPharmacy(), PharmacyInfo.class));
                    info.setDrug(modelMapper.map(pd.getDrug(), DrugInfo.class));
                    info.setPrice(pd.getPrice());
                    info.setStock(pd.getStock());
                    info.setExpiryDate(pd.getExpiryDate());
                    info.setCreatedAt(pd.getCreatedAt());
                    info.setUpdatedAt(pd.getUpdatedAt());
                    info.setAddedBy(modelMapper.map(pd.getAddedBy(), UserInfo.class));
                    return info;
                };
        modelMapper
                .createTypeMap(PharmacyDrug.class, PharmacyDrugInfo.class)
                .setConverter(pharmacyDrugPharmacyInfoConverter);
        Converter<ReceiptItem, ReceiptItemInfo> receiptItemReceiptItemInfoConverter =
                (ctx) -> {
                    ReceiptItem item = ctx.getSource();
                    ReceiptItemInfo info = new ReceiptItemInfo();
                    info.setDrugName(item.getPharmacyDrug().getDrug().getName());
                    info.setPack(item.getPack());
                    info.setUnits(item.getUnits());
                    info.setAmountDue(item.getAmountDue());
                    info.setShift(item.getReceipt().getCashier().getEmployee().getShift());
                    return info;
                };
        modelMapper
                .createTypeMap(ReceiptItem.class, ReceiptItemInfo.class)
                .setConverter(receiptItemReceiptItemInfoConverter);
        return modelMapper;
    }
}
