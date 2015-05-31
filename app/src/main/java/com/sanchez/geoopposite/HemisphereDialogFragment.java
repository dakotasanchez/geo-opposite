package com.sanchez.geoopposite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class HemisphereDialogFragment extends DialogFragment {

    public interface SuperListener {
        void onSelection(boolean yesSelected);
    }

    public static HemisphereDialogFragment newInstance(SuperListener listener) {
        HemisphereDialogFragment hDF = new HemisphereDialogFragment();
        hDF.setTargetFragment((Fragment) listener, 1234);
        return hDF;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.same_hemisphere)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment parent = getTargetFragment();
                        ((SuperListener) parent).onSelection(true);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment parent = getTargetFragment();
                        ((SuperListener) parent).onSelection(false);
                    }
                });
        return  builder.create();
    }
}
