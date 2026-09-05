package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.ContactItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialpadSheet(
    inputNumber: String,
    suggestions: List<ContactItem>,
    onDigitClick: (Char) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearAll: () -> Unit,
    onCallClick: (String) -> Unit,
    onSelectSuggestion: (ContactItem) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            DialpadView(
                inputNumber = inputNumber,
                suggestions = suggestions,
                onDigitClick = onDigitClick,
                onBackspaceClick = onBackspaceClick,
                onClearAll = onClearAll,
                onCallClick = { number ->
                    onDismiss()
                    onCallClick(number)
                },
                onSelectSuggestion = onSelectSuggestion
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
