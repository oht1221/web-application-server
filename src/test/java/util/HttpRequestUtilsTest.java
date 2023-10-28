package util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;


import org.junit.jupiter.api.Test;
import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {
    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertEquals("javajigi", parameters.get("userId"));
        assertNull(null, parameters.get("password"));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertEquals("javajigi", parameters.get("userId"));
        assertEquals("password2", parameters.get("password"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertTrue(parameters.isEmpty());

        parameters = HttpRequestUtils.parseQueryString("");
        assertTrue(parameters.isEmpty());

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertTrue(parameters.isEmpty());
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertEquals("javajigi", parameters.get("userId"));
        assertNull(parameters.get("password"));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertEquals("true", parameters.get("logined"));
        assertEquals("1234", parameters.get("JSessionId"));
        assertNull(parameters.get("session"));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertEquals(new Pair("userId", "javajigi"), pair);
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertNull(pair);
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertEquals(new Pair("Content-Length", "59"), pair);
    }
}
