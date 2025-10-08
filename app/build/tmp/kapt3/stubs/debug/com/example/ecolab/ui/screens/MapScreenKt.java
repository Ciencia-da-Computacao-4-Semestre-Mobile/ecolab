package com.example.ecolab.ui.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.core.content.ContextCompat;
import com.example.ecolab.R;
import com.example.ecolab.feature.map.MapViewModel;
import com.google.accompanist.permissions.ExperimentalPermissionsApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.compose.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0015\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0003\u00a2\u0006\u0002\u0010\b\u001a*\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0005H\u0002\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u000e\u0010\u000f\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0010"}, d2 = {"MapScreen", "", "viewModel", "Lcom/example/ecolab/feature/map/MapViewModel;", "getCategoryColor", "Landroidx/compose/ui/graphics/Color;", "category", "", "(Ljava/lang/String;)J", "getMarkerIconBitmap", "Lcom/google/android/gms/maps/model/BitmapDescriptor;", "context", "Landroid/content/Context;", "color", "getMarkerIconBitmap-mxwnekA", "(Landroid/content/Context;Ljava/lang/String;J)Lcom/google/android/gms/maps/model/BitmapDescriptor;", "app_debug"})
public final class MapScreenKt {
    
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class, androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void MapScreen(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.feature.map.MapViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final long getCategoryColor(java.lang.String category) {
        return 0L;
    }
}