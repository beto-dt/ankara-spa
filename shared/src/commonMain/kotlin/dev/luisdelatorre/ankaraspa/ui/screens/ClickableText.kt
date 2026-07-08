package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.clickableText(onClick: () -> Unit): Modifier = this.clickable { onClick() }