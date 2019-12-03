package halla.icsw.mysns.listener;

public interface OnPostListener {
    void onDelete(String Id);
    void onModify(String Id);
}
