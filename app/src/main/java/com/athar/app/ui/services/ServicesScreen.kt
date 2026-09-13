package com.athar.app.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.athar.app.ui.theme.*

data class ServiceItem(val name: String, val icon: ImageVector, val color: Color)

val mainServices = listOf(
    ServiceItem("القرآن الكريم", Icons.Default.Book, AtharGreen),
    ServiceItem("موسوعة الكتب", Icons.Default.List, AtharGold)
)

val tools = listOf(
    ServiceItem("الأجندة", Icons.Default.List, AtharRed),
    ServiceItem("التقويم الإسلامي", Icons.Default.List, AtharBlue),
    ServiceItem("تسبيح", Icons.Default.List, AtharPurple)
)

@Composable
fun ServicesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBlack)
            .padding(16.dp)
    ) {
        Text(
            "ركن المسلم",
            color = Color.White,
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main 2 Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            mainServices.forEach { service ->
                ServiceCard(service, modifier = Modifier.weight(1f).height(120.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "أدوات",
            color = Color.Gray,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            tools.forEach { tool ->
                ServiceCard(tool, modifier = Modifier.weight(1f).height(100.dp), small = true)
            }
        }
    }
}

@Composable
fun ServiceCard(service: ServiceItem, modifier: Modifier = Modifier, small: Boolean = false) {
    Surface(
        modifier = modifier,
        color = AtharCardGray,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(if (small) 32.dp else 48.dp)
                    .background(service.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(service.icon, contentDescription = null, tint = service.color, modifier = Modifier.size(if (small) 20.dp else 24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                service.name,
                color = Color.White,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = if (small) 12.sp else 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    AtharTheme {
        ServicesScreen()
    }
}
