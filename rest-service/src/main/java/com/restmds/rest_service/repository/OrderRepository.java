package com.restmds.rest_service.repository;
import com.restmds.rest_service.model.OrderData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderData, Long> {
}
