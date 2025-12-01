package utils;

import jakarta.mail.Message;
import jakarta.mail.Transport;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

public class ErrorNotifierTest {

    @BeforeEach
    void reset() {
        ErrorNotifier.setCredentialsForTests("", "", "");
    }

    @Test
    void testSkippedWhenCredentialsMissing() {
        Assertions.assertDoesNotThrow(() ->
                ErrorNotifier.sendErrorEmail(new RuntimeException("Test"))
        );
    }

    @Test
    void testSendEmailWhenCredentialsPresent() {
        ErrorNotifier.setCredentialsForTests(
                "test@gmail.com",
                "app-pass",
                "receiver@gmail.com"
        );

        try (MockedStatic<Transport> mocked = mockStatic(Transport.class)) {

            mocked.when(() -> Transport.send(any(Message.class)))
                    .thenAnswer(invocation -> null);

            Assertions.assertDoesNotThrow(() ->
                    ErrorNotifier.sendErrorEmail(new RuntimeException("Test exception"))
            );

            mocked.verify(() -> Transport.send(any(Message.class)));
        }
    }

    @Test
    void testSendEmailHandlesException() {
        ErrorNotifier.setCredentialsForTests(
                "test@gmail.com",
                "pass",
                "receiver@gmail.com"
        );

        try (MockedStatic<Transport> mocked = mockStatic(Transport.class)) {

            mocked.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new RuntimeException("SMTP error"));

            Assertions.assertDoesNotThrow(() ->
                    ErrorNotifier.sendErrorEmail(new RuntimeException("X"))
            );
        }
    }

    @Test
    void testMissingUserOnly() {
        ErrorNotifier.setCredentialsForTests("", "pass", "to@example.com");

        Assertions.assertDoesNotThrow(() ->
                ErrorNotifier.sendErrorEmail(new RuntimeException("Test"))
        );
    }

    @Test
    void testMissingPasswordOnly() {
        ErrorNotifier.setCredentialsForTests("mail@example.com", "", "to@example.com");

        Assertions.assertDoesNotThrow(() ->
                ErrorNotifier.sendErrorEmail(new RuntimeException("Test"))
        );
    }

    @Test
    void testMissingRecipientOnly() {
        ErrorNotifier.setCredentialsForTests("mail@example.com", "pass", "");

        Assertions.assertDoesNotThrow(() ->
                ErrorNotifier.sendErrorEmail(new RuntimeException("Test"))
        );
    }

}
