package com.example.model

enum class CardAnimationStyle(
    val title: String,
    val subtitle: String,
    val iconName: String
) {
    LIQUID_GLASS_STACK(
        title = "Liquid Glass Stack",
        subtitle = "Fluid frosted layers with depth blur & elevation",
        iconName = "Layers"
    ),
    IOS_TINDER_SNAP_3D(
        title = "iOS Tinder Snap 3D",
        subtitle = "Snappy rotation with spring velocity & bounce",
        iconName = "Swipe"
    ),
    CUBE_3D_ROTATION(
        title = "Cube 3D Rotation",
        subtitle = "Perspective 3D cube face roll on exit",
        iconName = "ViewInAr"
    ),
    DEPTH_FLIP(
        title = "Depth Flip",
        subtitle = "Smooth horizontal 180° card perspective turnover",
        iconName = "Flip"
    ),
    FLY_OUT_PHYSICS_SPRING(
        title = "Fly-Out Physics Spring",
        subtitle = "High-tension spring sling with momentum release",
        iconName = "RocketLaunch"
    ),
    FADE_SCALE_MORPH(
        title = "Fade & Scale Morph",
        subtitle = "Cinematic zoom-out dissolution with soft glow",
        iconName = "BlurOn"
    ),
    VELVET_SLIDE_REVEAL(
        title = "Velvet Slide & Reveal",
        subtitle = "Silky smooth horizontal glide revealing next card",
        iconName = "AutoAwesome"
    ),
    CARD_PEEL_EFFECT(
        title = "Card Peel Effect",
        subtitle = "Curved page corner peel with shadow gradient",
        iconName = "ContentCopy"
    ),
    PARALLAX_HOVER_GLIDE(
        title = "Parallax Hover Glide",
        subtitle = "Multi-plane floating illusion with gyro tilt",
        iconName = "Speed"
    ),
    SPRING_BOUNCY_SNAP(
        title = "Spring Bouncy Snap",
        subtitle = "Playful elastic rubber-band overshoot physics",
        iconName = "Animation"
    )
}
