package com.example

import com.example.model.CardAnimationStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CardAnimationStyleTest {

    @Test
    fun testAllTenAnimationStylesExistAndUnique() {
        val styles = CardAnimationStyle.values()
        assertEquals(10, styles.size)

        val uniqueTitles = styles.map { it.title }.toSet()
        assertEquals("Each animation style must have a unique title", 10, uniqueTitles.size)

        styles.forEach { style ->
            assertNotNull(style.title)
            assertNotNull(style.subtitle)
            assertNotNull(style.iconName)
        }
    }
}
