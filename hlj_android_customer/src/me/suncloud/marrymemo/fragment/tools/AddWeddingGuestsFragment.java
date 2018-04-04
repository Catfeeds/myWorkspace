package me.suncloud.marrymemo.fragment.tools;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.view.tools.EditWeddingTableActivity;
import me.suncloud.marrymemo.view.tools.ImportWeddingGuestsActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 手动添加宾客fragment
 * Created by chen_bin on 2017/11/22 0022.
 */
@RuntimePermissions
public class AddWeddingGuestsFragment extends DialogFragment {

    @BindView(R.id.et_guest_names)
    EditText etGuestNames;

    private WeddingTable table;

    private Unbinder unbinder;

    public final static String ARG_TABLE = "table";

    public static AddWeddingGuestsFragment newInstance(WeddingTable table) {
        AddWeddingGuestsFragment fragment = new AddWeddingGuestsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_TABLE, table);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
        if (getArguments() != null) {
            table = getArguments().getParcelable(ARG_TABLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_add_wedding_guests,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.dialog_anim_rise_style);
            getDialog().setCanceledOnTouchOutside(true);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        if (etGuestNames.length() == 0) {
            return;
        }
        String[] names = etGuestNames.getText()
                .toString()
                .replaceAll("(\\s{2,})|(,+)|(，+)|(。+)|(\\.+)|(;+)|(；+)", " ")
                .split(" ");
        if (names.length > 0) {
            List<WeddingGuest> guests = new ArrayList<>();
            for (String name : names) {
                WeddingGuest guest = new WeddingGuest();
                guest.setNum(1);
                guest.setFullName(name);
                guests.add(guest);
            }
            addGuests(guests);
        }
        dismiss();
    }

    @OnClick(R.id.import_guests_layout)
    void onImportGuests() {
        AddWeddingGuestsFragmentPermissionsDispatcher.onReadContactsWithCheck(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.IMPORT_WEDDING_GUESTS:
                    if (data == null) {
                        return;
                    }
                    List<WeddingGuest> guests = data.getParcelableArrayListExtra(
                            ImportWeddingGuestsActivity.ARG_GUESTS);
                    if (CommonUtil.isCollectionEmpty(guests)) {
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0, size = guests.size(); i < size; i++) {
                        WeddingGuest guest = guests.get(i);
                        sb.append(guest.getFullName());
                        if (i < size - 1) {
                            sb.append(" ");
                        }
                    }
                    etGuestNames.setText(sb.toString());
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void onReadContacts() {
        Intent intent = new Intent(getContext(), ImportWeddingGuestsActivity.class);
        intent.putExtra(ImportWeddingGuestsActivity.ARG_TABLE, table);
        intent.putExtra(ImportWeddingGuestsActivity.ARG_TYPE,
                ImportWeddingGuestsActivity.TYPE_CONTACT);
        startActivityForResult(intent, Constants.RequestCode.IMPORT_WEDDING_GUESTS);
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void onRationale(final PermissionRequest request) {
        final Dialog dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_wedding_guest_permission_hint);
        dialog.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        request.cancel();
                    }
                });
        dialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        request.proceed();
                    }
                });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AddWeddingGuestsFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addGuests(List<WeddingGuest> guests) {
        if (getContext() instanceof EditWeddingTableActivity) {
            ((EditWeddingTableActivity) getContext()).addGuestsData(guests);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
