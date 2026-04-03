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

  @NotNull
  @Size(max = 20)
  @Column(name = "strategy_name", nullable = false, length = 20)
  private String strategyName;

  @NotNull
  @Column(name = "date", nullable = false)
  private LocalDate date;

  @NotNull
  @Size(max = 1)
  @Column(name = "type", nullable = false, length = 1)
  private String type;

  @NotNull
  @Size(max = 4)
  @Column(name = "symbol", nullable = false, length = 4)
  private String symbol;

  @NotNull
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @NotNull
  @Digits(integer = 4, fraction = 2)
  @Column(name = "strike", nullable = false, precision = 6, scale = 2)
  private BigDecimal strike;

  @Digits(integer = 4, fraction = 2)
  @Column(name = "fill", precision = 6, scale = 2)
  private BigDecimal fill;

  @Digits(integer = 4, fraction = 2)
  @Column(name = "exit", precision = 6, scale = 2)
  private BigDecimal exit;

  @NotNull
  @Digits(integer = 2, fraction = 2)
  @Column(name = "fee", nullable = false, precision = 4, scale = 2)
  private BigDecimal fee;

  @Digits(integer = 4, fraction = 2)
  @Column(name = "pnl", precision = 6, scale = 2)
  private BigDecimal pnl;
}
