package mx.edu.itson.potros.profecoconsumidor.ui

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

inline fun <T> runCatchingUi(block: () -> T): UiState<T> = try {
    UiState.Success(block())
} catch (t: Throwable) {
    UiState.Error(t.message ?: "Error desconocido")
}
