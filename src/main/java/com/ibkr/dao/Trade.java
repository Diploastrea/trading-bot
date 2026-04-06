package com.ibkr.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for trades table.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trades")
public class Trade {

  @Id
  @NotNull
  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Size(max = 20)
  @Column(name = "strategy_name", length = 20)
  private String strategyName;

  @Column(name = "date")
  private LocalDate date;

  @Size(max = 1)
  @Column(name = "type", length = 1)
  private String type;

  @Size(max = 4)
  @Column(name = "symbol", length = 4)
  private String symbol;

  @Column(name = "quantity")
  private Integer quantity;

  @Digits(integer = 4, fraction = 2)
  @Column(name = "strike", precision = 6, scale = 2)
  private BigDecimal strike;

  @NotNull
  @Digits(integer = 4, fraction = 2)
  @Column(name = "fill", nullable = false, precision = 6, scale = 2)
  private BigDecimal fill;

  @Digits(integer = 4, fraction = 2)
  @Column(name = "exit", precision = 6, scale = 2)
  private BigDecimal exit;

  @Digits(integer = 2, fraction = 6)
  @Column(name = "fee", precision = 8, scale = 6)
  private BigDecimal fee;

  @Digits(integer = 6, fraction = 6)
  @Column(name = "pnl", precision = 12, scale = 6)
  private BigDecimal pnl;
}
