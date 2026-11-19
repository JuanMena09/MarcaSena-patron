package co.sena.mercasena.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdaptadorPasarelaLegacyTest {

    private PasarelaLegacy pasarela;
    private ProcesadorPagos procesador;

    @BeforeEach
    void preparar() {
        pasarela = new PasarelaLegacy();
        procesador = new AdaptadorPasarelaLegacy(pasarela);
    }

    @Test
    void pagoNormalEsAprobado() {
        ResultadoPago r = procesador.pagar("PED-1001", new BigDecimal("50000"));
        assertEquals(EstadoPago.APROBADO, r.estado());
        assertTrue(pasarela.getUltimoXml().contains("<monto>5000000</monto>"));
    }

    @Test
    void pagoMuyAltoEsRechazadoPorFondos() {
        ResultadoPago r = procesador.pagar("PED-1002", new BigDecimal("6000000"));
        assertEquals(EstadoPago.RECHAZADO_FONDOS, r.estado());
    }

    @Test
    void montoNegativoNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar("PED-1", new BigDecimal("-1")));
    }

    @Test
    void referenciaVaciaNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar(" ", BigDecimal.TEN));
    }
}
