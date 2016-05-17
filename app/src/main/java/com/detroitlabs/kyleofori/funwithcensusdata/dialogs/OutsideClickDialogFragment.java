package com.detroitlabs.kyleofori.funwithcensusdata.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class OutsideClickDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        CharSequence message = getArguments().getCharSequence("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
    }
}
