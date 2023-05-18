package org.xsafter.xmtpmessenger

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.MapView

/**
 * A composable Google Map.
 * @author Arnau Mora
 * @since 2021.12.30
 * @param modifier Modifiers to apply to the map.
 * @param onLoad This will get called once the map has been loaded.
 */
@Composable
fun MapView(
    modifier: Modifier = Modifier,
    geoMessage: GeoMessage,
    onLoad: ((map: MapView) -> Unit)? = null
) {
    val mapViewState = rememberMapViewWithLifecycle(geoMessage)

    AndroidView(
        { mapViewState },
        modifier
    ) { mapView -> onLoad?.invoke(mapView) }
}