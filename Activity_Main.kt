<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:ignore="UseCompoundDrawables">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="match_parent"
        android:layout_height="166dp"
        android:background="#E967FF"
        android:contentDescription="LOGO"
        android:src="@android:drawable/ic_dialog_map"
        tools:ignore="ImageContrastCheck" />

    <TextView
        android:id="@+id/titulo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="30dp"
        android:fontFamily="serif-monospace"
        android:text="GPS"
        android:textColor="@color/black"
        android:textSize="90sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/subtitulo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:fontFamily="serif-monospace"
        android:text="Enviar coordenadas"
        android:textColor="@color/black"
        android:textSize="30sp"
        android:textStyle="bold" />

    <Switch
        android:id="@+id/switch1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:scaleX="2"
        android:scaleY="2"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="30dp" />

    <Button
        android:id="@+id/Enviar"
        android:layout_width="153dp"
        android:layout_height="59dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="30dp"
        android:background="#F2A4FF"
        android:fontFamily="monospace"
        android:radius="30dp"
        android:text="Enviar"
        android:textSize="30sp"
        tools:ignore="TextSizeCheck" />

</LinearLayout>
