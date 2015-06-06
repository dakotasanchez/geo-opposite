package com.sanchez.geoopposite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class HemisphereDialogFragment extends DialogFragment {

    private static String COORDS_KEY = "com.sanchez.geoopposite.coords_key";

    public interface SuperListener {
        void onHemisphereSelection(double[] coords);
    }

    public static HemisphereDialogFragment newInstance(SuperListener listener, double[] coords) {
        HemisphereDialogFragment hDF = new HemisphereDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putDoubleArray(COORDS_KEY, coords);
        hDF.setArguments(bundle);
        hDF.setTargetFragment((Fragment) listener, 1234);
        return hDF;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final double[] coords = bundle.getDoubleArray(COORDS_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.same_hemisphere)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment parent = getTargetFragment();
                        coords[0] *= -1;
                        ((SuperListener) parent).onHemisphereSelection(coords);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment parent = getTargetFragment();
                        ((SuperListener) parent).onHemisphereSelection(coords);
                    }
                });
        return  builder.create();
    }
}
