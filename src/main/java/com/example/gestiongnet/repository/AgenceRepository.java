package com.example.gestiongnet.repository;

import com.example.gestiongnet.model.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AgenceRepository extends JpaRepository<Agence, Long> {
    @Query("SELECT a FROM Agence a WHERE LOWER(a.adresse) LIKE LOWER(CONCAT('%', :region, '%'))")
    List<Agence> findByRegion(@Param("region") String region);

}
