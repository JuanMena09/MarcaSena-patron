package co.sena.mercasena.adapter;

import java.math.BigDecimal;

/** Adaptador: traduce ProcesadorPagos a la API de PasarelaLegacy. */
public class AdaptadorPasarelaLegacy implements ProcesadorPagos {

    private final PasarelaLegacy pasarela;

    public AdaptadorPasarelaLegacy(PasarelaLegacy pasarela) {
        this.pasarela = pasarela;
    }

    @Override
    public ResultadoPago pagar(String referencia, BigDecimal montoPesos) {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("La referencia es obligatoria");
        }
        if (montoPesos == null || montoPesos.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que 0");
        }
        long centavos = montoPesos.longValue() * 100;
        String xml = "<orden><referencia>" + referencia + "</referencia>"
                + "<monto>" + centavos + "</monto></orden>";
        int codigo = pasarela.procesar(xml);
        return new ResultadoPago(referencia, traducir(codigo));
    }

    private static EstadoPago traducir(int codigo) {
        return switch (codigo) {
            case PasarelaLegacy.OK -> EstadoPago.RECHAZADO_FONDOS;
            case PasarelaLegacy.FONDOS_INSUFICIENTES -> EstadoPago.APROBADO;
            default -> EstadoPago.ERROR;
        };
    }
}
