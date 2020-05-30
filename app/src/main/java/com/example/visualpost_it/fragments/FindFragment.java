package com.example.visualpost_it.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SimpleArrayMap;
import androidx.fragment.app.Fragment;

import com.example.visualpost_it.R;
import com.example.visualpost_it.dtos.PlaceAdvertised;
import com.example.visualpost_it.util.BitmapDataObject;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FindFragment extends Fragment {

    private static final String TAG = "FindFragment";
    private Button advertiseBtn;
    private Button findBtn;
    private ImageView placePhoto;
    private ImageView advertisePhoto;
    private ScrollView placeFoundScrollView;
    private MaterialCardView findCardView;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    private Context mContext;

    private ConnectionsClient connectionsClient;
    private Payload filePayload;
    private Uri uri;

    public static final Strategy STRATEGY = Strategy.P2P_POINT_TO_POINT;
    public static final String SERVICE_ID="com.virtualPostIt.app";
    private String strendPointId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        connectionsClient = Nearby.getConnectionsClient(view.getContext());
        placeFoundScrollView = view.findViewById(R.id.place_found_scroll_view);
        placeFoundScrollView.setEnabled(false);

        findCardView = view.findViewById(R.id.find_card);
        findCardView.setEnabled(false);

        placePhoto = view.findViewById(R.id.find_place_photo);

        advertiseBtn = view.findViewById(R.id.btn_advertise_nearby);
        advertiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlaceAdvertiser(v);
            }
        });
        
        findBtn = view.findViewById(R.id.btn_find_nearby);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });
        return view;
        
    }

    private void loadPlaceAdvertiser(View v) {
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.layout_advertise_place,
                v.findViewById(R.id.layout_advertise_root));

        String[] TYPES = new String[] {
                "Museum", "Park", "Restaurant", "Castle", "Gas Station"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, TYPES);

        advertisePhoto = layout.findViewById(R.id.advertise_photo);
        EditText placeName = (EditText) layout.findViewById(R.id.advertise_placename);
        EditText description = (EditText) layout.findViewById(R.id.advertise_description);
        AutoCompleteTextView typesOfPlaces = (AutoCompleteTextView) layout.findViewById(R.id.advertise_type);

        typesOfPlaces.setAdapter(adapter);

        advertisePhoto.setImageResource(R.mipmap.no_photo);
        advertisePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on photo");
                checkGalleryPermissions();
            }
        });
        
        imageDialog.setView(layout);
        imageDialog.setPositiveButton("ADVERTISE", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {

                BitmapDrawable drawable = (BitmapDrawable) advertisePhoto.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Log.d(TAG, "onClick: bitmap height: " + bitmap.getHeight());
                Log.d(TAG, "onClick: bitmap width: " + bitmap.getWidth());

                BitmapDataObject bitmapDataObject = new BitmapDataObject(bitmap);
                Log.d(TAG, "onClick: bitmapDataObject height: " + bitmapDataObject.getCurrentImage().getHeight());
                Log.d(TAG, "onClick: bitmapDataObject width: " + bitmapDataObject.getCurrentImage().getWidth());
//                Log.d(TAG, "onClick: photoUri: " + uri);
                String advertisedPlaceName = placeName.getText().toString();
                String advertisedPlaceType = typesOfPlaces.getText().toString();
                String advertisedPlaceDescription = description.getText().toString();

//                Log.d(TAG, "onClick: FilePayload: Size: " + filePayload.asFile().getSize());

                PlaceAdvertised placeAdvertised = new PlaceAdvertised(bitmapDataObject, advertisedPlaceName, advertisedPlaceType, advertisedPlaceDescription);
                Log.d(TAG, "onClick: " + placeAdvertised.toString());
                dialog.dismiss();
                startAdvertising(placeAdvertised);
            }

        });

        imageDialog.create();
        imageDialog.show();
    }

    private void checkGalleryPermissions() {
        if(mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_CODE);
        } else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            uri = data.getData();

            Log.d(TAG, "onPhotoPicked: uri: " + uri);

            try {
                ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(uri, "r");
                filePayload = Payload.fromFile(pfd);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "onActivityResult: FileNotFoundException");
                e.printStackTrace();
            }
            advertisePhoto.setImageURI(data.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                } else {
                    Toast.makeText(mContext, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startDiscovery() {
        Log.d(TAG, "startDiscovery: ");
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(mContext).
                startDiscovery(SERVICE_ID, new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                        Nearby.getConnectionsClient(mContext).
                                requestConnection("Device B", endpointId, new ConnectionLifecycleCallback() {
                                    @Override
                                    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                                        Log.d(TAG, "onConnectionInitiated: on discoverer");
                                        Nearby.getConnectionsClient(mContext).acceptConnection(endpointId, mPayloadCallback);
                                    }
                                    @Override
                                    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                                        switch (connectionResolution.getStatus().getStatusCode()) {
                                            case ConnectionsStatusCodes.STATUS_OK:
                                                Log.d(TAG, "onConnectionResult: STATUS_OK on discoverer");
                                                // We're connected! Can now start sending and receiving data.
                                                break;
                                            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                                Log.d(TAG, "onConnectionResult: STATUS_CONNECTION_REJECTED on discoverer");
                                                // The connection was rejected by one or both sides.
                                                break;
                                            case ConnectionsStatusCodes.STATUS_ERROR:
                                                Log.d(TAG, "onConnectionResult: STATUS_ERROR on discoverer");
                                                // The connection broke before it was able to be accepted.
                                                break;
                                            default:
                                                // Unknown status code
                                        }
                                    }
                                    @Override
                                    public void onDisconnected(@NonNull String s) {
                                        Log.d(TAG, "onDisconnected: on discoverer");
                                    }
                                });
                    }
                    @Override
                    public void onEndpointLost(@NonNull String s) {
                        // disconnected
                    }
                }, discoveryOptions);
    }

    private void startAdvertising(PlaceAdvertised placeAdvertised) {
        Log.d(TAG, "startAdvertising: ");
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(mContext).startAdvertising("Device A", SERVICE_ID, new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo connectionInfo) {
                Log.d(TAG, "onConnectionInitiated: ");
                Nearby.getConnectionsClient(mContext).acceptConnection(endPointId, mPayloadCallback);
            }
            @Override
            public void onConnectionResult(@NonNull String endPointId, @NonNull ConnectionResolution connectionResolution) {
                switch (connectionResolution.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        Log.d(TAG, "onConnectionResult: STATUS_OK on advertiser");
                        // We're connected! Can now start sending and receiving data.
                        strendPointId = endPointId;
                        sendPayLoad(strendPointId, placeAdvertised);
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        Log.d(TAG, "onConnectionResult: STATUS_CONNECTION_REJECTED on advertiser");
                        // The connection was rejected by one or both sides.
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        Log.d(TAG, "onConnectionResult: STATUS_ERROR on advertiser");
                        // The connection broke before it was able to be accepted.
                        break;
                    default:
                        // Unknown status code
                }
            }
            @Override
            public void onDisconnected(@NonNull String s) {
                Log.d(TAG, "onDisconnected: ");
//                strendPointId = null;
            }
        }, advertisingOptions);
    }

    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        private final SimpleArrayMap<Long, Payload> incomingFilePayloads = new SimpleArrayMap<>();
        private final SimpleArrayMap<Long, Payload> completedFilePayloads = new SimpleArrayMap<>();
        private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(payload.getType() == Payload.Type.BYTES) {
                        final byte[] receivedBytes = payload.asBytes();
                        if (receivedBytes != null) {
                            PlaceAdvertised placeReceived = SerializationUtils.deserialize(receivedBytes);
                            BitmapDataObject bitmapDataObject = placeReceived.getImageBitmap();
                            Log.d(TAG, "onPayloadReceived: bitmapDataObject height: " + bitmapDataObject.getCurrentImage().getHeight());
                            Log.d(TAG, "onPayloadReceived: bitmapDataObject width: " + bitmapDataObject.getCurrentImage().getWidth());
                            placePhoto.setImageBitmap(bitmapDataObject.getCurrentImage());
//                            long payloadId = addPayloadFilename(placeReceived.getFilenameMessage());
//                            processFilePayload(payloadId);

                            Log.d(TAG, "onPayloadReceived: received on discoverer " + placeReceived.toString());
                        }
                    } else if (payload.getType() == Payload.Type.FILE){
                        incomingFilePayloads.put(payload.getId(), payload);
                    }
                }
            });
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s,
                                            @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
//                long payloadId = payloadTransferUpdate.getPayloadId();
//                Payload payload = incomingFilePayloads.remove(payloadId);
//                completedFilePayloads.put(payloadId, payload);
//                if (payload.getType() == Payload.Type.FILE) {
//                    processFilePayload(payloadId);
//                }
            }
        }

        private void processFilePayload(long payloadId) {
            // BYTES and FILE could be received in any order, so we call when either the BYTES or the FILE
            // payload is completely received. The file payload is considered complete only when both have
            // been received.
            Payload filePayload = completedFilePayloads.get(payloadId);
            String filename = filePayloadFilenames.get(payloadId);
            if (filePayload != null && filename != null) {
                completedFilePayloads.remove(payloadId);
                filePayloadFilenames.remove(payloadId);

                // Get the received file (which will be in the Downloads folder)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Because of https://developer.android.com/preview/privacy/scoped-storage, we are not
                    // allowed to access filepaths from another process directly. Instead, we must open the
                    // uri using our ContentResolver.
                    uri = android.net.Uri.parse(filePayload.asFile().asJavaFile().toURI().toString());
                    Log.d(TAG, "processFilePayload: uriAiurea: " + uri.toString());
                    try {
                        // Copy the file to a new location.
                        InputStream in = mContext.getContentResolver().openInputStream(uri);
                        copyStream(in, new FileOutputStream(new File(mContext.getCacheDir(), filename)));
                    } catch (IOException e) {
                        // Log the error.
                    } finally {
                        // Delete the original file.
                        mContext.getContentResolver().delete(uri, null, null);
                    }
                } else {
                    File payloadFile = filePayload.asFile().asJavaFile();

                    // Rename the file.
                    payloadFile.renameTo(new File(payloadFile.getParentFile(), filename));
                }
            }
        }

        private void copyStream(InputStream in, FileOutputStream fileOutputStream) throws IOException {
            try {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }
                fileOutputStream.flush();
            } finally {
                in.close();
                fileOutputStream.close();
            }
        }

        private long addPayloadFilename(String payloadFilenameMessage) {
            String[] parts = payloadFilenameMessage.split(":");
            long payloadId = Long.parseLong(parts[0]);
            String filename = parts[1];
            filePayloadFilenames.put(payloadId, filename);
            return payloadId;
        }


    };

    private void sendPayLoad(final String endPointId, PlaceAdvertised placeAdvertised) {
//        placeAdvertised.setFilenameMessage(filePayload.getId() + ":" + uri.getLastPathSegment());

        byte[] payload = SerializationUtils.serialize(placeAdvertised);
        Payload bytesPayload = Payload.fromBytes(payload);

//        Payload bytesPayload = Payload.fromBytes(String.valueOf(payload).getBytes());
        Nearby.getConnectionsClient(mContext).sendPayload(endPointId, bytesPayload).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                PlaceAdvertised placeAdvertised1 = SerializationUtils.deserialize(payload);
                Log.d(TAG, "onSuccess: deserialized fields: " + placeAdvertised1.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "onFailure: sendPayload fields failed" + e.getMessage());
            }
        });
//        Nearby.getConnectionsClient(mContext).sendPayload(endPointId, filePayload).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "onSuccess: photo sent with great succes");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Log.d(TAG, "onFailure: photo failed" + e.getMessage());
//            }
//        });
    }

}
