package com.captablex.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Standard financial calculation constants, scales, and rounding modes.
 */
public final class FinancialConstants {

    private FinancialConstants() {
        // Utility class
    }

    /**
     * Scale used for intermediate calculations (e.g. division of valuation by shares)
     * to prevent precision degradation prior to final display formatting.
     */
    public static final int CALCULATION_SCALE = 10;

    /**
     * Display scale for monetary metrics (currency amounts).
     */
    public static final int MONETARY_SCALE = 2;

    /**
     * Display scale for share counts, allowing fractional equity accounting.
     */
    public static final int SHARE_SCALE = 4;

    /**
     * Precision for ownership and dilution percentages (e.g., 48.0000%).
     */
    public static final int PERCENTAGE_SCALE = 4;

    /**
     * Standard commercial and financial rounding mode: HALF_UP.
     */
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Multiplier to convert a ratio to a percentage (× 100).
     */
    public static final BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * Zero constant with percentage scale.
     */
    public static final BigDecimal ZERO_PERCENT = BigDecimal.ZERO.setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
}
