package com.aquavera.aquavera.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquavera.aquavera.R
import com.aquavera.aquavera.viewmodel.LangViewModel

@Composable
fun LanguageSelectionScreen(
    langViewModel: LangViewModel,
    onLanguageSelected: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "AquaVera Logo",
                modifier = Modifier.size(120.dp)
            )
            Text(
                "AquaVera",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                "Choose Your Language",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Text(
                "आपकी भाषा चुनें",
                fontSize = 18.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            LanguageOption(
                languageName = "English",
                nativeName = "English",
                isSelected = langViewModel.currentLanguage == "en",
                onClick = { 
                    langViewModel.setLanguage("en")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LanguageOption(
                languageName = "Hindi",
                nativeName = "हिंदी",
                isSelected = langViewModel.currentLanguage == "hi",
                onClick = { 
                    langViewModel.setLanguage("hi")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LanguageOption(
                languageName = "Marathi",
                nativeName = "मराठी",
                isSelected = langViewModel.currentLanguage == "mr",
                onClick = { 
                    langViewModel.setLanguage("mr")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLanguageSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun LanguageOption(
    languageName: String,
    nativeName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color(0xFFF8F9FA)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = languageName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                )
                Text(
                    text = nativeName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            if (isSelected) {
                RadioButton(
                    selected = true,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
