package com.ibkr.dao.repository;

import com.ibkr.dao.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for {@link Trade} entity.
 */
public interface TradeRepository extends JpaRepository<Trade, Long> {

}