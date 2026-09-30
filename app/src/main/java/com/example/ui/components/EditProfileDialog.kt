package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.GlassBorderMint
import com.example.ui.theme.GlassBorderRefractionBrush
import com.example.ui.theme.PastelBluePrimary
import com.example.ui.theme.PastelMintLight
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VazirmatnFontFamily

/**
 * Modern Frosted Glass Dialog for editing user profile and picking custom avatar image
 * Allows selecting from device gallery, picking from realistic artisan portraits, or selecting stamps.
 */
@Composable
fun EditProfileDialog(
    initialName: String,
    initialShopTitle: String,
    initialBio: String,
    initialLocation: String,
    initialPhone: String,
    initialAvatarUri: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, shopTitle: String, bio: String, location: String, phone: String, avatarUri: String?) -> Unit,
    onAvatarSelected: (uri: String?) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialName) }
    var shopTitle by remember { mutableStateOf(initialShopTitle) }
    var bio by remember { mutableStateOf(initialBio) }
    var location by remember { mutableStateOf(initialLocation) }
    var phone by remember { mutableStateOf(initialPhone) }
    var currentAvatarUri by remember { mutableStateOf(initialAvatarUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentAvatarUri = uri.toString()
            onAvatarSelected(uri.toString())
            Toast.makeText(context, "تصویر نمایه انتخاب شد", Toast.LENGTH_SHORT).show()
        }
    }

    // High quality curated Iranian artisan / producer portrait photo samples
    val curatedArtisanPhotos = listOf(
        "https://images.unsplash.com/photo-1544717305-2782549b5136?w=300&fit=crop&q=80" to "قناد سنتی",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&fit=crop&q=80" to "نانوا و سرآشپز",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=300&fit=crop&q=80" to "عطاری و گیاهی",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&fit=crop&q=80" to "زنبوردار کوهستان",
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=300&fit=crop&q=80" to "هنرمند سفال و صنایع",
        "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&fit=crop&q=80" to "باغدار و مزارع گلاب"
    )

    val presetStickers = listOf(
        "👩🏻‍🍳" to "قناد",
        "👨🏻‍🌾" to "باغدار",
        "🐝" to "زنبوردار",
        "🍯" to "عسل",
        "🌿" to "عطاری",
        "🧁" to "شیرینی",
        "🌾" to "برنج‌کار",
        "🧵" to "صنایع",
        "💼" to "کاسب"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x330D9488))
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xF2FFFFFF),
                                Color(0xEBF0FDF4),
                                Color(0xEBE0F2FE)
                            )
                        )
                    )
                    .border(1.2.dp, GlassBorderRefractionBrush, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ویرایش نمایه و تصویر غرفه",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = TextMuted
                            )
                        }
                    }

                    // Avatar with Camera / Photo Picker overlay
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        MerchantAvatar(
                            name = name.ifBlank { "کاسب" },
                            size = 92.dp,
                            avatarUri = currentAvatarUri
                        )

                        // Camera icon badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "تغییر عکس",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Photo Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x66CCFBF1))
                                .border(1.dp, Color(0x6634D399), RoundedCornerShape(12.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = PastelMintPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "انتخاب از گالری",
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelMintPrimary
                                )
                            }
                        }

                        if (currentAvatarUri != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x33DC2626))
                                    .border(1.dp, Color(0x40DC2626), RoundedCornerShape(12.dp))
                                    .clickable {
                                        currentAvatarUri = null
                                        onAvatarSelected(null)
                                        Toast.makeText(context, "تصویر حذف شد", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف عکس",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Curated Realistic Artisan Portrait Avatars
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "تصاویر آماده پرتره و کارگاه کاسبان معتمد:",
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(curatedArtisanPhotos) { (url, label) ->
                                val isSelected = currentAvatarUri == url
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        currentAvatarUri = url
                                        onAvatarSelected(url)
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) PastelMintPrimary else Color(0x66BAE6FD),
                                                shape = CircleShape
                                            )
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = label,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 9.sp,
                                        color = if (isSelected) PastelMintPrimary else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Quick Traditional Artisan Avatar Stamps
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "یا انتخاب نشان سنتی فعالیت:",
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(presetStickers) { (emoji, label) ->
                                val isSelected = currentAvatarUri == "emoji:$emoji"
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFD1FAE5) else Color(0x33E0F2FE))
                                        .border(
                                            1.5.dp,
                                            if (isSelected) Color(0xFF059669) else Color(0x330284C7),
                                            CircleShape
                                        )
                                        .clickable {
                                            currentAvatarUri = "emoji:$emoji"
                                            onAvatarSelected("emoji:$emoji")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    // Text Fields
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام و نام‌خانوادگی", fontFamily = VazirmatnFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelMintPrimary,
                            unfocusedBorderColor = Color(0x6634D399)
                        )
                    )

                    OutlinedTextField(
                        value = shopTitle,
                        onValueChange = { shopTitle = it },
                        label = { Text("عنوان غرفه یا فعالیت (مثال: کارگاه قنادی خانگی)", fontFamily = VazirmatnFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelMintPrimary,
                            unfocusedBorderColor = Color(0x6634D399)
                        )
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("شهر و استان", fontFamily = VazirmatnFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelMintPrimary,
                            unfocusedBorderColor = Color(0x6634D399)
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("شماره تماس", fontFamily = VazirmatnFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelMintPrimary,
                            unfocusedBorderColor = Color(0x6634D399)
                        )
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("داستان کیفیت و معرفی دسترنج", fontFamily = VazirmatnFontFamily) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelMintPrimary,
                            unfocusedBorderColor = Color(0x6634D399)
                        )
                    )

                    // Save Button
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "نام نمی‌تواند خالی باشد", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            onSave(name, shopTitle, bio, location, phone, currentAvatarUri)
                            Toast.makeText(context, "اطلاعات نمایه و تصویر با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text(
                            text = "ذخیره تغییرات نمایه",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    }
}
