package spajam2017.haggy.yourrope.fragments;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import spajam2017.haggy.yourrope.R;
import spajam2017.haggy.yourrope.bluetooth.BleWrapper;
import spajam2017.haggy.yourrope.bluetooth.MyGattAttribute;
import spajam2017.haggy.yourrope.bluetooth.RopeDevice;
import spajam2017.haggy.yourrope.util.RopeUtils;

/**
 * Ropeデバイスペアリング画面
 */
public class PairingFragment extends Fragment
        implements BleWrapper.IBleScannerListener {

    public static final String TAG = PairingFragment.class.getSimpleName();

    /**
     * デバイス選択リスナー
     */
    public interface OnRopeSelectedListener {

        /**
         * デバイスが選択された
         */
        void onRopeSelected(final String name);
    }

    private OnRopeSelectedListener onRopeSelectedListener;

    private BleWrapper bleWrapper;

    private RopeAdapter ropeAdapter;

    public static PairingFragment newInstance() {
        final PairingFragment fragment = new PairingFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PairingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pairing, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRopeSelectedListener) {
            onRopeSelectedListener = (OnRopeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRopeSelectedListener = null;

        if (bleWrapper != null) {
            bleWrapper.stopScan();
            bleWrapper.terminate();
            bleWrapper = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        initViews();
        startSearchCarries();
    }

    private void initViews() {
        final View view = getView();

        ListView listView = (ListView) view.findViewById(R.id.search_listview);
        ropeAdapter = new RopeAdapter();
        listView.setAdapter(ropeAdapter);
        listView.setOnItemClickListener(onCarryClickedListener);
    }

    private void startSearchCarries() {

        try {
            bleWrapper = BleWrapper.getsInstance(getContext());
            bleWrapper.initialize();
            bleWrapper.startScan(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemClickListener onCarryClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final RopeDevice rope = (RopeDevice) parent.getAdapter().getItem(position);
            if (onRopeSelectedListener != null) {
                onRopeSelectedListener.onRopeSelected(rope.device.getAddress());
            }
        }
    };

    @Override
    public void onScanResult(BluetoothDevice device, int rssi, byte[] data) {

        boolean exist = false;
        final String address = device.getAddress();
        final String name = device.getName();
        for (RopeDevice rope : foundDeviceList) {
            if (rope.device.getAddress().equals(address)) {
                exist = true;
                rope.rssi = rssi;
                break;
            }
        }

        //TODO: デバイス名決まったら有効にする
//        if (!exist && (name != null) && (MyGattAttribute.MY_ROPE_DEVICE_NAME.equals(name))) {
        if (!exist && (name != null)) {
            final RopeDevice newDevice = new RopeDevice();
            newDevice.device = device;
            newDevice.rssi = rssi;
            foundDeviceList.add(newDevice);
        }

        if (ropeAdapter != null) {
            ropeAdapter.notifyDataSetChanged();
        }
    }

    private List<RopeDevice> foundDeviceList = new ArrayList<>();

    // ---------------------------------------
    // ---------------------------------------

    private static class ViewHolder {
        TextView name;
        TextView address;
        ImageView rssiIcon;
        TextView rssi;
    }

    private class RopeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return foundDeviceList.size();
        }

        @Override
        public RopeDevice getItem(int position) {
            if (position < getCount()) {
                return foundDeviceList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                final LayoutInflater inflater = getActivity().getLayoutInflater();
                View v = inflater.inflate(R.layout.pairing_device_list_item, parent, false);

                holder.name = (TextView) v.findViewById(R.id.search_item_text_name);
                holder.address = (TextView) v.findViewById(R.id.search_item_text_address);
                holder.rssiIcon = (ImageView) v.findViewById(R.id.search_item_image_rssi);
                holder.rssi = (TextView) v.findViewById(R.id.search_item_text_rssi);

                convertView = v;
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RopeDevice rope = getItem(position);
            if (rope != null) {

                holder.name.setText(rope.device.getName());
                holder.address.setText(rope.device.getAddress());
                holder.rssi.setText("" + rope.rssi);

                final int wifiLevelResourceId = RopeUtils.getWifiLevelResourceId(rope.rssi);
                holder.rssiIcon.setImageResource(wifiLevelResourceId);
            }

            return convertView;
        }
    }
}
