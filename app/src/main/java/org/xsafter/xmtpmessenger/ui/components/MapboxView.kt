package org.xsafter.xmtpmessenger.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.xsafter.xmtpmessenger.data.model.GeoMessage
import org.xsafter.xmtpmessenger.data.model.GeoMessageWrapper
import org.xsafter.xmtpmessenger.data.model.User
import java.util.Date
import kotlin.math.min



@Composable
fun Map(
    lat: Double,
    lng: Double,
    zoom: Double,
    geoMessageFlow: Flow<List<GeoMessageWrapper>>
) {
    val map = rememberMapboxViewWithLifecycle()
    val geoMessages by geoMessageFlow.collectAsState(initial = emptyList())

    MapboxMapContainer(map = map, lat = lat, lng = lng, zoom = zoom, geoMessages)
}


@Preview
@Composable
fun MapboxPreview() {
    Map(0.0, 0.0, 12.0,  flowOf(listOf(
        GeoMessageWrapper(GeoMessage(0.0, 0.0),
            User("0x0", "0x0", createFromObject("0x0"),
                "", "", Date())),
        GeoMessageWrapper(GeoMessage(1.0, 1.0),
            User("0x1", "0x0", createFromObject("0x0"),
                "", "", Date())))))
}

@Composable
fun MapboxMap(lat: Double, lng: Double, zoom: Double, bitmap: Bitmap) {
    val map = rememberMapboxViewWithLifecycle()

    //MapboxMapContainer(map = map, lat = lat, lng = lng, zoom = zoom, null)
}


@Composable
fun MapboxMapContainer(
    map: MapView,
    lat: Double,
    lng: Double,
    zoom: Double,
    geoMessages: List<GeoMessageWrapper>
) {
    val (isMapInitialized, setMapInitialized) = remember(map) { mutableStateOf(false) }

    LaunchedEffect(map, isMapInitialized, geoMessages) {
        if (!isMapInitialized) {
            val mbxMap = map.getMapboxMap()

            mbxMap.loadStyleUri(Style.OUTDOORS) {
                mbxMap.centerTo(lat = lat, lng = lng, zoom = zoom)

                Style.OnStyleLoaded {
                    map.location.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }
                }
                val annotationApi = map?.annotations
                val pointAnnotationManager = annotationApi?.createPointAnnotationManager()

                geoMessages.forEach { geoMessageWrapper ->
                    val avatar = createFromObject(geoMessageWrapper.user.id)
                    val testBitmap = createPinBitmap(avatar)

                    val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(geoMessageWrapper.geoMessage.latitude, geoMessageWrapper.geoMessage.longitude))
                        .withIconImage(testBitmap)

                    pointAnnotationManager?.create(pointAnnotationOptions)
                }

                setMapInitialized(true)
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    AndroidView(factory = { map }) {
        coroutineScope.launch {
            val mbxMap = it.getMapboxMap()

            mbxMap.centerTo(lat = lat, lng = lng, zoom = zoom)
        }
    }
}



@Composable
private fun rememberMapboxViewWithLifecycle(): MapView {
    val context = LocalContext.current

    val opt = MapInitOptions(context, antialiasingSampleCount = 4,)
    val map = remember { MapView(context, opt) }

    val observer = rememberMapboxViewLifecycleObserver(map)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return map
}

@Composable
private fun rememberMapboxViewLifecycleObserver(map: MapView): LifecycleEventObserver {
    return remember(map) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> map.onStart()
                Lifecycle.Event.ON_STOP -> map.onStop()
                Lifecycle.Event.ON_DESTROY -> map.onDestroy()
                else -> Unit // nop
            }
        }
    }
}


fun MapboxMap.centerTo(lat: Double, lng: Double, zoom: Double) {
    val point = Point.fromLngLat(lng, lat)

    val camera = CameraOptions.Builder()
        .center(point)
        .zoom(zoom)
        .build()

    setCamera(camera)
}

private fun createPinBitmap(srcBitmap: Bitmap?): Bitmap {
    val doubleSize = srcBitmap!!.width * 2
    val resizedBitmap = Bitmap.createScaledBitmap(srcBitmap, doubleSize, doubleSize, false)

    val squareBitmapWidth = min(resizedBitmap.width, resizedBitmap.height)
    val dstBitmap = Bitmap.createBitmap(
        squareBitmapWidth,
        squareBitmapWidth,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(dstBitmap)
    val paint = Paint()
    paint.isAntiAlias = true
    val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
    val rectF = RectF(rect)
    canvas.drawOval(rectF, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val left = ((squareBitmapWidth - resizedBitmap.width) / 2).toFloat()
    val top = ((squareBitmapWidth - resizedBitmap.height) / 2).toFloat()
    canvas.drawBitmap(resizedBitmap, left, top, paint)
    resizedBitmap.recycle()
    return dstBitmap
}
