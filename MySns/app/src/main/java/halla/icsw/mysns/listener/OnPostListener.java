package halla.icsw.mysns.listener;

import halla.icsw.mysns.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
