@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.smart.dashmate.map

import kotlin.js.JsAny
import kotlin.js.JsModule
import kotlin.js.JsName

@JsModule("mapbox-gl")
@JsName("default")
external val mapboxgl: JsAny

private fun setAccessToken(mapboxGlObj: JsAny, token: String): Unit = js("""
    {
        mapboxGlObj.accessToken = token;
    }
""")

private fun createMapInstance(
    mapboxGlObj: JsAny,
    container: String,
    style: String,
    lon: Double,
    lat: Double,
    zoom: Double,
    pitch: Double,
    bearing: Double
): JsAny = js("""
    {
        const map = new mapboxGlObj.Map({
            container: container,
            style: style,
            center: [lon, lat],
            zoom: zoom,
            pitch: pitch,
            bearing: bearing,
            antialias: true
        });

        map._dashmateStyleMode = 'DAY';

        // Add navigation & pitch controls to top right
        try {
            const nav = new mapboxGlObj.NavigationControl({ visualizePitch: true });
            map.addControl(nav, 'top-right');
        } catch(e) {}

        const setup3dBuildingsAndLighting = function() {
            if (!map || !map.getStyle || !map.getStyle()) return;

            // Remove existing 3d-buildings layer if present to refresh paint props
            if (map.getLayer('3d-buildings')) {
                try { map.removeLayer('3d-buildings'); } catch(e) {}
            }

            const layers = map.getStyle().layers;
            let labelLayerId;
            if (layers) {
                for (let i = 0; i < layers.length; i++) {
                    if (layers[i].type === 'symbol' && layers[i].layout && layers[i].layout['text-field']) {
                        labelLayerId = layers[i].id;
                        break;
                    }
                }
            }

            const mode = map._dashmateStyleMode || 'DAY';
            let buildingColor = '#d1d5db';
            let opacity = 0.75;
            if (mode === 'NIGHT') {
                buildingColor = '#2b3548';
                opacity = 0.85;
            } else if (mode === 'DAWN') {
                buildingColor = '#e5d9c5';
                opacity = 0.80;
            }

            try {
                map.addLayer(
                    {
                        'id': '3d-buildings',
                        'source': 'composite',
                        'source-layer': 'building',
                        'filter': ['==', 'extrude', 'true'],
                        'type': 'fill-extrusion',
                        'minzoom': 13,
                        'paint': {
                            'fill-extrusion-color': buildingColor,
                            'fill-extrusion-height': [
                                'interpolate',
                                ['linear'],
                                ['zoom'],
                                13,
                                0,
                                13.05,
                                ['get', 'height']
                            ],
                            'fill-extrusion-base': [
                                'interpolate',
                                ['linear'],
                                ['zoom'],
                                13,
                                0,
                                13.05,
                                ['get', 'min_height']
                            ],
                            'fill-extrusion-opacity': opacity,
                            'fill-extrusion-ambient-occlusion-intensity': 0.4
                        }
                    },
                    labelLayerId
                );
            } catch(e) {
                console.error("Error adding 3d-buildings layer:", e);
            }

            // Apply Atmospheric 3D Lighting & Shadows
            if (typeof map.setLight === 'function') {
                try {
                    if (mode === 'NIGHT') {
                        map.setLight({
                            anchor: 'viewport',
                            color: '#818cf8',
                            intensity: 0.55,
                            position: [1.5, 90, 40]
                        });
                    } else if (mode === 'DAWN') {
                        map.setLight({
                            anchor: 'scene',
                            color: '#fbbf24',
                            intensity: 0.65,
                            position: [1.15, 130, 20]
                        });
                    } else {
                        map.setLight({
                            anchor: 'scene',
                            color: '#ffffff',
                            intensity: 0.6,
                            position: [1.15, 210, 30]
                        });
                    }
                } catch(e) {}
            }
        };

        map._dashmateSetup3d = setup3dBuildingsAndLighting;

        if (map.isStyleLoaded()) {
            setup3dBuildingsAndLighting();
        } else {
            map.on('load', setup3dBuildingsAndLighting);
        }

        setTimeout(function() { map.resize(); }, 200);

        return map;
    }
""")

private fun removeMapInstance(mapObj: JsAny): Unit = js("""
    {
        if (mapObj && typeof mapObj.remove === 'function') {
            mapObj.remove();
        }
    }
""")

private fun resizeMapJS(mapObj: JsAny): Unit = js("""
    {
        if (mapObj && typeof mapObj.resize === 'function') {
            setTimeout(function() { mapObj.resize(); }, 150);
        }
    }
""")

private fun updateMarkersAndRouteJS(
    mapboxGlObj: JsAny,
    mapObj: JsAny,
    riderLon: Double,
    riderLat: Double,
    destLon: Double?,
    destLat: Double?,
    coordinatesJson: String
): Unit = js("""
    {
        if (!mapObj) return;

        // Cleanup existing markers
        if (!mapObj._dashmateMarkers) {
            mapObj._dashmateMarkers = [];
        }
        mapObj._dashmateMarkers.forEach(function(m) { m.remove(); });
        mapObj._dashmateMarkers = [];

        // 1. Custom Rider Marker Element (Blue Pulsing Badge)
        const riderEl = document.createElement('div');
        riderEl.className = 'dashmate-rider-marker';
        riderEl.innerHTML = `
            <div style="
                position: relative;
                width: 44px;
                height: 44px;
                display: flex;
                align-items: center;
                justify-content: center;
            ">
                <div style="
                    position: absolute;
                    width: 100%;
                    height: 100%;
                    border-radius: 50%;
                    background: rgba(0, 122, 255, 0.3);
                    animation: dashmatePulse 2s infinite;
                "></div>
                <div style="
                    width: 32px;
                    height: 32px;
                    background: #007AFF;
                    border: 3px solid #FFFFFF;
                    border-radius: 50%;
                    box-shadow: 0 4px 12px rgba(0, 122, 255, 0.5);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    font-size: 16px;
                    z-index: 2;
                ">🛵</div>
            </div>
            <style>
                @keyframes dashmatePulse {
                    0% { transform: scale(0.8); opacity: 0.8; }
                    70% { transform: scale(1.6); opacity: 0; }
                    100% { transform: scale(0.8); opacity: 0; }
                }
            </style>
        `;

        const riderMarker = new mapboxGlObj.Marker({ element: riderEl, anchor: 'center' })
            .setLngLat([riderLon, riderLat])
            .addTo(mapObj);
        mapObj._dashmateMarkers.push(riderMarker);

        // 2. Custom Destination Marker Element (Crimson Flag Badge)
        if (destLon !== null && destLat !== null && destLon !== undefined && destLat !== undefined) {
            const destEl = document.createElement('div');
            destEl.className = 'dashmate-dest-marker';
            destEl.innerHTML = `
                <div style="
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                ">
                    <div style="
                        background: #FF3B30;
                        color: white;
                        padding: 4px 8px;
                        border-radius: 12px;
                        font-family: sans-serif;
                        font-size: 11px;
                        font-weight: 600;
                        box-shadow: 0 3px 10px rgba(255, 59, 48, 0.4);
                        border: 2px solid white;
                        white-space: nowrap;
                    ">📍 Destination</div>
                    <div style="
                        width: 0;
                        height: 0;
                        border-left: 6px solid transparent;
                        border-right: 6px solid transparent;
                        border-top: 6px solid #FF3B30;
                    "></div>
                </div>
            `;

            const destMarker = new mapboxGlObj.Marker({ element: destEl, anchor: 'bottom' })
                .setLngLat([destLon, destLat])
                .addTo(mapObj);
            mapObj._dashmateMarkers.push(destMarker);
        }

        // 3. Dual-Layer Glowing Route Line
        mapObj._dashmateLastRouteCoords = coordinatesJson;

        const applyRoute = function() {
            if (!coordinatesJson || coordinatesJson.length <= 2) return;
            try {
                const coords = JSON.parse(coordinatesJson);
                const geojson = {
                    'type': 'Feature',
                    'properties': {},
                    'geometry': {
                        'type': 'LineString',
                        'coordinates': coords
                    }
                };

                // Casing/Glow layer
                if (mapObj.getSource('route')) {
                    mapObj.getSource('route').setData(geojson);
                } else {
                    mapObj.addSource('route', {
                        'type': 'geojson',
                        'data': geojson
                    });

                    // Outer Glow
                    mapObj.addLayer({
                        'id': 'route-glow',
                        'type': 'line',
                        'source': 'route',
                        'layout': {
                            'line-join': 'round',
                            'line-cap': 'round'
                        },
                        'paint': {
                            'line-color': '#0056B3',
                            'line-width': 10,
                            'line-opacity': 0.4,
                            'line-blur': 2
                        }
                    });

                    // Inner Core Line
                    mapObj.addLayer({
                        'id': 'route-core',
                        'type': 'line',
                        'source': 'route',
                        'layout': {
                            'line-join': 'round',
                            'line-cap': 'round'
                        },
                        'paint': {
                            'line-color': '#007AFF',
                            'line-width': 5,
                            'line-opacity': 1.0
                        }
                    });
                }

                // Auto-Fit Bounds to frame the journey
                if (coords.length > 1) {
                    try {
                        const bounds = coords.reduce(function(b, c) {
                            return b.extend(c);
                        }, new mapboxGlObj.LngLatBounds(coords[0], coords[0]));

                        mapObj.fitBounds(bounds, {
                            padding: { top: 80, bottom: 80, left: 80, right: 80 },
                            maxZoom: 16,
                            pitch: 50,
                            duration: 1200
                        });
                    } catch(e) {}
                }
            } catch (e) {
                console.error("Error updating route line:", e);
            }
        };

        mapObj._dashmateReapplyRoute = applyRoute;

        if (mapObj.isStyleLoaded()) {
            applyRoute();
        } else {
            mapObj.once('load', applyRoute);
        }

        setTimeout(function() { mapObj.resize(); }, 150);
    }
""")

private var currentMap: JsAny? = null

fun initializeMap(
    containerId: String,
    accessToken: String,
    longitude: Double,
    latitude: Double,
    zoom: Double = 15.5,
    pitch: Double = 60.0,
    bearing: Double = -17.6
) {
    if (currentMap == null) {
        setAccessToken(mapboxgl, accessToken)

        currentMap = createMapInstance(
            mapboxGlObj = mapboxgl,
            container = containerId,
            style = "mapbox://styles/mapbox/streets-v12",
            lon = longitude,
            lat = latitude,
            zoom = zoom,
            pitch = pitch,
            bearing = bearing
        )
    } else {
        resizeMap()
    }
}

fun resizeMap() {
    currentMap?.let { resizeMapJS(it) }
}

fun updateMapData(
    riderLon: Double,
    riderLat: Double,
    destLon: Double?,
    destLat: Double?,
    routePoints: List<Pair<Double, Double>>
) {
    val map = currentMap ?: return

    val jsonArray = routePoints.joinToString(prefix = "[", postfix = "]") { (lon, lat) ->
        "[$lon,$lat]"
    }

    updateMarkersAndRouteJS(
        mapboxGlObj = mapboxgl,
        mapObj = map,
        riderLon = riderLon,
        riderLat = riderLat,
        destLon = destLon,
        destLat = destLat,
        coordinatesJson = jsonArray
    )
}

private fun changeStyleJS(
    mapObj: JsAny,
    styleUrl: String,
    mode: String
): Unit = js("""
    {
        if (!mapObj) return;

        mapObj._dashmateStyleMode = mode;

        const onStyleLoad = function() {
            if (typeof mapObj._dashmateSetup3d === 'function') {
                mapObj._dashmateSetup3d();
            }
            if (typeof mapObj._dashmateReapplyRoute === 'function') {
                mapObj._dashmateReapplyRoute();
            }
        };

        if (typeof mapObj.once === 'function') {
            mapObj.once('style.load', onStyleLoad);
        }

        mapObj.setStyle(styleUrl);
    }
""")

private fun setMap2DJS(
    mapObj: JsAny
): Unit = js("""
    {
        if (!mapObj) return;

        mapObj.easeTo({
            pitch: 0,
            bearing: 0,
            duration: 800
        });
    }
""")

private fun setMap3DJS(
    mapObj: JsAny
): Unit = js("""
    {
        if (!mapObj) return;

        mapObj.easeTo({
            pitch: 60,
            duration: 800
        });

        if (typeof mapObj._dashmateSetup3d === 'function') {
            mapObj._dashmateSetup3d();
        }
    }
""")

private fun setDayStyleJS(mapObj: JsAny): Unit = changeStyleJS(mapObj, "mapbox://styles/mapbox/streets-v12", "DAY")

private fun setNightStyleJS(mapObj: JsAny): Unit = changeStyleJS(mapObj, "mapbox://styles/mapbox/dark-v11", "NIGHT")

private fun setDawnStyleJS(mapObj: JsAny): Unit = changeStyleJS(mapObj, "mapbox://styles/mapbox/light-v11", "DAWN")

private fun recenterMapJS(
    mapObj: JsAny,
    lon: Double,
    lat: Double
): Unit = js("""
    {
        if (!mapObj) return;

        mapObj.flyTo({
            center: [lon, lat],
            zoom: 16,
            duration: 1200
        });
    }
""")

fun setMap2D() {
    currentMap?.let { setMap2DJS(it) }
}

fun setMap3D() {
    currentMap?.let { setMap3DJS(it) }
}

fun setMapDayStyle() {
    currentMap?.let { setDayStyleJS(it) }
}

fun setMapNightStyle() {
    currentMap?.let { setNightStyleJS(it) }
}

fun setMapDawnStyle() {
    currentMap?.let { setDawnStyleJS(it) }
}

fun recenterMap(longitude: Double, latitude: Double) {
    currentMap?.let { recenterMapJS(it, longitude, latitude) }
}