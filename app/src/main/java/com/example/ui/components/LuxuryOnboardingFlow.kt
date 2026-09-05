package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryEmeraldContainer
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import com.example.ui.theme.RgbGlassBorder
import com.example.util.CallHelper
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val step: String,
    val badge: String,
    val title: String,
    val headline: String,
    val description: String,
    val quote: String,
    val icon: ImageVector,
    val accentColor: Color,
    val containerBg: Color,
    val gradientColors: List<Color>
)

@Composable
fun LuxuryOnboardingFlow(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // 5 Clean, Focused Feature Slides
    val slides = listOf(
        OnboardingSlide(
            step = "01 / 05",
            badge = "PROBLEM SOLVED: HABIT & CRM HESITATION",
            title = "THE COLD OUTREACH ENGINE",
            headline = "Stop Scrolling Feeds.\nStart Closing Real Deals.",
            description = "High-value business relationships don't grow in endless social scrolling. B2B Cold was built to kill hesitation, eliminate CRM friction, and turn free minutes into direct executive conversations.",
            quote = "\"Every free minute spent scrolling is a missed executive deal.\"",
            icon = Icons.Default.TrendingUp,
            accentColor = Color(0xFFD97706),
            containerBg = Color(0xFFFEF3C7),
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
        ),
        OnboardingSlide(
            step = "02 / 05",
            badge = "PROBLEM SOLVED: CRM FATIGUE",
            title = "ZERO-FRICTION CALLING",
            headline = "Who To Call Today?\nLet The 3D Deck Decide.",
            description = "Spreadsheets and clunky CRMs cause hesitation. B2B Cold serves exactly one curated executive card at a time with 10-card physics so you execute without friction.",
            quote = "\"No hesitation. No second-guessing. Just pure daily momentum.\"",
            icon = Icons.Default.SwapHoriz,
            accentColor = Color(0xFF059669),
            containerBg = Color(0xFFD1FAE5),
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669))
        ),
        OnboardingSlide(
            step = "03 / 05",
            badge = "REAL CALL LOG INTELLIGENCE",
            title = "AUTOMATIC TOUCHPOINTS",
            headline = "Live Call History.\nNever Call Blind Again.",
            description = "The engine checks your phone's call history directly. Instantly see whether you spoke 'Today', 'Yesterday', or '5 days ago' without ever having to manually type call logs.",
            quote = "\"Know exactly when you last spoke before you even tap call.\"",
            icon = Icons.Default.PhoneInTalk,
            accentColor = Color(0xFF2563EB),
            containerBg = Color(0xFFDBEAFE),
            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
        ),
        OnboardingSlide(
            step = "04 / 05",
            badge = "AUDIO BRAIN RECALL",
            title = "EXECUTIVE VOICE NOTES",
            headline = "5-Second Voice Notes.\nInstant Context Recall.",
            description = "Record quick voice memos right on the client's card after a call. Listen to previous deal terms, family notes, and pricing before saying hello again.",
            quote = "\"Instant context creates effortless executive rapport.\"",
            icon = Icons.Default.Mic,
            accentColor = Color(0xFF7C3AED),
            containerBg = Color(0xFFEDE9FE),
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
        ),
        OnboardingSlide(
            step = "05 / 05",
            badge = "PIPELINE ROTATION & GOALS",
            title = "SNOOZE & SMART FILTERS",
            headline = "Snooze For 3 Days.\nFilter Selected Contacts.",
            description = "Snooze clients who need space, filter Selected vs Unselected contacts in Settings, customize card deck swipe physics, and hit your daily calling commitment.",
            quote = "\"Consistency is the only non-depreciating business asset.\"",
            icon = Icons.Default.RocketLaunch,
            accentColor = Color(0xFFE11D48),
            containerBg = Color(0xFFFFE4E6),
            gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFBE123C))
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val currentSlide = slides[pagerState.currentPage]

    // Uniform, full-screen edge-to-edge gradient background (NO blurry bounding boxes or clipping)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        currentSlide.containerBg.copy(alpha = 0.55f),
                        Color(0xFFF8FAFC),
                        Color.White
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("luxury_onboarding_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: Step pill & Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = currentSlide.containerBg,
                    border = BorderStroke(1.dp, currentSlide.accentColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = currentSlide.step,
                        color = currentSlide.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
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

            // PAGER AREA
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val slide = slides[page]
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Vibrant Icon Orb
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(slide.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = slide.containerBg,
                        border = BorderStroke(1.dp, slide.accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = slide.badge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = slide.accentColor,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = slide.headline,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = LuxuryTextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = slide.description,
                        fontSize = 13.sp,
                        color = LuxuryTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // VIP Quote Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = slide.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = slide.quote,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // BOTTOM CONTROLS: Dots Indicator & Next / Get Started Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    slides.indices.forEach { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) currentSlide.accentColor else Color(0xFFCBD5E1)
                                )
                        )
                    }
                }

                // Next or Get Started Button
                val isLastPage = pagerState.currentPage == slides.size - 1
                Button(
                    onClick = {
                        if (isLastPage) {
                            onComplete()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag(if (isLastPage) "onboarding_launch_button" else "onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentSlide.accentColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Enter $10M Rolodex Deck" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isLastPage) Icons.Default.RocketLaunch else Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
