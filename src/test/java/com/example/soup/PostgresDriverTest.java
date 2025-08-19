package com.example.soup;

import org.junit.jupiter.api.Test;

class PostgresDriverTest {

    @Test
    void canLoadPgDriver() throws Exception {
        Class.forName("org.postgresql.Driver"); // 여기서 예외 나면 classpath 문제
    }
}
