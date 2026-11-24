package com.example.mindcheck.data

/** A place a student can go for support. [phone] is null when there is no number to call. */
data class HelpResource(
    val emoji: String,
    val name: String,
    val summary: String,
    val description: String,
    val hours: String,
    val phone: String? = null,
)

/**
 * Support options shown on the Help screen.
 * Check every number is still correct before a demo or release.
 */
object HelpResources {
    val all: List<HelpResource> = listOf(
        HelpResource(
            emoji = "🚨",
            name = "Emergency services",
            summary = "If you or someone else is in danger right now",
            description = "Call 999 or 112 for police, ambulance or fire services in Kenya. " +
                "Use this if you are thinking of harming yourself or someone is at risk right now.",
            hours = "24 hours, every day",
            phone = "999",
        ),
        HelpResource(
            emoji = "📞",
            name = "Kenya Red Cross helpline",
            summary = "Free counselling and psychosocial support",
            description = "A free, toll-free helpline. Trained counsellors offer psychological first aid " +
                "and can point you to further support.",
            hours = "24 hours, every day",
            phone = "1199",
        ),
        HelpResource(
            emoji = "💬",
            name = "Befrienders Kenya",
            summary = "Emotional support if you are struggling",
            description = "Confidential emotional support for anyone feeling distressed, " +
                "overwhelmed or having thoughts of suicide. You can talk without being judged.",
            hours = "Check current hours when calling",
            phone = "+254722178177",
        ),
        HelpResource(
            emoji = "🎓",
            name = "Student counselling",
            summary = "Free, confidential counselling on campus",
            description = "The university counselling service offers free, confidential sessions " +
                "with professional counsellors. Book through Student Affairs or the student portal.",
            hours = "Weekdays during term",
        ),
        HelpResource(
            emoji = "🌱",
            name = "Looking after yourself",
            summary = "Small steps that help with mood and stress",
            description = "• Keep a regular sleep time and limit screens before bed\n" +
                "• Move your body every day, even a short walk\n" +
                "• Talk to a friend or family member you trust\n" +
                "• Break big tasks into smaller steps\n" +
                "• Cut down on alcohol and other substances",
            hours = "Any time",
        ),
    )
}
