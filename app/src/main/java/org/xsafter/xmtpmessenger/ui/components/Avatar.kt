package org.xsafter.xmtpmessenger.ui.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.coil.CoilImage
import org.xsafter.xmtpmessenger.R


@Composable
fun CircleBorderAvatar(
    imageData: Any,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    borderInnerPadding: Dp = 5.dp
) {
    var boxModifier = modifier.size(size)
    if(border != null) {
        boxModifier = boxModifier
            .border(border, CircleShape)
            .padding(borderInnerPadding)
    }

    Box(boxModifier) {
        CircleImage(imageData)
    }
}

@Composable
fun CircleBadgeAvatar(
    imageData: Any,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier,
    showBadge: Boolean = true,
    badgeColor: Color = Color.Green,
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.background
) {
    Box(
        modifier = modifier.size(size)
    ) {
        CircleImage(
            imageData,
            modifier = Modifier.align(Alignment.Center)
        )

        if(showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size / 3)
                    .background(badgeBackgroundColor, CircleShape)
                    .padding(4.dp)
                    .background(badgeColor, CircleShape)
            )
        }
    }
}

@Composable
fun CircleImage(
    imageData: Any,
    modifier: Modifier = Modifier
) {
    CoilImage(
        data = imageData,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        fadeIn = true
    )
}

@Preview(showBackground = true)
@Composable
fun CircleBorderAvatarPreview() {
    val picture = painterResource(id = R.drawable.ic_launcher_background)

    CircleBorderAvatar(
        picture,
        border = BorderStroke(2.dp, Color.Blue)
    )
}

@Preview(showBackground = true)
@Composable
fun CircleBadgeAvatarPreview() {
    val picture = painterResource(id = R.drawable.ic_launcher_background)
    CircleBadgeAvatar(picture)
}