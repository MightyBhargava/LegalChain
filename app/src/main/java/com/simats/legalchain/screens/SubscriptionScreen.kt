package com.simats.legalchain.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.*

private val DarkGreen = Color(0xFF004D40)
private val SoftGreen = Color(0xFFE8F5F1)
private val ScreenBg = Color(0xFFF9FBFA)

@Composable
fun SubscriptionScreen(
    onSubscribed: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var billingClient by remember { mutableStateOf<BillingClient?>(null) }
    var productDetails by remember { mutableStateOf<ProductDetails?>(null) }
    var loading by remember { mutableStateOf(true) }

    val subscriptionId = "legalchain_premium_subscription"

    /* ---------- BILLING ---------- */
    LaunchedEffect(Unit) {
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener { result, purchases ->
                when (result.responseCode) {

                    BillingClient.BillingResponseCode.OK -> {
                        purchases?.forEach { purchase ->
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                if (!purchase.isAcknowledged) {
                                    billingClient?.acknowledgePurchase(
                                        AcknowledgePurchaseParams.newBuilder()
                                            .setPurchaseToken(purchase.purchaseToken)
                                            .build()
                                    ) {
                                        saveSubscription(context)
                                        onSubscribed()
                                    }
                                } else {
                                    saveSubscription(context)
                                    onSubscribed()
                                }
                            }
                        }
                    }

                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        Toast.makeText(context, "Purchase canceled", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(
                            context,
                            "Purchase failed: ${result.debugMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val params = QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            listOf(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(subscriptionId)
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        ).build()

                    billingClient?.queryProductDetailsAsync(params) { _, list ->
                        productDetails = list.firstOrNull()
                        loading = false
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                loading = false
            }
        })
    }

    /* ---------- UI ---------- */
    Scaffold(
        containerColor = ScreenBg,

        /* ---- SKIP ---- */
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TextButton(onClick = onSkip) {
                    Text("Skip", color = Color.Gray)
                }
            }
        },

        /* ---- CTA ---- */
        bottomBar = {
            Surface(shadowElevation = 12.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = DarkGreen
                        )
                    } else {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkGreen,
                                contentColor = Color.White
                            ),
                            onClick = {
                                if (billingClient == null) {
                                    Toast.makeText(context, "Billing not ready. Please try again.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (productDetails == null) {
                                    Toast.makeText(context, "Subscription is loading. Please wait.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val offer = productDetails?.subscriptionOfferDetails?.firstOrNull()
                                if (offer == null) {
                                    Toast.makeText(context, "No subscription plan available.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (activity == null) {
                                    Toast.makeText(context, "Unable to start billing flow.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                Toast.makeText(context, "Opening Google Play…", Toast.LENGTH_SHORT).show()

                                val params = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(
                                        listOf(
                                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(productDetails!!)
                                                .setOfferToken(offer.offerToken)
                                                .build()
                                        )
                                    )
                                    .build()

                                billingClient?.launchBillingFlow(activity, params)
                            }
                        ) {
                            Text("Upgrade to Premium", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            /* ---------- HERO ---------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkGreen, Color(0xFF00695C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "LegalChain Premium",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "₹100 / month",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Billed monthly · Cancel anytime",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Advanced tools for modern legal professionals",
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Premium includes",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            )

            Spacer(Modifier.height(14.dp))

            AnimatedPremiumCard(
                delay = 0,
                icon = Icons.Default.AutoAwesome,
                title = "AI-powered case insights",
                description = "Smart summaries, predictions, and timelines"
            )

            AnimatedPremiumCard(
                delay = 150,
                icon = Icons.Default.Description,
                title = "OCR & intelligent documents",
                description = "Scan, search, and organize legal files"
            )

            AnimatedPremiumCard(
                delay = 300,
                icon = Icons.Default.Security,
                title = "Enterprise-grade security",
                description = "Encrypted storage with secure cloud backup"
            )
        }
    }
}

/* ---------- ANIMATED CARD ---------- */
@Composable
private fun AnimatedPremiumCard(
    delay: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SoftGreen),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = DarkGreen)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

/* ---------- SAVE PREMIUM ---------- */
private fun saveSubscription(context: Context) {
    context.getSharedPreferences("subscription_prefs", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("is_premium_user", true)
        .apply()

    Toast.makeText(context, "Premium Activated!", Toast.LENGTH_LONG).show()
}
