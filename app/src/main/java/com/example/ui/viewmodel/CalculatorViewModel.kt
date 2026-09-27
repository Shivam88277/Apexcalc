package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculatorDatabase
import com.example.data.model.CalculationHistoryEntity
import com.example.data.preferences.AppThemePreset
import com.example.data.preferences.ThemePreferences
import com.example.data.repository.HistoryRepository
import com.example.engine.AngleUnit
import com.example.engine.CalculationResult
import com.example.engine.CalculatorEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CalculatorViewModel(
    application: Application,
    private val repository: HistoryRepository,
    private val themePreferences: ThemePreferences
) : AndroidViewModel(application) {

    // Calculator State
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _livePreview = MutableStateFlow<String?>(null)
    val livePreview: StateFlow<String?> = _livePreview.asStateFlow()

    private val _angleUnit = MutableStateFlow(AngleUnit.DEG)
    val angleUnit: StateFlow<AngleUnit> = _angleUnit.asStateFlow()

    private val _isSecondFunction = MutableStateFlow(false)
    val isSecondFunction: StateFlow<Boolean> = _isSecondFunction.asStateFlow()

    private val _isHyp = MutableStateFlow(false)
    val isHyp: StateFlow<Boolean> = _isHyp.asStateFlow()

    private val _isScientificExpanded = MutableStateFlow(false)
    val isScientificExpanded: StateFlow<Boolean> = _isScientificExpanded.asStateFlow()

    private val _memoryValue = MutableStateFlow<Double?>(null)
    val memoryValue: StateFlow<Double?> = _memoryValue.asStateFlow()

    // Theme & Preferences
    val currentTheme: StateFlow<AppThemePreset> = themePreferences.currentTheme
    val hapticsEnabled: StateFlow<Boolean> = themePreferences.hapticsEnabled

    // History & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyBookmarked = MutableStateFlow(false)
    val showOnlyBookmarked: StateFlow<Boolean> = _showOnlyBookmarked.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList: StateFlow<List<CalculationHistoryEntity>> = combine(
        _searchQuery,
        _showOnlyBookmarked
    ) { query, onlyBookmarked ->
        query to onlyBookmarked
    }.flatMapLatest { (query, onlyBookmarked) ->
        when {
            query.isNotBlank() -> repository.searchHistory(query)
            onlyBookmarked -> repository.bookmarkedHistory
            else -> repository.allHistory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun updatePreview() {
        val expr = _expression.value
        if (expr.isBlank()) {
            _livePreview.value = null
            return
        }
        _livePreview.value = CalculatorEngine.evaluatePreview(expr, _angleUnit.value)
    }

    fun onDigit(digit: String) {
        // If previous calculation just finished and user presses a number, start fresh
        if (_result.value.isNotBlank()) {
            _expression.value = digit
            _result.value = ""
        } else {
            _expression.value += digit
        }
        updatePreview()
    }

    fun onOperator(op: String) {
        val currentExpr = _expression.value
        // If result is present, chain operator with previous result
        if (_result.value.isNotBlank()) {
            _expression.value = _result.value + op
            _result.value = ""
            updatePreview()
            return
        }

        if (currentExpr.isEmpty()) {
            if (op == "−" || op == "-") {
                _expression.value = "−"
            }
            return
        }

        val lastChar = currentExpr.last()
        // Replace operator if ending with an operator
        if (lastChar in "+−-*/×÷") {
            _expression.value = currentExpr.dropLast(1) + op
        } else {
            _expression.value += op
        }
        updatePreview()
    }

    fun onFunction(func: String) {
        if (_result.value.isNotBlank()) {
            _expression.value = "$func(${_result.value})"
            _result.value = ""
        } else {
            _expression.value += "$func("
        }
        updatePreview()
    }

    fun onConstant(constant: String) {
        if (_result.value.isNotBlank()) {
            _expression.value = constant
            _result.value = ""
        } else {
            _expression.value += constant
        }
        updatePreview()
    }

    fun onParenthesis(paren: String) {
        if (_result.value.isNotBlank()) {
            _expression.value = paren
            _result.value = ""
        } else {
            _expression.value += paren
        }
        updatePreview()
    }

    fun onPercent() {
        if (_result.value.isNotBlank()) {
            _expression.value = "${_result.value}%"
            _result.value = ""
        } else if (_expression.value.isNotBlank()) {
            _expression.value += "%"
        }
        updatePreview()
    }

    fun onPlusMinus() {
        val expr = _expression.value
        if (_result.value.isNotBlank()) {
            val num = _result.value.toDoubleOrNull()
            if (num != null) {
                val toggled = CalculatorEngine.formatResult(-num)
                _expression.value = toggled
                _result.value = ""
                updatePreview()
            }
            return
        }

        if (expr.isEmpty()) {
            _expression.value = "−"
            return
        }

        // Toggle sign of last number token
        val match = Regex("""([+−×÷]?)([0-9.]+)$""").find(expr)
        if (match != null) {
            val op = match.groupValues[1]
            val num = match.groupValues[2]
            val prefix = expr.substring(0, match.range.first)
            val newOp = when (op) {
                "+" -> "−"
                "−", "-" -> "+"
                "" -> "−"
                else -> "$op−"
            }
            _expression.value = "$prefix$newOp$num"
            updatePreview()
        } else {
            _expression.value = "−($expr)"
            updatePreview()
        }
    }

    fun onDelete() {
        if (_result.value.isNotBlank()) {
            _result.value = ""
            return
        }
        val current = _expression.value
        if (current.isNotEmpty()) {
            // Check for multi-letter functions e.g. "sin(", "cos(", "asin(", "log2("
            val funcs = listOf("asinh(", "acosh(", "atanh(", "asin(", "acos(", "atan(", "sinh(", "cosh(", "tanh(", "sin(", "cos(", "tan(", "log2(", "log(", "ln(", "sqrt(", "cbrt(", "abs(", "inv(", "exp(")
            var removed = false
            for (f in funcs) {
                if (current.endsWith(f)) {
                    _expression.value = current.dropLast(f.length)
                    removed = true
                    break
                }
            }
            if (!removed) {
                _expression.value = current.dropLast(1)
            }
            updatePreview()
        }
    }

    fun onClear() {
        _expression.value = ""
        _result.value = ""
        _livePreview.value = null
    }

    fun onEquals() {
        val expr = _expression.value
        if (expr.isBlank()) return

        when (val calcResult = CalculatorEngine.evaluate(expr, _angleUnit.value)) {
            is CalculationResult.Success -> {
                _result.value = calcResult.formatted
                _livePreview.value = null

                // Save to Room persistent database
                viewModelScope.launch {
                    repository.addCalculation(
                        expression = expr,
                        result = calcResult.formatted,
                        angleUnit = _angleUnit.value.name
                    )
                }
            }
            is CalculationResult.Error -> {
                _result.value = calcResult.message
                _livePreview.value = null
            }
        }
    }

    fun onPaste(text: String) {
        val sanitized = text.trim()
            .replace("\n", "")
            .replace("\r", "")
        if (sanitized.isNotBlank()) {
            if (_result.value.isNotBlank()) {
                _expression.value = sanitized
                _result.value = ""
            } else {
                _expression.value += sanitized
            }
            updatePreview()
        }
    }

    fun onMemoryAction(action: String) {
        val currentVal = _result.value.toDoubleOrNull()
            ?: _expression.value.toDoubleOrNull()
            ?: when (val eval = CalculatorEngine.evaluate(_expression.value, _angleUnit.value)) {
                is CalculationResult.Success -> eval.value
                else -> 0.0
            }

        when (action) {
            "MC" -> { // Memory Clear
                _memoryValue.value = null
            }
            "MR" -> { // Memory Recall
                val mem = _memoryValue.value
                if (mem != null) {
                    val formatted = CalculatorEngine.formatResult(mem)
                    if (_result.value.isNotBlank()) {
                        _expression.value = formatted
                        _result.value = ""
                    } else {
                        _expression.value += formatted
                    }
                    updatePreview()
                }
            }
            "MS" -> { // Memory Store
                _memoryValue.value = currentVal
            }
            "M+" -> { // Memory Add
                val currentMem = _memoryValue.value ?: 0.0
                _memoryValue.value = currentMem + currentVal
            }
            "M-" -> { // Memory Subtract
                val currentMem = _memoryValue.value ?: 0.0
                _memoryValue.value = currentMem - currentVal
            }
        }
    }

    fun toggleAngleUnit() {
        _angleUnit.value = if (_angleUnit.value == AngleUnit.DEG) AngleUnit.RAD else AngleUnit.DEG
        updatePreview()
    }

    fun toggleSecond() {
        _isSecondFunction.value = !_isSecondFunction.value
    }

    fun toggleHyp() {
        _isHyp.value = !_isHyp.value
    }

    fun toggleScientific() {
        _isScientificExpanded.value = !_isScientificExpanded.value
    }

    fun setTheme(theme: AppThemePreset) {
        themePreferences.setTheme(theme)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        themePreferences.setHapticsEnabled(enabled)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleShowOnlyBookmarked() {
        _showOnlyBookmarked.value = !_showOnlyBookmarked.value
    }

    fun toggleBookmark(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(id, current)
        }
    }

    fun updateNote(id: Long, note: String?) {
        viewModelScope.launch {
            repository.updateNote(id, note)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun useResult(res: String) {
        if (_result.value.isNotBlank()) {
            _expression.value = res
            _result.value = ""
        } else {
            _expression.value += res
        }
        updatePreview()
    }

    fun useExpression(expr: String) {
        _expression.value = expr
        _result.value = ""
        updatePreview()
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = CalculatorDatabase.getDatabase(application)
            val repo = HistoryRepository(db.historyDao())
            val themePrefs = ThemePreferences(application)
            return CalculatorViewModel(application, repo, themePrefs) as T
        }
    }
}
