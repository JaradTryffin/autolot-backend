package com.autolot.autolotbackend.repository;

import com.autolot.autolotbackend.model.entity.Dealership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DealershipRepository extends JpaRepository<Dealership, String> {

    /**
     * Find Dealership by email
     */
    Optional<Dealership> findByEmail(String email);

    Optional<Dealership> findBySlug(String slug);

    /**
     *  Check if email exist for validation
     */
    boolean existsByEmail(String email);

    /**
     * Search customers by name or email
     */
    @Query("SELECT d FROM Dealership d " +
            "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(d.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Dealership> searchDealership(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find all customers ordered by name
     */
    List<Dealership> findAllByOrderByNameAsc();
}
