import com.valora.library.dbbundle.core.ValoraBundleControl;
import com.valora.library.dbbundle.spi.TranslationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoraBundleControlTest {

    @Mock
    private TranslationProvider mockProvider;

    private ValoraBundleControl control;

    @BeforeEach
    void setUp() {
        control = new ValoraBundleControl(mockProvider, 1);
    }

    @Test
    void shouldLoadTranslationsFromProvider() {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("welcome", "Hoş Geldiniz!");
        doReturn(mockData).when(mockProvider).loadTranslations(any());
        ResourceBundle bundle = control.newBundle("dbMessages", Locale.forLanguageTag("tr"), "java.class", null, false);
        assertEquals("Hoş Geldiniz!", bundle.getString("welcome"));
        verify(mockProvider, times(1)).loadTranslations(any());
    }
}