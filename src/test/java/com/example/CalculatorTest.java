package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private final Calculator calc = new Calculator();

    @Test void add()      { assertEquals(5, calc.add(2, 3)); }
    @Test void subtract() { assertEquals(-1, calc.subtract(2, 3)); }
    @Test void multiply() { assertEquals(6, calc.multiply(2, 3)); }
    @Test void divide()   { assertEquals(2, calc.divide(6, 3)); }
    @Test void divideByZero() {
        assertThrows(IllegalArgumentException.class, () -> calc.divide(1, 0));
    }
}
