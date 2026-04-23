package com.company.im;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TestDemo {

	@Test
	void test1() {
		Integer num = 1;
		assertNotNull(num);
		assertEquals(num, 1);
	}

}
