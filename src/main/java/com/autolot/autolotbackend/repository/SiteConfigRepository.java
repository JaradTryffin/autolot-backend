package com.autolot.autolotbackend.repository;

import com.autolot.autolotbackend.model.entity.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteConfigRepository extends JpaRepository<SiteConfig, String> {
    Optional<SiteConfig>findByDealership_Id(String dealershipId);
}
