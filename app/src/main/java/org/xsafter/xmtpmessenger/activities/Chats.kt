package org.xsafter.xmtpmessenger.activities

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.data.me
import org.xsafter.xmtpmessenger.ui.Routing
import org.xsafter.xmtpmessenger.ui.components.CircleBadgeAvatar
import org.xsafter.xmtpmessenger.ui.components.CircleBorderAvatar
import org.xsafter.xmtpmessenger.ui.components.SearchButton
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Routing.Main.BottomNav.Chats.Content(
    client: Client,
    users: List<User>,
    onChatClick: (user: User) -> Unit,
    onSearchClick: () -> Unit,
    navController: NavHostController,
    mainNavController: NavHostController
) {

    LazyColumn {
        item {
            SearchButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .padding(16.dp, 8.dp)
                    .fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            )
        }
        if (users.isEmpty()) {
            item {
                HintItem()
            }
        }
        itemsIndexed(users) { index, user ->
            Log.e("user",  user.username)
            ChatItem(
                user = user,
                lastMessage = user.lastMessage,
                dateTime = formatDateTime(user.lastMessageTime),
                isOnline = false,
                modifier = Modifier
                    .clickable(onClick = { onChatClick(user) })
                    .focusProperties { canFocus = true },
                navController = navController
            )
        }
    }


    Box(modifier =
     Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = {
                      mainNavController.navigate("add_contact")
            },
            modifier = Modifier
                .padding(16.dp, 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add contact",
                tint = Color.White,
            )
        }
    }
}

@Composable
fun ChatItem(
    user: User,
    lastMessage: String,
    dateTime: String,
    isOnline: Boolean = false,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val interactionSource = MutableInteractionSource()

    val scale = remember {
        Animatable(1f)
    }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Row(
        modifier = modifier
            .padding(12.dp, 8.dp)
            .fillMaxWidth()
            .scale(scale = scale.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(
                        0.95f,
                        animationSpec = tween(100),
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = tween(100),
                    )

                    navController.navigate("chat/${user.username}")
                }
            }
                ) {
                when {
                    isOnline -> {
                        CircleBadgeAvatar(
                            imageData = user.avatar,
                            size = 56.dp
                        )
                    }

                    else -> CircleBorderAvatar(
                        imageData = user.avatar,
                        size = 56.dp,
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        user.username,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.padding(2.dp))

                    ProvideTextStyle(value = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp
                    ), content = {
                        LastMessageWithDateTime(
                            user.lastMessageUser,
                            lastMessage,
                            dateTime
                        )
                    })
                }
            }
}


@Composable
fun LastMessageWithDateTime(
    name: String,
    lastMessage: String,
    dateTime: String
) {
    Row {
        Text(
            "${determineUsername(name)}: $lastMessage",
            modifier = Modifier.weight(1f, false),
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Text(" • $dateTime", maxLines = 1)
    }
}

@Composable
fun HintItem() {
    val interactionSource = MutableInteractionSource()

    val scale = remember {
        Animatable(1f)
    }

    val coroutineScope = rememberCoroutineScope()

    Text(
        text = "No chats are available. Add some people to contacts to chat with them",
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .padding(horizontal = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(
                        0.95f,
                        animationSpec = tween(100),
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = tween(100),
                    )
                }
            }
    )
}


private fun formatDateTime(date: Date): String {
    val currentTime = System.currentTimeMillis()
    val deltaTime = currentTime - date.time

    return if (deltaTime < 24 * 60 * 60 * 1000) {
        SimpleDateFormat("HH:mm").format(date)
    } else {
        SimpleDateFormat("dd/MM").format(date)
    }
}

private fun determineUsername(username: String): String {
    Log.e("username",me.username)
    if (username == me.username) {
        return "me"
    }
    return username.take(5)
}

@Preview(showBackground = true)
@Composable
fun ChatItem() {
    ChatItem(
        me,
        "Hey there",
        "17:45",
        navController = rememberNavController()
    )
}