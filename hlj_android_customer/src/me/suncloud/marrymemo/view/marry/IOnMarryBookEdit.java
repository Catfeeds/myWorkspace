package me.suncloud.marrymemo.view.marry;

import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

/**
 * Created by hua_rong on 2017/12/11
 */

public interface IOnMarryBookEdit {


    void onSave(
            double money,
            String remark,
            long typeId,
            long id,
            String guestName,
            ArrayList<Photo> photos);

    void onDelete(long id);

    void onCancel();

    void importGuestSuccess();

}
