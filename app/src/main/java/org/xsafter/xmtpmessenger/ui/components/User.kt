package org.xsafter.xmtpmessenger.ui.components


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension


@Composable
fun UserAvatarWithTitle(
    title: String,
    imageData: Any,
    imageSize: Dp = 56.dp,
    modifier: Modifier = Modifier.padding(4.dp)
) {
    ConstraintLayout(modifier = modifier) {
        val (avatar, name) = createRefs()

        val avatarModifier = Modifier.constrainAs(avatar) {
            top.linkTo(parent.top, margin = 2.dp)
            linkTo(
                start = parent.start,
                end = parent.end,
                startMargin = 2.dp,
                endMargin = 2.dp
            )
            width = Dimension.preferredWrapContent
        }

        val nameModifier = Modifier.constrainAs(name) {
            linkTo(
                top = avatar.bottom,
                bottom = parent.bottom,
                start = parent.start,
                end = parent.end,
                startMargin = 2.dp,
                endMargin = 2.dp,
                topMargin = 2.dp,
                bottomMargin = 2.dp,
            )
            width = Dimension.fillToConstraints
        }

        CircleBadgeAvatar(
            imageData = imageData,
            size = imageSize,
            modifier = avatarModifier
        )

        Text(
            text = title,
//          style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = nameModifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserAvatarWithTitlePreview() {
    UserAvatarWithTitle("Test", "")
}