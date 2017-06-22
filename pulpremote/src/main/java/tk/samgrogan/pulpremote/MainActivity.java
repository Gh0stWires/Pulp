package tk.samgrogan.pulpremote;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

import tk.samgrogan.pulp.R;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextView;
    private Button mNextButton;
    private GoogleApiClient mApiClient;
    private static final String node = "turn_page";
    private String nodeId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mNextButton = (Button) findViewById(R.id.next);
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PutDataRequest request = PutDataRequest.create("/next");
                        Wearable.DataApi.putDataItem(mApiClient, request);
                        Wearable.DataApi.deleteDataItems(mApiClient,request.getUri());
                    }
                });
            }
        });

        mApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        CapabilityApi.CapabilityListener capabilityListener = new CapabilityApi.CapabilityListener() {
            @Override
            public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                updateCapability(capabilityInfo);
                Log.d("Wear Cap", "It was called");
            }
        };

        Wearable.CapabilityApi.addCapabilityListener(mApiClient, capabilityListener, node);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiClient.connect();
    }

    public void changePage(View view){

    }

    public void getHost(){

        CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(mApiClient, node,
                CapabilityApi.FILTER_REACHABLE).await();
        updateCapability(result.getCapability());
    }

    public void updateCapability(CapabilityInfo info){

        Set<Node> nodes = info.getNodes();
        nodeId = pickBestNodeId(nodes);
        Log.d("Wear Cap", "It was called, Duuude");
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        Log.d("Wear Cap", "yep this too");
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Wear","Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Wear","Failed");
    }
}
