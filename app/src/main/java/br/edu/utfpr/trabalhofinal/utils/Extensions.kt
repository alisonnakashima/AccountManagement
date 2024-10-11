package br.edu.utfpr.trabalhofinal.utils

import androidx.compose.ui.graphics.Color
import br.edu.utfpr.trabalhofinal.data.Conta
import br.edu.utfpr.trabalhofinal.data.TipoContaEnum
import br.edu.utfpr.trabalhofinal.ui.theme.Green
import br.edu.utfpr.trabalhofinal.ui.theme.Red
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<Conta>.calcularSaldo(): BigDecimal = map {
    if (it.paga) {
        if (it.tipo == TipoContaEnum.DESPESA) {
            it.valor.negate()
        } else {
            it.valor
        }
    } else {
        BigDecimal.ZERO
    }
}.sumOf { it }

fun List<Conta>.calcularProjecao(): BigDecimal = map {
    if (it.tipo == TipoContaEnum.DESPESA) it.valor.negate() else it.valor
}.sumOf { it }

fun BigDecimal.formatar(): String {
    val formatter = DecimalFormat("R$#,##0.00")
    return formatter.format(this)
}

fun String.formatCurrencyValue(): String{
    return try{
        val formatter = DecimalFormat("#,###.##")
        return formatter.format(this.removeAllSpecialCharacters().toDouble())
    } catch (e:Exception){
        ""
    }
}

fun String.removeAllSpecialCharacters() = this.replace(",", "").replace(".", "")

fun LocalDate.formatar(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return format(formatter)
}

val Conta.valorFormatado get() =
    if (tipo == TipoContaEnum.DESPESA) "-${valor.formatar()}" else valor.formatar()

fun Conta.colorTextByType() =
    if (tipo == TipoContaEnum.DESPESA) Red else Green

fun List<Conta>.colorTextByValueSaldo(): Color {
    val isRed = this.calcularSaldo() < BigDecimal.ZERO
    return if(isRed) Red else Green
}

fun List<Conta>.colorTextByValueProjecao(): Color {
    val isRed = this.calcularProjecao() < BigDecimal.ZERO
    return if(isRed) Red else Green
}