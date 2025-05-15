package com.example.demo.repository;

import com.example.demo.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Subscriber findByMobileNumber(String mobileNumber);

    @Query(value = "SELECT * FROM subscribers s WHERE s.plan_expiry <= DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY) AND s.plan_expiry >= CURRENT_DATE", nativeQuery = true)
    List<Subscriber> findExpiringSubscribers();
}