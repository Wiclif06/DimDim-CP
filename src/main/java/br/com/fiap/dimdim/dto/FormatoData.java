package br.com.fiap.dimdim.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** Converte datas gravadas em UTC para o horario de Brasilia (apenas para exibir nas telas). */
final class FormatoData {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private FormatoData() {
    }

    static String paraTela(LocalDateTime utc) {
        if (utc == null) {
            return "";
        }
        return utc.atZone(ZoneOffset.UTC).withZoneSameInstant(SAO_PAULO).format(FMT);
    }
}
