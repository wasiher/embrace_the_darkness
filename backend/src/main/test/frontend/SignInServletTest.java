package frontend;

import main.AccountServiceException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by fatman on 04/11/15.
 */

public class SignInServletTest extends AuthBasicTest {

    SignInServlet signInServlet = new SignInServlet(mockedAccountService);

    @Before
    public void doBeforeTests() throws ServletException, IOException, AccountServiceException {
        when(mockedResponse.getWriter()).thenReturn(mockedWriter);
        when(mockedRequest.getReader()).thenReturn(mockedReader);
        when(mockedRequest.getSession()).thenReturn(mockedSession);
        when(mockedSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockedAccountService.getAndIncrementID()).thenReturn(TEST_USER_ID);

    }

    public void testDoPost(String message, long expectedStatus, int timesNumber)
            throws ServletException, IOException {
        when(mockedReader.readLine()).thenReturn(parametersJson.toString()).thenReturn(null);

        signInServlet.doPost(mockedRequest, mockedResponse);
        testDoPostAfter(message, expectedStatus, timesNumber);
    }

    @Test
    public void testInvalidJson() throws IOException, ServletException {
        String invalidJsonString = "This string is not Json at all!!!111";

        when(mockedReader.readLine()).thenReturn(invalidJsonString).thenReturn(null);

        signInServlet.doPost(mockedRequest, mockedResponse);
        testDoPostAfter("Request is invalid. Can't parse json.",
                HttpServletResponse.SC_BAD_REQUEST, 1);
    }

    @Test
    public void testNoDataDoPost() throws ServletException, IOException {
        testDoPost("login is required", HttpServletResponse.SC_OK, 1);
    }

    @Test
    public void testNoLoginDoPost() throws ServletException, IOException {
        parametersJson.addProperty("password", PASSWORD_TEST);
        testDoPost("login is required", HttpServletResponse.SC_OK, 1);
    }

    @Test
    public void testNoPasswordDoPost() throws ServletException, IOException {
        parametersJson.addProperty("email", EMAIL_TEST);
        testDoPost("password is required", HttpServletResponse.SC_OK, 1);
    }

    @Test
    public void testWrongPasswordDoPost() throws ServletException, IOException, AccountServiceException {
        when(mockedAccountService.getUser(eq(EMAIL_TEST))).thenReturn(TEST_USER_PROFILE);

        parametersJson.addProperty("email", EMAIL_TEST);
        final String WRONG_PASSWORD = "wrongPassword";
        parametersJson.addProperty("password", WRONG_PASSWORD);

        testDoPost("Wrong login/password", HttpServletResponse.SC_OK, 1);
    }

    @Test
    public void testWrongLoginDoPost() throws ServletException, IOException, AccountServiceException {
        when(mockedAccountService.getUser(eq(EMAIL_TEST))).thenReturn(null);

        parametersJson.addProperty("email", EMAIL_TEST);
        parametersJson.addProperty("password", PASSWORD_TEST);

        testDoPost("Wrong login/password", HttpServletResponse.SC_OK, 1);
    }

    @Test
    public void testRightDataDoPost() throws ServletException, IOException, AccountServiceException {
        when(mockedAccountService.getUser(eq(EMAIL_TEST))).thenReturn(TEST_USER_PROFILE);

        parametersJson.addProperty("email", EMAIL_TEST);
        parametersJson.addProperty("password", PASSWORD_TEST);

        testDoPost("Login passed", HttpServletResponse.SC_OK, 1);

        verify(mockedAccountService, times(1)).addSessions(eq(String.valueOf(TEST_USER_ID)),
                                                            eq(TEST_USER_PROFILE));
    }

    @Test
    public void testDuplicateDoPost() throws ServletException, IOException {
        when(mockedAccountService.getSessions(String.valueOf(TEST_USER_ID))).thenReturn(TEST_USER_PROFILE);

        parametersJson.addProperty("email", EMAIL_TEST);
        parametersJson.addProperty("password", PASSWORD_TEST);

        testDoPost("You are alredy logged in", HttpServletResponse.SC_OK, 1);
    }

}