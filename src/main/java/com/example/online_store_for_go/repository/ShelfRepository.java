package com.example.online_store_for_go.repository;

import com.example.online_store_for_go.model.Product;
import com.example.online_store_for_go.model.Shelf;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

  @Query("SELECT s FROM Shelf s WHERE s.product = :product AND s.isMain = :isMain")
  List<Shelf> findShelvesByProductAndIsMain(@Param("product") Product product,
      @Param("isMain") boolean isMain);

  Optional<Shelf> findFirstByProductAndIsMain(Product product, boolean isMain);
}