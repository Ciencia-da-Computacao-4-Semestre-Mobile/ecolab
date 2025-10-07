package com.example.ecolab.ui.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
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

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0003\u00a8\u0006\n"}, d2 = {"MapScreen", "", "viewModel", "Lcom/example/ecolab/feature/map/MapViewModel;", "getMarkerIconBitmap", "Lcom/google/android/gms/maps/model/BitmapDescriptor;", "context", "Landroid/content/Context;", "category", "", "app_debug"})
public final class MapScreenKt {
    
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class, androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void MapScreen(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.feature.map.MapViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final com.google.android.gms.maps.model.BitmapDescriptor getMarkerIconBitmap(android.content.Context context, java.lang.String category) {
        return null;
    }
}