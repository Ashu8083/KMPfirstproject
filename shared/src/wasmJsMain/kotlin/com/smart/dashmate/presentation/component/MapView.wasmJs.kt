package com.smart.dashmate.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.viewinterop.HtmlElementView
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint
import kotlinx.browser.document
import org.w3c.dom.HTMLIFrameElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun RiderMap(
    modifier: Modifier,
    riderLocation: Location,
    destinationLocation: Location?,
    route: List<RoutePoint>
) {

    println("RiderMap route size = ${route.size}")

    route.take(3).forEach {
        println(
            "Route point = ${it.latitude}, ${it.longitude}"
        )
    }

    val routeCoordinates = route.joinToString(
        separator = ",",
        prefix = "[",
        postfix = "]"
    ) {
        "[${it.latitude}, ${it.longitude}]"
    }

    val destinationScript = destinationLocation?.let {

        """
        L.marker([${it.latitude}, ${it.longitude}])
            .addTo(map)
            .bindPopup("Destination");
        """

    } ?: ""

    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">

            <link
                rel="stylesheet"
                href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
            />

            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                }

                #map {
                    width: 100%;
                    height: 100%;
                }
            </style>
        </head>

        <body>

            <div id="map"></div>

            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

            <script>

                const rider = [
                    ${riderLocation.latitude},
                    ${riderLocation.longitude}
                ];

                const route = $routeCoordinates;

                const map = L.map('map');

                L.tileLayer(
                    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                    {
                        maxZoom: 19,
                        attribution: '&copy; OpenStreetMap contributors'
                    }
                ).addTo(map);

                // Rider
                L.marker(rider)
                    .addTo(map)
                    .bindPopup("Rider / Start");

                // Destination
                $destinationScript

                // Route
                if (route.length > 1) {

                    console.log("Route points:", route.length);

                    const polyline = L.polyline(
                        route,
                        {
                            color: 'blue',
                            weight: 5,
                            opacity: 0.8
                        }
                    ).addTo(map);

                    map.fitBounds(
                        polyline.getBounds(),
                        {
                            padding: [30, 30]
                        }
                    );

                } else {

                    console.log("No route available");

                    map.setView(rider, 15);
                }

            </script>

        </body>
        </html>
    """.trimIndent()

    HtmlElementView(
        modifier = modifier,

        factory = {
            (document.createElement("iframe") as HTMLIFrameElement).apply {

                style.border = "none"

                srcdoc = html
            }
        },

        update = { iframe ->

            iframe.srcdoc = html
        }
    )
}