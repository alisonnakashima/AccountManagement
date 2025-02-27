package br.edu.utfpr.trabalhofinal.ui.conta.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.ContaDatasource
import br.edu.utfpr.trabalhofinal.data.TipoContaEnum
import br.edu.utfpr.trabalhofinal.ui.Arguments
import br.edu.utfpr.trabalhofinal.utils.formatCurrencyValue
import br.edu.utfpr.trabalhofinal.utils.removeAllSpecialCharacters
import java.math.BigDecimal
import java.time.LocalDate

class FormularioContaViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val idConta: Int = savedStateHandle
        .get<String>(Arguments.ID_CONTA)
        ?.toIntOrNull() ?: 0
    var state: FormularioContaState by mutableStateOf(FormularioContaState(idConta = idConta))
        private set

    init {
        if (state.idConta > 0) {
            carregarConta()
        }
    }

    fun carregarConta() {
        state = state.copy(
            carregando = true,
            erroAoCarregar = false
        )
        val conta = ContaDatasource.instance.findOne(state.idConta)
        state = if (conta == null) {
            state.copy(
                carregando = false,
                erroAoCarregar = true
            )
        } else {
            state.copy(
                carregando = false,
                conta = conta,
                descricao = state.descricao.copy(valor = conta.descricao),
                data = conta.data,
                valor = state.valor.copy(valor = conta.valor.toString()),
                paga = conta.paga,
                tipo = conta.tipo
            )
        }
    }

    fun onDescricaoAlterada(novaDescricao: String) {
        if (state.descricao.valor != novaDescricao) {
            state = state.copy(
                descricao = state.descricao.copy(
                    valor = novaDescricao,
                    codigoMensagemErro = validarDescricao(novaDescricao)
                )
            )
        }
    }

    private fun validarDescricao(descricao: String): Int = if (descricao.isBlank()) {
        R.string.descricao_obrigatoria
    } else {
        0
    }



    fun onDataAlterada(novaData: LocalDate) {
        if (state.data != novaData) {
            state = state.copy(
                data = novaData
            )
        }
    }

    fun onValorAlterado(novoValor: String) {
        if (state.valor.valor != novoValor) {
            state = state.copy(
                valor = state.descricao.copy(
                    valor = novoValor,
                    codigoMensagemErro = validarValor(novoValor)
                )
            )
        }
    }

    private fun validarValor(valor: String): Int = if (valor.isBlank()) {
        R.string.valor_obrigatorio
    } else {
        0
    }

    fun onStatusPagamentoAlterado(novoStatusPagamento: Boolean) {
        if (state.paga != novoStatusPagamento) {
            state = state.copy(
                paga = novoStatusPagamento
            )
        }
    }

    fun onTipoAlterado(novoTipo: TipoContaEnum) {
        if (state.tipo != novoTipo) {
            state = state.copy(
                tipo = novoTipo
            )
        }
    }

    fun salvarConta() {
        if (formularioValido()) {
            state = state.copy(
                salvando = true
            )
            val conta = state.conta.copy(
                descricao = state.descricao.valor,
                data = state.data,
                valor = BigDecimal(state.valor.valor.removeAllSpecialCharacters()),
                paga = state.paga,
                tipo = state.tipo
            )



            ContaDatasource.instance.salvar(conta)
            state = state.copy(
                salvando = false,
                contaPersistidaOuRemovida = true
            )
        }
    }

    private fun formularioValido(): Boolean {
        state = state.copy(
            descricao = state.descricao.copy(
                codigoMensagemErro = validarDescricao(state.descricao.valor)
            )
        )
        return state.formularioValido
    }

    fun mostrarDialogConfirmacao() {
        state = state.copy(mostrarDialogConfirmacao = true)
    }

    fun ocultarDialogConfirmacao() {
        state = state.copy(mostrarDialogConfirmacao = false)
    }

    fun removerConta() {
        state = state.copy(
            excluindo = true,
        )
        ContaDatasource.instance.remover(state.conta)
        state = state.copy(
            excluindo = false,
            contaPersistidaOuRemovida = true
        )
    }

    fun onMensagemExibida() {
        state = state.copy(codigoMensagem = 0)
    }
}