package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByDrug_Id(Integer drugId);
}
