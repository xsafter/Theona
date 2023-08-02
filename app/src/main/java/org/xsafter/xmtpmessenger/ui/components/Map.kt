package org.xsafter.xmtpmessenger.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.osmdroid.views.MapView
import org.xsafter.xmtpmessenger.data.GeoMessageWrapper
import org.xsafter.xmtpmessenger.rememberMapViewWithLifecycle
import org.xsafter.xmtpmessenger.ui.Routing

@Composable
fun Routing.Main.BottomNav.Map.Content(
    modifier: Modifier = Modifier,
    geoMessages: MutableList<GeoMessageWrapper>,
    onUserClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    onLoad: ((map: MapView) -> Unit)? = null,
    navController: NavHostController
) {
    val geoMessages by remember { mutableStateOf(geoMessages) }

    val mapViewState = rememberMapViewWithLifecycle(geoMessages)

    AndroidView(
        { mapViewState },
        modifier
    ) { mapView -> onLoad?.invoke(mapView) }
}

@Preview
@Composable
fun MapPreview() {
    Routing.Main.BottomNav.Map.Content(
        geoMessages = mutableListOf(),
        navController = rememberNavController()
    )
}