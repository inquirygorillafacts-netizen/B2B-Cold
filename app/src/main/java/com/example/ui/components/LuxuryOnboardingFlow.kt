package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightGlassBorderStroke
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryEmeraldContainer
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldBg
import com.example.ui.theme.LuxuryPurple
import com.example.ui.theme.LuxuryPurpleContainer
import com.example.ui.theme.LuxuryRose
import com.example.ui.theme.LuxuryRoseContainer
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import kotlinx.coroutines.launch

data class OnboardingSlideData(
    val step: String,
    val title: String,
    val headline: String,
    val description: String,
    val quote: String,
    val icon: ImageVector,
    val accentColor: Color,
    val containerBg: Color,
    val gradientColors: List<Color>,
    val badgeLabel: String
)

@Composable
fun LuxuryOnboardingFlow(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 5 Vibrant Rich-Tone Slides on Clean White Canvas (High-End Professional Mobile Experience)
    val slides = listOf(
        OnboardingSlideData(
            step = "01 / 05",
            title = "THE HABIT KILLER",
            headline = "Stop Scrolling Feeds.\nStart Closing Real Deals.",
            description = "High-value business relationships don't grow in endless social scrolling. Your free minutes are your hidden revenue pipeline.",
            quote = "\"Every free minute spent scrolling is a missed $100K executive conversation.\"",
            icon = Icons.Default.TrendingUp,
            accentColor = Color(0xFFD97706), // Amber Gold
            containerBg = Color(0xFFFEF3C7),
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
            badgeLabel = "THE $10M PARADIGM"
        ),
        OnboardingSlideData(
            step = "02 / 05",
            title = "ZERO THINKING CALLING",
            headline = "Who To Call Today?\nLet The Deck Decide.",
            description = "Eliminate decision fatigue completely. No CRM searching or spreadsheet friction. One curated client card appears at a time.",
            quote = "\"No hesitation. No second-guessing. Just pure daily momentum.\"",
            icon = Icons.Default.SwapHoriz,
            accentColor = Color(0xFF059669), // Vibrant Emerald
            containerBg = Color(0xFFD1FAE5),
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
            badgeLabel = "FRICTIONLESS ROTATION"
        ),
        OnboardingSlideData(
            step = "03 / 05",
            title = "THE AUDIO MEMORY",
            headline = "5-Second Voice Notes\nRefresh Context Instantly.",
            description = "Listen to your past audio memories before dialing. Re-capture their deal terms, priorities, and personal details before they say hello.",
            quote = "\"Instant context creates effortless executive rapport.\"",
            icon = Icons.Default.Mic,
            accentColor = Color(0xFF2563EB), // Royal Sapphire Blue
            containerBg = Color(0xFFDBEAFE),
            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
            badgeLabel = "AUDIO BRAIN RECALL"
        ),
        OnboardingSlideData(
            step = "04 / 05",
            title = "THE COMPOUND EFFECT",
            headline = "Just 5 Calls A Day =\n150 Relationships A Month.",
            description = "Consistency crushes cold outreach. Reach out to 5 existing VIP clients, partners, and key prospects each day effortlessly.",
            quote = "\"Strong relationships are the only non-depreciating business asset.\"",
            icon = Icons.Default.PhoneInTalk,
            accentColor = Color(0xFF7C3AED), // Vivid Royal Violet
            containerBg = Color(0xFFEDE9FE),
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
            badgeLabel = "DAILY DISCIPLINE"
        ),
        OnboardingSlideData(
            step = "05 / 05",
            title = "THE $10M CLIENT DECK",
            headline = "Swipe To Connect.\nYour Network Is Ready.",
            description = "Tactile physics, rich typography, and direct one-tap calling. Welcome to your ultra-focused executive deck.",
            quote = "\"Your high-value rolodex, distilled into pure daily action.\"",
            icon = Icons.Default.RocketLaunch,
            accentColor = Color(0xFFE11D48), // Electric Rose Crimson
            containerBg = Color(0xFFFFE4E6),
            gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFBE123C)),
            badgeLabel = "READY TO LAUNCH"
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()
    val currentSlide = slides[pagerState.currentPage]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("luxury_onboarding_container")
    ) {
        // Vibrant Ambient Background Glow Orbs tailored to each slide's RGB color
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .blur(90.dp)
                .background(currentSlide.accentColor.copy(alpha = 0.14f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart)
                .blur(90.dp)
                .background(currentSlide.accentColor.copy(alpha = 0.10f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Step pill & Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = currentSlide.containerBg,
                    border = BorderStroke(1.dp, currentSlide.accentColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = currentSlide.step,
                        color = currentSlide.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                if (pagerState.currentPage < slides.size - 1) {
                    TextButton(
                        onClick = onComplete,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip To Deck",
                            color = LuxuryTextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Slide Page Slider
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Vibrant Gradient Icon Orb with dynamic colorful glow
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(slide.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = slide.title,
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Badge Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = slide.containerBg,
                        border = BorderStroke(1.dp, slide.accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = slide.badgeLabel,
                            color = slide.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Big Bold Headline
                    Text(
                        text = slide.headline,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            lineHeight = 36.sp
                        ),
                        color = LuxuryTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clear Description
                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontSize = 15.sp
                        ),
                        color = LuxuryTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Quote Card with colorful accent border
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, slide.accentColor.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = slide.quote,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 19.sp,
                                fontSize = 13.sp
                            ),
                            color = slide.accentColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                }
            }

            // Bottom Navigation & Dynamic Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(slides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(7.dp)
                                .width(if (isSelected) 28.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) currentSlide.accentColor else Color(0xFFCBD5E1)
                                )
                        )
                    }
                }

                // CTA Button (Next or Final Enter)
                if (pagerState.currentPage == slides.size - 1) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("onboarding_enter_deck_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(currentSlide.gradientColors),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ENTER $10M CLIENT DECK",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(currentSlide.gradientColors),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Continue",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
